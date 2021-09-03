package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

class ProjectDetailBuildContentTitle (
    private val detailViewModel: ProjectDetailViewModel,
    val count : Int
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){


    val mCount = ObservableField<String>("")


    init {
        mCount.set("(${count})")
    }
}