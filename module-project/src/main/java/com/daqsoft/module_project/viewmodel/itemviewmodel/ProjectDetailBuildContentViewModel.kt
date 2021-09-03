package com.daqsoft.module_project.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.repository.pojo.vo.Standard
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils

class ProjectDetailBuildContentViewModel(
    private val detailViewModel: ProjectDetailViewModel,
    val bean: Standard
) : MultiItemViewModel<ProjectDetailViewModel>(detailViewModel){


    val  data = ObservableField<Standard>()
    init {

        data.set(bean)
    }


    /**
     * 跳转到列表页
     */
    val OnTopClick = BindingCommand<Unit>(BindingAction {
        detailViewModel.showRedContentPop(bean)
    })

    /**
     * 跳转到列表页
     */
    val OnButtomClick = BindingCommand<Unit>(BindingAction {
//        ToastUtils.showLong("111111")
//        var website:String= HttpGlobal.DAILY_TEAM_ADD
//        ARouter
//            .getInstance()
//            .build(ARouterPath.Web.PAGER_WEB)
//            .withString("url",website)
//            .navigation()
    })

}