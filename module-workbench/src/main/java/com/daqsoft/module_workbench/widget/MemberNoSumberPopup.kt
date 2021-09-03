package com.daqsoft.module_workbench.widget

import android.content.Context
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_common.widget.FullyGridLayoutManager
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.ProjectMemberAdapter
import com.daqsoft.module_workbench.repository.pojo.vo.UnreportedEmployee
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * 未提交日报成员
 */
class MemberNoSumberPopup  : BottomPopupView {

    constructor(context: Context) : super(context)


    private val listPopupAdapter : ProjectMemberAdapter by lazy { ProjectMemberAdapter() }

    private var data = mutableListOf<UnreportedEmployee>()



    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_project_no
    }

    override fun initPopupContent() {
        super.initPopupContent()

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dismiss()
        }

        val recycleView = findViewById<RecyclerView>(R.id.recycler_view)
        recycleView.apply {
            layoutManager = FullyGridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false)
            adapter = listPopupAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_project_head_icon2)
                setItems(data)
            }
        }
    }

    fun setData(data:MutableList<UnreportedEmployee>){
        this.data = data
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(getContext())*.7f).toInt()
    }


}