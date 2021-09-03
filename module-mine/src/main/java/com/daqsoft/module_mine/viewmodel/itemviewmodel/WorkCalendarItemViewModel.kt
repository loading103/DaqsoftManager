package com.daqsoft.module_mine.viewmodel.itemviewmodel

import android.app.Application
import android.graphics.Paint
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.coverTime
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.sp
import com.daqsoft.library_base.widget.flowlayout.FlowLayout
import com.daqsoft.library_base.widget.flowlayout.TagAdapter
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.pojo.vo.CareWord
import com.daqsoft.module_mine.repository.pojo.vo.Day
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel


/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作日历 itemViewModel
 */
class WorkCalendarItemViewModel(
    private val workCalendarViewModel: WorkCalendarViewModel,
    val day: Day
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){

    /**
     * 当前日期
     */
    val current = ObservableField<String>()

    /**
     * 背景图
     */
    val backgroundImage = ObservableField<Int>()

    val dayObservable = ObservableField<Day>()

    init {
        dayObservable.set(day)
        current.set(day.dateForParam.coverTime("yyyy-MM-dd", "MM/dd"))
        backgroundImage.set(if (day.dayInfo == "休息日") R.mipmap.mine_pic_xxr_9png else R.mipmap.mine_pic_jjr_9png)
    }

}

