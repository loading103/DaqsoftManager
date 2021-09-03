package com.daqsoft.library_base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import cn.jiguang.analytics.android.api.JAnalyticsInterface
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import timber.log.Timber
import com.daqsoft.library_base.init.IModuleInit
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.widget.ChrysanthemumFooter
import com.daqsoft.library_base.widget.ChrysanthemumHeader
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout


class BaseModuleInit : IModuleInit, Application.ActivityLifecycleCallbacks {

    companion object{
        var oaid:String = ""

        @SuppressLint("StaticFieldLeak")
        var currentActivity:Activity? = null
    }



    @SuppressLint("DefaultLocale")
    override fun onInitAhead(application: Application): Boolean {
        Timber.e("基础层初始化 -- onInitAhead")
        return false
    }

    override fun onInitLow(application: Application): Boolean {
        Timber.e("基础层初始化 -- onInitLow")
        application.registerActivityLifecycleCallbacks(this)
        initARouter(application)
        initLiveEventBus(application)
        initSmartRefreshLayout()
        initJPush(application)
        initJPushAnalytics(application)

        return false
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
    }


    /**
     *  初始化阿里路由框架
     */
    private fun initARouter(application: Application){
        if (BuildConfig.BUILD_TYPE != "release" ) {
            ARouter.openLog()     // 打印日志
            ARouter.openDebug()   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        // 尽可能早，推荐在Application中初始化
        ARouter.init(application)
    }


    /**
     * 配置LiveEventBus
     */
    private fun initLiveEventBus(application: Application){
        LiveEventBus.config().setContext(application)
    }


    /**
     * 初始化 下拉刷新
     */
    private fun initSmartRefreshLayout(){
        // 全局配置
        SmartRefreshLayout.setDefaultRefreshInitializer { context, layout ->
            // 关闭自动加载更多
            layout.setEnableAutoLoadMore(false)
            // 关闭加载更多（在需要的页面打开）
            layout.setEnableLoadMore(false)
        }

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            ChrysanthemumHeader(context)
        }

        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ChrysanthemumFooter(context)
        }
    }

    /**
     * 初始化  极光推送
     */
    private fun initJPush(application: Application){
        JPushInterface.setDebugMode(BuildConfig.BUILD_TYPE != "release")
        JPushInterface.init(application)
        val registrationID = JPushInterface.getRegistrationID(application)
        Timber.e("init $registrationID")
    }

    /**
     * 初始化  极光统计
     */
    private fun initJPushAnalytics(application: Application){
        JAnalyticsInterface.init(application)
        JAnalyticsInterface.setDebugMode(BuildConfig.BUILD_TYPE != "release")
        JAnalyticsInterface.initCrashHandler(application)
    }


}


