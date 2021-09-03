package com.daqsoft.module_mine.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 31/12/2020 上午 11:02
 * @author zp
 * @describe
 */
data class TaskInfo(
    val addByOthers: String,
    val addByOwn: String,
    val duration: String,
    val haveToComplete: String,
    val taskTitle: String,
    val todayComplete : String?
)