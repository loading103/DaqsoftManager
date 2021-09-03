package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.Importance
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.FileInputStream

/**
 * @ClassName    AddNotificationViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/20
 */
class NotificationAddViewModel :ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(
        application,
        workBenchRepository
    )

    var id:String? = null


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("添加通知公告")
    }


    val levelList = arrayListOf<Importance>(
        Importance.RED,
        Importance.ORANGE,
        Importance.BLUE,
        Importance.GREEN
    )
    /**
     * 等级
     */
    val levelObservable = ObservableField<Importance>()
    /**
     * 等级选择liveData
     */
    val levelLiveData = MutableLiveData<Unit>()
    /**
     * 等级选择
     */
    val levelOnClick = BindingCommand<Unit>(BindingAction {
        levelLiveData.value = null
    })


    /**
     * 类型
     */
    val typeObservable = ObservableField<NoticeType>()

    /**
     * 类型
     */
    val typeLiveData = MutableLiveData<List<NoticeType>>()
    /**
     * 类型点击
     */
    var typeOnClickLiveData = MutableLiveData<Unit>()

    /**
     * 类型点击
     */
    val typeOnClick = BindingCommand<Unit>(BindingAction {
        typeOnClickLiveData.value = null
    })


    /**
     * 标题
     */
    val titleObservable = ObservableField<String>()

    /**
     * 内容
     */
    val contentObservable = ObservableField<String>()


    /**
     * 推送对象点击事件
     */
    val pushObjectOnClick = BindingCommand<Unit>(BindingAction {
        pushObjectLiveData.value = null
    })

    val pushObjectList = arrayListOf<NoticeOrganization>()
    val pushObjectLiveData = MutableLiveData<Unit>()


    val cameraLiveData =  MutableLiveData<Unit>()
    /**
     * 相机点击事件
     */
    val cameraOnClick = BindingCommand<Unit>(BindingAction {
        cameraLiveData.value = null
    })

    val albumLiveData =  MutableLiveData<Unit>()
    /**
     * 相册点击事件
     */
    val albumOnClick = BindingCommand<Unit>(BindingAction {
        albumLiveData.value = null
    })

    /**
     * 选择的图片
     */
    var selectImage:MutableList<LocalMedia> = arrayListOf()

    // 处理推送人员
    val handlePush = MutableLiveData<Unit>()
    // 处理图片选择
    val handleImage = MutableLiveData<Unit>()

    /**
     * 存为草稿
     */
    var saveAsDraft = BindingCommand<Unit>(BindingAction {
        saveOrUpdateNotice("ToSubmit")
    })

    /**
     * 保存并提交
     */
    var saveAndSubmit = BindingCommand<Unit>(BindingAction {
        saveOrUpdateNotice("ToAudit")
    })

    /**
     * 获取公告类型
     */
    fun getNoticeType(){
        addSubscribe(
            model
                .getNoticeType()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<NoticeType>>>() {
                    override fun onSuccess(t: AppResponse<List<NoticeType>>) {
                        t.data?.let {
                            typeLiveData.value = it.filter { !it.deleted }
                            if (id !=null){
                                getNoticeDetail(id!!)
                            }
                        }
                    }

                })
        )
    }


    /**
     * 保存 或 更新 （id==null 保存  !=null 更新）
     * @param saveState String ToSubmit("保存为草稿"), ToAudit("保存并提交审核")
     */
    fun saveOrUpdateNotice(saveState: String){

        if (levelObservable.get() == null){
            ToastUtils.showShort("请选择公告等级")
            return
        }

        if (typeObservable.get() == null){
            ToastUtils.showShort("请选择公告类型")
            return
        }

        if(titleObservable.get().isNullOrEmpty()){
            ToastUtils.showShort("请输入标题")
            return
        }

        if(contentObservable.get().isNullOrEmpty()){
            ToastUtils.showShort("请输入文字说明")
            return
        }

        if (pushObjectList.isEmpty()){
            ToastUtils.showShort("请选择推送对象")
            return
        }

        addSubscribe(

        Observable.create(ObservableOnSubscribe<Map<String, RequestBody>> {
            val paramsMap: HashMap<String, RequestBody> = hashMapOf()
            val list: MutableList<LocalMedia> = if(id == null){
                selectImage
            }else{
                selectImage
                    .filter {
                        !(it.path.startsWith("http://") || it.path.startsWith("https://"))
                    }.toMutableList()
            }
            list.forEach {
                val filePath = if(it.realPath.isNullOrBlank()) it.path else it.realPath
                val bitmap = BitmapFactory.decodeStream(FileInputStream(filePath))
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                var options = 90
                while (stream.toByteArray().size / 1024 > (1024 * 5)){
                    stream.reset()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)
                    options -= 10
                }
                val body = RequestBody.create(MediaType.get("image/png"), stream.toByteArray())
                val name = "file\"; filename=\"${System.currentTimeMillis()}.jpg"
                paramsMap[name] = body
                bitmap.recycle()
            }
            if(paramsMap.isEmpty()){
                val part = MultipartBody.Part.createFormData("", "")
                val name = ""
                paramsMap[name] = part.body()
            }
            it.onNext(paramsMap)
        })
            .doOnSubscribe{
                Handler(Looper.getMainLooper()).post {
                    showLoadingDialog()
                }
            }
            .concatMap(Function<Map<String, RequestBody>,Observable<AppResponse<List<UploadResult>>>>{
                return@Function model.uploadOSSMulti(params = it)
            })
            .concatMap(Function<AppResponse<List<UploadResult>>,Observable<AppResponse<Any>>>{
                val noticeOrganizationList = if(isAllDept) allDept.map {
                    NoticeOrganization(it.id.toString(),it.orgName)
                }.toMutableList() else pushObjectList

                val saveNotice = SaveNotice(
                    noticeContent = null,
                    noticeOutline = contentObservable.get()!!,
                    noticeImportance = levelObservable.get()!!.color,
                    noticeTitle = titleObservable.get()!!,
                    noticeType = typeObservable.get()!!.id,
                    noticeOrganizationList = pushObjectList,
                    noticeStatus = saveState,
                    fileList = arrayListOf(),
                    id = id
                )
                // 添加上传的图片
                it.data?.let {
                    saveNotice.fileList.addAll(it.map {
                        NoticeFile(
                            fileUrl = it.url,
                            fileTitle = it.name
                        )
                    })
                }
                // 如果是修改 则添加原有的图片 执行修改
                if(id != null) {
                    saveNotice.fileList.addAll(selectImage.filter {
                        it.path.startsWith("http://") || it.path.startsWith("https://")
                    }.map {
                        NoticeFile(
                            fileUrl = it.path,
                            fileTitle = it.fileName,
                            id = it.id.toString()
                        )
                    })
                }
                return@Function model.saveNotice(saveNotice)
            })
            .compose(RxUtils.exceptionTransformer())
            .compose(RxUtils.schedulersTransformer())
            .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                override fun onSuccess(t: AppResponse<Any>) {
                    dismissLoadingDialog()
                    LiveEventBus.get(LEBKeyGlobal.ADDED_NOTICE_SUCCESSFUL).post(true.toString())
                    finish()
                }

                override fun onFail(e: Throwable) {
                    super.onFail(e)
                    dismissLoadingDialog()
                }
            })
        )
    }


    /**
     * 获取详情
     * @param id String
     */
    fun getNoticeDetail(id:String){
        addSubscribe(
            model
                .getNoticeDetail(id,false)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NoticeDetail>>(){
                    override fun onSuccess(t: AppResponse<NoticeDetail>) {
                        t.data?.let { detail->
                            // 等级
                            levelList.find { it.color == detail.noticeImportance }?.let {
                                levelObservable.set(it)
                            }
                            // 类型
                            typeLiveData.value?.let {
                                it.find { it.typeName == detail.noticeTypeName }?.let {
                                    typeObservable.set(it)
                                }
                            }
                            // 标题
                            titleObservable.set(detail.noticeTitle)
                            // 内容
                            contentObservable.set(detail.noticeOutline)
                            // 推送对象
                            if(detail.noticeOrganizationList.isEmpty()){
                                isAllDept = true
                                pushObjectList.add(companyWide)
                            }else{
                                isAllDept = false
                                pushObjectList.addAll(detail.noticeOrganizationList)
                            }
                            handlePush.value = null
                            // 图片
                            selectImage.addAll(detail.fileList.map {
                                val localMedia = LocalMedia()
                                localMedia.path = it.fileUrl
                                localMedia.fileName = it.fileTitle
                                localMedia.id = it.id?.toLong()?:-1
                                localMedia.mimeType = PictureMimeType.MIME_TYPE_IMAGE
                                return@map localMedia
                            })
                            handleImage.value = null
                        }
                    }

                })
        )
    }


    /**
     * 是否全公司
     */
    val companyWide = NoticeOrganization(  "0","全公司")
    var isAllDept = false

    /**
     * 全部部门
     */
    var allDept = mutableListOf<OrgSimple>()

    /**
     * 获取全公司部门
     */
    fun getAllDept(){
        addSubscribe(
            model
                .getOrgAllOrg(0)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<OrgSimple>>>() {
                    override fun onSuccess(t: AppResponse<List<OrgSimple>>) {
                        t.data?.let {
                            allDept.addAll(it)
                        }
                    }
                })
        )
    }
}