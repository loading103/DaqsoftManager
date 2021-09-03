package com.daqsoft.library_base.room.dto

/**
 * @package name：com.daqsoft.module_mine.repository.pojo.vo
 * @date 11/12/2020 下午 3:25
 * @author zp
 * @describe
 */
data class Holiday(
    val error_code: Int,
    val reason: String,
    val result: Result
)

data class Result(
    val `data`: Data
)

data class Data(
    val holiday: String,
    val holiday_array: List<HolidayArray>,
    val year: String,
    val `year-month`: String
)

data class HolidayArray(
    val desc: String,
    val festival: String,
    val list: List<Job>,
//    val `list#num#`: Int,
    val list_num: Int,
    val name: String,
    val rest: String
)

data class Job(
    // 格式 2021-1-1
    val date: String,
    // 1:放假,2:上班
    val status: String
)