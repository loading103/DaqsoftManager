package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFile
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileRoot
import com.daqsoft.module_workbench.repository.pojo.vo.DeptType
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 7/2/2021 上午 11:11
 * @author zp
 * @describe 部门文件 viewModel
 */
class ProjectDeptDocViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var netErrorLiveData = MutableLiveData<Unit?>()

    lateinit var containerViewModel : ProjectDeptDocContainerViewModel

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.bmwj_search)
        setTitleRightIconVisibleObservable(View.VISIBLE)
        setTitleText("文件库")
//        setTitleRightIconObservable(R.mipmap.bmwj_arrow_down)
    }

    override fun backOnClick() {
        containerViewModel.backOnClick()
    }

    override fun rightIconOnClick() {
        containerViewModel.searchLiveData.value = null
    }


    /**
     * 数据源
     */
    var deptFileLiveData  = MutableLiveData<MutableList<DeptFileInfo>>()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.FOLDER -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_folder)
            ConstantGlobal.FILE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_file)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_folder)
        }
    }

    var refreshLiveData = MutableLiveData<Boolean>()

    /**
     * 获取/搜索 部门/公共文件夹信息
     * @param folderId Int?
     * @param deptId Int?
     * @param type Int?
     * @param title String?
     */
   fun getDeptOrPublicFileInfo(projectId: Int?,docId: Int?,startTime: String?,endTime: String?,fileName:String?,userName:String?){
       addSubscribe(
           model
               .getProDeptOrPublicFileInfo(projectId,docId,startTime,endTime,fileName,userName)
               .compose(RxUtils.schedulersTransformer())
               .compose(RxUtils.exceptionTransformer())
               .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFileRoot>>() {
                   override fun onSuccess(t: AppResponse<DeptFileRoot>) {
                       refreshLiveData.value = true
                       t.data.let {
                           it?.datas.let {
                               deptFileLiveData.value = it?.toMutableList().apply {
                                   it?.forEach{
                                       it.employeeName=it.userName
                                       it.fileDowns=it.downloadTimes
                                       it.uploadDate=it.uploadTime

                                   }
                               }
                           }
                       }
                   }

                   override fun onFail(e: Throwable) {
                       super.onFail(e)
                       refreshLiveData.value = false
                       netErrorLiveData.value = null
                   }

               })
       )
   }




    val downloadLiveData = MutableLiveData<Pair<DeptFileInfo,String>>()
    /**
     * 下载
     * @param id String
     */
    fun downloadFile(deptFileInfo: DeptFileInfo){
        addSubscribe(
            model
                .downloadFiles(arrayListOf(deptFileInfo.id.toString()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFileInfo>>() {
                    override fun onSuccess(t: AppResponse<DeptFileInfo>) {

                        t.data?.let {
                            val url =it.downUrl
                            downloadLiveData.value = Pair(deptFileInfo,url?:"")
                        }

                    }
                })
        )
    }



    val previewLiveData = MutableLiveData<Unit?>()
    /**
     * 预览文件
     */
    fun previewFile(fileId:String){
        addSubscribe(
            model
                .previewFiles(fileId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFileInfo>>() {
                    override fun onSuccess(t: AppResponse<DeptFileInfo>) {
                        previewLiveData.value = null
                    }
                })
        )
    }

}