package com.daqsoft.module_workbench.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.lang.StringBuilder

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.vo
 * @date 15/3/2021 下午 3:51
 * @author zp
 * @describe
 */
data class DeptFile(
    val files: List<DeptFileInfo>,
    val folders: List<DeptFolderInfo>,
    val fullName: String,
    val oName: String,
    val oid: Int
)

data class ProDeptFile(
    val datas: List<DeptFileInfo>,
    val data: String
)

/**
 * 	"fileName":"企业微信截图_16075659502537.png",
"fileSize":"430.08 Kb",
"downloadTimes":0,
"fileUrl":"http://10.252.252.43:8081/file/oss/n5GcuczM1IDM1kTN2UzNwYTMf57mlrKimH6vk7qvlrJukHIvkf1UD9VRMlkRwQjM0UTOwMTM0UDN4MzNzQzMx8SOy0iMx0CMyAjM",
"id":308,
"uploadTime":"2020-12-29 09:59:49",
"viewTimes":0,
"userName":"测试小白",
"fileType":".png"
 */

data class DeptFileRoot(
    // 上传员工名称
    var data: Any,
    var datas: MutableList<DeptFileInfo>,
    var extraInfo: Any,
    var orderBy: String,
    var orderType: String,
    var pageCurr: Int,
    var pageSize: Int,
    var total: Int,
    var totalPages: Int
)

data class DeptFileInfo(
    // 上传员工名称
    var employeeName: String,

    // 下载次数
    var fileDowns: Int,
    // 查看次数
    val viewTimes: Int,
    // 文件名称
    val fileName: String,
    // 文件大小
    val fileSize: String,
    // 文件类型
    val fileType: String,
    // 文件地址
    val fileUrl: String,
    // 预览次数
    val fileViews: Int,
    val id: Int,
    // 上传时间
    var uploadDate: String,
    // 上传时间
    val uploadTime: String,

    // 下载次数
    val downloadTimes: Int,

    val downUrl: String?=null,

    val previewUrl: String?=null,

    val userName: String


){

    /**
     * 转换数量
     * @return String
     */
    fun coverCount():String{
        return "$fileSize  ·  ${fileDowns}次下载  ·  ${fileViews}次阅读"
    }

    /**
     * 转换作者
     * @return String
     */
    fun coverAuthor():String{
        val sb = StringBuilder()
        employeeName?.takeIf { it.isNotBlank() }?.apply {
            sb.append(this)
        }
        uploadDate?.takeIf { it.isNotBlank() }?.apply {
            sb.takeIf { it.isNotEmpty() }?.apply {
                this.append("  |  ")
            }
            sb.append(this)
        }
        return sb.toString()
    }



}

@Parcelize
data class DeptFolderInfo(
    // 文件数
    val fileCount: Int,
    // 文件夹数
    val folderCount: Int,
    // 文件夹名字
    val folderName: String,
    val id: Int
) : Parcelable {

    /**
     * 转换数量
     * @return String
     */
    fun coverCount():String{
        return "${folderCount}个文件夹  ·  ${fileCount}个文件"
    }

}

@Parcelize
data class DeptTypess(
    var data: List<DeptType>
) : Parcelable{
}


/**
 * 	"fileName":"企业微信截图_16075659502537.png",
"fileSize":"430.08 Kb",
"downloadTimes":0,
"fileUrl":"http://10.252.252.43:8081/file/oss/n5GcuczM1IDM1kTN2UzNwYTMf57mlrKimH6vk7qvlrJukHIvkf1UD9VRMlkRwQjM0UTOwMTM0UDN4MzNzQzMx8SOy0iMx0CMyAjM",
"id":308,
"uploadTime":"2020-12-29 09:59:49",
"viewTimes":0,
"userName":"测试小白",
"fileType":".png"
 */
@Parcelize
data class DeptType(
    // 文件数
    val fileCnt: Int?=null,
    // 文件夹数
    val baseParentId: Int?=null,
    // 文件夹名字
    val projectId: String?=null,
    var baseName: String?=null,
    var baseType: String?=null,
    var totalFile: Int=0,
    var totalFolder: Int=0,
    var isVisible: Boolean?=false,   //次级列表有没有 现实不显示
    var havechoose: Boolean?=false,
    var havechoosehave: Boolean?=false,//有次级列表 点击父类显示勾勾
    var childs: List<DeptType>?= ArrayList<DeptType>(),
    val id: Int?=null
) : Parcelable{
}