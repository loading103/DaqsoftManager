package com.daqsoft.module_project.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.module_project.repository.pojo.vo.MoneyTypeBean
import com.daqsoft.module_project.repository.pojo.vo.ProjectBuildContent
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Query
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_project.repository
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe
 */
class ProjectRepository  @Inject constructor(private val projectApiService:ProjectApiService) : BaseModel(),ProjectApiService {
    override fun uploadOSS(
        uploadUrl: String,
        file: MultipartBody.Part
    ): Observable<AppResponse<List<UploadResult>>> {
        return projectApiService.uploadOSS(uploadUrl, file)
    }

    override fun uploadOSSMulti(
        uploadUrl: String,
        params: Map<String, RequestBody>
    ): Observable<AppResponse<List<UploadResult>>> {
        return projectApiService.uploadOSSMulti(uploadUrl, params)
    }

    override fun uploadOSSMulti(
        uploadUrl: String,
        parts: List<MultipartBody.Part>
    ): Observable<AppResponse<List<UploadResult>>> {
        return projectApiService.uploadOSSMulti(uploadUrl, parts)
    }

    /**
     * 获取项目所有类型
     */
    override fun getProjectType(): Observable<AppResponse<List<ProjectType>>> {
        return projectApiService.getProjectType()
    }

    override fun getProjectStageTable(menuId: Int?): Observable<AppResponse<List<ProjectType>>> {
        return projectApiService.getProjectStageTable()
    }

    override fun getProjectFlow(): Observable<AppResponse<List<ProjectFlow>>> {
        return projectApiService.getProjectFlow()
    }

    override fun getProjectHeadInfor(): Observable<AppResponse<ProjectHeadBean>> {
        return projectApiService.getProjectHeadInfor()
    }

    /**
     * 获取该项目列表数据
     */
    override fun getProjectListData(
        projectName: String?,
        orderType: String?,
        orderBy: String?,
        pageSize: Int,
        pageCurr: Int,
        projectType: Int?,
        stage: Int?,
        menuId : Int,
        projectGrade: Int?,
        onlyCares : Boolean?,
        state : Int?
    ): Observable<AppResponse<ProjectListBean>> {
        return projectApiService.getProjectListData(projectName,orderType,orderBy,pageSize,pageCurr,projectType,stage,menuId,projectGrade,onlyCares,state)
    }

    /**
     * 关注/取消关注项目
     */
    override fun followProject(projectId: Int?): Observable<AppResponse<String>>{
        return projectApiService.followProject(projectId)
    }

    override fun getProjectDetailRequest(projectId: Int): Observable<AppResponse<ProjectDetailBean>> {
        return projectApiService.getProjectDetailRequest(projectId)
    }

    override fun getProjecBuildContentRequest(projectId: Int): Observable<AppResponse<ProjectBuildContent>> {
        return projectApiService.getProjecBuildContentRequest(projectId)
    }

    override fun getAccountType(groupCode: String): Observable<AppResponse<List<MoneyTypeBean>>> {
        return projectApiService.getAccountType(groupCode)
    }

    override fun saveProjectDynamics(body: ProjectDynamicRequest): Observable<AppResponse<Any>> {
        return projectApiService.saveProjectDynamics(body)
    }

    override fun getProjectBaseInfor(projectId: String): Observable<AppResponse<ProjectBaseInfor>> {
        return projectApiService.getProjectBaseInfor(projectId)
    }

    override fun getProjectBaseInfors(projectId: String): Observable<AppResponse<ProjectBaseInfors>> {
        return projectApiService.getProjectBaseInfors(projectId)
    }

    override fun getProjectDynamics(
        itrState: Boolean?,
        userId: String?,
        endTime: String?,
        startTime: String?,
        booking: Boolean?,
        tag: List<String>?,
        projectId: String,
        page: Int,
        size: Int
    ): Observable<AppResponse<ProjectDynamicPaging>> {
        return projectApiService.getProjectDynamics(itrState, userId, endTime, startTime, booking, tag, projectId, page, size)
    }

    override fun getProjectDynamicsItem(noteId: String): Observable<AppResponse<ProjectDynamic>> {
        return projectApiService.getProjectDynamicsItem(noteId)
    }

    override fun projectDynamicsScore(noteId: Int, score: Float): Observable<AppResponse<Any>> {
        return projectApiService.projectDynamicsScore(noteId, score)
    }

    override fun projectDynamicsPigeonhole(noteId: Int): Observable<AppResponse<Any>> {
        return projectApiService.projectDynamicsPigeonhole(noteId)
    }

    override fun cancelProjectDynamicsPigeonhole(noteId: Int): Observable<AppResponse<Any>> {
        return projectApiService.cancelProjectDynamicsPigeonhole(noteId)
    }

    override fun closeItr(noteId: String): Observable<AppResponse<Any>> {
        return projectApiService.closeItr(noteId)
    }

    override fun getProjectLabel(projectId: String): Observable<AppResponse<List<ProjectLabel>>> {
        return projectApiService.getProjectLabel(projectId)
    }
    override fun getMenus(app: Boolean): Observable<AppResponse<Menu>> {
        return projectApiService.getMenus(app)
    }

    override fun getOwnerList(key: String?): Observable<AppResponse<List<ProjectOwnerBean>>> {
        return projectApiService.getOwnerList(key)
    }

    override fun getPartnerList(key: String?): Observable<AppResponse<List<ProjectOwnerBean>>> {
        return projectApiService.getPartnerList(key)
    }

    override fun SaveNewProject(param: HashMap<String, String>): Observable<AppResponse<Any>> {
        return projectApiService.SaveNewProject(param)
    }

    override fun getProductDetail(productId: Int?): Observable<AppResponse<ProductBean>> {
        return projectApiService.getProductDetail(productId)
    }

    override fun getNextDetail(id: Int): Observable<AppResponse<NextMessage>> {
        return projectApiService.getNextDetail(id)
    }

    override fun getProjectSingleData(projectId: Int?): Observable<AppResponse<ProjectData>> {
        return projectApiService.getProjectSingleData(projectId)
    }
}