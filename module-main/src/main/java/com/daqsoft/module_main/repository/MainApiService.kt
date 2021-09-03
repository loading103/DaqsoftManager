package com.daqsoft.module_main.repository

import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.module_main.repository.pojo.vo.Pending
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * @package name：com.daqsoft.module_main.repository
 * @date 5/11/2020 下午 5:27
 * @author zp
 * @describe
 */
interface MainApiService {

    /**
     * 获取员工信息
     */
    @GET("employee/getEmployeeDetail")
    fun getEmployeeInfo(): Observable<AppResponse<EmployeeInfo>>


    /**
     * 获取未处理 信息
     */
    @GET("idaqInfoForApp/getCountForMobile")
    fun getPendingTotal() : Observable<AppResponse<Pending>>


    /**
     * 检查更新
     */
    @GET
    fun checkUpdate(
        @Url url: String = HttpGlobal.Update.URL,
        @Query("AppId") appId: String = HttpGlobal.Update.APP_ID,
        @Query("Method") method: String = HttpGlobal.Update.METHOD,
        @Query("token") token: String = HttpGlobal.Update.TOKEN,
        @Query("AppType") appType: String = HttpGlobal.Update.APP_TYPE,
        @Query("VersionCode") version: String = AppUtils.getVersionName()
    ) : Observable<Response<ResponseBody>>
}