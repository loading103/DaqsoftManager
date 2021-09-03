package com.daqsoft.module_main.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel

class DialogViewModel : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application,mainRepository: MainRepository):super(application, mainRepository)
}
