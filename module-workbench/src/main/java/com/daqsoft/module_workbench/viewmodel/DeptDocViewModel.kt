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
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 7/2/2021 上午 11:11
 * @author zp
 * @describe 部门文件 viewModel
 */
class DeptDocViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var netErrorLiveData = MutableLiveData<Unit?>()

    lateinit var containerViewModel : DeptDocContainerViewModel

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setRightIcon2Visible(View.VISIBLE)
        setRightIcon2Src(R.mipmap.bmwj_search)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.ellipsis)
        setTitleRightIconVisibleObservable(View.VISIBLE)
        setTitleRightIconObservable(R.mipmap.bmwj_arrow_down)
    }

    override fun backOnClick() {
        containerViewModel.backOnClick()
    }

    override fun rightIconOnClick() {
        containerViewModel.moreLiveData.value = null
    }

    override fun rightIcon2OnClick() {
        containerViewModel.searchLiveData.value = null
    }


    /**
     * 数据源
     */
    var deptFileLiveData  = MutableLiveData<DeptFile>()

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
   fun getDeptOrPublicFileInfo(folderId: Int?,deptId: Int?,type: Int?,title:String?){
       addSubscribe(
           model
               .getDeptOrPublicFileInfo(folderId,deptId,type,title)
               .compose(RxUtils.schedulersTransformer())
               .compose(RxUtils.exceptionTransformer())
               .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFile>>() {
                   override fun onSuccess(t: AppResponse<DeptFile>) {
                       refreshLiveData.value = true
                       t.data?.let {
                           deptFileLiveData.value = it
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

    /**
     * 获获取/搜索 共享文件夹信息
     * @param folderId Int?
     * @param deptId Int?
     * @param title String?
     */
    fun getShareFileInfo(folderId: Int?,deptId: Int?,title:String?){
        addSubscribe(
            model
                .getShareFileInfo(folderId,deptId,title)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFile>>() {
                    override fun onSuccess(t: AppResponse<DeptFile>) {
                        refreshLiveData.value = true
                        t.data?.let {
                            deptFileLiveData.value = it
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
                .downloadFile(arrayListOf(deptFileInfo.id.toString()))
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Map<String,List<String>>>>() {
                    override fun onSuccess(t: AppResponse<Map<String,List<String>>>) {

                        t.data?.let {
                            val url = it["downUrl"]
                            downloadLiveData.value = Pair(deptFileInfo,url?.get(0)?:"")
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
                .previewFile(fileId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        previewLiveData.value = null
                    }
                })
        )
    }
}