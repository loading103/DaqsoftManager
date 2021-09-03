package com.daqsoft.module_workbench.repository.pojo.vo

import android.graphics.Color
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.module_workbench.repository.pojo.bo.Importance

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 8/12/2020 上午 10:10
 * @author zp
 * @describe
 */
data class AnnouncementDetail(
    val auditString: List<Any>,
    val color: String,
    val commentNumbers: Int,
    val comments: List<AnnouncementComment>,
    val fileList: List<Any>,
    val id: Int,
    val issueEmployeeAvatar: String,
    val issueEmployeeId: Any,
    val issueEmployeeName: String,
    var like: Boolean,
    val likeNumbers: Int,
    val next: Boolean,
    val noticeContent: String,
    val noticeImportance: String,
    val noticeIssueTime: String,
    val noticeOrganizationList: List<Any>,
    val noticeOrganizationName: String,
    val noticeOutline: String,
    val noticeStatus: String,
    val noticeTitle: String,
    val noticeType: Any,
    val noticeTypeName: String,
    val oldStats: Any,
    val read: Boolean,
    val readNumbers: Int,
    val sendToALL: Any
){


    /**
     * 转换标题
     * @return String
     */
    fun coverTitle():String{
        return "         $noticeTitle"
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
        return  noticeIssueTime + if (howLongAgo.isBlank()) "" else "（$howLongAgo）"
    }


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

}

data class AnnouncementComment(
    val content: String,
    val employeeAvatar: String,
    val employeeId: Any,
    val employeeName: String,
    val id: Int,
    val modifyDate: String,
    val noticeId: Any,
    val commentId : String
){

    fun howLongAgo():String{
        if(modifyDate.isNullOrEmpty()){
            return ""
        }
        val howLongAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",modifyDate)
        return  modifyDate + if (howLongAgo.isBlank()) "" else "（$howLongAgo）"
    }

}