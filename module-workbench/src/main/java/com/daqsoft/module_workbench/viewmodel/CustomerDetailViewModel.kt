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
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.luck.picture.lib.tools.ToastUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

class CustomerDetailViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    /**
     * 刷新完成
     */
    val refreshCompleteLiveData = MutableLiveData<Boolean>()

    var typeDatas =MutableLiveData<MutableList<CustomerType>>()

    /**
     * 详情标签
     */
    val observableTagList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    var itemTagBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_tag_item_detail
    )
    /**
     * 详情责任领导
     */
    var itemAvaterBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_head_icon
    )
    var dataList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    /**
     * 详情操作记录
     */
    var itemRecordBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_item_record
    )
    var recordList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    override fun onCreate() {
        initToolbar()
    }


    val chooseTypesLiveData = MutableLiveData<MutableList<CustomerChooseType>>()



    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("客户详情")
        setTitleTextColor(R.color.color_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
    }

    /**
     * 排序按钮
     */
    var onOrderClick = BindingCommand<Any>(BindingAction {
    })

    /**
     * 筛选按钮
     */
    var onChooseClick = BindingCommand<Any>(BindingAction {
    })


    var recordOnClick = BindingCommand<Any>(BindingAction {
        ToastUtils.s(BaseApplication.getInstance()?.baseContext,"回访记录")
    })

    val haveLiveData = MutableLiveData<Boolean>(false)
    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    val detailBean: ObservableField<CustomerDetailBean> = ObservableField()
    fun getdatas(id:String) {
        addSubscribe(
            model.getCustomerDetail(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerDetailBean>>() {
                    override fun onSuccess(t: AppResponse<CustomerDetailBean>) {
                        t?.data.let {
                            detailBean.set(it)
                            it?.dutyLeaders?.let{its->
                                dataList.addAll(its.map { CustomerlHeadIcon (this@CustomerDetailViewModel, it)})
                            }
                            // tag标签
                            observableTagList.clear()
                            if(!TextUtils.isEmpty(it?.customerTypeName)){
                                typeDatas.value?.forEach {its->
                                    if(its.typeName==it?.customerTypeName){
                                        observableTagList.add(TagDetailItemViewModel(this@CustomerDetailViewModel, CustomerTagListBean(its.typeName!!,its.color,0)))
                                    }
                                }
                            }
                            if(!TextUtils.isEmpty(it?.customerGradeName)){
                                observableTagList.add(TagDetailItemViewModel(this@CustomerDetailViewModel, CustomerTagListBean(it?.customerGradeName!!,type = 1)))
                            }
                        }

                    }
                })
        )
    }
    fun getRecorddatas(id:String) {
        addSubscribe(
            model.getEditRecord(type =0 ,id = id.toInt())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<CustomerRecord>>>() {
                    override fun onSuccess(t: AppResponse<List<CustomerRecord>>) {
                        t?.data.let {
                            it?.reversed()?.forEach {
                                recordList.add( CustomerCzItemViewModel(this@CustomerDetailViewModel, it))
                            }
                        }
                    }
                })
        )
    }

    val onHeadItemClick = BindingCommand<Unit>(BindingAction {
        if(detailBean.get()?.pickupId.isNullOrBlank()){
            return@BindingAction
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",detailBean.get()?.pickupId)
            .withString("name",detailBean.get()?.pickupPeople)
            .navigation()
    })
    val onBtnleftClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_CUSTOMER_RECORD)
            .withString("id", detailBean.get()?.id.toString())
            .withString("title", detailBean.get()?.customerName.toString())
            .navigation()
    })

    val onBtnrightClick = BindingCommand<Unit>(BindingAction {
        var website:String= HttpGlobal.DAILY_CUSTOMER_PROJECT+detailBean.get()?.id.toString()
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    })
}

