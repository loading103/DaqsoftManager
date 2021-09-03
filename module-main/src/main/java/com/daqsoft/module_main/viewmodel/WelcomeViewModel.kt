package com.daqsoft.module_main.viewmodel

import android.app.Application
import android.content.Intent
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R

/**
 * @package name：com.daqsoft.module_main.viewmodel
 * @date 21/12/2020 上午 11:02
 * @author zp
 * @describe
 */
class WelcomeViewModel  : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application,mainRepository: MainRepository):super(application, mainRepository)

    val adapterLiveData = MutableLiveData<Boolean>()

    override fun onCreate() {
        adapterLiveData.value = DataStoreUtils.getBoolean(DSKeyGlobal.FIRST,true)
    }

}