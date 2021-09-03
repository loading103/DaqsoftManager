package com.daqsoft.module_project.adapter

import android.graphics.Rect
import android.view.View
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewPopupBottomItem1Binding
import com.daqsoft.module_project.databinding.RecyclerviewProjectHeadIcon1Binding
import com.daqsoft.module_project.databinding.RecyclerviewProjectRedBinding
import com.daqsoft.module_project.repository.pojo.vo.Member
import com.daqsoft.module_project.repository.pojo.vo.PostMember
import com.daqsoft.module_project.repository.pojo.vo.ProductMember
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/2/2021 上午 10:45
 * @author zp
 * @describe
 */
class ProjectRedAdapter : BindingRecyclerViewAdapter<ProductMember>() {

    private var  itemOnClickListener : ItemOnClickListener ? = null

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item:ProductMember?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)

        val itemBinding1 = binding as RecyclerviewProjectRedBinding
        itemBinding1.llProduct.text=item?.postName

        if(position== itemCount-1){
            itemBinding1.line1.visibility=View.GONE
        }else{
            itemBinding1.line1.visibility=View.VISIBLE
        }
        val listPopupAdapter : ProjectRedTopAdapter = ProjectRedTopAdapter()
        itemBinding1.recyclerViewTop.apply {
            adapter = listPopupAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_project_head_icon1)
                setItems(item?.postMemberList)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        outRect.right =20.dp
                    }
                })
            }
        }

    }

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int, text : String)
    }
}