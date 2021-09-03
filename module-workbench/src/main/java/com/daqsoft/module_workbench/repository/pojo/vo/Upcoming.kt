package com.daqsoft.module_workbench.repository.pojo.vo

import com.daqsoft.module_workbench.repository.pojo.bo.Importance

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 4/1/2021 上午 11:56
 * @author zp
 * @describe
 */


data class Upcoming(
    val datas: List<Mission>,
    val pageCurr: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
)

data class Mission(
    val END_TIME: String,
    val IMPORTANT_LEVEL: Int,
    val avatars: List<String>,
    // 花费时间	 小时
    var costHours: Double,
    // 延期时间 天数_小时数
    val delayDays: String,
    val endTime: String,
    val endTime2: String,
    val finishTime: Any,
    // 0未关注 1-关注
    val focusState: Int,
    // 重要程度 值越小越重要
    val importantLevel: Int,
    val isRead: Int,
    val itrState: Int,
    val projectName: String,
    val startTime: String,
    val startTime2: String,
    val taskId: Int,
    val taskTitle: String,
    val taskTypes: String,
    val tt: Int,
    val userNames: String,
    val workStart: Any,
    // 0-未开始 1-进行中 2- 暂停 3-完成 5-中止
    var workState: Int,
    // 0执行人，1抄送人
    val workType: Int,
    val workUsers: String,

    // 自定义属性 耗时总秒数
    var totalSeconds : Long
){

    /**
     * 项目 title
     * @return String
     */
    fun coverProjectTitle():String{
        return "【${projectName}】#${taskTypes}#$taskTitle"
    }

    /**
     * 项目时间
     * @return String
     */
    fun coverProjectTime():String{
        return "$startTime - $endTime"
    }


    /**
     * 项目等级
     */
    fun coverLevel():Int{
        return when(importantLevel){
            Importance.RED.level -> Importance.RED.icon
            Importance.ORANGE.level -> Importance.ORANGE.icon
            Importance.BLUE.level -> Importance.BLUE.icon
            Importance.GREEN.level -> Importance.GREEN.icon
            else -> Importance.BLUE.icon
        }
    }

    /**
     * 转换持续时间
     * @return String
     */
    fun coverDuration(time : Long? = null):String{
        val seconds =  time ?:totalSeconds
        var day  = 0
        var hour = 0
        var minute = 0
        var second = seconds.toInt()
        if (seconds > 60){
            second = ((seconds % 60).toInt())
            minute = ((seconds / 60).toInt())
            if (minute > 60){
                minute = (((seconds / 60) % 60).toInt())
                hour = (((seconds / 60 ) / 60).toInt())
                if (hour > 24 ){
                    hour = ((((seconds / 60 ) /60 )  % 24).toInt())
                    day = ((((seconds / 60 ) / 60) / 24).toInt())
                }
            }
        }
        return String.format("%1$02d天  %2\$02d:%3\$02d:%4\$02d",day,hour,minute,second)
    }
}