package com.daqsoft.module_workbench.repository.pojo.vo

import android.graphics.Color
import android.view.View

data class CustomerBean(
    val total: Int,
    val size: Int,
    val pages: Int,
    val current: Int,
    val records: MutableList<CustomerListBean>

)


data class CustomerListBean(
    val addTime: String,
    val contactPhone: String,
    val contactUser: String,
    val customerGrade: String,
    val customerGradeName: String,
    val customerName: String,
    val customerType: String,
    val customerTypeName: String,
    val dutyLeader: String,
    val dutyLeaderName: String,
    val fullAddress: String,
    val id: Int,
    val lastVisitTime: String,
    val pickupId: String,
    val pickupOrgName: String,
    val pickupPeople: String,
    val visitCnt: String
){
    fun getAdress():String{
        if(fullAddress.isNullOrBlank()){
            return ""
        }
        return "地址：${fullAddress}"
    }

}


data class CustomerDetailBean(
    val addTime: String,
    val addressCityCode: String,
    val addressCityName: String,
    val addressCountyCode: String,
    val addressCountyName: String,
    val addressProvinceCode: String,
    val addressProvinceName: String,
    val contactPhone: String?,
    val contactUser: String?,
    val customerDetailAddress: String?,
    val customerGrade: String,
    val customerGradeName: String?,
    val customerName: String?,
    val customerType: String?,
    val customerTypeName: String?,
    val dutyLeader: String,
    val dutyLeaderName: String?,
    val fullAddress: String,
    val id: String,
    val pickupId: String,
    val pickupPeople: String,
    val pickupPeopleAvator: String,
    val projectCnt: Int,   //相关项目数
    val visitNoteCnt: Int,  //相关回访记录数
    val dutyLeaders: MutableList<DutyLeaders>?


){
    fun getContaceName():String{
        if(contactUser.isNullOrBlank()){
            return ""
        }else{
            if(contactPhone.isNullOrBlank()){
                return contactUser
            }else{
                return contactUser+"(${contactPhone})"
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
data class DutyLeaders(
    val employeeId: String,
    val employeeName: String,
    val employeeAvatar: String
)

data class CustomerRecord(
    val id: String,
    val customerId: String,
    val createTime: String,
    val content: String,
    val recordType: String,
    val operaterName: String,
    val operaterId: String
){
    fun getUser():String{
        if(!operaterName.isNullOrEmpty()){
            if(getContents().isNullOrBlank()){
                return "由${operaterName}（${operaterId}）创建"
            }else{
                return "由${operaterName}（${operaterId}）修改"
            }
        }else{
            return ""
        }
    }
    fun getContents():String{
        if(!content.isNullOrEmpty()){
            return "修改内容：${content}"
        }else{
            return ""
        }
    }
}

data class CustomerTagListBean(
    val content: String,
    val color: String ?="fa4848",
    var type:Int?=0,
    var textColor:String?="ffffff"
)
data class CustomerChooseType(
    val id: String ?= null,
    val name: String
)