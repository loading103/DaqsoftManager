package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocCurtFolderBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocCurtPhotoBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFileBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFolderBinding
import com.daqsoft.module_workbench.viewmodel.DeptDocContainerViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AbbreviatedFolderItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AbbreviatedPhotoItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 20/2/2021 下午 5:20
 * @author zp
 * @describe
 */
class AbbreviatedAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    var containerViewModel : DeptDocContainerViewModel? = null


    var longClickItem : HashMap<String,Any> ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){
            ConstantGlobal.FOLDER -> {
                val itemBinding = binding as RecyclerviewDeptDocCurtFolderBinding
                val itemViewModel = item as AbbreviatedFolderItemViewModel

                handleFolderOnEvent(itemBinding,itemViewModel,position)
            }
            ConstantGlobal.FILE -> {
                val itemBinding = binding as RecyclerviewDeptDocCurtPhotoBinding
                val itemViewModel = item as AbbreviatedPhotoItemViewModel

                handleFileOnEvent(itemBinding,itemViewModel,position)
            }
        }

    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(binding)
    }


    fun handleFileOnEvent(itemBinding : RecyclerviewDeptDocCurtPhotoBinding,itemViewModel: AbbreviatedPhotoItemViewModel,position: Int){
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

            itemBinding.selected.visibility = View.VISIBLE

            itemOnClickListener?.fileOnLongClick(position, itemBinding, itemViewModel)
            return@setOnLongClickListener true
        }
    }

    fun handleFolderOnEvent(itemBinding : RecyclerviewDeptDocCurtFolderBinding, itemViewModel: AbbreviatedFolderItemViewModel,position: Int){

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

            itemBinding.selected.visibility = View.VISIBLE

            itemOnClickListener?.folderOnLongClick(position, itemBinding, itemViewModel)
            return@setOnLongClickListener true
        }
    }



    fun restore(){
        longClickItem?.let {
            val itemBinding = it["itemBinding"]
            when(itemBinding){
                is RecyclerviewDeptDocCurtFolderBinding ->{
                    itemBinding.selected.visibility = View.GONE
                }
                is RecyclerviewDeptDocCurtPhotoBinding ->{
                    itemBinding.selected.visibility = View.GONE
                }
            }
        }
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun fileOnClick(position: Int, itemBinding : RecyclerviewDeptDocCurtPhotoBinding, itemViewModel: AbbreviatedPhotoItemViewModel)
        fun fileOnLongClick(position: Int, itemBinding : RecyclerviewDeptDocCurtPhotoBinding, itemViewModel: AbbreviatedPhotoItemViewModel)

        fun folderOnClick(position: Int, itemBinding : RecyclerviewDeptDocCurtFolderBinding, itemViewModel: AbbreviatedFolderItemViewModel)
        fun folderOnLongClick(position: Int, itemBinding : RecyclerviewDeptDocCurtFolderBinding, itemViewModel: AbbreviatedFolderItemViewModel)

    }
}