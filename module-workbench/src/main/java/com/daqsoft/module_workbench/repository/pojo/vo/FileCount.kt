package com.daqsoft.module_workbench.repository.pojo.vo

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 10/3/2021 上午 9:49
 * @author zp
 * @describe
 */
data class FileCount(
    val orgName : String,
    val fileCount : Int,
    val folderCount : Int,
    val orgId : Int

) {
}


data class FileInfoWrapper(
    val `data`: Any,
    val datas: List<FileInfo>,
    val extraInfo: Any,
    val orderBy: String,
    val orderType: String,
    val pageCurr: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int
){

}

data class FileInfo(
    val fileDowns: Int,
    val fileName: String,
    val fileShare: Int,
    val fileSize: String,
    val fileUrl: String,
    val fileViews: Int,
    val id: Int,
    val loginUser: Int,
    val shareInfo: String,
    val uid: Int,
    val uploadDate: String,
    val userName: String
){

}