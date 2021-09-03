package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewPopupBottomItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/2/2021 上午 10:45
 * @author zp
 * @describe
 */
class BottomPopupAdapter : BindingRecyclerViewAdapter<Pair<Int, String>>() {

    private var  itemOnClickListener : ItemOnClickListener ? = null

    @SuppressLint("SetTextI18n")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Pair<Int, String>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewPopupBottomItemBinding
        itemBinding.root.setOnClickListener {
            when(itemBinding.tvText.text){
                "缩略图模式" -> {
                    binding.ivImage.setImageResource(R.mipmap.bmwj_more_list)
                    itemBinding.tvText.text = "列表模式"
                }
                "列表模式" ->{
                    binding.ivImage.setImageResource(R.mipmap.bmwj_more_slt)
                    itemBinding.tvText.text = "缩略图模式"
                }
            }
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