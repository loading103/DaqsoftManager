package com.daqsoft.module_task.repository

import com.daqsoft.mvvmfoundation.base.BaseModel
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:09
 * @author zp
 * @describe
 */
class TaskRepository @Inject constructor(private val taskApiService:TaskApiService) : BaseModel(),TaskApiService {


    fun test (){
        Timber.e(" MineRepository test")
    }
}