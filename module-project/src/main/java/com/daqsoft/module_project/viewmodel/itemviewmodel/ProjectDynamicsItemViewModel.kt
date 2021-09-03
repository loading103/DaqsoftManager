package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.repository.pojo.vo.ProjectDynamic
import com.daqsoft.module_project.viewmodel.ProjectDynamicsViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_project.viewmodel.itemviewmodel
 * @date 6/4/2021 下午 2:01
 * @author zp
 * @describe
 */
class ProjectDynamicsItemViewModel(
    private val dynamicsViewModel : ProjectDynamicsViewModel,
    var projectDynamic : ProjectDynamic
) : ItemViewModel<ProjectDynamicsViewModel>(dynamicsViewModel) {

    val projectDynamicObservable = ObservableField<ProjectDynamic>()


    init {
        projectDynamicObservable.set(projectDynamic)
    }
}