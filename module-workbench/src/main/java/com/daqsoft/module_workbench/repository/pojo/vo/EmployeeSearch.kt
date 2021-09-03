package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 4/1/2021 上午 10:35
 * @author zp
 * @describe
 */

data class EmployeeSearch(
    val current: Int,
    val pages: Int,
    val records: List<Detail>,
    val size: Int,
    val total: Int
)

data class Detail(
    // 员工头像
    val employeeAvatar: String,
    // 合同到期
    val employeeContractEnd: Boolean,
    // 邮箱
    val employeeEmail: String,
    // 入职时间
    val employeeEntryTime: String,
    // 性别
    val employeeGender: String,
    // id
    val employeeId: Int,
    // 电话
    val employeeMobile: String,
    // 名字
    val employeeName: String,
    // 工号
    val employeeNumber: String,
    // 所属中心
    val employeeOrganization: String,
    // 所属中心 id
    val employeeOrganizationId: Int,
    // 职位
    val employeePost: String,
    // 试用期结束
    val employeeProbationEnd: Boolean,
    // 员工类型
    val employeeType: String
)