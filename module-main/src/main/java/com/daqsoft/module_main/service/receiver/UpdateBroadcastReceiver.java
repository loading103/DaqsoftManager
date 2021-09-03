package com.daqsoft.module_main.service.receiver;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import com.daqsoft.library_base.global.ConstantGlobal;
import com.daqsoft.library_base.utils.SPUtils ;
import java.io.File;
import com.daqsoft.module_main.activity.MainActivity;
import com.daqsoft.mvvmfoundation.base.AppManager;
import com.daqsoft.mvvmfoundation.base.BaseApplication;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.daqsoft.library_base.global.LEBKeyGlobal;
/**
 * 广播实现自动安装的更新的APK
 * 应用版本更新服务
 *
 * @author 黄熙
 * @version 1.0.0
 * @date 2018/9/4 0004
 * @since JDK 1.8
 */

public class UpdateBroadcastReceiver extends BroadcastReceiver {

    @SuppressLint("NewApi")
    public void onReceive(Context context, Intent intent) {
        long myDwonloadID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long refernece = SPUtils.getInstance().getLong("refernece", 0);
        if (refernece != myDwonloadID) {
            return;
        }

        DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadFileUri = dManager.getUriForDownloadedFile(myDwonloadID);
        installAPK(context, downloadFileUri);
    }

    private void installAPK(Context context, Uri apk) {
        if (Build.VERSION.SDK_INT < 23) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setData(apk);
            intents.setDataAndType(apk, "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
        } else {
            File file = queryDownloadedApk(context);
            if (file!=null && file.exists()) {
                openFile(file, context);
            }

        }
    }

    /**
     * 通过downLoadId查询下载的apk，解决6.0以后安装的问题
     *
     * @param context
     * @return
     */
    public static File queryDownloadedApk(Context context) {
        File targetApkFile = null;
        DownloadManager downloader = (DownloadManager) context.getSystemService(Context
                .DOWNLOAD_SERVICE);
        long downloadId = SPUtils.getInstance().getLong("refernece", -1);
        if (downloadId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(downloadId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cur = downloader.query(query);
            if (cur != null) {
                if (cur.moveToFirst()) {
                    String uriString = cur.getString(cur.getColumnIndex(DownloadManager
                            .COLUMN_LOCAL_URI));
                    if (!TextUtils.isEmpty(uriString)) {
                        targetApkFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cur.close();
            }
        }
        return targetApkFile;

    }

    @SuppressLint("WrongConstant")
    private void openFile(File file, Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startInstallO(context, file);
            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startInstallN(context, file);
            } else {
                startInstall(context, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * android1.x-6.x
     */
    private void startInstall(Context context, File file) throws Exception {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * android7.x
     */
    private void startInstallN(Context context, File file) throws Exception {
        // 参数1 上下文, 参数2 在AndroidManifest中的android:authorities值, 参数3  共享的文件

        String authority = BaseApplication.Companion.getInstance().getPackageName() + ConstantGlobal.FILE_PROVIDER;
        Uri uri = FileProvider.getUriForFile(BaseApplication.Companion.getInstance(),authority, file);
        Intent install = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 添加这一句表示对目标应用临时授权该Uri所代表的文件
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(install);
    }

    /**
     * androidp_8.x
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallO(Context context, File file) {
        try {
            boolean isGranted = context.getPackageManager().canRequestPackageInstalls();
            if (isGranted) {
                startInstallN(context, file);
            } else {
                LiveEventBus.get(LEBKeyGlobal.UPDATE_FILE).post(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
