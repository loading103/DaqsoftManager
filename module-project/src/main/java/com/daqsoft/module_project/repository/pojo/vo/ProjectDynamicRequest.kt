package com.daqsoft.module_project.repository.pojo.vo

import com.daqsoft.library_common.pojo.vo.UploadResult

/**
 * @package name：com.daqsoft.module_project.repository.pojo.vo
 * @date 13/4/2021 上午 11:51
 * @author zp
 * @describe
 */
data class ProjectDynamicRequest (
    // 保存文件
    var files : String?,
    // 记账日期
    var bookingDate : String?,
    // 动态内容
    var noteInfo : String?,
    // 是否为itr
    var itrState : Boolean?,
    // 记账
    var bookkeepingDetails : List<Bookkeeping>?,
    // 项目ID
    var projectId:String?,
    // @人id
    var alertId : String?,
    // 标签id
    var tagIds : List<Int>?,
    // 回复id
    var replyId	 : String?,
    // 新增标签
    var newTags	 : List<String>?
)

data class Bookkeeping(
    var costType:String,
    var money:String,
    var remark:String
)