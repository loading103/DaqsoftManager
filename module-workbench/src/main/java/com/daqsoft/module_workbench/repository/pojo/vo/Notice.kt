package com.daqsoft.module_workbench.repository.pojo.vo

import android.os.Parcelable
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.bo.Importance
import kotlinx.android.parcel.Parcelize

/**
 * 通知
 */
data class NoticeCount(
    // 今年已发布数
    val thisYearCount : Int,
    // 待审核数
    val pending : Int,
    // 行政公告数
    val administration : Int,
    // 人事公告数
    val resource : Int
){

}

data class NoticeType(
    val deleted: Boolean,
    val id: Int?,
    val typeName: String
) {


    fun subName():String{
        if (typeName.isNullOrEmpty()){
            return ""
        }

        return if (typeName.length <=2){
            typeName
        }else{
            typeName.substring(0,2)
        }

    }
}

data class Notice(
    val current: Int,
    val pages: Int,
    val records: List<NoticeDetail>,
    val size: Int,
    val total: Int){
}

data class NoticeDetail(
    val fileList: List<NoticeFile>,
    val id: Int,
    val issueEmployeeAvatar: String,
    val issueEmployeeId: Any,
    val issueEmployeeName: String,
    val next: Any,
    val noticeContent: String,
    val noticeImportance: String,
    val noticeIssueTime: String,
    val noticeOrganizationList: List<NoticeOrganization>,
    val noticeOrganizationName: String,
    val noticeOutline: String,
    val noticeStatus: String,
    val noticeTitle: String,
    val noticeType: Int,
    val noticeTypeName: String,
    val read: Any,
    val readNumbers: Int,
    val sendToALL: Int,
    val auditString:List<NoticeAudit>,
    val oldStats:Boolean,
    val color : String
){

    /**
     * 转换 label
     * @return Int
     */
    fun coverImportance():Int{
        return when(noticeImportance){
            Importance.RED.color -> Importance.RED.icon
            Importance.ORANGE.color -> Importance.ORANGE.icon
            Importance.BLUE.color -> Importance.BLUE.icon
            Importance.GREEN.color -> Importance.GREEN.icon
            else -> Importance.BLUE.icon
        }
    }

    /**
     * 转换标题
     * @return String
     */
    fun coverTitle():String{
        return "              $noticeTitle"
    }


    /**
     * 转换部门
     * @return String
     */
    fun coverDept():String{
        if(noticeOrganizationName.isNullOrEmpty()){
           return "全公司"
        }
        val depts = noticeOrganizationName.split(",")
        if(depts.size > 1){
            return "${depts[0]} 等${depts.size}个部门"
        }
        return depts[0]
    }

    /**
     * 转换时间
     * @return String
     */
    fun coverTime():String{
        if(noticeIssueTime.isNullOrEmpty()){
            return ""
        }
        val howLongAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",noticeIssueTime)
        return  noticeIssueTime + if (howLongAgo.isBlank()) "" else "（${howLongAgo}）"
    }


    /**
     * 转换状态
     * @return String
     */
    fun coverStatus():String{
        return if (noticeStatus == "待提交") "提交审核" else noticeStatus
    }

    /**
     * 转换状态
     * @return Boolean
     */
    fun coverStatusFlag():Boolean{
        return noticeStatus == "待提交"
    }

    /**
     * 转换部门名称
     * @return String
     */
    fun coverNoticeOrganization():String{
        if (noticeOrganizationList.isNullOrEmpty()){
            return "全公司"
        }
        return noticeOrganizationList.map { it.organizationName }.joinToString(separator = "  ·  "){ it.toString() }
    }

    /**
     * 转换类型
     * @return String
     */
    fun coverTypeName():String{
        if (noticeTypeName.isNullOrBlank()){
            return ""
        }
        return if (noticeTypeName.length <=2){
            return noticeTypeName
        }else{
            noticeTypeName.substring(0,2)
        }

    }

    /**
     * 转换类型背景色
     * @return String
     */
    fun coverTypeBackground():Int{
        val typeBackground =  arrayListOf(R.array.blue,R.array.green,R.array.purple)
        return when{
            noticeTypeName.contains("行政公告") ->{ R.array.red }
            noticeTypeName.contains("人事公告") ->{ R.array.yellow}
            else -> {
                typeBackground.shuffled()[0]
            }
        }
    }
}

@Parcelize
data class NoticeOrganization(
    // 通知组织Id
    var organizationId : String,
    var organizationName : String? = null
) : Parcelable

data class NoticeFile(
    // 文件地址
    var fileUrl : String,
    // 文件名
    var fileTitle : String,
    // 文件id
    var id : String? = null
)

data class NoticeAudit(
    val auditTime : String,
    val auditInfo : String
){

}

data class NoticeAuditStatus(
    val desc : String,
    val value : String?
){

}