package com.daqsoft.module_webview

import android.app.Application
import com.daqsoft.library_base.init.IModuleInit
import timber.log.Timber

class WebViewModuleInit : IModuleInit {
    override fun onInitAhead(application: Application): Boolean {
        Timber.e("WebView模块初始化 -- onInitAhead")
        return false

    }

    override fun onInitLow(application: Application): Boolean {
        Timber.e("WebView模块初始化 -- onInitLow")
        return false
    }
}