package com.daqsoft.module_workbench.repository.pojo.bo

import com.daqsoft.module_workbench.R

/**
 * @package name：com.daqsoft.module_workbench.repository.pojo.bo
 * @date 8/12/2020 上午 11:21
 * @author zp
 * @describe
 */
/**
 *
 * @property color String  对应通知公告里的 颜色
 * @property level Int 对应任务重要程度
 * @property icon Int 对应资源
 * @constructor
 */
enum class Importance(var color:String, var level : Int, var icon:Int,var content:String,var announcement : String) {

    RED("RED",0, R.mipmap.ggxq_qz_wzyd,"五重一大","特别紧急"),
    ORANGE("ORANGE", 1,R.mipmap.ggxq_qz_zd,"重大","特别重要"),
    BLUE("BLUE",2, R.mipmap.ggxq_qz_jr,"一般","一般重要"),
    GREEN("GREEN", 3,R.mipmap.ggxq_qz_pt,"普通","普通重要"),
//    PURPLE("PURPLE",4, R.mipmap.ggxq_qz_yb,"较弱"),

}