package com.daqsoft.module_project.adapter

import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.databinding.RecyclerviewPopupBottomItem1Binding
import com.daqsoft.module_project.databinding.RecyclerviewProjectHeadIcon1Binding
import com.daqsoft.module_project.repository.pojo.vo.Member
import com.daqsoft.module_project.repository.pojo.vo.PostMember
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/2/2021 上午 10:45
 * @author zp
 * @describe
 */
class ProjectRedTopAdapter : BindingRecyclerViewAdapter<PostMember>() {

    private var  itemOnClickListener : ItemOnClickListener ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item:PostMember?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding = binding as RecyclerviewProjectHeadIcon1Binding
        itemBinding.content=item

        itemBinding.avatar.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", item?.employeeId)
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