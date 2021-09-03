package com.daqsoft.module_workbench.adapter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.databinding.RecyclerviewOrgSelectItemBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffSelectItemBinding
import com.daqsoft.module_workbench.viewmodel.BaseSelectViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.OrgSelectItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffSelectItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 3/3/2021 下午 5:57
 * @author zp
 * @describe 组织选择 adapter
 */
class OrgSelectAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>(){

    private var  selectedList = linkedSetOf<Employee>()

    private var isAllChecked : Boolean = false

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when (item?.itemType) {
            ConstantGlobal.STAFF -> {
                val itemBinding = binding as RecyclerviewStaffSelectItemBinding
                val itemViewModel = item as StaffSelectItemViewModel
            }
            ConstantGlobal.ORG -> {
                val itemBinding = binding as RecyclerviewOrgSelectItemBinding
                val itemViewModel = item as OrgSelectItemViewModel
                itemBinding.root.setOnClickListener {
                    orgItemOnClickListener?.onClick(position,itemViewModel.employee,itemBinding.checkbox.isChecked)
                }

            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)
        val itemBinding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView)
        val itemViewModel = getAdapterItem(position)
        if (itemBinding is RecyclerviewStaffSelectItemBinding && itemViewModel is StaffSelectItemViewModel){
            handleStaff(position,itemBinding,itemViewModel)
        }
        if (itemBinding is RecyclerviewOrgSelectItemBinding && itemViewModel is OrgSelectItemViewModel){
            handleOrg(position,itemBinding,itemViewModel)
        }
    }


    /**
     * 处理部门
     * @param itemBinding RecyclerviewOrgSelectItemBinding
     * @param itemViewModel OrgSelectItemViewModel
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun handleOrg(position: Int, itemBinding: RecyclerviewOrgSelectItemBinding, itemViewModel : OrgSelectItemViewModel){
//        if (isAllChecked){
//            selectedList.add(itemViewModel.employee)
//        }else{
            val finder = BaseSelectViewModel.selectedOrgSet.find { it.id == itemViewModel.employee.id }
            if(finder != null){
                selectedList.add(itemViewModel.employee)
            }else{
                selectedList.removeIf{
                    it.id == itemViewModel.employee.id
                }
            }
//        }

        itemBinding.checkbox.tag = itemViewModel.employee
        itemBinding.checkbox.isChecked = selectedList.contains(itemViewModel.employee)
        itemBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            val tag = itemBinding.checkbox.tag
            if (isChecked){
                if (!selectedList.contains(tag)) {
                    selectedList.add(itemViewModel.employee)
                    onCheckedChangeListener?.onOrgCheckedChanged(position,true, itemViewModel.employee)
                }
            }else{
                if (selectedList.contains(tag)) {
                    selectedList.remove(itemViewModel.employee)
                    onCheckedChangeListener?.onOrgCheckedChanged(position,false, itemViewModel.employee)
                }
            }
        }
    }

    /**
     * 处理人员
     * @param itemBinding RecyclerviewStaffSelectItemBinding
     * @param itemViewModel StaffSelectItemViewModel
     */
    @RequiresApi(Build.VERSION_CODES.N)
    fun handleStaff(position: Int, itemBinding: RecyclerviewStaffSelectItemBinding, itemViewModel : StaffSelectItemViewModel){
//        if (isAllChecked){
//            selectedList.add(itemViewModel.employee)
//        }else{
            val finder = BaseSelectViewModel.selectedStaffSet.find { it.id == itemViewModel.employee.id }
            if(finder != null){
                selectedList.add(itemViewModel.employee)
            }else{
                selectedList.removeIf{
                    it.id == itemViewModel.employee.id
                }
            }
//        }


        itemBinding.checkbox.tag = itemViewModel.employee
        itemBinding.checkbox.isChecked = selectedList.contains(itemViewModel.employee)
        itemBinding.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
            val tag = itemBinding.checkbox.tag
            if (isChecked){
                if (!selectedList.contains(tag)) {
                    selectedList.add(itemViewModel.employee)
                    onCheckedChangeListener?.onStaffCheckedChanged(position,true, itemViewModel.employee)
                }
            }else{
                if (selectedList.contains(tag)) {
                    selectedList.remove(itemViewModel.employee)
                    onCheckedChangeListener?.onStaffCheckedChanged(position,false, itemViewModel.employee)
                }
            }
        }
    }


    fun getSelectedList() = selectedList

    /**
     * 是否全部选中
     * @param isAllChecked Boolean
     */
    fun setAllIsChecked(isAllChecked: Boolean){
        this.isAllChecked = isAllChecked
    }

    /**
     * 添加选中
     * @param employee Employee
     */
    fun addChecked(employee: Employee){
        selectedList.add(employee)
    }

    /**
     * 添加选中
     * @param employee Employee
     */
    fun addChecked(employeeList : MutableList<Employee>){
        selectedList.addAll(employeeList)
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
        fun onStaffCheckedChanged(position: Int,isChecked:Boolean,employee: Employee)
        fun onOrgCheckedChanged(position: Int,isChecked:Boolean,employee: Employee)
    }




    private var orgItemOnClickListener : OrgItemOnClickListener ? = null

    fun setOrgItemOnClickListener(listener : OrgItemOnClickListener){
        this.orgItemOnClickListener = listener
    }

    interface OrgItemOnClickListener{
        fun onClick(position: Int,employee: Employee,isChecked: Boolean)
    }
}