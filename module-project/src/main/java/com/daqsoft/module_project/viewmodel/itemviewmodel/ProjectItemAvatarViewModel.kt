package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_project.R
import com.daqsoft.module_project.viewmodel.ProjectViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class ProjectItemAvatarViewModel (
    val projectViewModel: ProjectViewModel,
    val url: String,
    val placeholder : Int? = null
) : ItemViewModel<ProjectViewModel>(projectViewModel){

    var urlObservable  = ObservableField<String>()

    var placeholderObservable  = ObservableField<Int>()

    init {
        urlObservable.set(url)
        placeholderObservable.set(placeholder?: R.mipmap.txl_details_tx_default_1)
    }

}