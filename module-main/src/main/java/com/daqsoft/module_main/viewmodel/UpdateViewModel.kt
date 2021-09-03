package com.daqsoft.module_main.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel

/**
 * @package name：com.daqsoft.module_main.viewmodel
 * @date 25/1/2021 下午 3:55
 * @author zp
 * @describe
 */
class UpdateViewModel  : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application, mainRepository: MainRepository):super(application, mainRepository)
}
