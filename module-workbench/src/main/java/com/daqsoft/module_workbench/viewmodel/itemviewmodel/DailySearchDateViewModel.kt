package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.viewmodel.DailyListViewModel
import com.daqsoft.module_workbench.viewmodel.DailySearchViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class DailySearchDateViewModel (
    private val dailySearchViewModel: DailySearchViewModel,
    val date: String
) : MultiItemViewModel<DailySearchViewModel>(dailySearchViewModel){


    val mDate = ObservableField<String>()

    val day = ObservableField<String>()
    val month = ObservableField<String>()

    init {
        mDate.set(date)

        var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        var cal: Calendar = Calendar.getInstance()
        cal.time = sdf.parse(date)


        day.set("${cal.get(Calendar.DAY_OF_MONTH)}")
        month.set("/${cal.get(Calendar.MONTH)+1}月")
    }

}