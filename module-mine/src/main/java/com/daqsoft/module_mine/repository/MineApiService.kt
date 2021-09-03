package com.daqsoft.module_mine.repository

import cn.jpush.android.api.JPushInterface
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.room.dto.Holiday
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.library_common.pojo.vo.WelcomeMessage
import com.daqsoft.module_mine.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:10
 * @author zp
 * @describe
 */
interface MineApiService {


    /**
     * 登录
     * @param account String
     * @param password String
     * @param unique String
     * @param platform String
     * @return Observable<AppResponse<Any>>
     */
    @FormUrlEncoded
    @POST("author/login")
    fun login(
        @Field("account") account:String,
        @Field("token") password:String,
        @Field("code") code:String?=null,
        @Field("equipmentKey") unique : String = JPushInterface.getRegistrationID(ContextUtils.getContext()),
        @Field("platform") platform : String = "android",
        @Header("XQSESSION") verifySession : String? = null
    ):Observable<AppResponse<Any>>

    /**
     * 退出
     */
    @GET("author/logout")
    fun signOut(
        @Query("equipmentKey") unique : String = JPushInterface.getRegistrationID(ContextUtils.getContext())
    ):Observable<AppResponse<Any>>

    /**
     * 获取验证码
     */
    @GET("author/getVerifyCode")
    fun getVerifyCode(
        @Query("app") app: Boolean = true
    ):Observable<Response<ResponseBody>>

    /**
     * 获取员工信息
     */
    @GET("employee/getEmployeeDetail")
    fun getEmployeeInfo():Observable<AppResponse<EmployeeInfo>>


    /**
     * 获取员工部门组织架构
     */
    @GET("employee/getOrganizationEmployee")
    fun getOrganizationEmployee():Observable<AppResponse<List<EmployeeInfo>>>


    @FormUrlEncoded
    @POST("employee/updateLoginPassword")
    fun modifyLoginPassword(
        @Field("oldLoginPassword") old:String,
        @Field("newLoginPassword") new:String,
        @Field("confirmLoginPassword") confirm:String
    ):Observable<AppResponse<Any>>


    @FormUrlEncoded
    @POST("employee/updateSecondPassword")
    fun modifySecondaryPassword(
        @Field("loginPassword") login:String,
        @Field("newSecondPassword") secondary:String,
        @Field("confirmSecondPassword") confirm:String
    ):Observable<AppResponse<Any>>


    /**
     *  获取节假日
     * @param url String
     * @param date String 指定月份,格式为YYYY-MM,如月份和日期小于10,则取个位,如:2012-1
     * @param key String
     * @return Observable<Holiday>
     */
    @GET
    fun getHolidays (
        @Url url:String = HttpGlobal.JUHE_HOLIDAY,
        @Query("year-month") date : String,
        @Query("key") key  : String = HttpGlobal.JUHE_KEY
    ):Observable<Holiday>


    /**
     * 获取员工部门组织架构
     */
    @GET("idaqCareWords/randomOne")
    fun getWelcomeMessage(
        @Query("app") app:Boolean = true
    ):Observable<AppResponse<WelcomeMessage>>


    /**
     * 获取城市树
     */
    @GET("systemRegion/getRegionTree")
    fun getCityTree():Observable<AppResponse<List<Province>>>

    /**
     * 获取城市下级区域
     */
    @GET("systemRegion/getRegionChildren")
    fun getCityTreeChildren(
        @Query("parentId") pid : String
    ):Observable<AppResponse<List<Region>>>

    /**
     * 获取更新数据
     */
    @GET("employee/getEmployeeToUpdate")
    fun getEmployeeToUpdate(
    ):Observable<AppResponse<EmployeeInfoUpdate>>

    /**
     * 更新员工信息
     */
    @FormUrlEncoded
    @POST("employee/updateEmployee")
    fun updateEmployeeInfo(
        @FieldMap map: Map<String,String>
    ):Observable<AppResponse<Any>>

    /**
     * 上传文件
     */
    @Multipart
    @POST()
    fun uploadOSS(
        @Url uploadUrl:String = HttpGlobal.UPLOAD_URL,
        @Part file : MultipartBody.Part
    ): Observable<AppResponse<List<UploadResult>>>

    /**
     * 更新员工头像
     */
    @FormUrlEncoded
    @POST("employee/updateHeadImg")
    fun updateAvatar(
        @Field("url") url: String
    ):Observable<AppResponse<Any>>


    /**
     * 更新员工背景图
     */
    @GET("idaqInfoForApp/saveEmployeeBackground")
    fun updateBackgroundImage(
        @Query("imageUrl") url: String
    ):Observable<AppResponse<Any>>

    /**
     * 获取日期数据
     */
    @GET("idaqInfoForApp/dayInfo")
    fun getDateInfo():Observable<AppResponse<DateInfo>>

    /**
     * 获取考勤数据
     */
    @GET("idaqInfoForApp/getPersonalHomepageWorkRecordInfo")
    fun getAttendanceInfo(
        @Query("offset") offset:Int
    ):Observable<AppResponse<AttendanceInfo>>

    /**
     * 获取会议数据
     */
    @GET("idaqInfoForApp/meetingInfo")
    fun getMeetingInfo(
        @Query("date") date:String
    ):Observable<AppResponse<List<MeetingInfo>>>

    /**
     * 获取任务数据
     */
    @GET("idaqInfoForApp/getHomepageTaskInfo")
    fun getTaskInfo(
        @Query("date") date:String
    ):Observable<AppResponse<TaskInfo>>


    /**
     * 获取公司信息
     */
    @GET("api/1068/query")
    fun getCompanyInfo(
        @Query("OA_PASS") OA_PASS :String = "oa",
        @Query("OA_UID") OA_UID :String = "1"
    ): Observable<AppResponse<CompanyInfo>>
}