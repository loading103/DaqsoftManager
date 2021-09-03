package com.daqsoft.module_mine.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.view.TimePickerView
import com.contrarywind.view.WheelView
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityBirthdBinding
import com.daqsoft.module_mine.repository.pojo.bo.BirthdayCalendar
import com.daqsoft.module_mine.viewmodel.BirthdayViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 18/11/2020 下午 4:27
 * @author zp
 * @describe 修改生日
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_BIRTHDAY)
class BirthdayActivity : AppBaseActivity<ActivityBirthdBinding, BirthdayViewModel>() {


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_birthd
    }

    override fun initViewModel(): BirthdayViewModel? {
        return viewModels<BirthdayViewModel>().value
    }


    override fun initData() {
        viewModel.getEmployeeToUpdate()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.calendarLiveData.observe(this, Observer {
            val drawable = resources.getDrawable(R.mipmap.mine_sr_selected)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            when (it) {
                BirthdayCalendar.LUNAR_CALENDAR -> {
                    switchCalendar(BirthdayCalendar.LUNAR_CALENDAR, drawable)
                    showDatePicker(it)
                }
                BirthdayCalendar.NATIONAL_CALENDAR -> {
                    switchCalendar(BirthdayCalendar.NATIONAL_CALENDAR, drawable)
                    showDatePicker(it)
                }
            }
        })

    }

    /**
     * 切换日历
     * @param type String
     * @param drawable Drawable
     */
    private fun  switchCalendar(
        type: BirthdayCalendar? = null,
        drawable: Drawable? = null
    ){
        if (type == null && drawable == null){
            binding.lunarCalendar.setCompoundDrawables(null, null, null, null)
            binding.nationalCalendar.setCompoundDrawables(null, null, null, null)
            return
        }
        when(type){
            BirthdayCalendar.LUNAR_CALENDAR -> {
                binding.lunarCalendar.setCompoundDrawables(null, null, drawable, null)
                binding.nationalCalendar.setCompoundDrawables(null, null, null, null)
            }
            BirthdayCalendar.NATIONAL_CALENDAR -> {
                binding.lunarCalendar.setCompoundDrawables(null, null, null, null)
                binding.nationalCalendar.setCompoundDrawables(null, null, drawable, null)
            }
        }
    }


    var timePicker : TimePickerView? = null
    /**
     * 日期选择器
     * @param type String
     */
    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker(type: BirthdayCalendar) {
        val selectedDate = Calendar.getInstance()
        if (viewModel.update.get() != null){
            val formatter = SimpleDateFormat("yyyy-MM-dd").apply {
                timeZone = TimeZone.getTimeZone("GMT+8:00")
            }
            selectedDate.time =  formatter.parse(viewModel.update.get()!!.employeeBirthday)!!
        }
        val startDate = Calendar.getInstance()
        startDate.set(1950, 1, 1)
        val endDate = Calendar.getInstance()
        // 时间选择器 ，自定义布局
        timePicker = TimePickerBuilder(this) { date, v ->
            // 选中事件回调
        }
            .setDate(selectedDate)
            .setRangDate(startDate, endDate)
            .setLayoutRes(R.layout.pickerview_custom_lunar, object : CustomListener {
                override fun customLayout(v: View) {
                    val year = v.findViewById<WheelView>(R.id.year)
                    val month = v.findViewById<WheelView>(R.id.month)
                    val day = v.findViewById<WheelView>(R.id.day)
                    val determine = v.findViewById<TextView>(R.id.determine)
                    determine.setOnClickListener {
                        val sb = StringBuilder()
                        val yearItem = year.adapter.getItem(year.currentItem).toString()
                        val monthItem = month.adapter.getItem(month.currentItem).toString()
                        val dayItem = day.adapter.getItem(day.currentItem).toString()
//                        if (type == BirthdayCalendar.LUNAR_CALENDAR){
//                            sb.append(yearItem.substring(yearItem.indexOf("(")+1,yearItem.indexOf(")")))
//                            sb.append(resources.getString(R.string.module_mine_year))
//                            sb.append(monthItem)
//                            sb.append(dayItem)
//                        }else{
//                            sb.append(yearItem)
//                            sb.append(resources.getString(R.string.module_mine_year))
//                            sb.append(monthItem)
//                            sb.append(resources.getString(R.string.module_mine_month))
//                            sb.append(dayItem)
//                            sb.append(resources.getString(R.string.module_mine_day))
//                        }

                        sb.append(yearItem)
                        sb.append("-")
                        sb.append(String.format("%02d",monthItem.toInt()))
                        sb.append("-")
                        sb.append(String.format("%02d",dayItem.toInt()))

                        viewModel.update.get()!!.employeeBirthdayType = if (type == BirthdayCalendar.LUNAR_CALENDAR) "LunarCalendar" else "GregorianCalendar"
                        viewModel.update.get()!!.employeeBirthday = sb.toString()
                        viewModel.updateEmployeeInfo()

                        timePicker?.returnData()
                        timePicker?.dismiss()
                    }
                    val cancel = v.findViewById<TextView>(R.id.cancel)
                    cancel.setOnClickListener {
                        timePicker?.dismiss()
                        switchCalendar()
                    }
                    //自适应宽
                    setTimePickerChildWeight(
                        v,
                        if (type == BirthdayCalendar.LUNAR_CALENDAR) 0.9f else 1f,
                        if (type == BirthdayCalendar.LUNAR_CALENDAR) 1f else 1.1f
                    )
                }

                /**
                 * 公农历切换后调整宽
                 * @param v
                 * @param yearWeight
                 * @param weight
                 */
                private fun setTimePickerChildWeight(v: View, yearWeight: Float, weight: Float) {
                    val timePicker = v.findViewById<LinearLayout>(R.id.timepicker)
                    val year = timePicker.getChildAt(0)
                    val lp = year.layoutParams as LinearLayout.LayoutParams
                    lp.weight = yearWeight
                    year.layoutParams = lp
                    for (i in 1 until timePicker.childCount) {
                        val childAt = timePicker.getChildAt(i)
                        val childLp = childAt.layoutParams as LinearLayout.LayoutParams
                        childLp.weight = weight
                        childAt.layoutParams = childLp
                    }
                }
            })
            .setContentTextSize(15)
//            .setLunarCalendar(type == BirthdayCalendar.LUNAR_CALENDAR)
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isCenterLabel(false)
            .setDividerColor(resources.getColor(R.color.gray_e2e2e2))
            .setTextColorCenter(resources.getColor(R.color.black_333333))
            .setTextColorOut(resources.getColor(R.color.gray_999999))
            .setLineSpacingMultiplier(2.5f)
            .isDialog(true)
            .setItemVisibleCount(5)
            .build()

        timePicker?.dialog?.let {
            val params = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            timePicker?.dialogContainerLayout?.layoutParams = params
            it.setOnCancelListener {
                switchCalendar()
            }
            it.window?.let {
                it.setGravity(Gravity.BOTTOM)
                it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                it.setDimAmount(0.3f)
                it.setWindowAnimations(R.style.picker_view_slide_anim)
            }
        }
        timePicker?.show()
    }

}