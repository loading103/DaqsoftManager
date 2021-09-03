package com.daqsoft.module_workbench.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffSelectItemBinding
import com.daqsoft.module_workbench.viewmodel.BaseSelectViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffSelectItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 3/3/2021 下午 5:57
 * @author zp
 * @describe 员工选择 adapter
 */
class StaffSelectAdapter @Inject constructor() : BindingRecyclerViewAdapter<ItemViewModel<*>>(){

    private var  selectedList = arrayListOf<Employee>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        val itemBinding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView) as RecyclerviewStaffSelectItemBinding
        val itemViewModel = getAdapterItem(position) as StaffSelectItemViewModel

        val finder = BaseSelectViewModel.selectedStaffSet.find { it.id == itemViewModel.employee.id }
        if(finder != null){
            selectedList.add(itemViewModel.employee)
        }else{
            selectedList.removeIf{
                it.id == itemViewModel.employee.id
            }
        }

        itemBinding.checkbox.tag = itemViewModel.employee
        itemBinding.checkbox.isChecked = selectedList.contains(itemViewModel.employee)
        itemBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            val tag = itemBinding.checkbox.tag
            if (isChecked){
                if (!selectedList.contains(tag)) {
                    selectedList.add(itemViewModel.employee)
                    onCheckedChangeListener?.onCheckedChanged(position,true, itemViewModel.employee)
                }
            }else{
                if (selectedList.contains(tag)) {
                    selectedList.remove(itemViewModel.employee)
                    onCheckedChangeListener?.onCheckedChanged(position,false, itemViewModel.employee)
                }
            }
        }
    }

    /**
     * 添加选中
     * @param employee Employee
     */
    fun addChecked(employee: Employee){
        selectedList.add(employee)
    }

    /**
     * 移除选中
     * @param employee Employee
     */
    fun removeChecked(employee: Employee){
        selectedList.remove(employee)
    }

    private var onCheckedChangeListener : OnCheckedChangeListener ? = null

    fun setOnCheckedChangeListener(listener : OnCheckedChangeListener){
        this.onCheckedChangeListener = listener
    }

    interface OnCheckedChangeListener{
        fun onCheckedChanged(position: Int,isChecked:Boolean,employee: Employee)
    }

}