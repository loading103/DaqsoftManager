package com.daqsoft.module_workbench.repository.pojo.vo

import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.TimeUtils

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 7/12/2020 上午 10:22
 * @author zp
 * @describe
 */
data class Announcement(
    val current: Int,
    val pages: Int,
    val records: List<Record>,
    val size: Int,
    val total: Int
)

data class Record(
    val auditString: List<Any>,
    val color: String,
    val commentNumbers: Int,
    val comments: List<Any>,
    val fileList: List<Any>,
    val id: Int,
    val issueEmployeeAvatar: String,
    val issueEmployeeId: Any,
    val issueEmployeeName: String,
    val like: Boolean,
    val likeNumbers: Int,
    val next: Any,
    val noticeContent: String,
    val noticeImportance: String,
    var noticeIssueTime: String,
    val noticeOrganizationList: List<Any>,
    val noticeOrganizationName: String,
    val noticeOutline: String,
    val noticeStatus: Any,
    val noticeTitle: String,
    val noticeType: Any,
    val noticeTypeName: String,
    val oldStats: Boolean,
    val read: Boolean,
    val readNumbers: Int,
    val sendToALL: Any

){

    /**
     * 转换时间
     * @return String
     */
    fun coverTime():String{
        if (noticeIssueTime == null)
            noticeIssueTime = ""
        val howLongAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",noticeIssueTime)
        return  noticeIssueTime + if (howLongAgo.isBlank()) "" else "（${howLongAgo}）"
    }

    /**
     * 转换标题
     * @return String
     */
    fun coverTitle():String{
        return "         $noticeTitle"
    }

    /**
     * 转换阅读数
     * @return String
     */
    fun coverReadAmount():String{
        return AppUtils.numberFormatGroupingUsed(readNumbers)
    }

    /**
     * 转换评论数
     * @return String
     */
    fun coverCommentAmount():String{
        return AppUtils.numberFormatGroupingUsed(commentNumbers)
    }

    /**
     * 转换点赞数
     * @return String
     */
    fun coverLikeAmount():String{
        return AppUtils.numberFormatGroupingUsed(likeNumbers)
    }

}
