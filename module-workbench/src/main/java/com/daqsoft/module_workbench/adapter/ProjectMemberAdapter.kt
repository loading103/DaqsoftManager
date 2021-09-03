package com.daqsoft.module_workbench.adapter

import android.graphics.Rect
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.databinding.RecyclerviewProjectHeadIcon2Binding
import com.daqsoft.module_workbench.repository.pojo.vo.UnreportedEmployee
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/2/2021 上午 10:45
 * @author zp
 * @describe
 */
class ProjectMemberAdapter : BindingRecyclerViewAdapter<UnreportedEmployee>() {

    private var  itemOnClickListener : ItemOnClickListener ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item:UnreportedEmployee?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecyclerviewProjectHeadIcon2Binding
        itemBinding.content=item

        itemBinding.avatar.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", item?.employeeId.toString())
                .withString("name",item?.employeeName)
                .navigation()
        }

    }

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int, text : String)
    }
}