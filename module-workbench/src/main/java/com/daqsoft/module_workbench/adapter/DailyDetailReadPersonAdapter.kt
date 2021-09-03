package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyDetailHeadBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocCurtFolderBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocCurtPhotoBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AbbreviatedFolderItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AbbreviatedPhotoItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyDetailHeadViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 20/2/2021 下午 5:20
 * @author zp
 * @describe
 */
class DailyDetailReadPersonAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){

        }

    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(binding)
    }

}