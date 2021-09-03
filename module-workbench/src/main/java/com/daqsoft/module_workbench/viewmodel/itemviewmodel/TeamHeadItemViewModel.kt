package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.Member
import com.daqsoft.module_workbench.viewmodel.DailyMemberViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class TeamHeadItemViewModel(
    private val detailViewModel: DailyMemberViewModel,
    var member: DialyMemberBean
) : MultiItemViewModel<DailyMemberViewModel>(detailViewModel){

    val itemData = ObservableField<DialyMemberBean>()

    init {
        itemData.set(member)
    }

    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id","${member?.employeeId}")
            .withString("name",member?.employeeName)
            .navigation()
    })
}