package com.daqsoft.manager

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.webkit.WebView
import androidx.annotation.RequiresApi
import com.daqsoft.library_base.BuildConfig
import com.daqsoft.library_base.config.ModuleLifecycleConfig
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.crash.CaocConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import timber.log.Timber.DebugTree

@HiltAndroidApp
class AppApplication : BaseApplication() {

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate() {
        super.onCreate()

        webViewSetPath(this)
        initCaocConfig()
        initTimber()

        //初始化组件(靠后)
        ModuleLifecycleConfig.initModuleLow(this)
    }


    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        //初始化组件(靠前)
        ModuleLifecycleConfig.initModuleAhead(this)
    }

    /**
     *  日志打印
     */
    fun initTimber(){
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    /**
     *   配置全局异常崩溃操作
     */
    fun  initCaocConfig(){
        CaocConfig.Builder.create()
            .enabled(BuildConfig.BUILD_TYPE != "release") //是否启动全局异常捕获
            .showErrorDetails(BuildConfig.BUILD_TYPE != "release") //是否显示错误详细信息
            .showRestartButton(BuildConfig.BUILD_TYPE != "release") //是否显示重启按钮
            .trackActivities(BuildConfig.BUILD_TYPE != "release") //是否跟踪Activity
            .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
            .apply()
    }


    //Android P 以及之后版本不支持同时从多个进程使用具有相同数据目录的WebView
    //为其它进程webView设置目录

    @RequiresApi(Build.VERSION_CODES.P)
    fun webViewSetPath(context: Context?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = getProcessName(context)
            if ("com.daqsoft.manager" != processName) {//判断不等于默认进程名称
                WebView.setDataDirectorySuffix(processName)
            }
        }
    }

    private fun getProcessName(context: Context?): String? {
        if (context == null) return null
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val processInfo = manager.runningAppProcesses
        processInfo.forEach {
            if (it.pid == android.os.Process.myPid()) {
                return it.processName
            }
        }
        return null
    }
}