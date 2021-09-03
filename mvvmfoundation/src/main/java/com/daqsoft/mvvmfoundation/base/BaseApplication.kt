package com.daqsoft.mvvmfoundation.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.annotation.NonNull
import com.daqsoft.mvvmfoundation.utils.ContextUtils


/**
 * Author: Walk The Line
 * Date: 2020/10/25 15:59
 * Description:
 */

open class BaseApplication : Application() {

    companion object{
        private var sInstance: Application? = null
        fun getInstance(): Application? {
            if (sInstance == null) {
                throw NullPointerException("please inherit BaseApplication or call setApplication.")
            }
            return sInstance
        }
    }


    override fun onCreate() {
        super.onCreate()
        setApplication(this)
    }



    @Synchronized
    fun setApplication(@NonNull application: Application) {
        sInstance = application
        ContextUtils.init(application)
        //注册监听每个activity的生命周期,便于堆栈式管理
        application.registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                AppManager.addActivity(activity)
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {
                AppManager.removeActivity(activity)
            }
        })
    }

}