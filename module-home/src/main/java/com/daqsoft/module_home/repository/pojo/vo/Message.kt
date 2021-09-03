package com.daqsoft.module_home.repository.pojo.vo

import com.daqsoft.library_base.utils.TimeUtils

/**
 * @package name：com.daqsoft.module_home.repository.pojo.vo
 * @date 24/12/2020 下午 3:27
 * @author zp
 * @describe
 */
data class Message(
    val `data`: Any,
    val datas: List<MessageInfo>,
    val orderBy: String,
    val orderType: String,
    val pageCurr: Int,
    val pageSize: Int,
    val total: Int,
    val extraInfo: ExtraInfo,
    val totalPages: Int
)

data class MessageInfo(
    val eventTitle: String,
    val id: Int,
    val msgContent: String,
    val msgTime: String,
    val paramId: Int,
    val path: String,
    val statu: Boolean,
    val tmpName: String,


    // 自定义属性 用于分组
    var day: String
){
    fun coverTime():String{
        val howLongAgo = TimeUtils.howLongAgo("yyyy-MM-dd HH:mm:ss",msgTime)
        return msgTime + if (howLongAgo.isBlank()) "" else "（$howLongAgo）"
    }


}
data class ExtraInfo(
    val next: String,
    val unReadCount: String
)