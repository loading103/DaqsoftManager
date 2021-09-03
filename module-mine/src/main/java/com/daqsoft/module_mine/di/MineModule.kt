package com.daqsoft.module_mine.di

import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.net.BaseResponseInterceptor
import com.daqsoft.module_mine.repository.MineApiService
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.library_base.utils.DataStoreUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import timber.log.Timber
import javax.inject.Singleton
/**
 * @package name：com.daqsoft.module_mine.di
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
@Module
@InstallIn(ApplicationComponent::class)
class MineModule {

    @Singleton
    @Provides
    fun provideMineApiService() : MineApiService = RetrofitClient.Builder().build().create(MineApiService::class.java)

    @Singleton
    @Provides
    fun provideMineRepository(mineApiService: MineApiService)  = MineRepository(mineApiService)
}

