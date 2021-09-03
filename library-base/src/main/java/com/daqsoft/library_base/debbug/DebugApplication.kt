package com.daqsoft.library_base.debbug

import com.daqsoft.library_base.config.ModuleLifecycleConfig
import com.daqsoft.mvvmfoundation.base.BaseApplication
import dagger.hilt.android.HiltAndroidApp

/**
 * 弃用，本来是组件单独使用时的Application
 * 但是现在使用了 hilt  需要单独自己创建 并使用   @HiltAndroidApp 注解
 */
@Deprecated("")
class DebugApplication : BaseApplication() {
    override fun onCreate() {
        super.onCreate()
        //初始化组件(靠前)
        ModuleLifecycleConfig.initModuleAhead(this)
        //初始化组件(靠后)
        ModuleLifecycleConfig.initModuleLow(this)

    }
}