package com.daqsoft.module_mine.viewmodel.itemviewmodel

import android.view.View
import androidx.databinding.ObservableField
import com.daqsoft.module_mine.repository.pojo.vo.TaskInfo
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作任务 itemViewModel
 */
class WorkMissionItemViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel,
    val today : Boolean
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){

    val taskInfoObservable = ObservableField<TaskInfo>()

    val todayObservable = ObservableField<Boolean>(true)

    val holidaysObservable = ObservableField<Boolean>(false)


    init {
        todayObservable.set(today)
    }
}

