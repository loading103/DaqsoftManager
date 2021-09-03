package com.daqsoft.module_workbench.repository.pojo.vo

class DialyDetailDiscuss : ArrayList<DialyDetailDiscussItem>()

data class DialyDetailDiscussItem(
    val commentId: Int,
    val content: String,
    val createDate: String,
    val employeeAvatar: String,
    val employeeId: Int,
    val employeeName: String
)