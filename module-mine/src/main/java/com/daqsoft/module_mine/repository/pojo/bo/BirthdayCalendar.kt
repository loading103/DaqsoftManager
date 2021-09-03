package com.daqsoft.module_mine.repository.pojo.bo

import com.daqsoft.module_mine.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.bo
 * @date 14/12/2020 上午 11:00
 * @author zp
 * @describe
 */
enum class BirthdayCalendar(var text:String){
    LUNAR_CALENDAR(ContextUtils.getContext().resources.getString(R.string.module_mine_lunar_calendar)),
    NATIONAL_CALENDAR(ContextUtils.getContext().resources.getString(R.string.module_mine_national_calendar))
}