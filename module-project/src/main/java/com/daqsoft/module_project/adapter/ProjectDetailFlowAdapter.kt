package com.daqsoft.module_project.adapter

import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProjectFlowExpnadBinding
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectDetailFlowExpand
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject


class ProjectDetailFlowAdapter @Inject constructor()  : BindingRecyclerViewAdapter<ItemViewModel<*>>() {

    var projectIds : String = ""

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        var item = item as ProjectDetailFlowExpand
        var itemBinding = binding as RecyclerviewProjectFlowExpnadBinding
        if (itemBinding.llContent.visibility == View.VISIBLE) {
            itemBinding.ivArrow.setBackgroundResource(R.mipmap.xmxq_arrow_up)

        } else {
            itemBinding.ivArrow.setBackgroundResource(R.mipmap.xmxq_arrow_down)
        }

        // 顶部位置数显mangintop10
        if(position==0) {
            val lp: ViewGroup.MarginLayoutParams = itemBinding.viewLine1.layoutParams as ViewGroup.MarginLayoutParams
            lp.topMargin = 10.toFloat().dp
            val lp1: ViewGroup.MarginLayoutParams = itemBinding.viewLine2.layoutParams as ViewGroup.MarginLayoutParams
            lp1.topMargin = 10.toFloat().dp
        }else{
            val lp: ViewGroup.MarginLayoutParams = itemBinding.viewLine1.layoutParams as ViewGroup.MarginLayoutParams
            lp.topMargin = 0.toFloat().dp
            val lp1: ViewGroup.MarginLayoutParams = itemBinding.viewLine2.layoutParams as ViewGroup.MarginLayoutParams
            lp1.topMargin = 0.toFloat().dp
        }
        // 底部部位置数显manginBotton10
        if(position==itemCount-1){
            if(item.islastObservable.get()!!){
                itemBinding.viewLine2.visibility = View.VISIBLE
                itemBinding.viewLine1.visibility = View.GONE
                val lp: ViewGroup.MarginLayoutParams = itemBinding.viewLine2.layoutParams as ViewGroup.MarginLayoutParams
                lp.bottomMargin =5.toFloat().dp
            }else{
                itemBinding.viewLine2.visibility = View.GONE
                itemBinding.viewLine1.visibility = View.VISIBLE
            }
        }

        itemBinding.tvContent.setOnClickListener{
            if (itemBinding.recyclerView.visibility == View.VISIBLE) {
                itemBinding.recyclerView.visibility = View.GONE
                itemBinding.ivArrow.setBackgroundResource(R.mipmap.xmxq_arrow_down)
                if(position==itemCount-1){
                    itemBinding.viewLine2.visibility = View.VISIBLE
                    itemBinding.viewLine1.visibility = View.GONE
                    val lp: ViewGroup.MarginLayoutParams = itemBinding.viewLine2.layoutParams as ViewGroup.MarginLayoutParams
                    lp.bottomMargin =5.toFloat().dp
                }
            } else {
                itemBinding.recyclerView.visibility = View.VISIBLE
                itemBinding.ivArrow.setBackgroundResource(R.mipmap.xmxq_arrow_up)
                if(position==itemCount-1){
                    itemBinding.viewLine2.visibility = View.GONE
                    itemBinding.viewLine1.visibility = View.VISIBLE
                }
            }
        }
        itemBinding.llLeader.setOnClickListener {
            //跳转到阶段负责人
            if(item.data.get()?.leader?.id == DataStoreUtils.getInt(DSKeyGlobal.USER_ID)){
                var website:String= HttpGlobal.DAILY_TASK_MANAGE+projectIds+"/"+item.data.get()?.id
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url",website)
                    .navigation()
            }
        }
    }

}