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
data class Employee(
    val avartar: String,
    val id: Int,
    val name: String,
    val postFullName: String,

    // 自定义属性 以便分组排序
    var type : Int? = 0,
    var initials : String? = null
) : Parcelable