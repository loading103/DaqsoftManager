package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewDailyDetailHeadBinding
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
class DailyDetailAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    var readPersonAdapter: DailyDetailReadPersonAdapter = DailyDetailReadPersonAdapter()

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item.itemType){
            ConstantGlobal.DAILY_DETAIL_HEAD -> {
                var itemBinding = binding as RecyclerviewDailyDetailHeadBinding
                var itemModel = item as DailyDetailHeadViewModel

                //以查看人员2
                itemBinding.ivShowMore.setBackgroundResource(R.mipmap.rbxq_arrow_down)

                readPersonAdapter.setItems(itemModel.headShowTwoLineList)
                readPersonAdapter.itemBinding = itemModel.itemBindingHeadImg
                itemBinding.readNumRecycler.layoutManager = GridLayoutManager(binding.root.context, 6, GridLayoutManager.VERTICAL, false)
                itemBinding.readNumRecycler.adapter = readPersonAdapter

                if (itemModel.headShowMoreList.size > 0) {
                    itemBinding.ivShowMore.visibility = View.VISIBLE
                } else {
                    itemBinding.ivShowMore.visibility = View.GONE
                }

                if (itemModel.headShowTwoLineList.size == 0) {
                    itemBinding.tvReadPerson.visibility = View.GONE
                    itemBinding.readNumRecycler.visibility = View.GONE
                } else {
                    itemBinding.tvReadPerson.visibility = View.VISIBLE
                    itemBinding.tvReadPerson.visibility = View.VISIBLE
                }

                itemBinding.ivShowMore.setOnClickListener{
                    if (itemBinding.ivShowMore.visibility == View.VISIBLE) {
                        readPersonAdapter.setItems(itemModel.headShowMoreList)
                        itemBinding.ivShowMore.setBackgroundResource(R.mipmap.rbxq_arrow_up)
                    } else {
                        itemBinding.ivShowMore.setBackgroundResource(R.mipmap.rbxq_arrow_down)
                        readPersonAdapter.setItems(itemModel.headShowTwoLineList)
                    }
                }
            }
        }

    }

    override fun onCreateViewHolder(binding: ViewDataBinding): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(binding)
    }

}