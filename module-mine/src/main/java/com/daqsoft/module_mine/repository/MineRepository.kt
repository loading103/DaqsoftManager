package com.daqsoft.module_mine.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.room.dto.Holiday
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.library_common.pojo.vo.WelcomeMessage
import com.daqsoft.module_mine.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody

import okhttp3.ResponseBody
import retrofit2.Response
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:09
 * @author zp
 * @describe
 */
class MineRepository @Inject constructor(private val mineApiService:MineApiService) : BaseModel(),MineApiService {
    override fun login(
        account: String,
        password: String,
        code: String?,
        unique: String,
        platform: String,
        verifySession: String?
    ): Observable<AppResponse<Any>> {
        return mineApiService.login(account, password, code, unique, platform, verifySession)
    }


    override fun signOut(unique : String): Observable<AppResponse<Any>> {
        return mineApiService.signOut()
    }

    override fun getVerifyCode(app: Boolean): Observable<Response<ResponseBody>> {
        return mineApiService.getVerifyCode()
    }

    override fun getEmployeeInfo(): Observable<AppResponse<EmployeeInfo>> {
        return mineApiService.getEmployeeInfo()
    }

    override fun getOrganizationEmployee(): Observable<AppResponse<List<EmployeeInfo>>> {
        return mineApiService.getOrganizationEmployee()
    }

    override fun modifyLoginPassword(
        old: String,
        new: String,
        confirm: String
    ): Observable<AppResponse<Any>> {
        return mineApiService.modifyLoginPassword(old, new, confirm)
    }

    override fun modifySecondaryPassword(
        login: String,
        secondary: String,
        confirm: String
    ): Observable<AppResponse<Any>> {
        return mineApiService.modifySecondaryPassword(login, secondary, confirm)
    }

    override fun getHolidays(url: String, date: String, key: String): Observable<Holiday> {
        return mineApiService.getHolidays(url, date, key)
    }

    override fun getWelcomeMessage(app:Boolean): Observable<AppResponse<WelcomeMessage>> {
        return mineApiService.getWelcomeMessage(app)
    }

    override fun getCityTree(): Observable<AppResponse<List<Province>>> {
        return mineApiService.getCityTree()
    }

    override fun getCityTreeChildren(pid: String): Observable<AppResponse<List<Region>>> {
        return mineApiService.getCityTreeChildren(pid)
    }

    override fun getEmployeeToUpdate(): Observable<AppResponse<EmployeeInfoUpdate>> {
        return mineApiService.getEmployeeToUpdate()
    }

    override fun updateEmployeeInfo(map: Map<String,String>): Observable<AppResponse<Any>> {
        return mineApiService.updateEmployeeInfo(map)
    }

    override fun uploadOSS(
        uploadUrl: String,
        file: MultipartBody.Part
    ): Observable<AppResponse<List<UploadResult>>> {
        return mineApiService.uploadOSS(uploadUrl, file)
    }

    override fun updateAvatar(url:String): Observable<AppResponse<Any>> {
        return mineApiService.updateAvatar(url)
    }

    override fun updateBackgroundImage(url: String): Observable<AppResponse<Any>> {
        return mineApiService.updateBackgroundImage(url)
    }

    override fun getDateInfo(): Observable<AppResponse<DateInfo>> {
        return mineApiService.getDateInfo()
    }

    override fun getAttendanceInfo(offset: Int): Observable<AppResponse<AttendanceInfo>> {
        return mineApiService.getAttendanceInfo(offset)
    }

    override fun getMeetingInfo(date: String): Observable<AppResponse<List<MeetingInfo>>> {
        return mineApiService.getMeetingInfo(date)
    }

    override fun getTaskInfo(date: String): Observable<AppResponse<TaskInfo>> {
        return mineApiService.getTaskInfo(date)
    }

    override fun getCompanyInfo(
        OA_PASS: String,
        OA_UID: String
    ): Observable<AppResponse<CompanyInfo>> {
        return mineApiService.getCompanyInfo()
    }
}