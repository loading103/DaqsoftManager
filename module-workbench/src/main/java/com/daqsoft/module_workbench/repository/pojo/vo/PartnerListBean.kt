package com.daqsoft.module_workbench.repository.pojo.vo

import android.view.View



data class PartnerBean(
    val total: Int,
    val size: Int,
    val pages: Int,
    val current: Int,
    val records: MutableList<PartnerListBean>

)

data class PartnerListBean(
    val addTime: String?,
    val dutyLeader: String?,
    val dutyLeaderName: String?,
    val id: Int,
    val maxVisitTime: String,
    val maxVisitTimeT: String,
    val partnerGrade: String?,
    val partnerGradeName: String?,
    val partnerName: String?,
    val partnerPhone: String?,
    val partnerType: String?,
    val partnerTypeName: String?,
    val partnerTypeId: String,
    val partnerUser: String?,
    val pickupId: String,
    val pickupOrgName: String?,
    val pickupPeople: String?,
    val visitCnt: String
){
    fun getAdress():String{
        if(partnerUser.isNullOrBlank()){
            return "联系人："
        }
        return "联系人：${partnerUser}"
    }

}


data class  PartnerDetailBean(
    val addTime: String,
    val addUser: Any,
    val companyId: String,
    val dutyLeader: String,
    val dutyLeaderName: String,
    val id: String,
    val partnerGrade: Any,
    val partnerGradeName: String,
    val partnerName: String,
    val partnerPhone: String,
    val partnerType: String,
    val partnerTypeName: String,
    val partnerUser: String,
    val pickupId: String,
    val pickupPeople: String,
    val fullAddress: String,
    val pickupPeopleAvator: String,
    val contactPhone: String,
    val contactUser: String,
    val projectCnt: Int?=0,   //相关项目数
    val visitNoteCnt: Int?=0,  //相关回访记录数
    val dutyLeaders: MutableList<DutyLeaders>?
){
    fun getContaceName():String{
        if(partnerUser.isNullOrBlank()){
            return ""
        }else{
            if(partnerPhone.isNullOrBlank()){
                return partnerUser
            }else{
                return partnerUser+"(${partnerPhone})"
            }
        }
    }
    fun getShowLeader():Int{
        if(dutyLeaders.isNullOrEmpty()){
            return View.GONE
        }else{
            return View.VISIBLE
        }
    }
    fun getLeftNumber():String{
        if(visitNoteCnt?:0<1){
            return "回访记录"
        }else{
            return "回访记录(${visitNoteCnt})"
        }
    }

    fun getRightNumber():String{
        if(projectCnt?:0<1){
            return "相关项目"
        }else{
            return "相关项目(${projectCnt})"
        }
    }
}
