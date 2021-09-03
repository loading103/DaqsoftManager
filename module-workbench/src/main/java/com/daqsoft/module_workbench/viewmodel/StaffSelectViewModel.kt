package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.EmployeeSearch
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffSelectItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 3/3/2021 下午 4:30
 * @author zp
 * @describe  员工选择 viewModel
 */
class StaffSelectViewModel : BaseSelectViewModel<WorkBenchRepository>, Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)

    var selectAll = false

    val backLiveData = MutableLiveData<Unit?>()

    override fun onCreate() {
        initToolbar()
    }

    fun initToolbar() {
        setTitleText("员工选择")

        if(BaseSelectViewModel.type == 0 && BaseSelectViewModel.projectId != null) {
            setRightTextVisible(View.VISIBLE)
            setRightTextColor(R.color.red_fa4848)
            setRightTextTxt("全选")
        }
    }

    override fun backOnClick() {
        backLiveData.value = null
    }

    val rightTextLiveData = MutableLiveData<Boolean>()

    override fun rightTextOnClick() {
        if(BaseSelectViewModel.type == 0 && BaseSelectViewModel.projectId != null) {
            if (selectAll){
                selectAll = false
                setRightTextTxt("全选")
            }else{
                selectAll = true
                setRightTextTxt("取消全选")
            }
            rightTextLiveData.value = selectAll
        }
    }


    val searchLiveData = MutableLiveData<Unit>()
    /**
     * 搜索点击事件
     */
    val searchOnClick = BindingCommand<Unit>(BindingAction {
        searchLiveData.value = null
    })

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel,R.layout.recyclerview_staff_select_item)

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
                //获取@员工
                if(!BaseSelectViewModel.projectId.isNullOrBlank()){
                    addSubscribe(
                        model
                            .getATailStaff(BaseSelectViewModel.projectId!!)
                            .compose (RxUtils.schedulersTransformer())
                            .compose (RxUtils.exceptionTransformer())
                            .subscribeWith(object : AppDisposableObserver<AppResponse<List<Map<String,String>>>>() {
                                override fun onSuccess(t: AppResponse<List<Map<String,String>>>) {
                                    t.data?.let {

                                        val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

                                        it.map {
                                            val employee = Employee(
                                                avartar =  it["employeeAvatar"].toString(),
                                                id = it["id"].toString().toInt(),
                                                name = it["employeeName"].toString(),
                                                postFullName = it["postFullName"].toString()
                                            )
                                            employee
                                        }.forEach {
                                            observableList.add(StaffSelectItemViewModel(this@StaffSelectViewModel, it))
                                        }
                                        callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                    }
                                }
                            })
                    )
                    return
                }


                addSubscribe(
                    model
                        .searchEmployees()
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
                                        observableList.add(StaffSelectItemViewModel(this@StaffSelectViewModel, it))
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
                //获取@员工
                if(!BaseSelectViewModel.projectId.isNullOrBlank()){
                    return
                }

                addSubscribe(
                    model
                        .searchEmployees(current = params.key)
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
                                        observableList.add(StaffSelectItemViewModel(this@StaffSelectViewModel, it))
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