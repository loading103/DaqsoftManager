package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class ProjectDetailFlowNoExpand(
    private val detailViewModel: ProjectDetailViewModel,
    val data: Processe,
    islast: Boolean
) : ItemViewModel<ProjectDetailViewModel>(detailViewModel){


    val titleObservable = ObservableField<String>("")
    val showKcpObservable = ObservableField<Boolean>(false)
    val islastObservable = ObservableField<Boolean>(false)
    init {
        titleObservable.set(data.itemName+"(${data.taskCount})")
        showKcpObservable.set(data.kcpState==1)
        islastObservable.set(islast)
    }
}