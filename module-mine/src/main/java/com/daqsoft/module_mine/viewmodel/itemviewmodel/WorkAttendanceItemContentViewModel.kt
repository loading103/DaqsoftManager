package com.daqsoft.module_mine.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.pojo.vo.Staff
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作考勤 itemViewModel
 */
class WorkAttendanceItemContentViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel,
    val staff: Staff? = null,
    val placeholder : Int? = null
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){


    val staffObservable = ObservableField<Staff>()

    val placeholderObservable = ObservableField<Int>()


    val itemOnClick = BindingCommand<Unit>(BindingAction {
        if (staffObservable.get() == null){
            return@BindingAction
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",staffObservable.get()!!.employeeId.toString())
            .withString("name","")
            .navigation()
    })


    init {
        staffObservable.set(staff)
        placeholderObservable.set(placeholder?:R.mipmap.mine_bmkq_tx_default)
    }
}

