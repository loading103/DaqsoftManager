package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 9/12/2020 下午 4:50
 * @author zp
 * @describe
 */
data class PaySlip(
    val id: Int,
    val issueEmployeeAvatar: String,
    val issueEmployeeName: String,
    val wageIssueTime: Any,
    val wageMonth: String,
    val wageQuantity: Any,
    val wageStatus: Any,
    val wageTemplateId: Any,
    val wageTemplateTitle: String
)