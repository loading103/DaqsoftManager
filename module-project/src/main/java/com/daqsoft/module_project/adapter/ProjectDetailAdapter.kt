package com.daqsoft.module_project.adapter

import android.graphics.Color
import android.graphics.Rect
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProjectFlowBinding
import com.daqsoft.module_project.databinding.RecyclerviewProjectHeadBinding
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectDetailFlowViewModel
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectDetailHeadViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import timber.log.Timber
import javax.inject.Inject

class ProjectDetailAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {
    @Inject
    lateinit var progressadapter: ProjectDetailFlowAdapter
    var typeColor : String = ""

    var projectId : String = ""

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item?.itemType){
            ConstantGlobal.ITEM_PROJECT_HEAD -> {
                var itemBinding = binding as RecyclerviewProjectHeadBinding
                if(!typeColor.isNullOrBlank()){
                    itemBinding.projectType.setBackgroundColor(Color.parseColor(typeColor))
                }

                binding.iconRecyclerView.run {
                    if (itemDecorationCount == 0) {
                        addItemDecoration(object : RecyclerView.ItemDecoration(){
                            override fun getItemOffsets(
                                outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val position = parent.getChildAdapterPosition(view)
                                if (position > 0){
                                    outRect.left = 20.dp
                                }
                            }
                        })
                    }
                }
            }
            ConstantGlobal.ITEM_PROJECT_FLOW -> {
                var itemBinding = binding as RecyclerviewProjectFlowBinding
                var itemViewModel = item as ProjectDetailFlowViewModel
                itemBinding.recyclerViewFlow.adapter = progressadapter.apply {
                    projectIds=projectId
                    setItems(itemViewModel.observableList)
                    this.itemBinding = itemViewModel.itemBinding
                }
            }
        }
    }
}