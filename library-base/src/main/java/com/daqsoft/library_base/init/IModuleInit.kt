package com.daqsoft.library_base.init

import android.app.Application

interface IModuleInit {
    //初始化优先的
    abstract fun onInitAhead(application: Application): Boolean

    //初始化靠后的
    abstract fun onInitLow(application: Application): Boolean
}