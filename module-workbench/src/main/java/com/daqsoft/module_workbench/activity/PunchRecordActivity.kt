package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityPunchRecordBinding
import com.daqsoft.module_workbench.viewmodel.PunchRecordViewModel
import com.google.gson.Gson
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 3/2/2021 上午 10:43
 * @author zp
 * @describe 打卡记录
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PUNCH_RECORD)
class PunchRecordActivity: AppBaseActivity<ActivityPunchRecordBinding, PunchRecordViewModel>()
//    ,
//    CalendarView.OnCalendarSelectListener,
//    CalendarView.OnMonthChangeListener
{



    val selectedMonthLiveData = MutableLiveData<Pair<Int, Int>>()


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_punch_record
    }

    override fun initViewModel(): PunchRecordViewModel? {
        return viewModels<PunchRecordViewModel>().value
    }

    override fun initView() {
        super.initView()

        initCalendarView()
    }



    /**
     * 初始化 日历
     */
    private fun initCalendarView() {
        binding.calendarView.run{
//            setOnCalendarSelectListener(this@PunchRecordActivity)
//            setOnMonthChangeListener(this@PunchRecordActivity)
        }

        val map: HashMap<String, Calendar> = hashMapOf()
        map[getSchemeCalendar(2021, 2, 3,"1").toString()] = getSchemeCalendar(2021, 2, 3, "1")
        map[getSchemeCalendar(2021, 2, 4, "0").toString()] = getSchemeCalendar(2021, 2, 4, "0")



        binding.calendarView.setSchemeDate(map)
    }

    override fun initData() {
        super.initData()
        val year = binding.calendarView.curYear
        val month = binding.calendarView.curMonth
        val day = binding.calendarView.curDay

        selectedMonthLiveData.value = Pair(year, month)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        selectedMonthLiveData.observe(this, androidx.lifecycle.Observer {
            viewModel.setTitleText("${it.first}年${it.second}月")
        })
    }
//
//    /**
//     * 超出日历范围
//     * @param calendar Calendar
//     */
//    override fun onCalendarOutOfRange(calendar: Calendar?) {
//
//    }
//
//
//    /**
//     * 日历选中
//     * @param calendar Calendar
//     * @param isClick Boolean
//     */
//    override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
//        // TODO 日历选中
//        Timber.e("calendar ${Gson().toJson(calendar)}")
//    }
//
//    /**
//     * 月份变化
//     * @param year Int
//     * @param month Int
//     */
//    override fun onMonthChange(year: Int, month: Int) {
//        selectedMonthLiveData.value = Pair(year, month)
//    }

    /**
     * text 0 异常  1 正常
     * @param year Int
     * @param month Int
     * @param day Int
     * @param text String
     * @return Calendar
     */
    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int,
        text: String
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.scheme = text
        return calendar
    }
}