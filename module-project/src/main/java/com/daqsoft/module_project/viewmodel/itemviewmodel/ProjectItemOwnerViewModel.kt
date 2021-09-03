package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.repository.pojo.vo.ProjectOwnerBean
import com.daqsoft.module_project.viewmodel.ProjectChoseOwneViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class ProjectItemOwnerViewModel (
    projectViewModel: ProjectChoseOwneViewModel, data: ProjectOwnerBean
) : ItemViewModel<ProjectChoseOwneViewModel>(projectViewModel){

    val datas = ObservableField<ProjectOwnerBean>()

    init {
        datas.set(data)
    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        projectViewModel.setItemClick(data)
    })

}