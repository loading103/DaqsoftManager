package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.module_project.repository.pojo.vo.Member
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class ProjectDetailHeadIcon (
    private val detailViewModel: ProjectDetailViewModel,
    var member: Member
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){


    val itemData = ObservableField<Member>()

    init {
        itemData.set(member)
    }

    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id","${member.id}")
            .withString("name",member.name)
            .navigation()
    })
}