package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 1/12/2020 下午 3:30
 * @author zp
 * @describe 组织架构
 */
data class Organization(
    val companyName: String,
    val teams: List<Child>,
    val totalCount: Int
)

data class Child(
    val childs: List<Child>,
    val id: Int,
    val name: String,
    val persons: Int,
    val pid: Int
)

data class OrganizationChildren(
    val id: Int,
    val organizationIsParent: Boolean,
    val organizationName: String
)