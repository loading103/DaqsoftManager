package com.daqsoft.module_mine.utils

import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.pojo.bo.WorkCalendar
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.google.gson.Gson
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.utils
 * @date 13/11/2020 下午 1:50
 * @author zp
 * @describe
 */
object CalendarUtil {

    fun getCalendar(workCalendar : WorkCalendar): Pair<String,String> {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        if (workCalendar.amount != 0){
            calendar.add(Calendar.DATE,workCalendar.amount)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val week = calendar.get(Calendar.DAY_OF_WEEK)
        val calendarStr =  ContextUtils.getContext().resources.getString(R.string.module_mine_calendar_title,workCalendar.text,year.toString(),month.toString(),day.toString())
        return Pair(getDayOfWeek(week),calendarStr)
    }



    private fun getDayOfWeek(week : Int):String{
        return when (week) {
            1 -> ContextUtils.getContext().resources.getString(R.string.module_mine_sunday)
            2 -> ContextUtils.getContext().resources.getString(R.string.module_mine_monday)
            3 -> ContextUtils.getContext().resources.getString(R.string.module_mine_tuesday)
            4 -> ContextUtils.getContext().resources.getString(R.string.module_mine_wednesday)
            5 -> ContextUtils.getContext().resources.getString(R.string.module_mine_thursday)
            6 -> ContextUtils.getContext().resources.getString(R.string.module_mine_friday)
            7 -> ContextUtils.getContext().resources.getString(R.string.module_mine_saturday)
            else -> ""
        }
    }


    fun getCalendarToJson(workCalendar : WorkCalendar): String {
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
        if (workCalendar.amount != 0){
            calendar.add(Calendar.DATE,workCalendar.amount)
        }
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)+1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val week = calendar.get(Calendar.DAY_OF_WEEK)
        return Gson().toJson(mapOf<String,Int>("year" to year,"month" to month,"day" to day,"week" to week))

    }
}