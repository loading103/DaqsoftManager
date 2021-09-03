package com.daqsoft.library_base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import java.io.File;


public class MyUtils {

    /**
     * base 64编码
     * @param data
     * @return
     */

    public static String encodeString64(String data) {
        try {
            String base64String = Base64.encodeToString(data.getBytes("utf-8"), Base64.DEFAULT);
            return base64String;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * base 64解码
     * @param base64String
     * @return
     */
    public static String decodeString64(String base64String) {
        try {
            byte[] data = Base64.decode(base64String.getBytes("utf-8"), Base64.DEFAULT);
            return new String(data,"utf-8");
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return "";
    }

    public static boolean isNetworkAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        } else {
            //如果仅仅是用来判断网络连接
            //则可以使用 cm.getActiveNetworkInfo().isAvailable();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                Network[] networks = cm.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = cm.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        return true;
                    }
                }

            } else {
                NetworkInfo[] info = cm.getAllNetworkInfo();
                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * 版本命名 V1.1.3
     * 判断是否需要升级
     *
     * @param serverVersion
     * @return
     */
    public static Boolean isNeedUpdate(Context context,String currentVersion, String serverVersion) {
        Log.e("isNeedUpdate", "isNeedUpdate");
        String serverV = serverVersion.toLowerCase();
        String curVersion = currentVersion.toLowerCase();
        curVersion = curVersion.replace("v", "").trim();
        serverV = serverV.replace("v", "").trim();
        String[] curS = curVersion.toLowerCase().split("\\.");
        String[] servS = serverV.split("\\.");
        if (curS == null || servS == null) {
            Log.e("isNeedUpdate", "null fa sheng l");
            return false;
        }
        if (curS.length < 3 || servS.length < 3) {
            Log.e("isNeedUpdate", "<3 fuck");
            return false;
        }
        int cur1 = Integer.parseInt(curS[0]);
        int cur2 = Integer.parseInt(curS[1]);
        int cur3 = Integer.parseInt(curS[2]);
        int serv1 = Integer.parseInt(servS[0]);
        int serv2 = Integer.parseInt(servS[1]);
        int serv3 = Integer.parseInt(servS[2]);
        Log.e("isNeedUpdate", "" + cur1 + " " + cur2 + " " + cur3 + " " + serv1 + " " + serv2 + " " + serv3);
        int cur4 = 0;
        int serv4 = 0;
        if (curS.length > 3) {

        }

        if (serv1 == cur1) {
            if (serv2 < cur2) {
                return false;
            } else {

                if (serv2 == cur2) {
                    if (serv3 < cur3) {
                        return false;
                    }
                }
            }

        }
        if (serv1 < cur1) {
            return false;
        }

        if (serv1 > cur1) {
            return true;
        } else if (serv2 > cur2) {
            return true;
        } else if (serv3 > cur3) {
            return true;
        }
        return false;
    }
    /**
     * 通过Uri返回File文件
     * 注意：通过相机的是类似content://media/external/images/media/97596
     * 通过相册选择的：file:///storage/sdcard0/DCIM/Camera/IMG_20150423_161955.jpg
     * 通过查询获取实际的地址
     * */
    public static File getFileByUri(Activity activity,Uri uri) {
        String path = null;
        if ("file".equals(uri.getScheme())) {
            path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = activity.getContentResolver();
                StringBuffer buff = new StringBuffer();
                buff.append("(").append(MediaStore.Images.ImageColumns.DATA).append("=").append("'" + path + "'").append(")");
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.Images.ImageColumns._ID, MediaStore.Images.ImageColumns.DATA }, buff.toString(), null, null);
                int index = 0;
                int dataIdx = 0;
                for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                    index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                    index = cur.getInt(index);
                    dataIdx = cur.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    path = cur.getString(dataIdx);
                }
                cur.close();
                if (index == 0) {
                } else {
                    Uri u = Uri.parse("content://media/external/images/media/" + index);
                    System.out.println("temp uri is :" + u);
                }
            }
            if (path != null) {
                return new File(path);
            }
        } else if ("content".equals(uri.getScheme())) {
            // 4.2.2以后
            String[] proj = { MediaStore.Images.Media.DATA };
            Cursor cursor = activity.getContentResolver().query(uri, proj, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
            }
            cursor.close();

            return new File(path);
        } else {
            Log.i("MyUtils", "Uri Scheme:" + uri.getScheme());
        }
        return null;
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
}
