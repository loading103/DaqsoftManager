package com.daqsoft.module_project.repository.pojo.vo

import android.text.SpannableStringBuilder
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.library_base.utils.coverTime
import com.daqsoft.module_project.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import java.lang.StringBuilder

/**
 * @package name：com.daqsoft.module_project.repository.pojo.vo
 * @date 6/4/2021 下午 4:10
 * @author zp
 * @describe
 */

data class ProjectDynamicPaging(
    val current: Int,
    val pages: Int,
    val datas: List<ProjectDynamic>,
    val size: Int,
    val total: Int,
    val extraInfo : Map<String,String>
)

data class ProjectDynamic(
    val booking: Booking,
    val bookingState: Int,
    val employeeAvatar: String,
    val employeeName: String,
    val id: Int,
    val itrCloseTime: String,
    // itr状态（0 = 无，1=有，2=已关闭）
    val itrState: Int,
    // 0 是没有文件 1 是有文件没归档 2 是已归档
    val noteFileState: Int,
    val noteInfo: String,
    val noteScore: Double,
    val noteTimestamp: String,
    val noteUser: Int,
    val reply: List<ProjectDynamic>,
    val replyId: Int,
    val scores: List<Score>,
    val tags: List<ProjectLabelSimple>,
    val toUser: Int,
    val toUserName: String,
    val postName:String,
    val dailyReport:Boolean,
    val isManager: Boolean
){
    /**
     * 转换封面
     * @return StringBuilder
     */
    fun coverReply(): SpannableStringBuilder {
        val ssb = SimplifySpanBuild()
            .append(SpecialTextUnit(employeeName).setTextSize(12f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.red_fa4848)))
            .append(SpecialTextUnit("  回复  ").setTextSize(12f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.black_333333)))
            .append(SpecialTextUnit(toUserName).setTextSize(12f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.red_fa4848)))
        return ssb.build()
    }


    /**
     * 转换发布时间
     */
    fun coverNoteTimestamp():String{
        if(noteTimestamp.isNullOrEmpty()){
            return ""
        }
        val howLongAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",noteTimestamp)
        return  noteTimestamp + if (howLongAgo.isBlank()) "" else "（$howLongAgo）"
    }

}

class Booking(
    val total: String,
    val bookingDate: String,
    val detail: List<BookingDetail>
)

data class BookingDetail(
    val itemName : String,
    val money : String,
    val noteId : String
)

data class Score(
    val employeeAvatar: String,
    val employeeName: String,
    val noteId: Int,
    val postFullName: String,
    val score: Double
)


data class ProjectLabel(
    val name : String,
    val id : String,
    val cancelable: Any?,
    val createOrg: String,
    val createUser: Int,
    val disabled: Boolean,
    val projectId: Any?,
    val tagLevel: String,
    val used: Int,
    var isCheck:Boolean=false
)

data class ProjectLabelSimple(
    val tagName : String,
    val id : String
)
