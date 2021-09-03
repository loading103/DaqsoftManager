package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerListBean
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerTagListBean
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerType
import com.daqsoft.module_workbench.viewmodel.CustomerListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * 请假列表item
 */
class CustomerItemViewModel(
    private val customerViewModel: CustomerListViewModel,
    val data: CustomerListBean,
    datas: MutableList<CustomerType>?
) : ItemViewModel<CustomerListViewModel>(customerViewModel){

    val dataObservable = ObservableField<CustomerListBean>()
    val placeholderRes = ObservableField<Int>()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_tag_item
    )
    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    init {
        dataObservable.set(data)
        if(!TextUtils.isEmpty(data.customerTypeName)){
            datas?.forEach {
                if(it.typeName==data.customerTypeName){
                    observableList.add(TagItemViewModel(customerViewModel, CustomerTagListBean(it.typeName,it.color,0)))
                }
            }
        }
        if(!TextUtils.isEmpty(data.customerGradeName)){
            observableList.add(TagItemViewModel(customerViewModel, CustomerTagListBean(data.customerGradeName,type = 1)))
        }
    }



    /**
     * item点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

        customerViewModel.saveItemClick(this@CustomerItemViewModel)
        var colorJson=""
        if(!customerViewModel.typeDatas.value.isNullOrEmpty()){
            colorJson= Gson().toJson(customerViewModel.typeDatas.value)
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_CUSTOMER_DETAIL)
            .withString("id", data?.id.toString())
            .withString("colorJson",colorJson)
            .navigation()
    })


    /**
     * 打电话
     */
    val onCallClick = BindingCommand<Unit>(BindingAction {
        if(data?.contactPhone.isNullOrBlank()){
            return@BindingAction
        }
        customerViewModel.callLiveData.value = data?.contactPhone
    })
}
