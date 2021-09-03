package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.databinding.RecyclerviewContactsItemBinding
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 26/11/2020 上午 11:42
 * @author zp
 * @describe 通讯录 adapter
 */
class ContactsAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when (item?.itemType) {
            ConstantGlobal.ITEM -> {
                val itemBinding = binding as RecyclerviewContactsItemBinding
                itemBinding.recycleView.apply {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(GridSpacingItemDecoration(3,1.dp,false))
                    }
                    layoutManager = GridLayoutManager(ContextUtils.getContext(),3)
                }
            }
        }
    }
}