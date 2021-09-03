package com.daqsoft.module_mine.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 14/12/2020 下午 4:33
 * @author zp
 * @describe
 */
data class EmployeeInfoUpdate(
    var employeeAddressCity: Int,
    var employeeAddressDetail: String,
    var employeeAddressDistrict: Int,
    var employeeAddressProvince: Int,
    var employeeBirthday: String,
    var employeeBirthdayType: String,
    var employeeHobby: String
){

    fun  coverBirthdayType():String{
        return if (employeeBirthdayType == "公历") "GregorianCalendar" else "LunarCalendar"
    }

}