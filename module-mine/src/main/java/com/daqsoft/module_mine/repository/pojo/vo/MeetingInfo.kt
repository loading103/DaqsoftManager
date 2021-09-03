package com.daqsoft.module_mine.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 30/12/2020 下午 3:43
 * @author zp
 * @describe
 */
data class MeetingInfo (
    val url : String,
    val endTime : String,
    val startTime : String,
    val meetingTitle : String,

    // 自定义属性 时间time
    var timeBegin : Long,
    var timeEnd : Long
){



}