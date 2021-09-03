package com.daqsoft.module_mine.viewmodel.itemviewmodel

import android.graphics.Rect
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.pojo.vo.Staff
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作考勤 itemViewModel
 */
class WorkAttendanceItemViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel,
    val today : Boolean = true
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){

    /**
     * 今天
     */
    val todayObservable = ObservableField<Boolean>(true)

    /**
     * 最早
     */
    val earliest = ObservableField<Staff>()

    /**
     * 最迟
     */
    val latest = ObservableField<Staff>()

    /**
     * 请假标语
     */
    val leaveSlogan = ObservableField<String>()

    /**
     * 考勤异常标语
     */
    val abnormalSlogan = ObservableField<String>()

    /**
     * 请假
     */
    var leaveTotal = ObservableField<Int>(0)
    var leaveObservableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    var leaveItemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recyclerview_work_attendance_item_content)


    /**
     * 异常
     */
    val abnormalTotal = ObservableField<Int>(0)
    var abnormalObservableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    var abnormalItemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recyclerview_work_attendance_item_content)

    /**
     * 最早员工头像点击事件
     */
    val earliestOnClick = BindingCommand<Unit>(BindingAction {
        if (earliest.get() == null){
            return@BindingAction
        }

        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",earliest.get()!!.employeeId.toString())
            .withString("name","")
            .navigation()
    })

    /**
     * 最迟员工头像点击事件
     */
    val latestOnClick = BindingCommand<Unit>(BindingAction {
        if (latest.get() == null){
            return@BindingAction
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",latest.get()?.employeeId.toString())
            .withString("name","")
            .navigation()
    })

    init {
        todayObservable.set(today)
    }
}

