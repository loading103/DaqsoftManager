package com.daqsoft.module_workbench

import android.app.Application
import com.daqsoft.library_base.init.IModuleInit
import timber.log.Timber

class WorkbenchModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        Timber.e("工作台模块初始化 -- onInitAhead")
        return false

    }

    override fun onInitLow(application: Application): Boolean {
        Timber.e("工作台模块初始化 -- onInitLow")
        return false
    }
}