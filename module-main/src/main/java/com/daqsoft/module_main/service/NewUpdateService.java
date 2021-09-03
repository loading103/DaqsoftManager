package com.daqsoft.module_main.service;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.daqsoft.module_main.activity.MainActivity;
import com.daqsoft.module_main.R;
import com.daqsoft.library_base.utils.SPUtils ;
import com.daqsoft.mvvmfoundation.base.BaseApplication;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import timber.log.Timber;
/**
 * 应用版本更新服务
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/9/4 0004
 * @since JDK 1.8
 */
public class NewUpdateService extends Service {

    private static final int TIMEOUT = 10 * 10000;// 超时
    private static String down_url = "";
    private static final int DOWN_OK = 1;
    private static final int DOWN_ERROR = 0;

    private String app_name;

    private NotificationManager notificationManager;
    private Notification notification;

    private Intent updateIntent;
    private PendingIntent pendingIntent;

    private int notification_id = 52012;
    public static File updateDir = null;
    public static File updateFile = null;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            app_name = intent.getStringExtra("app_name");
            down_url = intent.getStringExtra("updatepath");
            downLoad(this, down_url);
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 下载
     *
     * @param context
     * @param url
     */
    public static void downLoad(Context context, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            String serviceString = Context.DOWNLOAD_SERVICE;
            // 取得系统的下载服务
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService
                    (serviceString);

            Uri uri = Uri.parse(url);
            // 创建下载请求对象
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            String path = BaseApplication.Companion.getInstance().getApplicationContext().getExternalCacheDir().getPath()+"/"+context.getResources().getString(R.string.app_name) + ".apk";

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            request.setDestinationUri(Uri.fromFile(file));
            long refernece = downloadManager.enqueue(request);
            SPUtils.getInstance().put("refernece", refernece);

        } catch (Exception exception) {
            Toast.makeText(context, "更新失败", Toast.LENGTH_SHORT).show();
            Timber.e(exception.toString());
        }


    }

    /***
     * 开线程下载
     */
    public void createThread() {
        /***
         * 更新UI
         */
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case DOWN_OK:
                        // 下载完成，点击安装
                        Uri uri = Uri.fromFile(updateFile);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, "application/vnd.android.package-archive");
                        pendingIntent = PendingIntent.getActivity(NewUpdateService.this, 0, intent, 0);
                        // 点击后消失
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        notification.contentIntent = pendingIntent;
                        notification.contentView.setTextViewText(R.id.notificationTitle, "下载完成");
                        // 点击后消失
                        notification.flags = Notification.FLAG_AUTO_CANCEL;

                        notificationManager.notify(notification_id, notification);

                        stopService(updateIntent);
                        stopSelf();
                        break;
                    case DOWN_ERROR:
                        // 点击后消失
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        notification.contentView.setTextViewText(R.id.notificationTitle, "下载失败");
                        // 点击后消失
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        stopSelf();
                        break;

                    default:
                        stopService(updateIntent);
                        break;
                }

            }

        };

        final Message message = new Message();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    long downloadSize = downloadUpdateFile(down_url, updateFile.toString());
                    if (downloadSize > 0) {
                        // 下载成功
                        message.what = DOWN_OK;
                        handler.sendMessage(message);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    message.what = DOWN_ERROR;
                    handler.sendMessage(message);
                }

            }
        }).start();
    }


    RemoteViews contentView;

    /***
     * 创建通知栏
     */
    public void createNotification() {
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.mipmap.ic_launcher_1;
        // // 这个参数是通知提示闪出来的值.
        notification.tickerText = "开始下载";

        /***
         * 在这里我们用自定的view来显示Notification
         */
        contentView = new RemoteViews(getPackageName(), R.layout
                .zsystem_activity_notification_update_item);
        contentView.setTextViewText(R.id.notificationTitle, "正在下载");
        contentView.setTextViewText(R.id.notificationPercent, "0%");
        contentView.setProgressBar(R.id.notificationProgress, 100, 0, false);

        notification.contentView = contentView;

        //TODO 若无法下载更新需改这里
        updateIntent = new Intent(this, MainActivity.class);
        updateIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);

        notification.contentIntent = pendingIntent;

        notificationManager.notify(notification_id, notification);

    }

    /***
     * 下载文件
     *
     * @return
     * @throws MalformedURLException
     */
    public long downloadUpdateFile(String down_url, String file) throws Exception {
        // 提示step
        int down_step = 5;
        // 文件总大小
        int totalSize;
        // 已经下载好的大小
        int downloadCount = 0;
        // 已经上传的文件大小
        int updateCount = 0;
        InputStream inputStream;
        OutputStream outputStream;

        URL url = new URL(down_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        // 获取下载文件的size
        totalSize = httpURLConnection.getContentLength();
        if (httpURLConnection.getResponseCode() == 404) {
            throw new Exception("fail!");
        }
        inputStream = httpURLConnection.getInputStream();
        // 文件存在则覆盖掉
        outputStream = new FileOutputStream(file, false);
        byte buffer[] = new byte[1024];
        int readsize = 0;
        while ((readsize = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, readsize);
            // 时时获取下载到的大小
            downloadCount += readsize;
            /**
             * 每次增张5%
             */
            if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
                updateCount += down_step;
                // 改变通知栏
                // notification.setLatestEventInfo(this, "正在下载...", updateCount
                // + "%" + "", pendingIntent);
                contentView.setTextViewText(R.id.notificationPercent, updateCount + "%");
                contentView.setProgressBar(R.id.notificationProgress, 100, updateCount, true);
                // show_view
                notificationManager.notify(notification_id, notification);

            }

        }
        if (httpURLConnection != null) {
            httpURLConnection.disconnect();
        }
        inputStream.close();
        outputStream.close();

        return downloadCount;

    }
}
