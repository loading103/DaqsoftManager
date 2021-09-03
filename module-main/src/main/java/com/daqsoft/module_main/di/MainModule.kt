package com.daqsoft.module_main.di

import com.daqsoft.module_main.repository.MainApiService
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.library_base.net.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @package name：com.daqsoft.module_main.di
 * @date 5/11/2020 下午 5:20
 * @author zp
 * @describe
 */
@InstallIn(ApplicationComponent::class)
@Module
class MainModule {

    @Singleton
    @Provides
    fun provideMainApiService() = RetrofitClient.Builder().build().create(MainApiService::class.java)

    @Singleton
    @Provides
    fun provideMainRepository(mainApiService: MainApiService)  = MainRepository(mainApiService)

}