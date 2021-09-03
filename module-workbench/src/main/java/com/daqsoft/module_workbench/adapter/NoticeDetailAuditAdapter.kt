package com.daqsoft.module_workbench.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewNoticeDetailAuditItemBinding
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeAudit
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 2/3/2021 下午 2:32
 * @author zp
 * @describe 通知公告详情 审批
 */
class NoticeDetailAuditAdapter: BindingRecyclerViewAdapter<NoticeAudit>()  {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: NoticeAudit?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewNoticeDetailAuditItemBinding
        if (position == 0){
            itemBinding.topLine.visibility = View.GONE
        }else{
            itemBinding.topLine.visibility = View.VISIBLE
        }

        if (position == itemCount - 1){
            itemBinding.bottomLine.visibility = View.GONE
        }else{
            itemBinding.bottomLine.visibility = View.VISIBLE
        }
    }
}