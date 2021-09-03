package com.daqsoft.module_workbench.repository

import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.*
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.base.BaseModel
import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

/**
 * @ClassName    WorkBenchRepository
 * @Description
 * @Author       yuxc
 * @CreateDate   2020/11/17
 */
class WorkBenchRepository  @Inject constructor(private val workBenchApiService:WorkBenchApiService) : BaseModel(),WorkBenchApiService {

    override fun getMenus(app: Boolean): Observable<AppResponse<Menu>> {
        return workBenchApiService.getMenus(app)
    }

    override fun getSecondMenus(menuId: Int, app: Boolean): Observable<AppResponse<List<MenuInfo>>> {
        return workBenchApiService.getSecondMenus(menuId,app)
    }

    override fun getMenuPermission(menuId: Int, app: Boolean): Observable<AppResponse<List<MenuPermission>>> {
        return workBenchApiService.getMenuPermission(menuId,app)
    }


    override fun saveScanTime(body: Map<String, String>): Observable<AppResponse<String>> {
        return workBenchApiService.saveScanTime(body)
    }

    override fun getOrganization(): Observable<AppResponse<Organization>> {
        return workBenchApiService.getOrganization()
    }

    override fun getMember(pid: Int): Observable<AppResponse<Member>> {
        return workBenchApiService.getMember(pid)
    }

    override fun getCompanyInfo(
        OA_PASS: String,
        OA_UID: String
    ): Observable<AppResponse<CompanyInfo>> {
        return workBenchApiService.getCompanyInfo(OA_PASS, OA_UID)
    }

    override fun getInvoiceInfo(): Observable<AppResponse<InvoiceInfo>> {
        return workBenchApiService.getInvoiceInfo()
    }

    override fun getCompanyNumberOfEmployees(): Observable<AppResponse<Int>> {
        return workBenchApiService.getCompanyNumberOfEmployees()
    }

    override fun getAnnouncement(
        title: String?,
        type: String?,
        partnerName: String?,
        size: Int,
        current: Int
    ): Observable<AppResponse<Announcement>> {
        return workBenchApiService.getAnnouncement(title, type, partnerName, size, current)
    }

    override fun getAnnouncementDetail(id: String): Observable<AppResponse<AnnouncementDetail>> {
        return workBenchApiService.getAnnouncementDetail(id)
    }

    override fun announcementLike(
        like: Boolean,
        noticeId: String
    ): Observable<AppResponse<Any>> {
        return workBenchApiService.announcementLike(like, noticeId)
    }

    override fun sendAnnouncementComment(body: Map<String, String>): Observable<AppResponse<Any>> {
        return workBenchApiService.sendAnnouncementComment(body)
    }

    override fun markAllAsRead(): Observable<AppResponse<Any>> {
        return workBenchApiService.markAllAsRead()
    }

    override fun verifySecondPassword(secondPassword: String, type: String): Observable<AppResponse<Any>> {
        return workBenchApiService.verifySecondPassword(secondPassword,type)
    }

    override fun getPaySlipList(secret: String): Observable<AppResponse<List<PaySlip>>> {
        return workBenchApiService.getPaySlipList(secret)
    }

    override fun getPaySlipDetail(
        id: String,
        secret: String
    ): Observable<AppResponse<PaySlipDetail>> {
        return workBenchApiService.getPaySlipDetail(id, secret)
    }

    override fun searchEmployees(
        name: String?,
        size: Int,
        current: Int
    ): Observable<AppResponse<EmployeeSearch>> {
        return workBenchApiService.searchEmployees(name, size, current)
    }

    override fun getEmployeeInfo(id: String): Observable<AppResponse<EmployeeDetail>> {
        return workBenchApiService.getEmployeeInfo(id)
    }

    override fun getUpcomingTasks(
        state: Int,
        id: String,
        page: Int,
        size: Int
    ): Observable<AppResponse<Upcoming>> {
        return workBenchApiService.getUpcomingTasks(state, id, page, size)
    }

    override fun getParticipatingProjects(id :String ): Observable<AppResponse<List<Participate>>> {
        return workBenchApiService.getParticipatingProjects(id)
    }

    override fun getParticipatingProjectsTotal(id: String): Observable<AppResponse<Any>> {
        return workBenchApiService.getParticipatingProjectsTotal(id)
    }

    override fun updateTaskStatus(status: Int, taskId: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.updateTaskStatus(status, taskId)
    }

    override fun read(taskId: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.read(taskId)
    }

    override fun readSingle(id: String): Observable<AppResponse<Any>> {
        return workBenchApiService.readSingle(id)
    }

    override fun uploadOSS(
        uploadUrl: String,
        file: MultipartBody.Part
    ): Observable<AppResponse<List<UploadResult>>> {
        return workBenchApiService.uploadOSS(uploadUrl, file)
    }

    override fun uploadOSSMulti(
        uploadUrl: String,
        params: Map<String, RequestBody>
    ): Observable<AppResponse<List<UploadResult>>> {
        return workBenchApiService.uploadOSSMulti(uploadUrl, params)
    }

    override fun uploadOSSMultis(
        uploadUrl: String,
        parts: List<MultipartBody.Part>
    ): Observable<AppResponse<List<UploadResult>>> {
        return workBenchApiService.uploadOSSMultis(uploadUrl, parts)
    }


    override fun clockIn(): Observable<AppResponse<Any>> {
        return workBenchApiService.clockIn()
    }

    override fun getWelcomeMessage(app: Boolean): Observable<AppResponse<WelcomeMessage>> {
        return workBenchApiService.getWelcomeMessage(app)
    }

    override fun getCareListData(
        title: String?,
        position: Int?,
        page: Int,
        size: Int
    ): Observable<AppResponse<CareThesausuRoot>> {
        return workBenchApiService.getCareListData(title, position, page, size)
    }

    override fun getCareListNumber(position: Int?): Observable<AppResponse<CareThesausuNumberBean>> {
        return workBenchApiService.getCareListNumber(position)
    }


    override fun getShowPlace(): Observable<AppResponse<List<CareThesaususPlaceBean>>> {
        return workBenchApiService.getShowPlace()
    }
    override fun saveqCareWord(carePosition: String,careUrl: String,careInfo: String,id: String?): Observable<AppResponse<Any>> {
        return workBenchApiService.saveqCareWord(carePosition,careUrl,careInfo,id)
    }

    override fun getNoticeCount(): Observable<AppResponse<NoticeCount>> {
        return workBenchApiService.getNoticeCount()
    }

    override fun getNoticeType(): Observable<AppResponse<List<NoticeType>>> {
        return workBenchApiService.getNoticeType()
    }

    override fun getNoticeList(
        page: Int,
        size: Int,
        title: String?,
        type:Int?,
        startTime: Long?,
        endTime: Long?,
        noticeStatus: Int?
    ): Observable<AppResponse<Notice>> {
        return workBenchApiService.getNoticeList(page, size, title, type,startTime, endTime, noticeStatus)
    }

    override fun submitNoticeReview(
        id: String,
        status:String
    ): Observable<AppResponse<Any>> {
        return workBenchApiService.submitNoticeReview(id,status)
    }

    override fun saveNotice(body: SaveNotice): Observable<AppResponse<Any>> {
        return workBenchApiService.saveNotice(body)
    }

    override fun getNoticeDetail(
        id: String,
        richText: Boolean
    ): Observable<AppResponse<NoticeDetail>> {
        return workBenchApiService.getNoticeDetail(id, richText)
    }

    override fun getAllAuditStatus(): Observable<AppResponse<List<NoticeAuditStatus>>> {
        return workBenchApiService.getAllAuditStatus()
    }

    override fun withdrawNotice(id: String): Observable<AppResponse<Any>> {
        return workBenchApiService.withdrawNotice(id)
    }

    override fun updateNotice(body: SaveNotice): Observable<AppResponse<Any>> {
        return workBenchApiService.updateNotice(body)
    }

    override fun approveNotice(
        result: Boolean,
        remark: String,
        id: String
    ): Observable<AppResponse<Any>> {
        return workBenchApiService.approveNotice(result, remark, id)
    }

    override fun getOrganizationChildren(parentId: Int): Observable<AppResponse<List<OrganizationChildren>>> {
        return workBenchApiService.getOrganizationChildren(parentId)
    }

    override fun getOrgAllEmployees(organizationId: Int): Observable<AppResponse<List<Employee>>> {
        return workBenchApiService.getOrgAllEmployees(organizationId)
    }

    override fun getOrgAllOrg(organizationId: Int): Observable<AppResponse<List<OrgSimple>>> {
        return workBenchApiService.getOrgAllOrg(organizationId)
    }

    override fun getNumberOfFiles(type: Int,deptId : Int?): Observable<AppResponse<FileCount>> {
        return workBenchApiService.getNumberOfFiles(type,deptId)
    }

    override fun getNumberOfShare(): Observable<AppResponse<FileCount>> {
        return workBenchApiService.getNumberOfShare()
    }

    override fun getFile(
        folderId: Int,
        fileShare: Int?,
        size: Int,
        page: Int
    ): Observable<AppResponse<FileInfoWrapper>> {
        return workBenchApiService.getFile(folderId, fileShare, size, page)
    }

    override fun saveFolder(
        id: Int?,
        folderType: Int,
        folderName: String,
        folderParentId: Int
    ): Observable<AppResponse<Any>> {
        return workBenchApiService.saveFolder(id, folderType, folderName, folderParentId)
    }

    override fun deleteFolder(foldId: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.deleteFolder(foldId)
    }

    override fun saveFile(files: String, folderId: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.saveFile(files, folderId)
    }

    override fun deleteFile(fileId: Int, menuId: Int?): Observable<AppResponse<Any>> {
        return workBenchApiService.deleteFile(fileId, menuId)
    }

    override fun downloadFile(fileIds: List<String>): Observable<AppResponse<Map<String,List<String>>>> {
        return workBenchApiService.downloadFile(fileIds)
    }

    override fun downloadFiles(fileIds: List<String>): Observable<AppResponse<DeptFileInfo>> {
        return workBenchApiService.downloadFiles(fileIds)
    }

    override fun previewFile(fileId: String): Observable<AppResponse<Any>> {
        return workBenchApiService.previewFile(fileId)
    }

    override fun previewFiles(fileId: String): Observable<AppResponse<DeptFileInfo>> {
        return workBenchApiService.previewFiles(fileId)
    }

    override fun getCaringWordType(): Observable<AppResponse<List<CaringWordType>>> {
        return workBenchApiService.getCaringWordType()
    }

    override fun enableOrDisableCaringWord(enable: Boolean, id: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.enableOrDisableCaringWord(enable, id)
    }

    override fun deleteCaringWord(id: Int): Observable<AppResponse<Any>> {
        return workBenchApiService.deleteCaringWord(id)
    }

    override fun getCaringWordDetail(id: String): Observable<AppResponse<CaringWordDetail>> {
        return workBenchApiService.getCaringWordDetail(id)
    }

    override fun getDeptOrPublicFileInfo(
        folderId: Int?,
        departmentId: Int?,
        type: Int?,
        title: String?
    ): Observable<AppResponse<DeptFile>> {
        return workBenchApiService.getDeptOrPublicFileInfo(folderId, departmentId, type, title)
    }

    override fun getShareFileInfo(
        folderId: Int?,
        departmentId: Int?,
        title: String?
    ): Observable<AppResponse<DeptFile>> {
        return workBenchApiService.getShareFileInfo(folderId, departmentId, title)
    }

    override fun sendComment(body: Map<String, String>): Observable<AppResponse<Any>> {
        return workBenchApiService.sendComment(body)
    }

    override fun getDailyListRequest(size: Int, page: Int, name: String): Observable<AppResponse<DialyListBean>> {
        return workBenchApiService.getDailyListRequest(size,page, name)
    }

    override fun getSearchDailyListRequest(
        body: Map<String, String>
    ): Observable<AppResponse<DialySearchBean>> {
        return workBenchApiService.getSearchDailyListRequest(body)
    }
    override fun getDailyPersonRequest(
        body: Map<String, String>
    ): Observable<AppResponse<DialyListBeanItem>> {
        return workBenchApiService.getDailyPersonRequest(body)
    }

    override fun getDailyPersonDayRequest(date: String,simple: Boolean ): Observable<AppResponse<DialyRecord>> {
        return workBenchApiService.getDailyPersonDayRequest(date)
    }

    override fun getDailyTeamDayRequest(id: String): Observable<AppResponse<DialyRecord>> {
        return workBenchApiService.getDailyTeamDayRequest(id)
    }

    override fun getDailyMemberRequest(body: Map<String, String>): Observable<AppResponse<List<DialyMemberBean>>> {
        return workBenchApiService.getDailyMemberRequest(body)
    }

    override fun getDailyNoSubMemberRequest(
        date: String?,
        withChild: Boolean?,
        uncommitOnly: Boolean?
    ): Observable<AppResponse<List<DialyMemberBean>>> {
        return workBenchApiService.getDailyNoSubMemberRequest(date,withChild,uncommitOnly)

    }

    override fun getAllMyTeamReport(
        body: Map<String, String>
    ): Observable<AppResponse<DialyListBeanItem>> {
        return workBenchApiService.getAllMyTeamReport(body)
    }

    override fun getDailyReportData(
        date: String?
    ): Observable<AppResponse<DialyStatisticBean>> {
        return workBenchApiService.getDailyReportData(date)

    }
    override fun getDailyDetailRequest(id: Int): Observable<AppResponse<DialyDetailBean>> {
        return workBenchApiService.getDailyDetailRequest(id)
    }

    override fun getDailyDetailDiscussRequest(id: Int): Observable<AppResponse<DialyDetailDiscuss>> {
        return workBenchApiService.getDailyDetailDiscussRequest(id)
    }

    override fun getDailyDetailRaiseRequest(like: Boolean, id: String): Observable<AppResponse<Any>> {
        return workBenchApiService.getDailyDetailRaiseRequest(like, id)
    }

    override fun readDialy(id: String): Observable<AppResponse<Any>> {
        return workBenchApiService.readDialy(id)
    }


    override fun sendDailyData(body: Map<String, String?>): Observable<AppResponse<Any>> {
        return workBenchApiService.sendDailyData(body)
    }

    override fun getDailyProject(): Observable<AppResponse<List<DialyProjec>>> {
        return workBenchApiService.getDailyProject()
    }

    override fun getATailStaff(projectId: String): Observable<AppResponse<List<Map<String, String>>>> {
        return workBenchApiService.getATailStaff(projectId)
    }

    override fun getAnnouncementComment(noticeId: String): Observable<AppResponse<List<AnnouncementComment>>> {
        return workBenchApiService.getAnnouncementComment(noticeId)
    }
    override fun getDayReportSet(): Observable<AppResponse<DialyRuleBean>> {
        return workBenchApiService.getDayReportSet()
    }

    override fun getProjectBase( projectId: String): Observable<AppResponse<List<DeptType>>> {
        return workBenchApiService.getProjectBase(projectId)
    }

    override fun getProDeptOrPublicFileInfo(
        projectId: Int?,
        docId: Int?,
        endTime: String?,
        startTime: String?,
        fileName: String?,
        userName: String?,
        size: Int,
        page: Int
    ): Observable<AppResponse<DeptFileRoot>> {
        return workBenchApiService.getProDeptOrPublicFileInfo(projectId,docId,endTime,startTime,fileName,userName,size,page)
    }

    override fun getPersonCalender(month: String?): Observable<AppResponse<DailyDataBean>> {
        return workBenchApiService.getPersonCalender(month)
    }

    override fun getTeamCalender(month: String?): Observable<AppResponse<DailyTeamDataBeans>> {
        return workBenchApiService.getTeamCalender(month)
    }

    override fun getMemberCalender(month: String?): Observable<AppResponse<List<DailyTeamDataBean>>> {
        return workBenchApiService.getMemberCalender(month)
    }

    override fun getNextDetail(id: Int): Observable<AppResponse<NextMessage>> {
        return workBenchApiService.getNextDetail(id)
    }

    override fun getCustomerRecordList(
        isAsc: Boolean?,
        selfOnly: Boolean?,
        tagIds: String?,
        itrState: Int?,
        key: String?,
        visitId: String?,
        userIds: Boolean?,
        endDate: String?,
        startDate: String?,
        isSimple: Boolean?,
        page: Int,
        size: Int
    ): Observable<AppResponse<CustomerRecordBeanPaging>> {
        return workBenchApiService.getCustomerRecordList(isAsc, selfOnly, tagIds, itrState, key, visitId, userIds,endDate,startDate,isSimple, page, size)
    }
    override fun getPatherRecordList(
        isAsc: Boolean?,
        selfOnly: Boolean?,
        tagIds: String?,
        itrState: Int?,
        key: String?,
        visitId: String?,
        userIds: Boolean?,
        endDate: String?,
        startDate: String?,
        isSimple: Boolean?,
        page: Int,
        size: Int
    ): Observable<AppResponse<CustomerRecordBeanPaging>> {
        return workBenchApiService.getPatherRecordList(isAsc, selfOnly, tagIds, itrState, key, visitId, userIds,endDate,startDate,isSimple, page, size)
    }
    override fun getProjectLabel(projectId: String): Observable<AppResponse<List<ProjectLabel>>> {
        return workBenchApiService.getProjectLabel(projectId)
    }

    override fun saveCustomerRecord(body: CustomerRecordRequest): Observable<AppResponse<Any>> {
        return workBenchApiService.saveCustomerRecord(body)
    }

    override fun savePartnerRecord(body: CustomerRecordRequest): Observable<AppResponse<Any>> {
        return workBenchApiService.savePartnerRecord(body)
    }

    override fun getCustomerRecordItem(noteId: String): Observable<AppResponse<CustomerRecordBean>> {
        return workBenchApiService.getCustomerRecordItem(noteId)
    }

    override fun getGradeList(gradeClassify: String): Observable<AppResponse<List<Leader>>> {
        return workBenchApiService.getGradeList(gradeClassify)
    }

    override fun getTypeList(typeClassify: String): Observable<AppResponse<List<CustomerType>>> {
        return workBenchApiService.getTypeList(typeClassify)
    }

    override fun getCustomerDetail(gradeClassify: String): Observable<AppResponse<CustomerDetailBean>> {
        return workBenchApiService.getCustomerDetail(gradeClassify)
    }

    override fun getParnterDetail(partnerId: String): Observable<AppResponse<PartnerDetailBean>> {
        return workBenchApiService.getParnterDetail(partnerId)
    }



    override fun getParnterList(
        key: String?,
        partnerType: Int?,
        partnerGrade: Int?,
        page: Int,
        size: Int,
        menuId: Int
    ): Observable<AppResponse<PartnerBean>> {
        return workBenchApiService.getParnterList(key,partnerType,partnerGrade,page,size,menuId)
    }

    override fun getCustomerList(
        key: String?,
        customerGrade: Int?,
        customerType: Int?,
        page: Int,
        size: Int,
        menuId: Int
    ): Observable<AppResponse<CustomerBean>> {
        return workBenchApiService.getCustomerList(key,customerGrade,customerType,page,size,menuId)
    }

    override fun getEditRecord(type: Int?, id: Int?): Observable<AppResponse<List<CustomerRecord>>> {
        return workBenchApiService.getEditRecord(type,id)
    }

    override fun getReportTags(): Observable<AppResponse<List<ProjectLabel>>> {
        return workBenchApiService.getReportTags()
    }

    override fun getCustomerSingle(customerId: String): Observable<AppResponse<CustomerListBean>> {
        return workBenchApiService.getCustomerSingle(customerId)
    }

    override fun getParnterSigle(partnerId: String): Observable<AppResponse<PartnerListBean>> {
        return workBenchApiService.getParnterSigle(partnerId)
    }

    override fun closeItr(noteId: String): Observable<AppResponse<Any>> {
        return workBenchApiService.closeItr(noteId)
    }
}