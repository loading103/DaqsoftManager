package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 员工详情  更多 itemViewModel
 */
class StaffItemMoreViewModel (
    private val staffViewModel : StaffViewModel
) : MultiItemViewModel<StaffViewModel>(staffViewModel){


    /**
     * 更多点击事件
     */
    val moreOnClick = BindingCommand<Unit>(BindingAction {
        staffViewModel.index = staffViewModel.observableList.indexOf(this)
        staffViewModel.getUpcomingTasks()
    })

}