package com.daqsoft.module_project.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.module_project.databinding.RecyclerviewPopupBottomItem1Binding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/2/2021 上午 10:45
 * @author zp
 * @describe
 */
class BottomProjectDetailAdapter : BindingRecyclerViewAdapter<Pair<Int, String>>() {

    private var  itemOnClickListener : ItemOnClickListener ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Pair<Int, String>) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecyclerviewPopupBottomItem1Binding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,itemBinding.tvText.text.toString())
        }
    }

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int, text : String)
    }
}