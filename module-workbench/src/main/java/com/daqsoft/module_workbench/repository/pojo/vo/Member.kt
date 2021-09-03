package com.daqsoft.module_workbench.repository.pojo.vo

import android.os.Parcelable
import com.daqsoft.library_common.pojo.vo.Employee
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 28/12/2020 下午 4:41
 * @author zp
 * @describe
 */
data class Member(
    val employee: List<Employee>,
    val org: List<Org>
)

data class Org(
    val id: Int,
    val organizationName: String,
    val cnt:Int
)

data class OrgSimple(
    val id: Int,
    val orgName: String
)