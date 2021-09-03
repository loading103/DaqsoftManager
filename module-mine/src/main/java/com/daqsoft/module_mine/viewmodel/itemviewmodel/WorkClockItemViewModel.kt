package com.daqsoft.module_mine.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作时间 itemViewModel
 */
class WorkClockItemViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){

    /**
     * 上班时间
     */
    val startWork = ObservableField<String>("--")

    /**
     * 下班时间
     */
    val offWork = ObservableField<String>("--")

}


