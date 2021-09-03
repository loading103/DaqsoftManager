package com.daqsoft.module_workbench.repository.pojo.vo

import android.text.TextUtils
import android.view.View
import java.math.RoundingMode
import java.text.DecimalFormat

class DialyListBean : ArrayList<DialyListBeanItem>()

data class DialyListBeanItem(

    val date: String,
    val record: List<DialyRecord>,
    val records: List<DialyRecord>
)

data class DialyRecord(
    val employeeAvatar: String,
    val employeeId: Int,
    val employeeName: String,
    val id: Int,
    val publishTime: String,
    val read: Int,
    val reportDate: String,
    val todayInfo: String,
    val tomorrowPlan: String,
    val dailyCheck: String,

    val weekend: Boolean,
    val leaderHelp: String,   //需要领导帮助
    val reportMode: String,
    val date: String,
    val statistics: String,
    val reportNews: MutableList<String>,
    val needHelp: String,
    val publishStatus: Boolean,
    val recallStatus: Boolean,

    val submitStatus: Boolean,
    val submitTime: String,
    val title: String
){
    fun getTopContent():String{
        if(reportMode=="AUTO"){
            return "今日日报还未自动生成，查看自动生成模式规则"
        }else{
            return "今日的日报还未提交哦，赶快去提交吧！"
        }
    }

    fun getTeamContent():String{
        return "今日团队日报还未提交哦，赶快去提交吧！"
    }
    // 只在团队日报中用
    fun getNodata():Boolean{
        // 如果内容都为空
        if(TextUtils.isEmpty(todayInfo ) &&  TextUtils.isEmpty(tomorrowPlan ) &&  TextUtils.isEmpty(needHelp )){
            return true
        }
        return false
    }
}


data class DialyProjec(
    val projectGrade: String,
    val grade: String,
    val projectCode: String,
    val projectType: String,
    val id: String,
    val projectName: String
)

data class DialyRuleBean(
    val id: String,
    val orgId: String,
    val type: String,   //   AUTO=自动；MANUAL=手动@mock=AUTO
    val setupObj: DialyRuleObject
)

data class DialyRuleObject(
    val tomorrowTask: Boolean,
    val tomorrowPlan: Boolean,
    val task: Boolean,    //当日完成的任务或任务阶段成果
    val project: Boolean,   //	当日新创建的项目

    val notebook: Boolean,   //	项目动态
    val notebookTags: String,
    val notebookMode: String, //项目动态模式

    val tagList: MutableList<DialyRuleObjectBean>

){
    fun getTitle1():String{
        if(task || project || notebook){
            return "今日工作完成情况："
        }
        return ""
    }
    fun getContent1():String{
        if(task ){
            return "当日完成的任务或任务阶段成果"
        }
        return ""
    }
    fun getContent2():String{
        if(notebook && notebookMode=="PORJECT_NOTE_BOOK" ){
            return "当日发布的所有项目动态"
        }
        if(notebook && notebookMode=="REPORT" ){
            return "在项目动态中发布的当日项目日报"
        }
        if(notebook && notebookMode=="INCLUDE_TAG" ){
            var sb=StringBuffer()
            tagList?.forEach {
                sb.append("#${it.name}#")
            }

            return "当日发布的项目动态或工作任务包含：$sb"
        }
        return ""
    }
    fun getContent3():String {
        if (project) {
            return "当日新创建的项目"
        }
        return ""
    }
    fun getTitle2():String{
        if(tomorrowTask || tomorrowPlan){
            return "明日工作计划："
        }
        return ""
    }

    fun getContent4():String {
        if (tomorrowTask) {
            return "明日待办工作任务"
        }
        return ""
    }
    fun getContent5():String {
        if (tomorrowTask) {
            return "在项目动态中发布的当日项目日报"
        }
        return ""
    }
}
data class DialyRuleObjectBean(
    val name: String,
    val id: String
)


data class DataTime(
    var startTime: String?,
    var endTime: String?
)
data class DialyMemberBean(
    val dayReport: DialyRecord,
    val employeeId: String,
    val employeeName: String,
    val headerUrl: String,

    val leaderHelp: String,
    val reportDate: String,
    val todayInfo: String,
    val tomorrowPlan: String,
    val weekend: Boolean,

    val projectList: List<String>,
    val dateList: List<String>,

    val isReport: Boolean,



    val employeeAvatar: String
)

data class DialyStatisticBean(
    val reportedRatio: String,
    val reported: String,
    val notReported: String,
    val needReport: String
){

    fun getreportedRatios():String {
        if (reportedRatio.isNullOrBlank()) {
            return " "
        }
        return getNoMoreThanOneDigits(reportedRatio.toDouble())
    }

    public fun getNoMoreThanOneDigits(number:Double):String {
        var format = DecimalFormat("0.#");
        //未保留小数的舍弃规则，RoundingMode.FLOOR表示直接舍弃。
        format.roundingMode = RoundingMode.FLOOR;
        return format.format(number)+"%";
    }
}


data class DialySearchBean(
    val pages: String,
    val current: String,
    val size: String,
    val total: String,
    val records: List<DialyMemberBean>
)


