package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffSelectedItemBinding
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 30/11/2020 下午 5:19
 * @author zp
 * @describe 员工选中 adapter
 */
class StaffSelectedAdapter @Inject constructor() : BindingRecyclerViewAdapter<Employee>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: Employee
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBing = binding as RecyclerviewStaffSelectedItemBinding
        itemBing.root.setOnClickListener {
            itemOnClickListener?.onClick(position,item)
        }

    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,item: Employee)
    }
}

