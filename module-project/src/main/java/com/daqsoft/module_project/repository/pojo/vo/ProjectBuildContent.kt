package com.daqsoft.module_project.repository.pojo.vo

data class ProjectBuildContent(
    val custom: List<Custom>,
    val standard: List<Standard>
)

data class Custom(
    val count: String,
    val id: Int,
    val introduction: String,
    val moduleId: String,
    val productName: String,
    val reviewTimes: String,
    val serviceMethod: String,
    val status: String
)

data class Standard(
    val customFunctionCount: String,
    val extraFunctionCount: String,
    val fullName: String,
    val functionCount: String,
    val id: String,
    val nameUrl: String,
    val pjpServiceMethod: String,
    val priceId: String,
    val productId: String,
    val productName: String,
    val productServiceMethod: String,
    val verName: String
)
data class refresh(
    val fullName: String,
    val functionCount: String,
    val id: String,
    val nameUrl: String,
    val pjpServiceMethod: String,
    val priceId: String,
    val productId: String,
    val productName: String,
    val productServiceMethod: String,
    val verName: String
)
