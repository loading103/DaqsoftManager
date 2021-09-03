package com.daqsoft.module_main.repository.pojo.vo

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_main.repository.pojo.vo
 * @date 7/1/2021 下午 3:35
 * @author zp
 * @describe
 */
@Parcelize
data class MyNotificationExtra(

    val employeeId : String?,
    val messageExtId :String?,
    val templateCode : String?,
    val messageId:String?,
    val messageInfos : String?
) : Parcelable {
}

@Parcelize
data class MessageInfo(
    val empId : String,
    val messageId : String
) : Parcelable {
}
