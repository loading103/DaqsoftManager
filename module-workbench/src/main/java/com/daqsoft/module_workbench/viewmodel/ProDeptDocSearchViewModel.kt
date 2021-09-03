package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFile
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileRoot
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ProDeptDocSearchViewModel : ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    var projectId:String? = null

    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })

    /**
     * 搜索内容
     */
    val searchObservable = ObservableField<String>("")

    val changedLiveData = MutableLiveData<Boolean>(false)

    /**
     * 输入框监听
     */
    var searchTextChanged = BindingCommand<String>(BindingConsumer {
        if (!changedLiveData.value!!){
            changedLiveData.value = true
        }

        if(it.isNullOrBlank()){
            return@BindingConsumer
        }

        getDeptOrPublicFileInfo(projectId = projectId?.toInt(),fileName = searchObservable.get())


    })


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
//            ConstantGlobal.FOLDER -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_folder)
            ConstantGlobal.FILE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_file)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_file)
        }
    }

    /**
     * 获取/搜索 部门/公共文件夹信息
     * @param folderId Int?
     * @param deptId Int?
     * @param type Int?
     * @param title String?
     */
    fun getDeptOrPublicFileInfo(projectId: Int?,fileName:String?){
        addSubscribe(
            model
                .getProDeptOrPublicFileInfo(projectId=projectId,fileName=fileName)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        observableList.clear()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFileRoot>>() {
                    override fun onSuccess(t: AppResponse<DeptFileRoot>) {
                        t.data?.let {
                            it?.datas.let {

                                it.forEach {
                                    val itemViewModel = DeptDocFileItemViewModel(this@ProDeptDocSearchViewModel,it.apply {
                                        it.employeeName=it.userName
                                        it.fileDowns=it.downloadTimes
                                        it.uploadDate=it.uploadTime
                                    })
                                    itemViewModel.multiItemType(ConstantGlobal.FILE)
                                    observableList.add(itemViewModel)
                                }

                            }

                        }
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
                        getDeptOrPublicFileInfo(projectId = projectId?.toInt(),fileName = searchObservable.get())
                    }
                })
        )
    }
}