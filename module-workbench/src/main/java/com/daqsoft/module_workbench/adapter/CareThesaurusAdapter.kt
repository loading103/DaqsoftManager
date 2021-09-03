package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewCareThesauruBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.CareThesausuItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 11/3/2021 上午 9:57
 * @author zp
 * @describe
 */
class CareThesaurusAdapter @Inject constructor() : BindingRecyclerViewAdapter<ItemViewModel<*>>()  {

    var longClickItem : HashMap<String,Any> ? = null


    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewCareThesauruBinding
        val itemViewModel = item as CareThesausuItemViewModel

        itemBinding.root.setOnLongClickListener {
            Timber.e("setOnLongClickListener")
            longClickItem = hashMapOf()
            longClickItem!!["position"] = position
            longClickItem!!["itemBinding"] = itemBinding
            longClickItem!!["itemViewModel"] = itemViewModel

            itemBinding.cl.setBackgroundColor(itemBinding.root.context.resources.getColor(R.color.red_fa4848_alpha90))

            itemOnClickListener?.onLongClick(position, itemBinding, itemViewModel)
            return@setOnLongClickListener true
        }
    }

    fun restore(){
        longClickItem?.let {
            (it["itemBinding"] as RecyclerviewCareThesauruBinding).apply {
                this.cl.setBackgroundColor(root.context.resources.getColor(R.color.white_ffffff))
            }
        }
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int, itemBinding : RecyclerviewCareThesauruBinding, itemViewModel: CareThesausuItemViewModel)

        fun onLongClick(position: Int, itemBinding : RecyclerviewCareThesauruBinding, itemViewModel: CareThesausuItemViewModel)
    }

}