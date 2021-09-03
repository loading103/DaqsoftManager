package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFileBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFolderBinding
import com.daqsoft.module_workbench.viewmodel.DeptDocContainerViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 8/2/2021 上午 11:31
 * @author zp
 * @describe 列表模式
 */
class DeptDocAdapter  @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    var containerViewModel : DeptDocContainerViewModel ? = null


    var longClickItem : HashMap<String,Any> ? = null

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

                handleFolderOnEvent(itemBinding, itemViewModel, position)
            }

            ConstantGlobal.FILE -> {
                val itemBinding = binding as RecyclerviewDeptDocFileBinding
                val itemViewModel = item as DeptDocFileItemViewModel

                handleFileOnEvent(itemBinding, itemViewModel, position)
            }
        }
    }

    fun restore(){
        longClickItem?.let {

            val itemBinding = it["itemBinding"]

            if (itemBinding is RecyclerviewDeptDocFileBinding){
                itemBinding.root.setBackgroundColor(itemBinding.root.context.resources.getColor(R.color.white_ffffff))
                itemBinding.selected.visibility = View.GONE
            }

            if (itemBinding is RecyclerviewDeptDocFolderBinding){
                itemBinding.root.setBackgroundColor(itemBinding.root.context.resources.getColor(R.color.white_ffffff))
                itemBinding.selected.visibility = View.GONE
            }

        }
    }

    fun handleFileOnEvent(itemBinding : RecyclerviewDeptDocFileBinding,itemViewModel: DeptDocFileItemViewModel,position: Int){
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.fileOnClick(position, itemBinding, itemViewModel)
        }

        if (containerViewModel?.dirTypeObservable?.get()?.contains("共享文件") == true){
            return
        }

        itemBinding.root.setOnLongClickListener {
            longClickItem = hashMapOf()
            longClickItem!!["position"] = position
            longClickItem!!["itemBinding"] = itemBinding
            longClickItem!!["itemViewModel"] = itemViewModel

            itemBinding.root.setBackgroundColor(itemBinding.root.context.resources.getColor(R.color.red_fff1f1))
            itemBinding.selected.visibility = View.VISIBLE

            itemOnClickListener?.fileOnLongClick(position, itemBinding, itemViewModel)
            return@setOnLongClickListener true
        }
    }

    fun handleFolderOnEvent(itemBinding : RecyclerviewDeptDocFolderBinding, itemViewModel: DeptDocFolderItemViewModel,position: Int){
        itemBinding.root.setOnClickListener {
            itemOnClickListener?.folderOnClick(position, itemBinding, itemViewModel)
        }

        if (containerViewModel?.dirTypeObservable?.get()?.contains("共享文件") == true){
            return
        }

        itemBinding.root.setOnLongClickListener {
            longClickItem = hashMapOf()
            longClickItem!!["position"] = position
            longClickItem!!["itemBinding"] = itemBinding
            longClickItem!!["itemViewModel"] = itemViewModel

            itemBinding.root.setBackgroundColor(itemBinding.root.context.resources.getColor(R.color.red_fff1f1))
            itemBinding.selected.visibility = View.VISIBLE

            itemOnClickListener?.folderOnLongClick(position, itemBinding, itemViewModel)
            return@setOnLongClickListener true
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
        fun fileOnLongClick(position: Int, itemBinding : RecyclerviewDeptDocFileBinding, itemViewModel: DeptDocFileItemViewModel)

        fun folderOnClick(position: Int, itemBinding : RecyclerviewDeptDocFolderBinding, itemViewModel: DeptDocFolderItemViewModel)
        fun folderOnLongClick(position: Int, itemBinding : RecyclerviewDeptDocFolderBinding, itemViewModel: DeptDocFolderItemViewModel)

    }

}