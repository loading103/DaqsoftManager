package com.daqsoft.module_project.adapter

import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProListPopupItemLeftBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectChooseType
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 19/2/2021 下午 4:26
 * @author zp
 * @describe
 */
class ListLeftPopupAdapter : BindingRecyclerViewAdapter<ProjectChooseType>() {

    private var selectedPosition= 0

    private var icons : List<Int> ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ProjectChooseType
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewProListPopupItemLeftBinding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        itemBinding.content.isSelected = selectedPosition == position
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : ProjectChooseType)
    }
}