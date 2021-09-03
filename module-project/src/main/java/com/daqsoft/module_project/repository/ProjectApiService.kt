package com.daqsoft.module_project.repository

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.module_project.repository.pojo.vo.*
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

/**
 * @package name：com.daqsoft.module_project.repository
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
interface ProjectApiService {

    /**
     * 单文件上传
     */
    @Multipart
    @POST()
    fun uploadOSS(
        @Url uploadUrl:String = HttpGlobal.UPLOAD_URL,
        @Part file : MultipartBody.Part
    ): Observable<AppResponse<List<UploadResult>>>


    /**
     * 多文件上传
     */
    @Multipart
    @POST()
    fun uploadOSSMulti(
        @Url uploadUrl:String = HttpGlobal.UPLOAD_URL,
        @PartMap params : Map<String, @JvmSuppressWildcards RequestBody>
    ): Observable<AppResponse<List<UploadResult>>>

    /**
     * 多文件上传
     */
    @Multipart
    @POST()
    fun uploadOSSMulti(
        @Url uploadUrl:String = HttpGlobal.UPLOAD_URL,
        @Part parts: List<MultipartBody.Part>
    ): Observable<AppResponse<List<UploadResult>>>


    /**
     * 获取项目类型
     */
    @GET("idaqProjectType/getAllType")
    fun getProjectType(): Observable<AppResponse<List<ProjectType>>>


    /**
     * 获取项目阶段统计
     */
    @GET("idaqProject/getProjectStageTable")
    fun getProjectStageTable(
        @Query("menuId") menuId : Int? =1062
    ): Observable<AppResponse<List<ProjectType>>>

    /**
     * 获取项目流程
     */
    @GET("idaqProjectProcess/getProcessList")
    fun getProjectFlow(): Observable<AppResponse<List<ProjectFlow>>>

    /**
     * 获取项目类型
     */
    @GET("idaqProject/getProjectListHeadInfoForApp")
    fun getProjectHeadInfor(): Observable<AppResponse<ProjectHeadBean>>
    /**
     * 获取项目列表
     */
    @GET("idaqProject/getProject")
    fun getProjectListData(
        @Query("projectName") projectName : String ?=null,
        @Query("orderType") orderType : String ?="DESC",
        @Query("orderBy") orderBy : String ?= "focus",
        @Query("pageSize") pageSize : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("pageCurr") pageCurr : Int = ConstantGlobal.INITIAL_PAGE,
        @Query("projectType") projectType : Int ?=null,
        @Query("stage") stage : Int ?=null,
        @Query("menuId") menuId : Int = 1062,
        @Query("projectGrade") projectGrade : Int ?=null,
        @Query("onlyCares") onlyCares : Boolean?= false,
        @Query("state") state : Int?= null

    ): Observable<AppResponse<ProjectListBean>>


    @GET("idaqProject/getSimpleDetail")
    fun getProjectSingleData(
        @Query("projectId") projectId : Int ?=null
    ): Observable<AppResponse<ProjectData>>

    /**
     * 关注/取消关注项目
     */
    @GET("api/1062/focus")
    fun followProject(
        @Query("projectId") projectId : Int ?=null
    ): Observable<AppResponse<String>>


    //获取项目详情页数据
    @GET("api/1062/projectdetail")
    fun getProjectDetailRequest(@Query("projectId") projectId : Int = 0) : Observable<AppResponse<ProjectDetailBean>>

    //获取项目详情建设内容
    @GET("idaqProductInProject/functionMain")
    fun getProjecBuildContentRequest(@Query("projectId") projectId : Int = 0) : Observable<AppResponse<ProjectBuildContent>>

    /**
     * 获取项目动态
     */
    @GET("idaqProjectNotebook/getProjectNoteList")
    fun  getProjectDynamics(
        @Query("itrState") itrState : Boolean?=false,
        @Query("userId") userId : String?=null,
        @Query("endTime") endTime : String?=null,
        @Query("startTime") startTime : String?=null,
        @Query("booking") booking : Boolean?=false,
        @Query("tag") tag	 : List<String>?=null,
        @Query("projectId") projectId	 : String,
        @Query("pageCurr") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<ProjectDynamicPaging>>


    // 项目动态 item
    @GET("idaqProjectNotebook/getNoteById")
    fun getProjectDynamicsItem(
        @Query("noteId") noteId : String
    ) : Observable<AppResponse<ProjectDynamic>>

    // 项目动态 打分
    @GET("idaqProjectNotebook/score")
    fun projectDynamicsScore(
        @Query("noteId") noteId : Int,
        @Query("score") score : Float
    ) : Observable<AppResponse<Any>>

    // 项目动态 归档
    @GET("idaqProjectNotebook/classify")
    fun projectDynamicsPigeonhole(
        @Query("noteId") noteId : Int
    ) : Observable<AppResponse<Any>>

    // 项目动态 取消归档
    @GET("idaqProjectNotebook/cancelClassify")
    fun cancelProjectDynamicsPigeonhole(
        @Query("noteId") noteId : Int
    ) : Observable<AppResponse<Any>>

    // 项目动态 关闭 itr
    @GET("idaqProjectNotebook/closeItr")
    fun closeItr(
        @Query("noteId") noteId : String
    ) : Observable<AppResponse<Any>>

    // 项目标签
    @GET("idaqNoteTag/getProjectCurrentTags")
    fun getProjectLabel(
        @Query("projectId") projectId : String
    ) : Observable<AppResponse<List<ProjectLabel>>>

    //获取费用类型
    @GET("idaqDicItems/getDicItems")
    fun getAccountType(@Query("groupCode") groupCode : String = "cost_type") : Observable<AppResponse<List<MoneyTypeBean>>>

    /**
     * 保存项目动态
     * @param body Body
     * @return Observable<AppResponse<Any>>
     */
    @POST("idaqProjectNotebook/save")
    fun  saveProjectDynamics(
        @Body body: ProjectDynamicRequest
    ):Observable<AppResponse<Any>>

    // 项目标签
    @GET("idaqProjectBasicInfo/getProjectBasicInfo")
    fun getProjectBaseInfor(
        @Query("projectId") projectId : String
    ) : Observable<AppResponse<ProjectBaseInfor>>

    // 项目标签
    @GET("idaqProject/getBaseDetail")
    fun getProjectBaseInfors(
        @Query("projectId") projectId : String
    ) : Observable<AppResponse<ProjectBaseInfors>>

    /**
     * 获取 可用菜单
     */
    @GET("nav/getMenus")
    fun getMenus(
        @Query("app") app : Boolean = true
    ): Observable<AppResponse<Menu>>

    /**
     * 获取 业主列表
     */
    @GET("idaqCustomer/simpleList")
    fun getOwnerList(
        @Query("key") key : String? = null
    ): Observable<AppResponse<List<ProjectOwnerBean>>>

    /**
     * 获取 合作伙伴列表
     */
    @GET("idaqPartner/simpleList")
    fun getPartnerList(
        @Query("key") key : String? = null
    ): Observable<AppResponse<List<ProjectOwnerBean>>>

    /**
     * 提交项目
     */
    @POST("idaqProject/saveData")
    fun SaveNewProject(
        @QueryMap param: HashMap<String, String>
    ): Observable<AppResponse<Any>>

    /**
     * 提交项目
     */
    @GET("idaqProductShow/simpleProductDetail")
    fun getProductDetail(
        @Query("productId") productId : Int? = null
    ): Observable<AppResponse<ProductBean>>


    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun getNextDetail(
        @Query("id") id: Int
    ):Observable<AppResponse<NextMessage>>
}