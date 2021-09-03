package com.daqsoft.module_project.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 12/3/2021 上午 9:39
 * @author zp
 * @describe
 */

data class Menu(
    val teamReport: List<MenuInfo>,
    val daily: List<MenuInfo>,
    val office: List<MenuInfo>
)

@Parcelize
data class MenuInfo(
    val appMenuName: String,
    val appModule: String,
    val icon: String?,
    val id: Int,
    val orderNo: Int
) : Parcelable


data class MenuPermission(
    val code: String,
    val menuId: Int,
    val menuName: String,
    val opId: Int,
    val opName: String,
    val permisson: Int
)
