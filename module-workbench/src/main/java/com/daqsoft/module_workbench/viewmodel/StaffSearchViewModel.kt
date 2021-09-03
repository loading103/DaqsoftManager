package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.content.res.Resources
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
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementItemViewModel
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
class StaffSearchViewModel : BaseViewModel<WorkBenchRepository>,Paging2Utils<ItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var fromSelect : Boolean = false

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
        R.layout.recyclerview_department_item
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
                        .searchEmployees(name = searchObservable.get()!!)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeSearch>>() {
                            override fun onSuccess(t: AppResponse<EmployeeSearch>) {
                                t.data?.let {

                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

                                    it.records.map {
                                        val employee = Employee(
                                            avartar =  it.employeeAvatar,
                                            id = it.employeeId,
                                            name = it.employeeName,
                                            postFullName = it.employeePost
                                        )
                                        employee
                                    }.forEach {
                                        observableList.add(DepartmentItemViewModel(this@StaffSearchViewModel, 0,it,fromSelect))
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
                        .searchEmployees(name = searchObservable.get()!!,current = params.key)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeSearch>>() {
                            override fun onSuccess(t: AppResponse<EmployeeSearch>) {
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.map {
                                        val employee = Employee(
                                            avartar =  it.employeeAvatar,
                                            id = it.employeeId,
                                            name = it.employeeName,
                                            postFullName = it.employeePost
                                        )
                                        employee
                                    }.forEach {
                                        observableList.add(DepartmentItemViewModel(this@StaffSearchViewModel,0, it,fromSelect))
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