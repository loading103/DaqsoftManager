package com.daqsoft.mvvmfoundation.binding.viewadapter.recycleview

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator

/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.view
 * @date 26/10/2020 上午 11:32
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["itemDecoration"], requireAll = false)
        fun addRecycleViewItemDecoration(
            view: RecyclerView,
            itemDecoration: RecyclerView.ItemDecoration
        ) {
            view.addItemDecoration(itemDecoration)
        }

        @JvmStatic
        @BindingAdapter(value = ["itemAnimator"],requireAll = false)
        fun addRecycleViewItemAnimator(recyclerView: RecyclerView, animator: RecyclerView.ItemAnimator?) {
            recyclerView.itemAnimator = animator
        }

    }
}