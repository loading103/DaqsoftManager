package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ProjectDetailHeadViewModel(
    private val detailViewModel: ProjectDetailViewModel,
    bean: ProjectDetailBean
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){

    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
            BR.viewModel,
            R.layout.recyclerview_project_head_icon
    )

    var dataList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    var data = ObservableField<ProjectDetailBean>()

    /**
     * 是不是被选中
     */
    val ischecked = ObservableField<Boolean>(false)
    /**
     * 关注
     */
    val focusOnClick = BindingCommand<Unit>(BindingAction {
        detailViewModel.focusProjectData(bean.id,this@ProjectDetailHeadViewModel)
    })


    init {
        data.set(bean)
        ischecked.set(bean.isFocus.toString()=="1")
        dataList.addAll(bean.members.map { ProjectDetailHeadIcon (detailViewModel, it)})
    }
}