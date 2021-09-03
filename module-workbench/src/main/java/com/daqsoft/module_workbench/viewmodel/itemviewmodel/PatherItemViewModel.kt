package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.TextUtils
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerTagListBean
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerType
import com.daqsoft.module_workbench.repository.pojo.vo.PartnerListBean
import com.daqsoft.module_workbench.viewmodel.PatherListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.google.gson.Gson
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * 请假列表item
 */
class PatherItemViewModel(
    private val customerViewModel: PatherListViewModel,
    val data: PartnerListBean,
    datas: MutableList<CustomerType>?
) : ItemViewModel<PatherListViewModel>(customerViewModel){

    val dataObservable = ObservableField<PartnerListBean>()
    val placeholderRes = ObservableField<Int>()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recycleview_tag_pater_item
    )
    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    init {
        dataObservable.set(data)

        if(!TextUtils.isEmpty(data.partnerTypeName)){
            datas?.forEach {
                if(it.typeName==data.partnerTypeName) {
                    observableList.add(
                        TagViewModel(
                            customerViewModel,
                            CustomerTagListBean(it.typeName!!, it.color, 0)
                        )
                    )
                }
            }
        }
        if(!TextUtils.isEmpty(data.partnerGradeName)){
            observableList.add(TagViewModel(customerViewModel, CustomerTagListBean(data.partnerGradeName!!,type = 1)))
        }
    }


    /**
     * item点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

        customerViewModel.saveItemClick(this@PatherItemViewModel)
        var colorJson=""
        if(!customerViewModel.typeDatas.value.isNullOrEmpty()){
            colorJson= Gson().toJson(customerViewModel.typeDatas.value)
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_PATERNER_DETAIL)
            .withString("id", data?.id.toString())
            .withString("colorJson",colorJson)
            .navigation()
    })


    /**
     * 打电话
     */
    val onCallClick = BindingCommand<Unit>(BindingAction {
        if(data?.partnerPhone.isNullOrBlank()){
            return@BindingAction
        }
        customerViewModel.callLiveData.value = data?.partnerPhone!!
    })
}
