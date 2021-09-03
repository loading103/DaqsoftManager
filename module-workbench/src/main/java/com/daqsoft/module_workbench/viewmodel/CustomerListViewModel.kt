package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewCustomerRecordItemBinding
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CustomerItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CustomerRecordItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CustomerlHeadIcon
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.TagDetailItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.luck.picture.lib.tools.ToastUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

class CustomerListViewModel : ToolbarViewModel<WorkBenchRepository> ,
    Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository) : super(
        application,
        workBenchRepository
    )


    /**
     * 刷新完成
     */
    val refreshCompleteLiveData = MutableLiveData<Boolean>()

    val rightLiveData = MutableLiveData<String>()

    /**
     * 打电话
     */
    val callLiveData = MutableLiveData<String>()

    val leftLiveData = MutableLiveData<String>()

    val leftTextObserFild = ObservableField<String>("全部类型")

    val rightTextObserFild = ObservableField<String>("全部等级")

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_customer_item
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("客户管理")
        setTitleTextColor(R.color.color_333333)
        setRightIcon2Visible(View.VISIBLE)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.bmwj_more_add)
        setRightIcon2Src(R.mipmap.txl_list_search_black)
    }

    override fun rightIconOnClick() {
        var website: String = HttpGlobal.DAILY_ADD_CUSTOMER
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url", website)
            .navigation()
    }

    override fun rightIcon2OnClick() {
        var colorJson = ""
        if (!typeDatas.value.isNullOrEmpty()) {
            colorJson = Gson().toJson(typeDatas.value)
        }
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_CUSTOMER_SEARCH)
            .withString("colorJson", colorJson)
            .navigation()
    }

    /**
     * 排序按钮
     */
    var onOrderClick = BindingCommand<Any>(BindingAction {
        leftLiveData.value = ""
    })

    /**
     * 筛选按钮
     */
    var onChooseClick = BindingCommand<Any>(BindingAction {
        rightLiveData.value = ""
    })


    /**
     * 回访记录点击事件
     */
    var recordOnClick = BindingCommand<Any>(BindingAction {
        ToastUtils.s(BaseApplication.getInstance()?.baseContext, "回访记录")
    })

    /**
     * 获取等级	0-客户 1-伙伴
     */
    var typeDatas = MutableLiveData<MutableList<CustomerType>>()
    var chooseType = MutableLiveData<CustomerType>()
    fun getTypeList() {
        addSubscribe(
            model.getTypeList(typeClassify = "0")
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<CustomerType>>>() {
                    override fun onSuccess(t: AppResponse<List<CustomerType>>) {
                        t.datas?.let {

                            it.forEachIndexed { index, bean ->
                                when (index % 8) {
                                    0 -> bean.color = "fa4848"
                                    1 -> bean.color = "ff9e05"
                                    2 -> bean.color = "23c070"
                                    3 -> bean.color = "25a2f1"
                                    4 -> bean.color = "a94cf8"
                                    5 -> bean.color = "676ecf"
                                    6 -> bean.color = "14c5bf"
                                    7 -> bean.color = "da69c6"
                                }
                            }
                            typeDatas.value = it.toMutableList().apply {
                                add(0, CustomerType(typeName = "全部类型"))
                                chooseType.value = CustomerType(typeName = "全部类型")
                            }
                        }
                    }
                })
        )
    }

    /**
     * 获取等级	0-客户 1-伙伴
     */
    var levelDatas = MutableLiveData<MutableList<Leader>>()
    var chooseLevel = MutableLiveData<Leader>()
    fun getLevelList() {
        addSubscribe(
            model.getGradeList(gradeClassify = "0")
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<Leader>>>() {
                    override fun onSuccess(t: AppResponse<List<Leader>>) {
                        t.data?.let {
                            levelDatas.value = it.toMutableList().apply {
                                add(0, Leader(gradeName = "全部等级"))
                                chooseLevel.value = Leader(gradeName = "全部等级")
                            }
                        }
                    }
                })
        )
    }

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
    var dataSource: DataSource<Int, ItemViewModel<*>>? = null

    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>() {
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getCustomerList(
                            customerType = chooseType?.value?.id,
                            customerGrade = chooseLevel?.value?.id
                        )
                        .compose(RxUtils.schedulersTransformer())
                        .compose(RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerBean>>() {
                            override fun onSuccess(t: AppResponse<CustomerBean>) {
                                refreshCompleteLiveData.value = true
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> =
                                        ObservableArrayList()
                                    it.records?.forEach { it1 ->
                                        observableList.add(
                                            CustomerItemViewModel(
                                                this@CustomerListViewModel,
                                                it1,
                                                typeDatas.value
                                            )
                                        )
                                    }
                                    callback.onResult(
                                        observableList,
                                        ConstantGlobal.INITIAL_PAGE,
                                        ConstantGlobal.INITIAL_PAGE + 1
                                    )
                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                                refreshCompleteLiveData.value = false
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
                        .getCustomerList(
                            customerType = chooseType?.value?.id,
                            customerGrade = chooseLevel?.value?.id,
                            page = params.key
                        )
                        .compose(RxUtils.schedulersTransformer())
                        .compose(RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerBean>>() {
                            override fun onSuccess(t: AppResponse<CustomerBean>) {
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> =
                                        ObservableArrayList()
                                    it.records?.forEach {
                                        observableList.add(
                                            CustomerItemViewModel(
                                                this@CustomerListViewModel,
                                                it,
                                                typeDatas.value
                                            )
                                        )
                                    }
                                    callback.onResult(observableList, params.key + 1)
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


    /**
     * 保存点击item
     */
    val clickItem = hashMapOf<String, Any>()
    fun saveItemClick(customerItemViewModel: CustomerItemViewModel) {
        clickItem["itemViewModel"] = customerItemViewModel
    }

    /**
     * 刷新单条数据
     */
    fun refreshItemClick() {
        try {
            clickItem?.run {
                val itemViewModel = this["itemViewModel"] as CustomerItemViewModel
                addSubscribe(
                    model.getCustomerSingle(itemViewModel.dataObservable.get()?.id.toString())
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object :
                            AppDisposableObserver<AppResponse<CustomerListBean>>() {
                            override fun onSuccess(t: AppResponse<CustomerListBean>) {
                                t?.data.let {
                                    itemViewModel.dataObservable.set(it!!)
                                }

                            }
                        })
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
