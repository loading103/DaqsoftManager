package com.daqsoft.module_workbench.repository

import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.*
import com.daqsoft.module_workbench.repository.pojo.vo.*
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*
import java.util.*

/**
 * @ClassName    WorkBenchApiService
 * @Description
 * @Author       yuxc
 * @CreateDate   2020/11/17
 */
interface WorkBenchApiService {



    /**
     * 获取 可用菜单
     */
    @GET("nav/getMenus")
    fun getMenus(
        @Query("app") app : Boolean = true
    ): Observable<AppResponse<Menu>>



    /**
     * 获取次级菜单
     */
    @GET("nav/getMenus")
    fun getSecondMenus(
        @Query("menuId") menuId : Int,
        @Query("app") app : Boolean = true
    ): Observable<AppResponse<List<MenuInfo>>>



    /**
     * 获取菜单权限
     */
    @GET("nav/getMenuOperates")
    fun getMenuPermission (
        @Query("menuId") menuId : Int,
        @Query("app") app : Boolean = true
    ): Observable<AppResponse<List<MenuPermission>>>

    /**
     * 更新团队日报阅读时长
     */
    @POST("idaqDayReportTeam/updateReadTime")
    fun saveScanTime (
        @Body body: Map<String, String>
    ): Observable<AppResponse<String>>


    /**
     * 获取组织架构
     */
    @GET("idaqTask/getPhoneBook")
    fun getOrganization(): Observable<AppResponse<Organization>>

    /**
     * 获取 组织成员
     */
    @GET("companyOrganization/getOrganizationWithEmployee")
    fun getMember(
        @Query("parentId") pid : Int
    ): Observable<AppResponse<Member>>

    /**
     * 获取公司信息
     */
    @GET("api/1068/query")
    fun getCompanyInfo(
        @Query("OA_PASS") OA_PASS :String = "oa",
        @Query("OA_UID") OA_UID :String = "1"
    ): Observable<AppResponse<CompanyInfo>>


    /**
     * 获取公司信息
     */
    @GET("invoice/query")
    fun getInvoiceInfo(): Observable<AppResponse<InvoiceInfo>>

    /**
     * 获取公司人数
     */
    @GET("employeeInformation/getCompanyEmployeeNumber")
    fun getCompanyNumberOfEmployees(): Observable<AppResponse<Int>>


    /**
     * 获取通知公告
     */
    @GET("notice/getOrganizationNoticeList")
    fun getAnnouncement(
        @Query("title") title :String? = null,
        @Query("type") type :String? = null,
        @Query("partnerName") partnerName :String? = null,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("current") current :Int = ConstantGlobal.INITIAL_PAGE
    ): Observable<AppResponse<Announcement>>

    /**
     * 获取通知公告详情
     */
    @GET("notice/getDetail")
    fun getAnnouncementDetail(
        @Query("id") id :String
    ): Observable<AppResponse<AnnouncementDetail>>

    /**
     * 通知公告点赞
     */
    @FormUrlEncoded
    @POST("notice/setLike")
    fun announcementLike(
        @Field("like") like :Boolean,
        @Field("noticeId") noticeId :String
    ): Observable<AppResponse<Any>>


    /**
     * 通知公告评论
     */
    @POST("idaqNoticeComment/saveNoticeComment")
    fun sendAnnouncementComment(@Body body: Map<String, String>): Observable<AppResponse<Any>>


    /**
     * 全部标为已读 （通知公告）
     */
    @GET("notice/readAllNotice")
    fun markAllAsRead() : Observable<AppResponse<Any>>

    /**
     * 验证二级密码
     */
    @FormUrlEncoded
    @POST("employee/verifySecondPassword")
    fun verifySecondPassword(
        @Field("secondPassword") secondPassword:String,
        @Field("type") type:String
    ):Observable<AppResponse<Any>>


    /**
     * 获取当前用户工资条月份列表
     */
    @GET("wageEmployee/getEmployeeWageMonthList")
    fun getPaySlipList(
        @Query("secret") secret :String
    ): Observable<AppResponse<List<PaySlip>>>

    /**
     * 获取当前用户某一月份工资
     */
    @GET("wageEmployee/getEmployeeWage")
    fun getPaySlipDetail(
        @Query("wageCompanyId") id :String,
        @Query("secret") secret :String
    ): Observable<AppResponse<PaySlipDetail>>


    /**
     * 员工搜索
     */
    @GET("employeeInformation/list")
    fun searchEmployees(
        @Query("name") name : String?=null,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("current") current :Int = ConstantGlobal.INITIAL_PAGE
    ): Observable<AppResponse<EmployeeSearch>>

    /**
     * 获取员工共信息
     */
    @GET("idaqInfoForApp/getUserHeadInfo")
    fun getEmployeeInfo(
        @Query("employeeId") id :String
    ): Observable<AppResponse<EmployeeDetail>>


    /**
     * 获取未完成得任务
     */
    @FormUrlEncoded
    @POST("api/1064/mytask")
    fun getUpcomingTasks(
        @Field("state") state :Int = 1,
        @Field("employeeId") id:String,
        @Field("pageCurr") page : Int = ConstantGlobal.INITIAL_PAGE,
        @Field("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<Upcoming>>

    /**
     * 获取参与项目
     */
    @GET("idaqInfoForApp/getUserProjectInfo")
    fun getParticipatingProjects(
        @Query("employeeId") id :String
    ): Observable<AppResponse<List<Participate>>>


    /**
     * 获取参与项目 总数
     */
    @GET("idaqInfoForApp/getUserProjectCount")
    fun getParticipatingProjectsTotal(
        @Query("employeeId") id :String
    ): Observable<AppResponse<Any>>


    /**
     * 更新任务状态
     * @param status Int 0-未开始 1-进行中 2- 暂停 3-完成
     * @param taskId Int
     * @return Observable<AppResponse<List<Participate>>>
     */
    @FormUrlEncoded
    @POST("api/1064/updateState")
    fun updateTaskStatus(
        @Field("state") status : Int,
        @Field("taskId") taskId : Int
    ): Observable<AppResponse<Any>>

    /**
     * 已阅
     * @param taskId Int
     * @return Observable<AppResponse<List<Participate>>>
     */
    @FormUrlEncoded
    @POST("idaqTask/updateFocusTask")
    fun read(
        @Field("taskId") taskId : Int
    ): Observable<AppResponse<Any>>

    /**
     * 阅读单条数据
     */
    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun readSingle(
        @Query("id") id : String
    ) : Observable<AppResponse<Any>>

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
        @PartMap  params : Map<String, @JvmSuppressWildcards RequestBody>
    ): Observable<AppResponse<List<UploadResult>>>

    @Multipart
    @POST()
    fun uploadOSSMultis(
        @Url uploadUrl:String = HttpGlobal.UPLOAD_URL,
        @Part parts: List<MultipartBody.Part>
    ): Observable<AppResponse<List<UploadResult>>>


    /**
     * 打卡
     * @param id String
     * @return Observable<AppResponse<Any>>
     */
    @GET("")
    fun clockIn() : Observable<AppResponse<Any>>


    /**
     * 获取关怀词条
     */
    @GET("idaqCareWords/randomOne")
    fun getWelcomeMessage(
        @Query("app") app:Boolean = true
    ):Observable<AppResponse<WelcomeMessage>>

    /**
     * 获取关怀词条列表数据
     */
    @GET("idaqCareWords/getListForApp")
    fun getCareListData(
        @Query("title") title : String ? = null,
        @Query("position") position : Int? = null,
        @Query("page") page : Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<CareThesausuRoot>>

    /**
     * 获取关怀词词条数目
     */
    @GET("idaqCareWords/getStatics")
    fun getCareListNumber(
        @Query("position") position : Int?
    ): Observable<AppResponse<CareThesausuNumberBean>>


    /**
     * 获取关怀词条显示位置
     */
//    @FormUrlEncoded
//    @POST("api/1093/list")
//    fun getShowPlace(
//        @Field("pageCurr") pageCurr : Int = ConstantGlobal.INITIAL_PAGE,
//        @Field("pageSize") pageSize : Int = ConstantGlobal.INITIAL_PAGE_SIZE
//    ): Observable<AppResponse<List<CareThesaususPlaceBean>>>
    @GET("idaqCareWords/careWordPositionList")
    fun getShowPlace(): Observable<AppResponse<List<CareThesaususPlaceBean>>>

    /**
     * 保存关怀词条(修改时传Id)
     */
    @FormUrlEncoded
    @POST("api/1093/add")
    fun saveqCareWord(
        @Field("carePosition") carePosition:String,
        @Field("careUrl") careUrl:String,
        @Field("careInfo") careInfo:String,
        @Field("id") id:String?
    ):Observable<AppResponse<Any>>


    /**
     * 获取 公告数量
     */
    @GET("notice/getNoticeCountForApp")
    fun getNoticeCount(): Observable<AppResponse<NoticeCount>>


    /**
     * 获取 公告类型
     */
    @GET("noticeType/listType")
    fun getNoticeType(): Observable<AppResponse<List<NoticeType>>>


    /**
     * 获取 公告列表
     */
    @GET("notice/list")
    fun getNoticeList(
        @Query("current") page : Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("title") title : String? = null,
        @Query("type") type : Int? = null,
        @Query("startTime") startTime : Long ? = null,
        @Query("endTime") endTime : Long ? = null,
        @Query("noticeStatus") noticeStatus : Int ? = null
    ): Observable<AppResponse<Notice>>



    /**
     * 提交公告审核
     */
    @FormUrlEncoded
    @POST("notice/updateNoticeStatus")
    fun submitNoticeReview(
        @Field("id") id:String,
        @Field("status") status:String = "ToAudit"
    ):Observable<AppResponse<Any>>


    /**
     * 保存公告
     */
    @POST("notice/save")
    fun saveNotice(
        @Body body: SaveNotice
    ):Observable<AppResponse<Any>>


    /**
     * 获取详情
     * @param id String
     * @param richText Boolean app端是否需要富文本内容（详情页 - true,编辑页 -false）有些旧版本数据app不可编辑，需要判断一下
     * @return Observable<AppResponse<List<NoticeType>>>
     */
    @GET("notice/query")
    fun getNoticeDetail(
        @Query("id") id : String,
        @Query("text") richText : Boolean
    ): Observable<AppResponse<NoticeDetail>>


    /**
     * 获取所有审核状态
     * @return Observable<AppResponse<NoticeDetail>>
     */
    @GET("notice/getAllAuditStatus")
    fun getAllAuditStatus(): Observable<AppResponse<List<NoticeAuditStatus>>>


    /**
     * 撤回通知
     * @param id String
     * @return Observable<AppResponse<Any>>
     */
    @FormUrlEncoded
    @POST("notice/recall")
    fun withdrawNotice(
        @Field("id") id : String
    ): Observable<AppResponse<Any>>



    /**
     * 更新公告
     */
    @POST("notice/update")
    fun updateNotice(
        @Body body: SaveNotice
    ):Observable<AppResponse<Any>>

    /**
     * 审批
     */
    @FormUrlEncoded
    @POST("noticeAudit/audit")
    fun approveNotice(
        @Field("auditResult") result : Boolean,
        @Field("auditNote") remark : String,
        @Field("noticeId") id : String
    ):Observable<AppResponse<Any>>


    /**
     * 获取组织
     * @param parentId String
     * @return Observable<AppResponse<List<OrganizationChildren>>>
     */
    @GET("companyOrganization/getOrganizationChildren")
    fun getOrganizationChildren(
        @Query("parentId") parentId : Int
    ): Observable<AppResponse<List<OrganizationChildren>>>


    /**
     * 获取部门下所有员工
     */
    @GET("companyOrganization/getOrgEmployees")
    fun getOrgAllEmployees(
        @Query("organizationId") organizationId : Int
    ): Observable<AppResponse<List<Employee>>>


    /**
     * 获取部门下所有部门
     */
    @GET("companyOrganization/getOrgAllOrgs")
    fun getOrgAllOrg(
        @Query("organizationId") organizationId : Int
    ): Observable<AppResponse<List<OrgSimple>>>


    /**
     * 获取文件数量
     *
     * 0 - 部门 1 -公共 2 - 共享
     */
    @GET("idaqFile/getMyDepartmentFolderCount")
    fun getNumberOfFiles(
        @Query("type") type : Int,
        @Query("id") deptId : Int? = null
    ): Observable<AppResponse<FileCount>>


    /**
     * 获取文件数量 共享文件
     */
    @GET("idaqFileFolder/getShareCountForApp")
    fun getNumberOfShare(): Observable<AppResponse<FileCount>>


    /**
     * 获取文件
     */
    @GET("idaqFile/getFile")
    fun getFile(
        @Query("folderId") folderId : Int ,
        @Query("fileShare") fileShare : Int? = null,
        @Query("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("pageCurr") page  : Int = ConstantGlobal.INITIAL_PAGE
    ): Observable<AppResponse<FileInfoWrapper>>


    /**
     * 保存文件夹
     * @param id String
     * @param folderType String 部门-0 公共 1
     * @param folderName String 名称
     * @param folderParentId String 上级文件夹
     * @return Observable<AppResponse<Any>>
     */
    @FormUrlEncoded
    @POST("api/1084/savefolder")
    fun saveFolder(
        @Field("id") id : Int? = null,
        @Field("folderType") folderType : Int,
        @Field("folderName") folderName : String,
        @Field("folderParentId") folderParentId : Int
    ): Observable<AppResponse<Any>>


    /**
     * 删除文件夹
     * @param foldId Int
     * @return Observable<AppResponse<Any>>
     */
    @GET("api/1084/deleteFolder")
    fun deleteFolder(
        @Query("foldId") foldId : Int
    ): Observable<AppResponse<Any>>

    /**
     * 保存文件
     * @param files List<UploadResult>
     * @param folderId Int
     * @return Observable<AppResponse<Any>>
     */
    @GET("api/1084/addFile")
    fun saveFile(
        @Query("files") files : String,
        @Query("folderId") folderId : Int
    ): Observable<AppResponse<Any>>


    /**
     * 删除文件
     * @return Observable<AppResponse<Any>>
     */
    @GET("idaqFile/deleteFile")
    fun deleteFile(
        @Query("fileId") fileId : Int,
        @Query("menuId") menuId : Int? = null
    ): Observable<AppResponse<Any>>



    /**
     * 下载文件
     * @return Observable<AppResponse<String>>
     */
    @GET("idaqFile/downFile")
    fun downloadFile(
        @Query("fileIds") fileIds : List<String>
    ): Observable<AppResponse<Map<String,List<String>>>>


    /**
     * 下载文件（项目文件库）
     * @return Observable<AppResponse<String>>
     */
    @GET("idaqProjectFile/downFile")
    fun downloadFiles(
        @Query("fileId") fileIds : List<String>
    ): Observable<AppResponse<DeptFileInfo>>

    /**
     * 预览文件
     * @return Observable<AppResponse<Any>>
     */
    @GET("idaqFile/previewFile")
    fun previewFile(
        @Query("fileId") fileId : String
    ): Observable<AppResponse<Any>>


    /**
     * 预览文件(项目文件库)
     * @return Observable<AppResponse<Any>>
     */
    @GET("idaqProjectFile/previewFile")
    fun previewFiles(
        @Query("fileId") fileId : String
    ): Observable<AppResponse<DeptFileInfo>>
    /**
     * 获取关怀词类型
     */
    @GET("idaqCareWords/careWordPositionList")
    fun getCaringWordType(): Observable<AppResponse<List<CaringWordType>>>


    /**
     * 启用禁用关怀词
     */
    @GET("idaqCareWords/enableOne")
    fun enableOrDisableCaringWord(
        @Query("enable") enable : Boolean ,
        @Query("id") id : Int
    ): Observable<AppResponse<Any>>

    /**
     * 删除关怀词
     */
    @GET("api/1093/deleteByIds")
    fun deleteCaringWord(
        @Query("ids") id : Int
    ): Observable<AppResponse<Any>>


    /**
     * 删除关怀词
     */
    @GET("idaqCareWords/getById")
    fun getCaringWordDetail(
        @Query("id") id : String
    ): Observable<AppResponse<CaringWordDetail>>


    /**
     * 获取/搜索 部门/公共文件夹信息
     */
    @GET("idaqFileFolder/getDepartFoldersForApp")
    fun getDeptOrPublicFileInfo(
        @Query("folderId") folderId : Int? = null,
        @Query("departmentId") departmentId : Int? = null,
        @Query("type") type : Int? = null,
        @Query("title") title : String? = null
    ): Observable<AppResponse<DeptFile>>

    /**
     * 获获取/搜索 共享文件夹信息
     */
    @GET("idaqFileFolder/getShareFoldersForApp")
    fun getShareFileInfo(
        @Query("folderId") folderId : Int? = null,
        @Query("departmentId") departmentId : Int? = null,
        @Query("title") title : String? = null
    ): Observable<AppResponse<DeptFile>>


    /**
     * 日报发送评论
     */
    @POST("idaqDayReportTeam/commentReport")
    fun sendComment(
        @Body body: Map<String, String>): Observable<AppResponse<Any>>


    /**
     * 日报列表
     */
    @GET("idaqDayReportTeam/getPublishedTeamDayReportForApp")
    fun getDailyListRequest(
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("page") current :Int = ConstantGlobal.INITIAL_PAGE,
        @Query("name") name: String = ""
    ): Observable<AppResponse<DialyListBean>>

    /**
     * 搜索日报列表
     */
    @POST("idaqDayReportTeam/searchDateTeamDayReport")
    fun getSearchDailyListRequest(
        @Body body: Map<String, String>
    ): Observable<AppResponse<DialySearchBean>>

    /**
     * 个人日报列表
     */
    @POST("idaqDayReport/getDateDayReport")
    fun getDailyPersonRequest(
        @Body body: Map<String, String>): Observable<AppResponse<DialyListBeanItem>>

    /**
     * 个人当天日报列表
     */
    @GET("idaqDayReport/getPersonalDayReport")
    fun getDailyPersonDayRequest(
        @Query("date") date: String = "",
        @Query("simple") simple: Boolean = true): Observable<AppResponse<DialyRecord>>


    /**
     * 团队当天日报列表
     */
    @GET("idaqDayReportTeam/getSimpleTeamReport")
    fun getDailyTeamDayRequest(
        @Query("id") id: String = ""): Observable<AppResponse<DialyRecord>>


    /**
     * 获取主管团队日报列表
     */
    @POST("idaqDayReportTeam/getDateTeamDayReport")
    fun getDailyMemberRequest(
        @Body body: Map<String, String>): Observable<AppResponse<List<DialyMemberBean>>>

    /**
     * 获取我的团队日报列表
     */
    @POST("idaqDayReportTeam/getPagedTeamReport")
    fun getAllMyTeamReport(
        @Body body: Map<String, String>
    ): Observable<AppResponse<DialyListBeanItem>>

    /**
     * 获取未提交日报成员
     */
    @GET("idaqDayReportTeam/getTeamEmployee")
    fun getDailyNoSubMemberRequest(
        @Query("date") date : String?="",
        @Query("withChild") withChild : Boolean?=null,
        @Query("uncommitOnly") uncommitOnly :Boolean?=true
    ): Observable<AppResponse<List<DialyMemberBean>>>

    /**
     * 获取主管团队日报提交统计
     */
    @GET("idaqDayReportTeam/getDateTeamDayReportStatistic")
    fun getDailyReportData(
        @Query("date") date : String?=""
    ): Observable<AppResponse<DialyStatisticBean>>


    /**
     * 日报详情
     */
    @GET("idaqDayReportTeam/getPublishedReport")
    fun getDailyDetailRequest(
        @Query("id") id : Int
    ): Observable<AppResponse<DialyDetailBean>>

    /**
     * 日报讨论列表
     */
    @GET("idaqDayReportTeam/getCommentByReportId")
    fun getDailyDetailDiscussRequest(
        @Query("id") id : Int
    ): Observable<AppResponse<DialyDetailDiscuss>>

    /**
     * 点赞
     */
    @FormUrlEncoded
    @POST("idaqDayReportTeam/likeOrNot")
    fun getDailyDetailRaiseRequest(
        @Field("like") like : Boolean,
        @Field("id") id: String
    ): Observable<AppResponse<Any>>

    /**
     * ri
     */
    /**
     * 阅读单条数据
     */
    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun readDialy(
        @Query("id") id : String
    ) : Observable<AppResponse<Any>>


    /**
     * 日报发送评论
     */
    @POST("idaqProjectNotebook/saveProjectNoteByDailyReport")
    fun sendDailyData(
        @Body body: Map<String, String?>): Observable<AppResponse<Any>>

    /**
     * 通过日报所属项目列表
     */
    @GET("idaqProject/getSimpleProject")
    fun getDailyProject(
    ): Observable<AppResponse<List<DialyProjec>>>


    /**
     * 通过日报所属项目列表
     */
    @GET("idaqProject/getSimpleProjectMembers")
    fun getATailStaff(
        @Query("projectId") projectId : String
    ): Observable<AppResponse<List<Map<String,String>>>>

    /**
     * 获取公告评论列表
     */
    @GET("notice/getCommentsByNoticeId")
    fun getAnnouncementComment(
        @Query("noticeId") noticeId : String
    ):Observable<AppResponse<List<AnnouncementComment>>>


    /**
     * 获取日报规则接口
     */
    @GET("idaqDayReportSet/getDayReportSet")
    fun getDayReportSet():Observable<AppResponse<DialyRuleBean>>

    /**
     * 获取日报规则接口
     */
    @GET("idaqFileBase/getProjectBase")
    fun getProjectBase(
        @Query("projectId") projectId : String
    ):Observable<AppResponse<List<DeptType>>>

    /**
     * 获取/搜索 部门/公共文件夹信息
     */
    @GET("idaqProjectFile/getFileList")
    fun getProDeptOrPublicFileInfo(
        @Query("projectId") projectId : Int? = null,
        @Query("docId") docId : Int? = null,
        @Query("endTime") endTime : String? = null,
        @Query("startTime") startTime : String? = null,
        @Query("fileName") fileName : String? = null,
        @Query("userName") userName : String? = null,
        @Query("pageSize") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("pageCurr") page  : Int = ConstantGlobal.INITIAL_PAGE
    ): Observable<AppResponse<DeptFileRoot>>

    /**
     * 个人日报统计日历
     */
    @GET("idaqDayReport/getCalender")
    fun getPersonCalender(
        @Query("month") month : String? = null
    ): Observable<AppResponse<DailyDataBean>>

    /**
     * 团队统计日历
     */
    @GET("idaqDayReportTeam/team/getCalender")
    fun getTeamCalender(
        @Query("month") month : String? = null
    ): Observable<AppResponse<DailyTeamDataBeans>>

    /**
     * 成员统计日历
     */
    @GET("idaqDayReportTeam/member/getCalender")
    fun getMemberCalender(
        @Query("month") month : String? = null
    ): Observable<AppResponse<List<DailyTeamDataBean>>>

    @Headers(HttpGlobal.MESSAGE_HEADER)
    @GET("message/getDetail")
    fun getNextDetail(
        @Query("id") id: Int
    ):Observable<AppResponse<NextMessage>>


    /**
     * 获取项目动态
     */
    @GET("idaqVisitNotebook/customer/getPage")
    fun  getCustomerRecordList(
        @Query("isAsc") isAsc : Boolean?=false,
        @Query("selfOnly") selfOnly : Boolean?=null,
        @Query("tagIds") tagIds	 : String?=null,
        @Query("itrState") itrState : Int?=null,
        @Query("key") key : String?=null,
        @Query("visitId") visitId : String?=null, //必填
        @Query("userIds") userIds : Boolean?=null,
        @Query("endDate") endDate	 : String?=null,
        @Query("startDate") startDate: String?=null,
        @Query("isSimple") isSimple: Boolean ?= true,
        @Query("page") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<CustomerRecordBeanPaging>>


    /**
     * 获取项目动态
     */
    @GET("idaqVisitNotebook/partner/getPage")
    fun  getPatherRecordList(
        @Query("isAsc") isAsc : Boolean?=false,
        @Query("selfOnly") selfOnly : Boolean?=null,
        @Query("tagIds") tagIds	 : String?=null,
        @Query("itrState") itrState : Int?=null,
        @Query("key") key : String?=null,
        @Query("visitId") visitId : String?=null, //必填
        @Query("userIds") userIds : Boolean?=null,
        @Query("endDate") endDate	 : String?=null,
        @Query("startDate") startDate: String?=null,
        @Query("isSimple") isSimple: Boolean ?= true,
        @Query("page") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size : Int = ConstantGlobal.INITIAL_PAGE_SIZE
    ): Observable<AppResponse<CustomerRecordBeanPaging>>

    // 项目标签
    @GET("idaqNoteTag/getProjectCurrentTags")
    fun getProjectLabel(
        @Query("projectId") projectId : String
    ) : Observable<AppResponse<List<ProjectLabel>>>

    // 获取日报设置标签
    @GET("idaqNoteTag/getReportTags")
    fun getReportTags(
    ) : Observable<AppResponse<List<ProjectLabel>>>
    /**
     * 保存
     * @param body Body
     * @return Observable<AppResponse<Any>>
     */
    @POST("idaqVisitNotebook/customer/save")
    fun  saveCustomerRecord(
        @Body body: CustomerRecordRequest
    ):Observable<AppResponse<Any>>
    /**
     * 保存
     */
    @POST("idaqVisitNotebook/partner/save")
    fun  savePartnerRecord(
        @Body body: CustomerRecordRequest
    ):Observable<AppResponse<Any>>


    // 项目动态 item
    @GET("idaqVisitNotebook/getNote")
    fun getCustomerRecordItem(
        @Query("noteId") noteId : String
    ) : Observable<AppResponse<CustomerRecordBean>>
    // 项目动态 0-客户 1-伙伴
    @GET("idaqCustomerGrade/getGradeList")
    fun getGradeList(
        @Query("gradeClassify") gradeClassify : String
    ) : Observable<AppResponse<List<Leader>>>

    // 项目动态 0-客户 1-伙伴
    @GET("idaqCustomerType/getPageList")
    fun getTypeList(
        @Query("typeClassify") typeClassify : String
    ) : Observable<AppResponse<List<CustomerType>>>

    // 获取客户列表
    @GET("idaqCustomer/getPages")
    fun getCustomerList(
        @Query("key") key: String?=null,
        @Query("customerGrade") customerGrade: Int?=null,
        @Query("customerType") customerType: Int? = null,
        @Query("page") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("menuId") menuId: Int =1070
    ) : Observable<AppResponse<CustomerBean>>



    // 获取客户详情
    @GET("idaqCustomer/getTheCustomer")
    fun getCustomerDetail(
        @Query("customerId") customerId : String
    ) : Observable<AppResponse<CustomerDetailBean>>


    // 获取客户详情
    @GET("idaqCustomer/getDetail")
    fun getCustomerSingle(
        @Query("customerId") customerId : String
    ) : Observable<AppResponse<CustomerListBean>>

    // 获取伙伴列表
    @GET("idaqPartner/getPages")
    fun getParnterList(
        @Query("key") key: String?=null,
        @Query("partnerType") partnerType: Int? = null,
        @Query("partnerGrade") partnerGrade: Int?=null,
        @Query("page") page: Int = ConstantGlobal.INITIAL_PAGE,
        @Query("size") size: Int = ConstantGlobal.INITIAL_PAGE_SIZE,
        @Query("menuId") menuId: Int = 1071
    ) : Observable<AppResponse<PartnerBean>>

    // 获取伙伴列表
    @GET("idaqPartner/getDetail")
    fun getParnterSigle(
        @Query("partnerId") partnerId : String
    ) : Observable<AppResponse<PartnerListBean>>

    // 获取伙伴详情
    @GET("idaqPartner/getThePartner")
    fun getParnterDetail(
        @Query("partnerId") partnerId : String
    ) : Observable<AppResponse<PartnerDetailBean>>


    // 获取编辑记录
    @GET("idaqCustomerEditRecord/getEditRecord")
    fun getEditRecord(
        @Query("type") type : Int? = null,
        @Query("id") id : Int? = null
    ) : Observable<AppResponse<List<CustomerRecord>>>

    // 项目动态 关闭 itr
    @GET("idaqProjectNotebook/closeItr")
    fun closeItr(
        @Query("noteId") noteId : String
    ) : Observable<AppResponse<Any>>
}

