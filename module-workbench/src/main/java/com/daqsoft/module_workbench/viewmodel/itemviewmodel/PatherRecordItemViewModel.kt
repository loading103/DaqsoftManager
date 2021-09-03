package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecordBean
import com.daqsoft.module_workbench.viewmodel.CustomerRecordViewModel
import com.daqsoft.module_workbench.viewmodel.PatherRecordViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class PatherRecordItemViewModel(
    private val dynamicsViewModel : PatherRecordViewModel,
    var projectDynamic : CustomerRecordBean
) : ItemViewModel<PatherRecordViewModel>(dynamicsViewModel) {

    val projectDynamicObservable = ObservableField<CustomerRecordBean>()


    init {
        projectDynamicObservable.set(projectDynamic)
    }
}