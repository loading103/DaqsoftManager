package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFileBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFolderBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 8/2/2021 上午 11:31
 * @author zp
 * @describe 部门文件搜索
 *
 * ps : 搜索列表和展示列表有所区别 所以操作都放到 adapter 里  分两个 adapter
 */
class DeptDocSearchAdapter  @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        when (item?.itemType) {
            ConstantGlobal.FOLDER -> {

                val itemBinding = binding as RecyclerviewDeptDocFolderBinding
                val itemViewModel = item as DeptDocFolderItemViewModel

                itemBinding.root.setOnClickListener {
                    itemOnClickListener?.folderOnClick(position, itemBinding, itemViewModel)
                }
            }

            ConstantGlobal.FILE -> {
                val itemBinding = binding as RecyclerviewDeptDocFileBinding
                val itemViewModel = item as DeptDocFileItemViewModel

                itemBinding.root.setOnClickListener {
                    itemOnClickListener?.fileOnClick(position, itemBinding, itemViewModel)
                }
            }
        }

    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(binding)
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{

        fun fileOnClick(position: Int, itemBinding : RecyclerviewDeptDocFileBinding, itemViewModel: DeptDocFileItemViewModel)
        fun folderOnClick(position: Int, itemBinding : RecyclerviewDeptDocFolderBinding, itemViewModel: DeptDocFolderItemViewModel)

    }

}