package com.daqsoft.module_workbench.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.adapter.ListFilePopupAdapter
import com.daqsoft.module_workbench.repository.pojo.vo.DeptType
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @describe 项目类型 popup
 */
class ProFileTypePopup  : BottomPopupView {

    constructor(context: Context) : super(context)

    constructor(context: Context, title: String) : super(context) {
        this.titleContent = title
    }

    private val listPopupAdapter : ListFilePopupAdapter by lazy { ListFilePopupAdapter() }

    private var titleContent: String = ""

    private var title: TextView? = null

    fun setData(data:MutableList<DeptType>){
        this.data = data
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
    }

    private var data = mutableListOf<DeptType>()


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_file_type
    }



    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.85f).toInt()
    }


    override fun initPopupContent() {
        super.initPopupContent()

        title = findViewById<TextView>(R.id.title)
        title?.text = titleContent

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dismiss()
        }

        val recycleView = findViewById<RecyclerView>(R.id.recycler_view)
        recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listPopupAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_pro_file_item)
                datas=data
                setItems(data)
            }
        }
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }

}