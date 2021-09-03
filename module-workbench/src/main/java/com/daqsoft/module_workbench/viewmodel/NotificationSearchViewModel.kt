package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.Notice
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.NotificationItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @ClassName    NotificationViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/19
 */
class NotificationSearchViewModel : ToolbarViewModel<WorkBenchRepository> ,
    Paging2Utils<ItemViewModel<*>> {


    var permission : MenuPermissionCover? = null

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(
        application,
        workBenchRepository
    )

    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_notification_item
    )

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
        dataSource?.invalidate()
    })

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
                        .getNoticeList(
                           title = searchObservable.get()
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Notice>>() {
                            override fun onSuccess(t: AppResponse<Notice>) {
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = NotificationItemViewModel(this@NotificationSearchViewModel,it,false)
                                        observableList.add(itemViewModel)
                                    }
                                    callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
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
                        .getNoticeList(
                            page = params.key,
                            title = searchObservable.get()
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Notice>>() {
                            override fun onSuccess(t: AppResponse<Notice>) {
                                t.data?.let {

                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = NotificationItemViewModel(this@NotificationSearchViewModel,it,false)
                                        observableList.add(itemViewModel)
                                    }
                                    callback.onResult(observableList,params.key+1)
                                }
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
}