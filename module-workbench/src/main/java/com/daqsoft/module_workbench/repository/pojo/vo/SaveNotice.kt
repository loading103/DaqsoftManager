package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 1/3/2021 上午 11:10
 * @author zp
 * @describe
 */
data class SaveNotice(
    // 详细内容
    var noticeContent : String ? ,
    // 内容概要
    var noticeOutline : String  ,
    // 重要程度	RED("特色紧急"), ORANGE("特别重要"), BLUE("一般重要"), GREEN("普通重要")
    var noticeImportance : String  ,
    // 公告标题
    var noticeTitle : String  ,
    // 公告类型
    var noticeType : Int ?,
    // 通知组织
    var noticeOrganizationList : MutableList<NoticeOrganization>,
    // 保存状态	 ToSubmit("保存为草稿"), ToAudit("保存并提交审核")
    var noticeStatus:String,
    // 文件
    var fileList : MutableList<NoticeFile>,
    // id 仅修改时
    var id :String?

)