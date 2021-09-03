package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecord
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecordBean
import com.daqsoft.module_workbench.repository.pojo.vo.DutyLeaders
import com.daqsoft.module_workbench.viewmodel.CustomerDetailViewModel
import com.daqsoft.module_workbench.viewmodel.CustomerRecordViewModel
import com.daqsoft.module_workbench.viewmodel.PatherDetailViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class PatherCzItemViewModel(
    private val dynamicsViewModel : PatherDetailViewModel,
    var projectDynamic : CustomerRecord
) : ItemViewModel<PatherDetailViewModel>(dynamicsViewModel) {

    val projectDynamicObservable = ObservableField<CustomerRecord>()
    init {
        projectDynamicObservable.set(projectDynamic)
    }
}