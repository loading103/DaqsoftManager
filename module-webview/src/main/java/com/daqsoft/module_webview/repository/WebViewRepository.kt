package com.daqsoft.module_webview.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_webview.repository
 * @date 22/12/2020 下午 2:09
 * @author zp
 * @describe
 */
class WebViewRepository  @Inject constructor(private val webViewApiService:WebViewApiService) : BaseModel(),WebViewApiService {

    override fun exportOvertimeReport(
        orgId: String,
        endDate: String,
        beginDate: String,
        employeeName: String
    ): Observable<Response<ResponseBody>> {
        return webViewApiService.exportOvertimeReport(orgId, endDate, beginDate, employeeName)
    }

    override fun exportAttendanceReport(
        orgId: String,
        state: String,
        endDate: String,
        beginDate: String,
        employeeName: String
    ): Observable<Response<ResponseBody>> {
        return webViewApiService.exportAttendanceReport(orgId, state, endDate, beginDate, employeeName)
    }

    override fun getNextDetail(id: Int): Observable<AppResponse<NextMessage>> {
        return webViewApiService.getNextDetail(id)
    }
}