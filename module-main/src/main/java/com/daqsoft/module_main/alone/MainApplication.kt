package com.daqsoft.module_main.alone

import com.daqsoft.library_base.config.ModuleLifecycleConfig
import com.daqsoft.mvvmfoundation.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        //初始化组件(靠前)
        ModuleLifecycleConfig.initModuleAhead(this)
        //初始化组件(靠后)
        ModuleLifecycleConfig.initModuleLow(this)

    }
}