package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 4/1/2021 上午 11:05
 * @author zp
 * @describe
 */
data class EmployeeDetail(
    // 在职时间
    val days: Int,
    // 头像
    val employeeAvatar: String,
    // 生日类型（1 - 公历，0 -阴历）
    val employeeBirthdayType: Int,
    // 邮箱
    val employeeEmail: String,
    // 兴趣爱好
    val employeeHobby: String,
    // id
    val employeeId: Int,
    // 阴历生日
    val employeeLunarBirthday: String,
    // 联系电话
    val employeeMobile: String,
    // 名字
    val employeeName: String,
    // 部门名称
    val employeeOrganizationStr: String,
    // 职位
    val employeePostStr: String,
    // 阳历生日
    val employeeSolarBirthday: String,
    // 背景图
    val employeeAppBackground:String
){

    fun  coverBirthday():String{
        if (employeeBirthdayType == 0){
            return "$employeeLunarBirthday  (农历)"
        }else{
            return "$employeeSolarBirthday  (国历)"
        }
    }

}