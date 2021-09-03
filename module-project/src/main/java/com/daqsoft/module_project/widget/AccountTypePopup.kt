package com.daqsoft.module_project.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_project.R
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.adapter.ListPopupAdapter
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @describe 费用类型 popup
 */
class AccountTypePopup  : BottomPopupView {

    constructor(context: Context) : super(context)

    constructor(context: Context, title: String) : super(context) {
        this.titleContent = title
    }

    private val listPopupAdapter : ListPopupAdapter by lazy { ListPopupAdapter() }

    private var titleContent: String = ""

    private var title: TextView? = null

    fun setData(data:MutableList<String>){
        this.data = data
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
    }

    private var data = mutableListOf<String>()

    fun getData() = data

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_type
    }


    private var  icons : List<Int> ? = null

    fun setIcons(icons:MutableList<Int>){
        this.icons = icons
        listPopupAdapter.setIcons(icons)
        listPopupAdapter.notifyDataSetChanged()
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
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_pro_list_popup_item)
                setItems(data)
                setIcons(icons)
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

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(getContext())*.85f).toInt()
    }
    fun setTitle(title: String) {
        this.title?.text = title
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