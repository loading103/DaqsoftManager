package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 8/1/2021 下午 4:17
 * @author zp
 * @describe
 */
class PunchViewModel : BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)


    var statusBarHeight = ObservableField<Int>(AppUtils.getStatusBarHeight())
    var toolbarHeight = ObservableField<Int>(AppUtils.getStatusBarHeight() + AppUtils.getToolbarHeight())

    /**
     * 记录点击事件
     */
    val backOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })


    /**
     * 记录点击事件
     */
    val recordOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PUNCH_RECORD).navigation()
    })


}