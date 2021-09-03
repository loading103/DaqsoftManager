package com.daqsoft.module_mine.repository.pojo.vo

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 30/12/2020 下午 1:48
 * @author zp
 * @describe
 */
@Parcelize
data class DateInfo(
    val today: Day,
    val tomorrow: Day
) : Parcelable

@Parcelize
data class Day(
    val date: String,
    val dateForParam: String,
    val dayInfo: String,
    val dayOfWeek: String,
    val restDay: RestDay?
) : Parcelable

@Parcelize
data class RestDay(
    val careWord: List<CareWord>?,
    val slogan: String?
) : Parcelable{

    /**
     * 转换 关怀语
     * @return String
     */
    fun coverCareWord():String{
        return careWord?.joinToString(separator = "    ") { it.careInfo }?:""
    }

}


@Parcelize
data class CareWord(
    val careInfo: String,
    val careUrl: String,
    val id: Int
) : Parcelable