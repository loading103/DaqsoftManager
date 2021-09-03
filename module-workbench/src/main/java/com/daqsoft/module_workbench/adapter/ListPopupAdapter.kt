package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewListPopupItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 19/2/2021 下午 4:26
 * @author zp
 * @describe
 */
class ListPopupAdapter : BindingRecyclerViewAdapter<String>() {

    private var selectedPosition= -1

    private var icons : List<Int> ? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewListPopupItemBinding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        itemBinding.content.isSelected = selectedPosition == position

        var icon : Drawable? = null
        if (!icons.isNullOrEmpty()){
            icon = itemBinding.content.context.resources.getDrawable(icons!![position])
            icon.setBounds(0, 0, icon.minimumWidth, icon.minimumHeight)
        }

        if (selectedPosition != -1 && selectedPosition == position){
            val drawable = itemBinding.content.context.resources.getDrawable(R.mipmap.mine_sr_selected)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            itemBinding.content.setCompoundDrawables(icon, null, drawable, null)
        }else{
            itemBinding.content.setCompoundDrawables(icon, null, null, null)
        }
    }


    /**
     * 设置 icon
     */
    fun setIcons(icons:List<Int>?){
        this.icons = icons
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        selectedPosition = position
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }
}