package com.daqsoft.module_workbench.widget

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.BottomPopupAdapter
import com.lxj.xpopup.core.BottomPopupView
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 22/2/2021 下午 4:51
 * @author zp
 * @describe
 */
class DeptDocBottomMenuPopup  : BottomPopupView{

    constructor(context: Context):super(context)

    constructor(context: Context,title: String):super(context){
        this.titleContent = title
    }

    private var titleContent : String = ""

    private var title : TextView? = null

    private val bottomPopupAdapter  by lazy { BottomPopupAdapter() }

    fun setData(data:MutableList<Pair<Int, String>>){
        this.data = data
        bottomPopupAdapter.setItems(data)
        bottomPopupAdapter.notifyDataSetChanged()
    }

    private var data = mutableListOf<Pair<Int, String>>()

    fun getData() = data


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_bottom
    }

    override fun initPopupContent() {
        super.initPopupContent()

        title = findViewById<TextView>(R.id.title)
        title?.text = titleContent

        val close = findViewById<ImageView>(R.id.close)
        close?.setOnClickListener {
            dismiss()
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView?.adapter = bottomPopupAdapter.apply {
            itemBinding = ItemBinding.of(BR.pair, R.layout.recyclerview_popup_bottom_item)
            setItems(data)
            setItemOnClickListener(object : BottomPopupAdapter.ItemOnClickListener {
                override fun onClick(position: Int, text: String) {
                    Timber.e("text ${text}")
                    itemOnClickListener?.onClick(position,text)
                    postDelayed(Runnable {
                        if (popupInfo.autoDismiss){
                            dismiss()
                        }
                    },100)
                }
            })
        }
    }


    fun setTitle(title:String){
        this.title?.text = title
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }
}