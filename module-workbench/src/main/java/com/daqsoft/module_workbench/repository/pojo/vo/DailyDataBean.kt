package com.daqsoft.module_workbench.repository.pojo.vo

data class DailyDataBean(
    var unreported: String, //未提交数量
    var reported: String,//	已提交数量
    var proportion: String, //提交百分比
    var isShowDate: Boolean?=false, //是否显示提交率
    var dates: MutableList<DailyDayBean>
){

    fun getDesc():String{
        if(proportion?.toDouble()>=100.00){
            return "本月提交情况完美！"
        }
        if(proportion?.toDouble()>=80.00){
            return "本月提交情况良好，继续加油！"
        }
        if(proportion?.toDouble()<80.00){
            return "本月提交情况不理想，努力空间巨大！"
        }
        return "本月提交情况良好，继续加油！"
    }
}
data class DailyDayBean(
    var isWeekend: Boolean?=false, //是否周末
    var isReported: Boolean?=false,//	是否已提交
    var date: String
)




data class DailyTeamDataBeans(
    var unreported: String, //未提交数量
    var reported: String,//	已提交数量
    var proportion: String, //提交百分比
    var isShowDate: Boolean?=false, //是否显示提交率
    var dates: MutableList<DailyTeamDataBean>
){
    fun getDesc():String{
        if(proportion?.toDouble()>=100.00){
            return "本月提交情况完美！"
        }
        if(proportion?.toDouble()>=80.00){
            return "本月提交情况良好，继续加油！"
        }
        if(proportion?.toDouble()<80.00){
            return "本月提交情况不理想，努力空间巨大！"
        }
        return "本月提交情况良好，继续加油！"
    }
}


data class DailyTeamDataBean(
    val date: String,
    val isReported: Boolean?=false,
    val isWeekend: Boolean?=false,
    var proportion: String,
    val reported: String?=null,
    val unreported: String?=null,
    val unreportedEmployees: List<UnreportedEmployee>
){
    fun getDesc():String{
        if(proportion?.toDouble()>=100.00){
            return "本月提交情况完美！"
        }
        if(proportion?.toDouble()>=80.00){
            return "本月提交情况良好，继续加油！"
        }
        if(proportion?.toDouble()<80.00){
            return "本月提交情况不理想，努力空间巨大！"
        }
        return "本月提交情况良好，继续加油！"
    }
}

data class UnreportedEmployee(
    val employeeAvatar: String,
    val employeeId: Int,
    val employeeIsBirthday: Any,
    val employeeName: String,
    val employeeOrganizationStr: String,
    val employeePostStr: String
)