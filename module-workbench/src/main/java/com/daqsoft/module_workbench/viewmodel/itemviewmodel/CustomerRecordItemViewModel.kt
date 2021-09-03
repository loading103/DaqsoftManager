package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecordBean
import com.daqsoft.module_workbench.viewmodel.CustomerRecordViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class CustomerRecordItemViewModel(
    private val dynamicsViewModel : CustomerRecordViewModel,
    var projectDynamic : CustomerRecordBean
) : ItemViewModel<CustomerRecordViewModel>(dynamicsViewModel) {

    val projectDynamicObservable = ObservableField<CustomerRecordBean>()


    init {
        projectDynamicObservable.set(projectDynamic)
    }
}