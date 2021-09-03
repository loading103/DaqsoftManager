
package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.ProjectFlowViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class ProjectFlowNoExpand(
    private val flowViewModel: ProjectFlowViewModel,
    val data: Processe,
    islast: Boolean
) : ItemViewModel<ProjectFlowViewModel>(flowViewModel){
    val titleObservable = ObservableField<String>("")
    val showKcpObservable = ObservableField<Boolean>(false)
    val islastObservable = ObservableField<Boolean>(false)
    init {
        titleObservable.set(data.itemName+"(${data.taskCount})")
        showKcpObservable.set(data.kcpState==1)
        islastObservable.set(islast)
    }
}