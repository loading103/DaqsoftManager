package com.daqsoft.module_home.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_home.repository.pojo.vo
 * @date 18/1/2021 下午 4:11
 * @author zp
 * @describe
 */data class NewsBrief(
    val current: Int,
    val pages: Int,
    val records: List<NewsBriefInfo>,
    val size: Int,
    val total: Int
)
data class NewsBriefInfo(
    /**
     * 数量
     */
    val count: Int,
    /**
     * 员工id
     */
    val employeeId: Int,
    /**
     * 额外参数
     */
    val ext: String,
    /**
     * id
     */
    val id: Int,
    /**
     * 最新 信息简要
     */
    val lastInfo: String,
    /**
     * 最新 信息时间
     */
    val msgTime: String,
    /**
     * 标题
     */
    val title: String,
    /**
     * 是否置顶
     */
    val top: Boolean,
    /**
     * 类型
     */
    val type: String,


    /**
     * 自定义属性 用于排序
     */
    var millisecond:Long? = null
)