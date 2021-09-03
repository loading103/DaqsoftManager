package com.daqsoft.module_home.repository.pojo.bo

import android.icu.text.CaseMap
import com.daqsoft.module_home.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_home.repository.pojo.bo
 * @date 25/12/2020 下午 3:45
 * @author zp
 * @describe
 */
enum class SystemInfo(var title:String,var drawable: Int,var type : String) {

    APPROVE(ContextUtils.getContext().resources.getString(R.string.approve),R.mipmap.xq_hys,"AUDIT"),

    ANNOUNCEMENT(ContextUtils.getContext().resources.getString(R.string.announcement),R.mipmap.xq_tzgg,"NOTICE"),

    NEWS(ContextUtils.getContext().resources.getString(R.string.message),R.mipmap.xq_xx,"MESSAGE"),

    DAILY(ContextUtils.getContext().resources.getString(R.string.daily),R.mipmap.xqzg_rb,"DAILYREPORT"),
}

