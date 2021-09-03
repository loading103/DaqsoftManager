package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils

class ProjectDetailCountViewModel(
    private val detailViewModel: ProjectDetailViewModel,
    bean:ProjectDetailBean
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){

    val data = ObservableField<ProjectDetailBean>()
    init {
        this.data.set(bean)
    }

    /**
     * 跳转到列表页
     */
    val ItemOnClick = BindingCommand<Unit>(BindingAction {
//        ToastUtils.showLong("跳转到列表页")
        var website:String= HttpGlobal.DAILY_TASK_TASK+detailViewModel.detailData.value?.id
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    })

}