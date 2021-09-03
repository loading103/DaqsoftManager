package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 4/1/2021 下午 1:33
 * @author zp
 * @describe
 */
data class Participate(
    // 业主名称
    val customerName: String,
    // 项目简称
    val simpleName: String,
    // 重要等级，（1 - 5）数字越小等级越高
    val projectGrade: Int
)