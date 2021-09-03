package com.daqsoft.module_project.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProListPopupItemBinding
import com.daqsoft.module_project.databinding.RecyclerviewProListPopupItemRightBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectChooseType
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 19/2/2021 下午 4:26
 * @author zp
 * @describe
 */
class ListRightPopupAdapter : BindingRecyclerViewAdapter<ProjectType>() {

    private var  leftPosition=0

    private var  leftselectedPosition=0

    private var selectedPosition=0

    private var icons : List<Int> ? = null

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ProjectType
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewProListPopupItemRightBinding
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
            selectedPosition = position
            notifyDataSetChanged()
        }
        itemBinding.content.isSelected =(selectedPosition == position && leftselectedPosition==leftPosition)

        var icon : Drawable? = null
        if (!icons.isNullOrEmpty()){
            icon = itemBinding.content.context.resources.getDrawable(icons!![position])
            icon.setBounds(0, 0, icon.minimumWidth, icon.minimumHeight)
        }
        if (selectedPosition != -1 &&  selectedPosition == position && leftselectedPosition==leftPosition){
            val drawable = itemBinding.content.context.resources.getDrawable(R.mipmap.mine_sr_selected)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            itemBinding.content.setCompoundDrawables(icon, null, drawable, null)
        }else{
            itemBinding.content.setCompoundDrawables(icon, null, null, null)
        }
    }


    fun setLeftSelectedPosition(position : Int){
        leftselectedPosition = position
    }

    fun setleftPosition(position : Int){
        leftPosition = position
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : ProjectType)
    }
}