package com.daqsoft.module_home.repository

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.module_home.repository.pojo.vo.Message
import com.daqsoft.module_home.repository.pojo.vo.NewsBrief
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*

/**
 * @package name：com.daqsoft.module_mine.repository
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
interface HomeApiService {

    /**
     * 获取首页数据
     */
    @GET("idaqInfoForApp/getAllNumbers")
    fun getAllNewsList() : Observable<AppResponse<NewsBrief>>


    /**
     * 全部标为已读 （消息）
     */
    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/updateMessageState")
    fun markAllAsRead(
        @Query("state") state : Int = 0
    ) : Observable<AppResponse<Any>>

    /**
     * 阅读单条数据
     */
    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun readSingle(
        @Query("id") id : String
    ) : Observable<AppResponse<Any>>


    /**
     * 获取消息
     */
    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getList")
    fun getMessage(
        // 0 = 未读，null = 所有消息
        @Query("state") state: Int? = null,
        @Query("pageCurr") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("pageSize") size: Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<Message>>


    /**
     * 置顶
     * @return Observable<AppResponse<List<NewsBrief>>>
     */
    @GET("idaqInfoForApp/topItem")
    fun top(
        @Query("id") id: String,
        @Query("top") top: Boolean
    ) : Observable<AppResponse<Any>>

    /**
     * 更新团队日报阅读时长
     */
    @POST("idaqDayReportTeam/updateReadTime")
    fun saveScanTime (
        @Body body: Map<String, String>
    ): Observable<AppResponse<String>>

    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun getNextDetail(
        @Query("id") id: Int
    ):Observable<AppResponse<NextMessage>>
}