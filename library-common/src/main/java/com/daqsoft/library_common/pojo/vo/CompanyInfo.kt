package com.daqsoft.library_common.pojo.vo

import com.daqsoft.library_common.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 4/12/2020 下午 1:39
 * @author zp
 * @describe
 */
data class CompanyInfo(
    /**
     * 地址
     */
    val companyAddress: String,
    /**
     * 城市
     */
    val companyAddressCity: Int,
    /**
     * 详细地址
     */
    val companyAddressDetail: String,
    /**
     * 区
     */
    val companyAddressDistrict: Int,
    /**
     * 县
     */
    val companyAddressProvince: Int,
    /**
     * 邮箱
     */
    val companyEmailAccount: String,
    /**
     * 邮箱密码
     */
    val companyEmailPassword: String,
    /**
     * 公司全程
     */
    val companyFullName: String,
    /**
     * 公司 logo
     */
    val companyLogo: String,
    /**
     * 公司名称
     */
    val companyName: String,
    /**
     * 联系电话
     */
    val companyPhone: String,
    /**
     * smtp 地址
     */
    val companySmtpAddress: String,
    /**
     * 系统名称
     */
    val companySystemName: String,
    /**
     * 官网
     */
    val companyWebsite: String,
    val enable: Boolean,
    val id: Int
){

    fun  getCloud() = "Cloud.Daqsoft.com"


    fun coverCompanySystemName():String{
        return if (companySystemName.isNullOrBlank()){
            ContextUtils.getContext().resources.getString(R.string.xiaoqizhiguan)
        }else{
            companySystemName
        }
    }
}