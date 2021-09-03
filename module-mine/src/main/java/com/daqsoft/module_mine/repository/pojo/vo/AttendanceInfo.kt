package com.daqsoft.module_mine.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 30/12/2020 下午 4:56
 * @author zp
 * @describe
 */
data class AttendanceInfo(
    // 考勤异常名单
    val exceptionList: List<Staff>,
    // 第一个抵达公司的
    val firstInOrg: Staff?,
    // 请假名单
    val holidayList: List<Staff>,
    // 我的部门最后离开的人
    val lastInOrg: Staff?,
     // 我的到达时间
    val myArriveTime: String,
    // 我的离开时间
    val myLeaveTime: String
)

data class Staff(
    val avatar: String,
    val employeeId: Int,
    val time: String
){

    fun coverTime():String{
        return  if (time.isBlank()) "--" else time
    }

}
