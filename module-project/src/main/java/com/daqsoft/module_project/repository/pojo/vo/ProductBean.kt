package com.daqsoft.module_project.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductBean(
    val member: List<ProductMember>,
    val product: Product
): Parcelable
@Parcelize
data class  ProductMember(
    val postMemberCount: Int,
    val postMemberList: List<PostMember>,
    val postName: String
): Parcelable
@Parcelize
data class Product(
    val createDate: String,
    val enable: Boolean,
    val group: String,
    val id: String,
    val productExperienceDescription: String,
    val productExperienceUrl: String,
    val productFullName: String,
    val productIntroduction: String,
    val productLogoUrl: String,
    val productName: String,
    val productNameUrl: String,
    val productNature: String,
    val productPackageList: List<String>,
    val productPartSale: Boolean,
    val productPermitSale: Boolean,
    val productProjectId: String,
    val productServiceMethod: String,
    val productTargetUser: String
): Parcelable

@Parcelize
data class PostMember(
    val appBackground: String,
    val employeeAvatar: String,
    val employeeId: String,
    val employeeName: String,
    val hobby: String
): Parcelable