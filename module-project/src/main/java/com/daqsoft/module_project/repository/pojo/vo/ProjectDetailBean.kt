package com.daqsoft.module_project.repository.pojo.vo

import com.daqsoft.module_project.utils.StringUtils


data class ProjectDetailBean(
    val contractSignTime: String,
    val createTime: String,
    val createUser: Int,
    val crew: String,
    val customerName: String,
    val delayDays: String,
    val delayTaskCount: String,
    val finalCheckTime: String,
    val finalState: Int,
    val stop: String,
    val firstCheckTime: String,
    val firstState: Int,
    val grade: Int,
    val id: Int,
    val isFocus: Int,
    val isManager: Boolean,
    val itrState: Int,
    val loginUser: Int,
    val memberCnt: Int,
    val members: List<Member>,
    val noteItrCount: Int,
    val noteTotalCount: Int,
    val nowUser: Int,
    val percentage: String,
    val processes: List<Processe>,
    val projectAmount: Any,
    val projectBackgroud: String,
    val projectCode: String,
    val projectGrade: String,
    val projectName: String,
    val projectOverview: String,
    val projectType: Int,
    val projectTypeName: String,
    val simpleName: String,
    val starterTime: String,
    val taskTotalCount: String,

    val yesterDayCnt: String,
    val todayCnt: String,
    val yesterDayDelayCnt: String,
    val todayCompleteCnt: String,
    val lastNote: String,
    val leaders: List<Leaders>,
    val totalHours: String
){
    fun  getDays():String{
        if(totalHours.isNullOrBlank()){
            return "0.0天"
        }else{
            val format = String.format("%.1f", totalHours.toInt() * 1.0f / 8)
            return "${format}天"
        }
    }
    fun  getLeaders():String{
        if(leaders.isNullOrEmpty()){
            return ""
        }
        var sb=StringBuffer()
        leaders?.forEach {
            sb.append(it.employeeName?:"")
            sb.append("、")
        }
        return sb.toString().substring(0,sb.length-1)
    }


}
data class Leaders(
    val postFullName: String,
    val id: Int,
    val employeeAvatar: String,
    val employeeName: String
)

data class Member(
    val headImg: String,
    val id: Int,
    val name: String,
    val phone: String
)

data class Processe(
    val actualEndTime: String,
    val actualStartTime: String,
    val id: Int,
    val itemName: String,
    val itemParentId: String,
    val kcpState: Int,
    val leader: Leader,
    val planEndTime: String,
    val planStartTime: String,
    val processUser: Int,
    val taskCount: Int,
    val taskRequire: Any,
    val tasks: List<Any>,
    val child: List<Processe>,
    val tools: Any
){
    fun getplanStartTimes(): String{
        if(planStartTime.isNullOrBlank()){
            return " "
        }
        return  planStartTime.split(" ")[0]
    }
    fun getplanEndTimes(): String{
        if(planEndTime.isNullOrBlank()){
            return " "
        }
        return  planEndTime.split(" ")[0]
    }
    fun getSjStartTimes(): String{
        if(actualStartTime.isNullOrBlank()){
            return " "
        }
        return  actualStartTime.split(" ")[0]
    }
    fun getSjEndTimes(): String{
        if(actualEndTime.isNullOrBlank()){
            return " "
        }
        return  actualEndTime.split(" ")[0]
    }

    fun showTime():Boolean{
       return StringUtils.isEmptyString(planStartTime) && StringUtils.isEmptyString(planEndTime)
               && StringUtils.isEmptyString(actualStartTime) && StringUtils.isEmptyString(actualEndTime)
    }
}

data class Leader(
    val gradeName: String,
    val headImg: String,
    val id: Int,
    val name: String
){
    fun getNameString():String{
        if(name.isNullOrBlank()){
            return gradeName
        }else{
            return "$name-$gradeName"
        }
    }
}
data class MoneyTypeBean(
    val groupCode: String,
    val id: String,
    val itemCode: String,
    val itemName: String,
    val itemValue: String,
    val serialNo: String
)