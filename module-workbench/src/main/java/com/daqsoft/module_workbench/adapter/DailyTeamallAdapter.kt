package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyListContentBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyTeamItemContentBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyTeamOwnContentBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyTeamOwnItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

class DailyTeamallAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {


    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, layoutRes: Int, position: Int, item: MultiItemViewModel<*>) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
//        when(item.itemType){
//            ConstantGlobal.DAILY_LIST_CONTENT -> {
//                val itemBinding = binding as RecyclerviewDailyTeamOwnContentBinding
//                val itemViewModel = item as DailyTeamOwnItemViewModel
//                if(position==1){
//                    itemBinding.tv1.maxLines=30
//                    itemBinding.tv2.maxLines=30
//                    itemBinding.tv3.maxLines=30
//                }
//            }
//        }
    }
}