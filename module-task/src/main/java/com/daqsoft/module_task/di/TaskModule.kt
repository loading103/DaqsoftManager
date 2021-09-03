package com.daqsoft.module_task.di

import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.module_task.repository.TaskApiService
import com.daqsoft.module_task.repository.TaskRepository
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
class TaskModule {

    @Singleton
    @Provides
    fun provideTaskApiService() = RetrofitClient.Builder().build().create(TaskApiService::class.java)

    @Singleton
    @Provides
    fun provideTaskRepository(taskApiService: TaskApiService)  = TaskRepository(taskApiService)
}