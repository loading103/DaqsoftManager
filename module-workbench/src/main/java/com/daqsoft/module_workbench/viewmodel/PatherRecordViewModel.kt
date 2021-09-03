package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.library_common.utils.NextMessageOpenHelper
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CustomerRecordItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.PatherRecordItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.entity.LocalMedia
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

class PatherRecordViewModel : ToolbarViewModel<WorkBenchRepository>,Paging2Utils<ItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application, projectRepository: WorkBenchRepository):super(
        application,
        projectRepository
    )

    var projectId  = ""



    fun initToolbar(title:String) {
        setTitleText(title)
    }

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_pather_record_item
    )

    /**
     * 分页 差分器
     */
    var diff = createDiff()

    /**
     * 分页 数据监听
     */
    var pageList = createPagedList()

    /**
     * 分页 数据源
     */
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getPatherRecordList(
                            visitId = projectId
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object :
                            AppDisposableObserver<AppResponse<CustomerRecordBeanPaging>>() {
                            override fun onSuccess(t: AppResponse<CustomerRecordBeanPaging>) {
                                t.data?.let {

                                    val observableList: ObservableList<ItemViewModel<*>> =
                                        ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = PatherRecordItemViewModel(this@PatherRecordViewModel, it)
                                        observableList.add(itemViewModel)
                                    }
                                    callback.onResult(
                                        observableList,
                                        ConstantGlobal.INITIAL_PAGE,
                                        ConstantGlobal.INITIAL_PAGE + 1
                                    )
                                }
                            }

                        })
                )
            }

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getPatherRecordList(
                            visitId = projectId,
                            page = params.key
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerRecordBeanPaging>>() {
                            override fun onSuccess(t: AppResponse<CustomerRecordBeanPaging>) {
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = PatherRecordItemViewModel(this@PatherRecordViewModel,it)
                                        observableList.add(itemViewModel)
                                    }
                                    callback.onResult(observableList,params.key+1)
                                }
                            }

                            override fun onFailToast(): Boolean {
                                return false
                            }

                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }



    val projectLabelLiveData = MutableLiveData<List<ProjectLabel>>()
    /**
     * 获取项目标签
     */
    fun getProjectLabel(id: String){
        addSubscribe(
            model
                .getReportTags()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectLabel>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectLabel>>) {
                        t.data?.let {
                            projectLabelLiveData.value = it.map {
                                it.copy(name = if (it.name.startsWith("#") && it.name.endsWith("#")) it.name else "#${it.name}#")
                            }
                        }
                    }
                })
        )
    }

    
    val saveLiveData = MutableLiveData<Boolean>()

    /**
     * 保存项目动态
     */
    fun saveProjectDynamics(
        imageList: List<LocalMedia>? = arrayListOf(),
        fileList: List<String>? = arrayListOf(),
        alertId: List<Int>? = arrayListOf(),
        tagIds: List<Int>? = arrayListOf(),
        replyId: String? = "",
        itrState: Boolean? = false,
        noteInfo: String? = "",
        visitId: Int? = null,
        noteId :String?=""
    ){
        addSubscribe(
            Observable.create(ObservableOnSubscribe<List<MultipartBody.Part>> {
                val parts = arrayListOf<MultipartBody.Part>()
                if (!imageList.isNullOrEmpty()) {
                    imageList.forEach {
                        val filePath = if(it.realPath.isNullOrBlank()) it.path else it.realPath
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(filePath))
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                        var options = 90
                        while (stream.toByteArray().size / 1024 > (1024 * 5)) {
                            stream.reset()
                            bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)
                            options -= 10
                        }
                        val body = RequestBody.create(
                            MediaType.get("image/png"),
                            stream.toByteArray()
                        )
                        val part = MultipartBody.Part.createFormData(
                            "file",
                            "${System.currentTimeMillis()}.jpg",
                            body
                        )
                        parts.add(part)
                        bitmap.recycle()
                    }
                }

                if (!fileList.isNullOrEmpty()) {
                    fileList.forEach {
                        val file = File(it)
                        val body = RequestBody.create(MediaType.get("multipart/form-data"), file)
                        val part = MultipartBody.Part.createFormData(
                            "file",
                            file.name,
                            body
                        )
                        parts.add(part)
                    }
                }

                if (parts.isEmpty()) {
                    val part = MultipartBody.Part.createFormData("", "")
                    parts.add(part)
                }

                it.onNext(parts)

            })
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<List<MultipartBody.Part>, Observable<AppResponse<List<UploadResult>>>> {
                    return@Function model.uploadOSSMultis(parts = it)
                })
                .concatMap(Function<AppResponse<List<UploadResult>>, Observable<AppResponse<Any>>> {
                    val request = CustomerRecordRequest(
                        files = if (it.data.isNullOrEmpty()) "" else Gson().toJson(it.data),
                        noteInfo = noteInfo,
                        itrState = itrState,
                        visitId = projectId,
                        alertId = alertId,
                        tagIds = tagIds,
                        replyId = replyId
                    )
                    return@Function model.savePartnerRecord(request)
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        saveLiveData.value = true
                        if (!replyId.isNullOrBlank() && !noteId.isNullOrBlank()){
                            getProjectDynamicsItem(noteId)
                            return
                        }
                        dataSource?.invalidate()
                        LiveEventBus.get(LEBKeyGlobal.PATNER_SENT_SUCCESSFULLY).post(true)

                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                        saveLiveData.value = false
                    }
                })
        )
    }




    /**
     * 获取单条  项目动态
     */
    val refreshItemLiveData = MutableLiveData<CustomerRecordBean>()
    fun getProjectDynamicsItem(id:String){
        addSubscribe(
            model
                .getCustomerRecordItem(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerRecordBean>>() {
                    override fun onSuccess(t: AppResponse<CustomerRecordBean>) {
                        t.data?.let {
                            refreshItemLiveData.value = it
                            LiveEventBus.get(LEBKeyGlobal.PATNER_SENT_SUCCESSFULLY).post(true)
                        }

                    }
                })
        )
    }


    var nextLiveData=MutableLiveData<NextMessage>()
    var nextId:String=""
    fun getNextDetailData(nextIds: String) {
        nextId=nextIds;
    }

    /**
     * 下一条是否可见
     */
    var nextVisible=ObservableField<Int>(View.GONE)
    val NextOnClick = BindingCommand<String>(BindingAction {
        addSubscribe(
            model
                .getNextDetail(nextId.toInt())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NextMessage>>() {
                    override fun onSuccess(it: AppResponse<NextMessage>) {
                        nextLiveData.value=it.data
                        if(nextLiveData?.value==null ){
                            return
                        }
                        NextMessageOpenHelper.pageJump(nextLiveData?.value!!.next,nextLiveData?.value?.templateCode,nextLiveData?.value!!.messageExtId)
                    }
                })
        )

    })
}