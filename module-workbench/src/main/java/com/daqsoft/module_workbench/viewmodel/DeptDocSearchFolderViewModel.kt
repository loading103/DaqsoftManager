package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.fragment.app.activityViewModels
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFile
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 19/3/2021 上午 9:33
 * @author zp
 * @describe
 */
class DeptDocSearchFolderViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    override fun onCreate() {
        super.onCreate()
    }


    var backLiveData = MutableLiveData<Unit?>()

    override fun backOnClick() {
        backLiveData.value  = null
    }

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

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
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        observableList.clear()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFile>>() {
                    override fun onSuccess(t: AppResponse<DeptFile>) {
                        t.data?.let {

                            it.folders.forEach {
                                val itemViewModel = DeptDocFolderItemViewModel(this@DeptDocSearchFolderViewModel,it)
                                itemViewModel.multiItemType(ConstantGlobal.FOLDER)
                                observableList.add(itemViewModel)
                            }

                            it.files.forEach {
                                val itemViewModel = DeptDocFileItemViewModel(this@DeptDocSearchFolderViewModel,it)
                                itemViewModel.multiItemType(ConstantGlobal.FILE)
                                observableList.add(itemViewModel)
                            }
                        }
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
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        observableList.clear()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DeptFile>>() {
                    override fun onSuccess(t: AppResponse<DeptFile>) {
                        t.data?.let {

                            it.folders.forEach {
                                val itemViewModel = DeptDocFolderItemViewModel(this@DeptDocSearchFolderViewModel,it)
                                itemViewModel.multiItemType(ConstantGlobal.FOLDER)
                                observableList.add(itemViewModel)
                            }
                            it.files.forEach {
                                val itemViewModel = DeptDocFileItemViewModel(this@DeptDocSearchFolderViewModel,it)
                                itemViewModel.multiItemType(ConstantGlobal.FILE)
                                observableList.add(itemViewModel)
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