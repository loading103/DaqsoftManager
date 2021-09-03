package com.daqsoft.module_project.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_project.R
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.adapter.BottomProjectDetailAdapter
import com.daqsoft.module_project.adapter.ListPopupAdapter
import com.lxj.xpopup.core.BottomPopupView
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @describe 项目详情点击更多
 */
class ProjectDetailPopup  : BottomPopupView {

    private var data = mutableListOf<Pair<Int, String>>()

    constructor(context: Context) : super(context)

    constructor(context: Context, title: String) : super(context) {
        this.titleContent = title
    }

    private val listPopupAdapter : BottomProjectDetailAdapter by lazy { BottomProjectDetailAdapter() }

    private var titleContent: String = ""

    private var title: TextView? = null

    fun setData(data: ArrayList<Pair<Int, String>>){
        this.data = data
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
    }


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_project_more
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
                itemBinding = ItemBinding.of(BR.pair, R.layout.recyclerview_popup_bottom_item1)
                setItems(data)
                setItemOnClickListener(object : BottomProjectDetailAdapter.ItemOnClickListener{
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


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }

}