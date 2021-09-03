package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerBean
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerType
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

class CustomerSearchViewModel :  ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_customer_item_research
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("客户搜索")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
    }
    /**
     * 打电话
     */
    val callLiveData = MutableLiveData<String>()

    /**
     * 搜索内容
     */
    val searchObservable = ObservableField<String>(" ")
    val changedLiveData = MutableLiveData<Boolean>(false)

    /**
     * 输入框监听
     */
    var searchTextChanged = BindingCommand<String>(BindingConsumer {
        if(it.isNullOrBlank()){
            getdatas(it)
        }else{
            getdatas(it?.trim())
        }
    })

    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })


    /**
     * 获取等级	0-客户 1-伙伴
     */
    var typeDatas =MutableLiveData<MutableList<CustomerType>>()

    var chooseType = MutableLiveData<CustomerType>()

    /**
     * 获取列表数据
     */
    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    fun getdatas(key: String) {
        addSubscribe(
            model
                .getCustomerList(
                    key=key,
                    size = 500 )
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CustomerBean>>() {
                    override fun onSuccess(t: AppResponse<CustomerBean>) {
                        changedLiveData.value = !t.data?.records.isNullOrEmpty()
                        t.data?.let {
                            observableList.clear()
                            t.data?.records?.forEach {it1->
                                observableList.add(CustomerSearchItemViewModel(this@CustomerSearchViewModel, it1,typeDatas.value))
                            }
                        }
                    }
                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                })
        )
    }

}

