package com.daqsoft.module_workbench.adapter

import android.graphics.Rect
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.databinding.RecycleviewWorkbenchItemBinding
import com.daqsoft.library_common.widget.FullyGridLayoutManager
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 3/12/2020 下午 1:38
 * @author zp
 * @describe 工作台 adapter
 */
class WorkBenchAdapter  @Inject constructor() : BindingRecyclerViewAdapter<ItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecycleviewWorkbenchItemBinding
        itemBinding.recycleView.apply {
            val spanCount = 4
//            layoutManager = GridLayoutManager(context, spanCount)
            layoutManager = FullyGridLayoutManager(
                context,
                spanCount,
                GridLayoutManager.VERTICAL,
                false
            )
            if (itemDecorationCount == 0){
                val spacing = 0.dp
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        val position = parent.getChildAdapterPosition(view)
                        if (position >= spanCount) {
                            outRect.top = 24.dp
                        }
                        val column = position % spanCount
                        outRect.left = column * spacing / spanCount
                        outRect.right = spacing - (column + 1) * spacing / spanCount
                    }
                })
            }
        }

    }
}