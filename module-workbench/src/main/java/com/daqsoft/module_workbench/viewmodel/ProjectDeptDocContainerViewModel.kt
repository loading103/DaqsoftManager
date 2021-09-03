package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.DeptType
import com.daqsoft.module_workbench.repository.pojo.vo.FileCount
import com.daqsoft.module_workbench.repository.pojo.vo.MenuPermission
import com.daqsoft.module_workbench.utils.IMTimeUtils
import com.daqsoft.module_workbench.utils.MenuPermissionCoverUtils
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 16/3/2021 下午 5:26
 * @author zp
 * @describe
 */
class ProjectDeptDocContainerViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository : WorkBenchRepository): super(application, workBenchRepository)



    /**
     * 今天
     */
    val today = ObservableField<String>(IMTimeUtils.StringData())

    /**
     * 部门
     */
    var deptObservable = ObservableField<Employee>()

    /**
     * 文件数
     */
    val fileCount = ObservableField<FileCount>()

    /**
     * 类型
     */
    var dirTypeObservable = ObservableField<String>("全部文件")

    /**
     * 目录
     */
    var catalogList = ObservableArrayList<String>()

    var catalogObservable = ObservableField<SpannableStringBuilder>()


    /**
     * 搜索 liveData
     */
    val searchLiveData = MutableLiveData<Unit>()

    /**
     * 模式 0 列表   1 缩略图
     */
    val modeLiveData = MutableLiveData<Int>(0)



    /**
     * 文件总数
     * @param type Int
     */
    fun getNumberOfFiles(type:Int){
        addSubscribe(
            model
                .getNumberOfFiles(type,deptObservable.get()?.id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<FileCount>>() {
                    override fun onSuccess(t: AppResponse<FileCount>) {
                        t.data?.let {
                            fileCount.set(it)
                            Timber.e("fileCount   ${Gson().toJson(fileCount.get())}")
                            deptObservable.set(Employee("",it.orgId,it.orgName,""))
                        }
                    }

                })
        )
    }



    val refreshLiveData = MutableLiveData<Unit>()

    /**
     * 保存/修改 文件夹
     * @param folderType Int
     * @param folderName String
     * @param folderParentId Int
     */
    fun saveOrModifyFolder(id : Int?, folderType: Int, folderName: String, folderParentId: Int){
        addSubscribe(
            model
                .saveFolder(id,folderType, folderName, folderParentId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        // 刷新
                        refreshLiveData.value = null
                    }

                })
        )
    }


    /**
     * 删除文件夹
     * @param foldId Int
     */
    fun deleteFolder(foldId: Int){
        addSubscribe(
            model
                .deleteFolder(foldId)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        // 刷新
                        refreshLiveData.value = null
                    }

                })
        )
    }

    /**
     * 删除文件
     * @param fileId String
     * @param menuId String
     */
    fun deleteFile(fileId: Int,menuId : Int){
        addSubscribe(
            model
                .deleteFile(fileId,menuId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        // 刷新
                        refreshLiveData.value = null
                    }
                })
        )
    }


    /**
     * 上传文件
     * @param file File
     * @param folderId Int
     */
    fun uploadFiles(file: File, folderId : Int){
        addSubscribe(
            Observable.create(ObservableOnSubscribe<MultipartBody.Part> {
                val body = RequestBody.create(MediaType.get("multipart/form-data"), file)
                val part = MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    body
                )
                it.onNext(part)
            })
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<MultipartBody.Part, Observable<AppResponse<List<UploadResult>>>> {
                    return@Function model.uploadOSS(file = it)
                })
                .concatMap(Function<AppResponse<List<UploadResult>>, Observable<AppResponse<Any>>> {
                    var files = listOf<UploadResult>()
                    it.data?.let {
                        if (it.isNotEmpty()) {
                            files = it
                        }
                    }
                    Timber.e("Gson().toJson(files) ${Gson().toJson(files)}")
                    return@Function model.saveFile(Gson().toJson(files),folderId)
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    // 未执行 ？
//                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        // 刷新
                        refreshLiveData.value = null
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }




    var menuPermissions: List<MenuPermission>?=null
    /**
     * 获取操作权限
     * @param id Int
     */
    fun getMenuPermission(id:Int){
        addSubscribe(
            model
                .getMenuPermission(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MenuPermission>>>() {
                    override fun onSuccess(t: AppResponse<List<MenuPermission>>) {
                        t.data?.let {
                            menuPermissions = it
                        }
                    }
                })
        )
    }

    fun addPermission() : Boolean{
        if (dirTypeObservable.get()!!.contains("部门文件")){
            val menuPermission = menuPermissions?.find { it.opName == "新增文件夹" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }

        if (dirTypeObservable.get()!!.contains("公共文件")){
            val menuPermission = menuPermissions?.find { it.opName == "公共文件新增文件夹" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }
        return true
    }


    fun deletePermission(folder:Boolean):Boolean{
        if(folder){
            if (dirTypeObservable.get()!!.contains("部门文件")){
                val menuPermission = menuPermissions?.find { it.opName == "删除文件夹" }?.run{this.permisson==1}
                if (menuPermission == null || !menuPermission){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return false
                }
            }

            if (dirTypeObservable.get()!!.contains("公共文件")){
                val menuPermission = menuPermissions?.find { it.opName == "公共文件删除文件夹" }?.run{this.permisson==1}
                if (menuPermission == null || !menuPermission){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return false
                }
            }
        }else{
            if (dirTypeObservable.get()!!.contains("部门文件")){
                val menuPermission = menuPermissions?.find { it.opName == "删除文件" }?.run{this.permisson==1}
                if (menuPermission == null || !menuPermission){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return false
                }
            }

            if (dirTypeObservable.get()!!.contains("公共文件")){
                val menuPermission = menuPermissions?.find { it.opName == "公共文件删除文件" }?.run{this.permisson==1}
                if (menuPermission == null || !menuPermission){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return false
                }
            }
        }

        return true
    }

    fun uploadPermission():Boolean{
        if (dirTypeObservable.get()!!.contains("部门文件")){
            val menuPermission = menuPermissions?.find { it.opName == "上传" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }

        if (dirTypeObservable.get()!!.contains("公共文件")){
            val menuPermission = menuPermissions?.find { it.opName == "公共文件上传" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }
        return true
    }


    fun downloadPermission() : Boolean{
        if (dirTypeObservable.get()!!.contains("部门文件")){
            val menuPermission = menuPermissions?.find { it.opName == "下载文件" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }

        if (dirTypeObservable.get()!!.contains("公共文件")){
            val menuPermission = menuPermissions?.find { it.opName == "公共文件下载文件" }?.run{this.permisson==1}
            if (menuPermission == null || !menuPermission){
                ToastUtils.showShort("对不起，您无权操作！")
                return false
            }
        }
        return true
    }



    /**
     * 文件分类
     */
    val fileTypeDatas = MutableLiveData<List<DeptType>>()


    val totalFile = ObservableField<Int>()

    val totalFolder = ObservableField<Int>()

    fun getProjectBase(projectId:String){
        addSubscribe(
            model
                .getProjectBase(projectId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<DeptType>>>() {
                    override fun onSuccess(t: AppResponse<List<DeptType>>) {
                        totalFile.set(t?.totalFile)
                        totalFolder.set(t?.totalFolder)
                        fileTypeDatas.value= t.data?.toMutableList()?.apply {
                            add(0, DeptType(baseName = "全部文件",havechoose = true))
                        }
                    }
                })
        )
    }
}