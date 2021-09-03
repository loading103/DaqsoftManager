package com.daqsoft.library_common.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.library_common.pojo.vo
 * @date 25/1/2021 下午 5:00
 * @author zp
 * @describe
 */
@Parcelize
data class UpdateInfo(
    /**
     * 更新内容
     */
    val AppUpdateInfo: String,
    /**
     * 下载地址
     */
    val DownPath: String,
    val IsUpdate: Int,
    val UpdateTime: String,
    val VersionCode: String
) : Parcelable {
}
