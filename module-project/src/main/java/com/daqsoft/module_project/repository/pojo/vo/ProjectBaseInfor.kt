package com.daqsoft.module_project.repository.pojo.vo

import android.graphics.Color
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Description
 * @ClassName   ProjectBaseInfor
 * @Author      luoyi
 * @Time        2021/7/2 9:10
 */
@Parcelize
data class ProjectBaseInfor(
    val billClose: String?=null,
    val billCloseStatus: String?=null, //关账状态
    val billCloseTime: String?=null,  //关账时间
    val checkState: String?=null,
    val contractStatus: String?=null,  //签约状态
    val controlUnit: String?=null,  //监理单位
    val createTime: String?=null, //项目创建时间
    val customerContactPhone: String?=null,  //客户电话
    val customerName: String?=null,  //客户单位
    val customerUser: String?=null,  //客户联系人
    val designUnit: String?=null,//设计单位
    val disclosure: Disclosure?=null,
    val disclosureEmployee: DisclosureEmployee?=null,
    val disclosureTime: String?=null,   //交底时间
    val finalCheckEmployee: DisclosureEmployee?=null,  //终验人
    val finalCheckPaperTime: String?=null,  //初验规定时间
    val finalCheckTime: String?=null,   //实际终验时间
    val firstCheckEmployee: DisclosureEmployee?=null,   //初验人
    val firstCheckPaperTime: String?=null,  //初验规定时间
    val firstCheckTime: String?=null,  //实际初验时间
    val gradeEmployee: DisclosureEmployee?=null,  //定级人
    val gradeTime: String?=null,   //定级时间
    val operateTime: String?=null,  //运营时间
    val operationState: String?=null,  //运营有无
    val operativeState: String?=null,  //运维有无
    val operativeTime: String?=null,   //运维时间
    val processName: String?=null,   //流程名称
    val projectCode: String?=null,   //项目编号
    val projectCreator: DisclosureEmployee?=null,  //项目创建人
    val projectLeader:List<DisclosureEmployee>?=null, //项目创建人

    val projectGrade: String?=null,  //项目等级
    val projectManager: List<DisclosureEmployee>?=null, //项目经理
    val projectName: String?=null,
    val projectSenior: List<DisclosureEmployee>?=null,  //项目管理人员
    val projectTypeName: String?=null,   //项目类型
    val qualityInsurance: String?=null,  //保证金金额
    val signEmployee: DisclosureEmployee?=null,   //	签约人
    val signEndDate: String?=null,  //合同中止日期
    val signMoney: String?=null,   //合同金额
    val signTime: String?=null,   //签约时间
    val stopProcessItemId: String?=null, //项目终止审批单id
    val stopProcessItemName: String?=null, //项目终止审批单名称
    val stopTime: String?=null ,  //项目中止时间

    val operationTime: String?=null, //交维时间
    val operationUser: String?=null ,  //交维人员
    val projectAmount: String?=null ,
    val projectBackgroud: String?=null ,
    val projectOverview: String?=null
) : Parcelable {

    fun getFxd():String{
        if(disclosure==null || disclosure.taskTitle.isNullOrBlank()){
            return "—"
        }
        return disclosure.taskTitle
    }
    fun getXmjl():String{
        if(projectManager.isNullOrEmpty()){
            return "—"
        }
        var sb=StringBuffer()
        projectManager.forEach {
            sb.append(it.employeeName?:"")
            sb.append("、")
        }
        return sb.toString().substring(0,sb.length-1)
    }
    fun getXmjlColor():Int{
        if(projectManager.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }
    fun getSssColor():Int{
        if(stopProcessItemName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getCustomerUserColor():Int{
        if(customerUser.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getXmGlry():String{
        if(projectSenior.isNullOrEmpty()){
            return "—"
        }
        var sb=StringBuffer()
        projectSenior.forEach {
            sb.append(it.employeeName?:"")
            sb.append("、")
        }
        return sb.toString().substring(0,sb.length-1)
    }

    fun getXmZjLd():String{
        if(projectLeader.isNullOrEmpty()){
            return "—"
        }
        var sb=StringBuffer()
        projectLeader?.forEach {
            sb.append(it.employeeName?:"")
            sb.append("、")
        }
        return sb.toString().substring(0,sb.length-1)
    }


    fun getXmGlryColor():Int{
        if(projectSenior.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getEmployeeColor():Int{
        if(signEmployee==null  || signEmployee?.employeeName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getEmployeeZjColor():Int{
        if(projectLeader.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getDjColor():Int{
        if(gradeEmployee==null  || gradeEmployee?.employeeName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }
    fun getJdrColor():Int{
        if(disclosureEmployee==null  || disclosureEmployee?.employeeName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getCyrColor():Int{
        if(firstCheckEmployee==null  || firstCheckEmployee?.employeeName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getZyrColor():Int{
        if(finalCheckEmployee==null  || finalCheckEmployee?.employeeName.isNullOrEmpty()){
            return Color.parseColor("#333333")
        }else{
            return Color.parseColor("#fa4848")
        }
    }

    fun getXmDiji():String{
        if(gradeEmployee==null  || gradeEmployee?.employeeName.isNullOrEmpty() || projectGrade.isNullOrBlank()){
            return "—"
        }
        return projectGrade
    }
    fun getXmDijiTime():String{
        if(gradeEmployee==null  || gradeEmployee?.employeeName.isNullOrEmpty() || gradeTime.isNullOrBlank()){
            return "—"
        }
        return gradeTime
    }

}
@Parcelize
data class Disclosure(
    val taskId: String,
    val taskTitle: String
): Parcelable
@Parcelize
data class DisclosureEmployee(
    val employeeId: String,
    val employeeName: String
): Parcelable

data class ProjectBaseInfors(
    var projectPartner: String,
    var partnerName: String,
    var projectGrade: String,
    var projectCustomer: String,
    var simpleName: String,
    var processId: String
)
