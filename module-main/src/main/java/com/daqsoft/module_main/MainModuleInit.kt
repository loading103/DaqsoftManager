package com.daqsoft.module_main

import android.app.Application
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.BuildConfig
import com.daqsoft.library_base.init.IModuleInit
import timber.log.Timber

class MainModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        Timber.e("主业务模块初始化 -- onInitAhead")
        return false

    }

    override fun onInitLow(application: Application): Boolean {
        Timber.e("主业务模块初始化 -- onInitLow")
        return false
    }



}