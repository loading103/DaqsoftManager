package com.daqsoft.module_workbench.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDailyDateBinding
import com.daqsoft.module_workbench.viewmodel.DailyDateViewModel
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.CalendarView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * 日报数据
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_DATA)
class DailyDateFragment : AppBaseFragment<ActivityDailyDateBinding, DailyDateViewModel>(){

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.activity_daily_date
    }

    override fun initViewModel(): DailyDateViewModel? {
        return ViewModelProvider(this).get(DailyDateViewModel::class.java)
    }

    override fun initView() {
        super.initView()
        initOnClickListener()
        initCalendarView()
    }

    private fun initOnClickListener() {
        binding.ivLeft.setOnClickListener {
            binding.calendarView.scrollToPre(false);
        }
        binding.ivRight.setOnClickListener {
            binding.calendarView.scrollToNext(false)
        }
        binding.tvTjl.setOnClickListener {
            showTpDialog("统计范围：上月26-当月25日")
        }
    }

    override fun initData() {
        var curYear = binding.calendarView.curYear
        var curMonth = binding.calendarView.curMonth
        viewModel.getMonthData(curYear, curMonth)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.showOnClickLiveData.observe(this, androidx.lifecycle.Observer {
            showTpDialog("周末不纳入未提交数据统计")
        })

        viewModel.finishedLiveData.observe(this, androidx.lifecycle.Observer {
            activity?.finish()
        })
        viewModel.dailyDataBean.observe(this, androidx.lifecycle.Observer {
            if (it.dates.isNullOrEmpty()) {
                return@Observer
            }

            // 日历选中的时间
            val year = binding?.calendarView?.selectedCalendar.year.toString()

            val month = TimeUtils.getAddZeroTime(binding?.calendarView?.selectedCalendar.month)

            val day = TimeUtils.getAddZeroTime(binding?.calendarView?.selectedCalendar.day)

            viewModel.selectedData = "$year-$month-$day"

            // 显示提交日期下面的小红点
            var map: MutableMap<String, Calendar> = HashMap()
            it?.dates?.forEachIndexed { index, bean ->
                if (bean.isReported == true) {
                    var curYear = bean.date.substring(0, 4).toInt()
                    var curMonth = bean.date.substring(5, 7).toInt()
                    var day = bean.date.substring(bean.date.lastIndexOf("-") + 1, bean.date.length)
                    if (day.startsWith("0")) {
                        day.substring(1, day.length)
                    }
                    map[getSchemeCalendar(curYear, curMonth, day.toInt()).toString()] =
                        getSchemeCalendar(
                            curYear,
                            curMonth,
                            day.toInt())
                }
            }
            binding?.calendarView?.setSchemeDate(map)

            binding.sportview?.setProgress(it.proportion.toDouble())


        })

    }


    /**
     * 初始化 日历
     */
    private fun initCalendarView() {

        // 年切换
        binding.calendarView.setOnYearChangeListener(object : CalendarView.OnYearChangeListener{
            override fun onYearChange(year: Int) {
                val calendar: Calendar = binding.calendarView.selectedCalendar
                binding.tvTitleTime.text = calendar?.year.toString() + "年" + calendar?.month + "月"
            }

        })
        // 选中日期
        binding.calendarView.setOnCalendarSelectListener(object : CalendarView.OnCalendarSelectListener{
            override fun onCalendarOutOfRange(calendar: Calendar?) {
            }

            override fun onCalendarSelect(calendar: Calendar?, isClick: Boolean) {
                val calendar: Calendar = binding.calendarView.selectedCalendar
                binding.tvTitleTime.text = calendar?.year.toString() + "年" + calendar?.month + "月"

                // 日历选中的时间
                val year = binding?.calendarView?.selectedCalendar.year.toString()

                val month = TimeUtils.getAddZeroTime(binding?.calendarView?.selectedCalendar.month)

                val day = TimeUtils.getAddZeroTime(binding?.calendarView?.selectedCalendar.day)

                viewModel.selectedData = "$year-$month-$day"
                // 修改当日提交状态
                viewModel.unDataTodayReport()
            }

        })
        // 月切换
        binding.calendarView.setOnMonthChangeListener { year, month ->
            binding.tvTitleTime.text = year.toString() + "年" + month.toString() + "月"
            viewModel.getMonthData(year, month)
        }


        val cal: java.util.Calendar = java.util.Calendar.getInstance()
        val year: Int = cal.get(java.util.Calendar.YEAR)
        val month: Int = cal.get(java.util.Calendar.MONTH)+1
        val day: Int = cal.get(java.util.Calendar.DAY_OF_MONTH)
        binding.calendarView.setSelectRangeMode()
        binding.calendarView.setRange(2018, 1, 1, year, month, day)

    }



    /**
     * 温馨提示
     */
    private var signOutDialog: MaterialDialog? = null
    private fun showTpDialog(contents:String) {
        signOutDialog= MaterialDialog
            .Builder(activity as Context)
            .customView(R.layout.dialog_tips_wx, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = signOutDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text ="温馨提示"
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = contents

        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            signOutDialog?.dismiss()
        }
        signOutDialog?.setCancelable(false)
        signOutDialog?.show()
        signOutDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun getSchemeCalendar(
        year: Int,
        month: Int,
        day: Int
    ): Calendar {
        val calendar = Calendar()
        calendar.year = year
        calendar.month = month
        calendar.day = day
        calendar.schemeColor = Color.RED
        calendar.scheme = "  "
        return calendar
    }
}