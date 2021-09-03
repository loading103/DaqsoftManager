package com.daqsoft.module_home.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_home.repository.HomeApiService
import com.daqsoft.module_home.repository.HomeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @package name：com.daqsoft.module_mine.di
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
@Module
@InstallIn(ApplicationComponent::class)
class HomeModule {

    @Singleton
    @Provides
    fun provideHomeApiService() = RetrofitClient.Builder().build().create(HomeApiService::class.java)

    @Singleton
    @Provides
    fun provideHomeRepository(mineApiService: HomeApiService)  = HomeRepository(mineApiService)
}