package com.daqsoft.module_project.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_project.repository.ProjectApiService
import com.daqsoft.module_project.repository.ProjectRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

/**
 * @package name：com.daqsoft.module_project.di
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
@Module
@InstallIn(ApplicationComponent::class)
class ProjectModule {

    @Singleton
    @Provides
    fun provideProjectApiService() = RetrofitClient.Builder().build().create(ProjectApiService::class.java)

    @Singleton
    @Provides
    fun provideProjectRepository(mineApiService: ProjectApiService)  = ProjectRepository(mineApiService)
}