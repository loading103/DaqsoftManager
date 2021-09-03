package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DutyLeaders
import com.daqsoft.module_workbench.viewmodel.CustomerDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class CustomerlHeadIcon (
    private val detailViewModel: CustomerDetailViewModel,
    var member: DutyLeaders
) : MultiItemViewModel<CustomerDetailViewModel>(detailViewModel){


    val itemData = ObservableField<DutyLeaders>()

    init {
        itemData.set(member)
    }

    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id","${member.employeeId}")
            .withString("name",member.employeeName)
            .navigation()
    })
}