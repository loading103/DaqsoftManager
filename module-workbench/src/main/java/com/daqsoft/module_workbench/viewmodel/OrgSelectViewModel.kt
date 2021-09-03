package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableArrayList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository

import com.daqsoft.module_workbench.repository.pojo.vo.Member
import com.daqsoft.module_workbench.repository.pojo.vo.OrgSimple
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 3/3/2021 下午 4:30
 * @author zp
 * @describe  组织选择 viewModel
 */
class OrgSelectViewModel : BaseSelectViewModel<WorkBenchRepository>, Paging2Utils<MultiItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)


    val backLiveData = MutableLiveData<Unit?>()

    override fun backOnClick() {
        backLiveData.value = null
    }

    var current : Employee? = null


    override fun onCreate() {
        super.onCreate()

        initToolbar()
    }

    private fun initToolbar() {
        if (!BaseSelectViewModel.orgSingleChoice){
            setRightTextVisible(View.VISIBLE)
            setRightTextColor(R.color.red_fa4848)
            setRightTextTxt("全选")
        }
    }

    var selectAll = false
    val companyWide = Employee(  "",0,"全公司","")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun rightTextOnClick() {
        when (BaseSelectViewModel.type) {
            0 ->{
                if (!selectAll){
                    addOrgAllStaff(current?:companyWide,fromSelectAll = true)
                }else{
                    removeOrgAllStaff(current?:companyWide,fromSelectAll = true)
                }
            }
            1 -> {
                if (!selectAll){
                    addOrgAllOrg(current?:companyWide,fromSelectAll = true)
                }else{
                    removeOrgAllOrg(current?:companyWide,fromSelectAll = true)
                }
            }
        }
    }


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =  ItemBinding.of { itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.STAFF -> itemBinding.set(
                BR.viewModel,
                R.layout.recyclerview_staff_select_item
            )
            ConstantGlobal.ORG -> itemBinding.set(
                BR.viewModel,
                R.layout.recyclerview_org_select_item
            )
        }
    }

    val multipleLayoutLiveData = MutableLiveData<String>()

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
    var dataSource : DataSource<Int, MultiItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, MultiItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, MultiItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, MultiItemViewModel<*>>
            ) {

                addSubscribe(
                    model
                        .getMember(current?.id?:0)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Member>>() {
                            override fun onSuccess(t: AppResponse<Member>) {
                                t.data?.let {

                                    val observableList = ObservableArrayList<MultiItemViewModel<*>>()
                                    if (BaseSelectViewModel.type == 0) {
                                        it.employee.forEach {
                                            observableList.add(
                                                StaffSelectItemViewModel(
                                                    this@OrgSelectViewModel,
                                                    it
                                                ).apply {
                                                    multiItemType(ConstantGlobal.STAFF)
                                                }
                                            )
                                        }
                                    }

                                    it.org.map {
                                        Employee(
                                            avartar ="",
                                            id = it.id,
                                            name =  it.organizationName,
                                            postFullName = "",
                                            type = 1
                                        )
                                    }.forEach {
                                        observableList.add(
                                            OrgSelectItemViewModel(this@OrgSelectViewModel, it).apply {
                                                multiItemType(ConstantGlobal.ORG)
                                            }
                                        )
                                    }

                                    if (observableList.isEmpty()){
                                        setRightTextVisible(View.GONE)
                                        multipleLayoutLiveData.value = MultipleLayoutManager.EMPTY
                                    }else{
                                        if(!BaseSelectViewModel.orgSingleChoice){
                                            setRightTextVisible(View.VISIBLE)
                                        }
                                        multipleLayoutLiveData.value = MultipleLayoutManager.SUCCESS
                                    }

                                    callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                                multipleLayoutLiveData.value = MultipleLayoutManager.ERROR
                            }
                        })
                )
            }

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }




    val checkSelection = MutableLiveData<Boolean>()


    /**
     * 部门下所有员工
     */
    val orgAllStaff : HashMap<Employee,List<Employee>> by lazy { hashMapOf<Employee,List<Employee>>() }

    /**
     * 获取部门下所有员工
     */
    fun addOrgAllStaff(employee: Employee,remove : Boolean  = false,fromSelectAll : Boolean = false){
        val sunAllStaff =  orgAllStaff[employee]
        if (sunAllStaff != null){
            val add = sunAllStaff.filter {
                !selectedStaffSet.map { it.id }.contains(it.id)
            }
            selectedStaffSet.addAll(add)
            LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("add")

            if (fromSelectAll){
                selectAll = true
                setRightTextTxt("取消全选")
            }

            checkSelection.value = fromSelectAll

            return
        }
        addSubscribe(
            model
                .getOrgAllEmployees(employee.id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<Employee>>>() {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onSuccess(t: AppResponse<List<Employee>>) {
                        t.data?.let {
                            orgAllStaff[employee] = it

                            if (remove){
                                selectedStaffSet.removeIf { employee->
                                    it.map { it.id }.contains(employee.id)
                                }
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("remove")

                                if (fromSelectAll){
                                    selectAll = false
                                    setRightTextTxt("全选")
                                }

                            }else{
                                val add = it.filter {
                                    !selectedStaffSet.map { it.id }.contains(it.id)
                                }
                                selectedStaffSet.addAll(add)
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("add")

                                if (fromSelectAll){
                                    selectAll = true
                                    setRightTextTxt("取消全选")
                                }
                            }
                            checkSelection.value = fromSelectAll
                        }
                    }
                })
        )
    }

    /**
     * 移除部门下所有员工
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeOrgAllStaff(employee: Employee,fromSelectAll : Boolean = false){
        val subAllStaff = orgAllStaff[employee]
        if (subAllStaff != null){
            selectedStaffSet.removeIf { employee->
                subAllStaff.map { it.id }.contains(employee.id)
            }
            LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("remove")

            if (fromSelectAll){
                selectAll = false
                setRightTextTxt("全选")
            }
            checkSelection.value = fromSelectAll
            return
        }

        addOrgAllStaff(employee,true,fromSelectAll = fromSelectAll)
    }


    /**
     * 部门下所有部门
     */
    val orgAllOrg : HashMap<Employee,List<Employee>> by lazy { hashMapOf<Employee,List<Employee>>() }

    /**
     * 获取部门下所有部门
     */
    fun addOrgAllOrg(employee: Employee,remove : Boolean  = false,fromSelectAll : Boolean = false){
        val sunAllOrg =  orgAllOrg[employee]
        if (sunAllOrg != null){
            val add = sunAllOrg.filter {
                !selectedOrgSet.map { it.id }.contains(it.id)
            }
            selectedOrgSet.addAll(add)

            if (BaseSelectViewModel.type == 1){
                LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("add")
            }

            if (fromSelectAll){
                selectAll = true
                setRightTextTxt("取消全选")
            }

            checkSelection.value = fromSelectAll

            return
        }
        addSubscribe(
            model
                .getOrgAllOrg(employee.id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<OrgSimple>>>() {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onSuccess(t: AppResponse<List<OrgSimple>>) {
                        t.data?.let {
                            orgAllOrg[employee] = it.map {
                                val names = it.orgName.split("-")
                                Employee(
                                    avartar = "",
                                    id = it.id,
                                    name = names[names.size-1],
                                    postFullName = "",
                                    type = 1
                                )
                            }

                            if (remove) {
                                selectedOrgSet.removeIf { employee ->
                                    it.map { it.id }.contains(employee.id)
                                }
                                if (BaseSelectViewModel.type == 1){
                                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("remove")
                                }

                                if (fromSelectAll){
                                    selectAll = false
                                    setRightTextTxt("全选")
                                }

                            } else {
                                val add = it.map {
                                    val names = it.orgName.split("-")
                                    Employee(
                                        avartar = "",
                                        id = it.id,
                                        name = names[names.size-1],
                                        postFullName = "",
                                        type = 1
                                    )
                                }.filter {
                                    !selectedOrgSet.map { it.id }.contains(it.id)
                                }
                                selectedOrgSet.addAll(add)
                                if (BaseSelectViewModel.type == 1){
                                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("add")
                                }

                                if (fromSelectAll){
                                    selectAll = true
                                    setRightTextTxt("取消全选")
                                }

                            }
                            checkSelection.value = fromSelectAll
                        }
                    }
                })
        )
    }


    /**
     * 移除部门下所有部门
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeOrgAllOrg(employee: Employee,fromSelectAll : Boolean = false){
        val subAllOrg = orgAllOrg[employee]
        if (subAllOrg != null){
            selectedOrgSet.removeIf { employee->
                subAllOrg.map { it.id }.contains(employee.id)
            }

            if (BaseSelectViewModel.type == 1){
                LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("remove")
            }

            if (fromSelectAll){
                selectAll = false
                setRightTextTxt("全选")
            }

            checkSelection.value = fromSelectAll

            return
        }
        addOrgAllOrg(employee,true,fromSelectAll = true)
    }
}