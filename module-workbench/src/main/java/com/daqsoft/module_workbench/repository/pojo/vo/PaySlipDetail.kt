package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 10/12/2020 上午 11:44
 * @author zp
 * @describe
 */
data class PaySlipDetail(
    val employeeAvatar: String,
    val employeeName: String,
    val employeeWageContent: String,
    val templateContent: String,
    val wageDifferenceContent: String,
    val wageIssueTime: String,
    val wageMonth: String
)