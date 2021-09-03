package com.daqsoft.module_workbench.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_workbench.repository.WorkBenchApiService
import com.daqsoft.module_workbench.repository.WorkBenchRepository
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
class WorkbenchModule {

    @Singleton
    @Provides
    fun provideWorkBenchApiService() = RetrofitClient.Builder().build().create(WorkBenchApiService::class.java)

    @Singleton
    @Provides
    fun provideWorkBenchRepository(mineApiService: WorkBenchApiService)  = WorkBenchRepository(mineApiService)
}