package com.daqsoft.library_common.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.library_common.pojo.vo
 * @date 14/4/2021 上午 11:37
 * @author zp
 * @describe
 */

@Parcelize
data class NextMessage(
    val messageTime: String,
    val messageStatus: Int,
    val messageModule: Int,
    val messageExtId: Int,//消息额外id
    val messageContent: String,//消息额外id
    val employeeId: Int,//消息额外id
    val next: String,//消息额外id
    var templateCode : String? = null
) : Parcelable