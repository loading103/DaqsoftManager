package com.daqsoft.module_mine.repository.pojo.bo

import com.daqsoft.module_mine.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.bo
 * @date 11/12/2020 下午 4:16
 * @author zp
 * @describe
 */
enum class  WorkCalendar(var amount: Int,var text:String) {
    TODAY(0, ContextUtils.getContext().resources.getString(R.string.module_mine_today)),
    TOMORROW(1, ContextUtils.getContext().resources.getString(R.string.module_mine_tomorrow));
}