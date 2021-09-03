package com.daqsoft.module_workbench.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewPushObjectItemBinding
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeDetail
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeOrganization
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 26/2/2021 上午 10:54
 * @author zp
 * @describe 通知公告 推送对象adapter
 */
class PushObjectAdapter : BindingRecyclerViewAdapter<NoticeOrganization>()  {
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: NoticeOrganization
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewPushObjectItemBinding

        itemBinding.delete.setOnClickListener {
            deleteOnClickListener?.deleteOnClick(position,item)
        }

    }


    private var deleteOnClickListener : DeleteOnClickListener? = null

    fun setDeleteOnClickListener(listener: DeleteOnClickListener){
        this.deleteOnClickListener = listener
    }

    interface  DeleteOnClickListener{
        fun deleteOnClick(position: Int,content : NoticeOrganization)
    }

}