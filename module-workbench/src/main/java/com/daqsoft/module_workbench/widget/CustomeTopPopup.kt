package com.daqsoft.module_workbench.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.adapter.ListPopupAdapter
import com.lxj.xpopup.impl.PartShadowPopupView
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 19/2/2021 下午 2:58
 * @author zp
 * @describe
 */
class CustomeTopPopup(context: Context) : PartShadowPopupView(context) {

    private val listPopupAdapter : ListPopupAdapter by lazy { ListPopupAdapter() }

    fun setData(data:MutableList<String>){
        this.data = data
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
    }

    private var data = mutableListOf<String>()


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_top
    }

    override fun initPopupContent() {
        super.initPopupContent()
        initRecycleView()
    }

    private fun initRecycleView() {
        val recycleView = findViewById<RecyclerView>(R.id.recycle_view)
        recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listPopupAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_list_popup_item)
                setItems(data)
                setItemOnClickListener(object : ListPopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, content: String) {
                        itemOnClickListener?.onClick(position, content)
                        postDelayed(Runnable {
                            if (popupInfo.autoDismiss){
                                dismiss()
                            }
                        },100)
                    }
                })
            }
        }
    }


    /**
     * 设置选中位置
     */
    fun setSelectedPosition(position : Int){
        listPopupAdapter.setSelectedPosition(position)
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }

}