package com.daqsoft.module_webview.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_webview.repository.WebViewApiService
import com.daqsoft.module_webview.repository.WebViewRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @package name：com.daqsoft.module_webview.di
 * @date 5/11/2020 下午 5:59
 * @author zp
 * @describe
 */
@InstallIn(ApplicationComponent::class)
@Module
class WebViewModule {

    @Singleton
    @Provides
    fun provideWebViewService() = RetrofitClient.Builder().build().create(WebViewApiService::class.java)

    @Singleton
    @Provides
    fun provideWebViewRepository(taskApiService: WebViewApiService)  = WebViewRepository(taskApiService)
}