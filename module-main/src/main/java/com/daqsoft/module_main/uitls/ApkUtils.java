package com.daqsoft.module_main.uitls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.daqsoft.library_base.global.ConstantGlobal;
import com.daqsoft.library_base.global.LEBKeyGlobal;
import com.daqsoft.module_main.R;
import com.daqsoft.mvvmfoundation.base.BaseApplication;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;

public class ApkUtils {

    public static  void installApk(Context context){
        String path = BaseApplication.Companion.getInstance().getApplicationContext().getExternalCacheDir().getPath()+"/"+context.getResources().getString(R.string.app_name) + ".apk";
        File file =new File(path);
        if (Build.VERSION.SDK_INT < 23) {
            Intent intents = new Intent();
            intents.setAction("android.intent.action.VIEW");
            intents.addCategory("android.intent.category.DEFAULT");
            intents.setType("application/vnd.android.package-archive");
            intents.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intents);
        } else {
            if (file.exists()) {
                openFile(file, context);
            }
        }
    }

    @SuppressLint("WrongConstant")
    private static  void openFile(File file, Context context) {
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
    private static void startInstall(Context context, File file) throws Exception {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }

    /**
     * android7.x
     */
    private static void startInstallN(Context context, File file) throws Exception {
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
    private static void startInstallO(Context context, File file) {
        try {
            startInstallN(context, file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
