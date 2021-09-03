package com.daqsoft.module_mine.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 12/11/2020 下午 2:15
 * @author zp
 * @describe 部门同事 item ViewModel
 */
class DepartmentColleaguesItemViewModel(
    private val mineViewModel : MineViewModel,
    private val employeeInfo: EmployeeInfo
) : ItemViewModel<MineViewModel>(mineViewModel){

    val employeeInfoObservable = ObservableField<EmployeeInfo>()

    init {
        employeeInfoObservable.set(employeeInfo)
    }

    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",employeeInfo.employeeId.toString())
            .withString("name",employeeInfo.employeeName)
            .navigation()
    })

}

