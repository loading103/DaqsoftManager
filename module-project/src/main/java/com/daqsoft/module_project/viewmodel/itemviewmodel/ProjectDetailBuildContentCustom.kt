package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.repository.pojo.vo.Custom
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils

class ProjectDetailBuildContentCustom(
    private val detailViewModel: ProjectDetailViewModel,
    val bean: Custom
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){


    val data = ObservableField<Custom>()

    init {
        data.set(bean)
    }

    /**
     * 跳转到列表页
     */
    val OnTopClick = BindingCommand<Unit>(BindingAction {
        detailViewModel.showGrayContentPop(bean)
    })

    /**
     * 跳转到列表页
     */
    val OnButtomClick = BindingCommand<Unit>(BindingAction {
//        var website:String= HttpGlobal.DAILY_TEAM_ADD
//        ARouter
//            .getInstance()
//            .build(ARouterPath.Web.PAGER_WEB)
//            .withString("url",website)
//            .navigation()
    })

}