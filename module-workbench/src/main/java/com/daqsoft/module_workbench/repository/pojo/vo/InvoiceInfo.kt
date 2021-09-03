package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 30/3/2021 下午 3:37
 * @author zp
 * @describe
 */
data class InvoiceInfo(
    // 详细地址
    val address: String,
    // 市州
    val addressCity: Int,
    // 街道门牌号
    val addressDetail: String,
    // 区县
    val addressDistrict: Int,
    // 省
    val addressProvince: Int,
    // 开户银行
    val bank: String,
    // 银行账号
    val bankAccount: String,
    // 单位名称
    val companyName: String,
    // 电话号码
    val phone: String,
    // 税号
    val taxCode: String
)