package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CareThesausuItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 30/12/2020 上午 9:29
 * @author zp
 * @describe
 */
class CareThesauruSearchViewModel : BaseViewModel<WorkBenchRepository>,Paging2Utils<ItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    var statusBarHeight = ObservableField<Int>(AppUtils.getStatusBarHeight())

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
        R.layout.recyclerview_care_thesauru





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
                        .getCareListData(searchObservable.get())
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CareThesausuRoot>>() {
                            override fun onSuccess(t: AppResponse<CareThesausuRoot>) {
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = CareThesausuItemViewModel(this@CareThesauruSearchViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
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
                        .getCareListData(title = searchObservable.get(),page = params.key)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CareThesausuRoot>>() {
                            override fun onSuccess(t: AppResponse<CareThesausuRoot>) {
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = CareThesausuItemViewModel(this@CareThesauruSearchViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
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