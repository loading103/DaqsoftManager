package com.daqsoft.module_workbench.repository.pojo.vo

import android.text.SpannableStringBuilder
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.module_workbench.R
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import java.lang.StringBuilder

/**
 * 关怀词条
 */
data class CareThesausuRoot(
    val current: Int,
    val pages: Int,
    val records: List<CareThesausuBean>,
    val size: Int,
    val total: Int
)
data class CareThesausuBean(
    val addDate: String,
    val careInfo: String,//	关怀词条
    val careUrl: String,//	关怀词条
    var employeeAvatar: String,
    var employeeName: String,
    val enable: Boolean, //是否启用
    val id: Int,
    val posList: List<String>,
    val uploader: String
){
    public fun  getPosString():String{
        if(posList.isEmpty()){
            return ""
        }
        var sb=StringBuilder()
        posList.forEach {
            sb.append("$it·")
        }
        return sb.substring(0,sb.length-1).toString()
    }

    fun coverLabel():String{
        if(posList.isEmpty()){
            return ""
        }
        return posList.joinToString(separator = "  ·  ") { it }
    }

    fun coverTitle(): SpannableStringBuilder {
        val ssb = SimplifySpanBuild()
        if (!enable){
            ssb
                .append(SpecialTextUnit("【").setTextSize(16f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.black_333333)))
                .append(SpecialTextUnit("已禁用").setTextSize(16f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.red_fa4848)))
                .append(SpecialTextUnit("】").setTextSize(16f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.black_333333)))
        }
        val content= SpecialTextUnit(careInfo).setTextSize(16f).setTextColor(ContextUtils.getContext().resources.getColor(R.color.black_333333))
        ssb.append(content)
        return ssb.build()
    }

}
data class CareThesausuNumberBean(
    val totalCount: String,
    val countByMe: String
)


data class CaringWordType(
    val title : String,
    val id: Int?
)

data class CaringWordDetail(
    val careInfo: String,
    val carePosition: String,
    val careUrl: String,
    val enable: Boolean,
    val id: Int,
    val uploader: Int
){

}