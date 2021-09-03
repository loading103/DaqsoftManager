package com.daqsoft.module_workbench.repository.pojo.vo

import android.os.Parcelable
import android.text.SpannableStringBuilder
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.utils.TimeUtils
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.module_workbench.R
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_project.repository.pojo.vo
 * @date 6/4/2021 下午 4:10
 * @author zp
 * @describe
 */

data class CustomerRecordBeanPaging(
    val current: Int,
    val pages: Int,
    val records: List<CustomerRecordBean>,
    val size: Int,
    val total: Int,
    val extraInfo : Map<String,String> ?= null
)

data class CustomerRecordBean(
    val dayNoteSize: String,
    val headImg: String,
    val id: Int,
    val itrCloseTime: Any,
    val itrState: Int,
    val noteDate: String,
    val noteInfo: String,
    val noteTimestamp: String,
    val noteUser: Int,
    val orgName: String,
    val platform: String,
    val postName: String,
    val replySize: Int,
    val replys: List<CustomerRecordBean>,
    val tags: List<ProjectLabelSimple>,
    val toUser: Any,
    val toUserHead: String,
    val toUserName: String,
    val undoState: Int,
    val userName: String
){
    /**
     * 转换封面
     * @return StringBuilder
     */
    fun coverReply(): SpannableStringBuilder {
        val ssb = SimplifySpanBuild()
            .append(SpecialTextUnit(userName).setTextSize(12f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.red_fa4848)))
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
//data  class Replys(
//    val dayNoteSize: String,
//    val headImg: String,
//    val id: Int,
//    val itrCloseTime: String,
//    val itrState: String,
//    val noteDate: String,
//    val noteInfo: String,
//    val noteTimestamp: String,
//    val noteUser: Int,
//    val orgName: String,
//    val platform: String,
//    val postName: String,
//    val replySize: String,
//    val replys: List<Replys>,
//    val tags: List<Any>,
//    val toUser: Int,
//    val toUserHead: String,
//    val toUserName: String,
//    val undoState: Int,
//    val userName: String
//)
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


data class Bookkeeping(
    var costType:String,
    var money:String,
    var remark:String
)

data class CustomerRecordRequest (
    // 保存文件
    var files : String?,
    // 动态内容
    var noteInfo : String?,
    // 是否为itr
    var itrState : Boolean?,
    // 项目ID
    var visitId:String?,
    // @人id
    var alertId : List<Int>?,
    // 标签id
    var tagIds : List<Int>?,
    // 回复id
    var replyId	 : String?
)


@Parcelize
data class AccountBackBean(
    var itemValue : String?=null,
    // 费用类型
    var costType: String?=null,
    // 单笔金额
    var money: String?=null,
    // 费用用途
    var costUse: String?=null,
    var totleMoney: String?=null,
    var time: String?=null
) : Parcelable

data class Leader(
    val gradeName: String?,
    val headImg: String?=null,
    val id: Int?=null,
    val name: String?=null
){
    fun getNameString():String{
        if(name.isNullOrBlank()){
            return gradeName!!
        }else{
            return "$name-$gradeName"
        }
    }
}
data class CustomerType(
    val typeName: String?,
    val typeClassify: String?=null,
    var color: String?=null,
    val id: Int?=null,
    val addTime: String?=null
)