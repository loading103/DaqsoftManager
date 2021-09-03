package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyListContentBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyTeamItemContentBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.DailyTeamOtherViewModel
import com.daqsoft.module_workbench.viewmodel.DailyTeamViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyTeamOtherItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

class DailyTeamOtherAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

   lateinit var  datas : MutableList<DialyRecord>

    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, layoutRes: Int, position: Int, item: MultiItemViewModel<*>) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){
            ConstantGlobal.DAILY_LIST_CONTENT -> {
                val itemBinding = binding as RecyclerviewDailyTeamItemContentBinding
                val itemViewModel = item as DailyTeamOtherItemViewModel

                if(position>0 &&  datas[position-1].reportDate==itemViewModel.record.get()?.reportDate){
                    itemBinding.llTop.visibility= View.GONE
                }else{
                    itemBinding.llTop.visibility= View.VISIBLE
                }
            }
        }
    }
}