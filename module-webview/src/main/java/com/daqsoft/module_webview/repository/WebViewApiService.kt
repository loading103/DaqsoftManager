package com.daqsoft.module_webview.repository

import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.mvvmfoundation.http.BaseResponse
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:10
 * @author zp
 * @describe
 */
interface WebViewApiService {

    /**
     * 导出加班报表
     */
    @GET("idaqWorkRecord/exportExtraWorkRecord")
    @Streaming
    fun exportOvertimeReport(
        @Query("orgId") orgId: String ,
        @Query("endDate") endDate: String ,
        @Query("beginDate") beginDate: String ,
        @Query("employeeName") employeeName: String
    ):Observable<Response<ResponseBody>>


    /**
     * 导出考勤报表
     */
    @GET("idaqWorkRecord/exportWorkRecord")
    @Streaming
    fun exportAttendanceReport(
        @Query("orgId") orgId: String ,
        @Query("state") state: String ,
        @Query("endDate") endDate: String ,
        @Query("beginDate") beginDate: String ,
        @Query("employeeName") employeeName: String
    ):Observable<Response<ResponseBody>>


    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun getNextDetail(
        @Query("id") id: Int
    ):Observable<AppResponse<NextMessage>>
}