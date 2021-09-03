package com.daqsoft.module_main.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.module_main.repository.pojo.vo.Pending
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_main.repository
 * @date 5/11/2020 下午 5:25
 * @author zp
 * @describe
 */
class MainRepository @Inject constructor(private val mainApiService: MainApiService) : BaseModel(),MainApiService {
    override fun getEmployeeInfo(): Observable<AppResponse<EmployeeInfo>> {
        return mainApiService.getEmployeeInfo()
    }

    override fun getPendingTotal(): Observable<AppResponse<Pending>> {
        return mainApiService.getPendingTotal()
    }

    override fun checkUpdate(
        url: String,
        appId: String,
        method: String,
        token: String,
        appType: String,
        version: String
    ): Observable<Response<ResponseBody>> {
        return mainApiService.checkUpdate(url, appId, method, token, appType, version)
    }


}