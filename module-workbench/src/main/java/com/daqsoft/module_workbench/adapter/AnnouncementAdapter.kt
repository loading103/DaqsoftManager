package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewAnnouncementItemBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 7/12/2020 上午 9:44
 * @author zp
 * @describe 通知公共 adapter
 */
class AnnouncementAdapter @Inject constructor() : BindingRecyclerViewAdapter<ItemViewModel<*>>() {

    @SuppressLint("SetTextI18n")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewAnnouncementItemBinding
        val itemViewModel = item as AnnouncementItemViewModel
    }
}