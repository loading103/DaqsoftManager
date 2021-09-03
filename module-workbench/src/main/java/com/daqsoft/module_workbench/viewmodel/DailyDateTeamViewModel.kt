package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DailyDataBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyListBeanItem
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListDateViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.TeamHeadItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.lang.StringBuilder
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailyDateTeamViewModel :  BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    val finishedLiveData = MutableLiveData<Boolean>()


    override fun onCreate() {
        initToolbar()

    }


    private fun initToolbar() {

    }


}

