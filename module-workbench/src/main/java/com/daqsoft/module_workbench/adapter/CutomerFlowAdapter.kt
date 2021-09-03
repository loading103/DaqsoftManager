package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewItemRecordBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CustomerCzItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

class CutomerFlowAdapter  @Inject constructor()  : BindingRecyclerViewAdapter<ItemViewModel<*>>() {
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        var item = item as CustomerCzItemViewModel
        var itemBinding = binding as RecyclerviewItemRecordBinding

        if (itemCount == 1) {
            itemBinding.viewLineAll.visibility = View.GONE
            itemBinding.viewLineTop.visibility = View.GONE
            itemBinding.viewLineButtom.visibility = View.GONE
        }
        if (itemCount > 1) {
            if (position == 0) {
                itemBinding.viewLineAll.visibility = View.GONE
                itemBinding.viewLineTop.visibility = View.GONE
                itemBinding.viewLineButtom.visibility = View.VISIBLE
            } else if (position == itemCount - 1) {
                itemBinding.viewLineAll.visibility = View.GONE
                itemBinding.viewLineTop.visibility = View.VISIBLE
                itemBinding.viewLineButtom.visibility = View.GONE
            } else {
                itemBinding.viewLineAll.visibility = View.VISIBLE
                itemBinding.viewLineTop.visibility = View.GONE
                itemBinding.viewLineButtom.visibility = View.GONE
            }
        }
    }
}