package com.daqsoft.module_workbench.repository.pojo.vo

import com.daqsoft.module_workbench.utils.IMTimeUtils

data class DialyDetailBean(
    val commentCnt: Int,
    val employeeAvatar: String,
    val employeeId: Int,
    val employeeName: String,
    val id: Int,
    val likeCount: Int,
    val likeOrNot: Int,
    val publishTime: String,
    val read: Int,
    val readList: List<Read>,
    val reportDate: String,
    val todayInfo: String,
    val tomorrowPlan: String,
    val dailyCheck: String,
    val title: String,
    val needHelp: String
)

data class Read(
    val employeeAvatar: String,
    val employeeId: Int,
    val employeeName: String,
    val readTime: String
){
    fun getRead():String{
        if(readTime.isNullOrBlank()){
            return ""
        }
        return IMTimeUtils.toDhmsStyle(readTime.toLong());
    }
}