package com.daqsoft.module_project.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


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

