package com.daqsoft.module_workbench.widget

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.ListPopupSearchAdapter
import com.daqsoft.module_workbench.repository.pojo.vo.DialyProjec
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.KeyboardUtils
import com.lxj.xpopup.util.XPopupUtils
import com.ruffian.library.widget.REditText
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.regex.Pattern

/**
 * @describe 日报所属项目
 */
class ProjectTypesPopup  : BottomPopupView, TextWatcher {

    private var isfirst:Boolean=true
    constructor(context: Context) : super(context)

    constructor(context: Context, title: String) : super(context) {
        this.titleContent = title
    }

    private val listPopupAdapter : ListPopupSearchAdapter by lazy { ListPopupSearchAdapter() }

    private var titleContent: String = ""

    private var title: TextView? = null

    private var search: REditText? = null

    fun setData(
        data: MutableList<DialyProjec>,
        chooseType: DialyProjec
    ){
        this.data = data
        this.savedata.clear()
        this.savedata.addAll(data)
        listPopupAdapter.setItems(data)
        listPopupAdapter.notifyDataSetChanged()
        if(isfirst){
            setSelectedPosition(chooseType)
            isfirst=false
        }

    }

    private var data = mutableListOf<DialyProjec>()
    private var savedata = mutableListOf<DialyProjec>()

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_bottom_type
    }


    private var  icons : List<Int> ? = null

    override fun initPopupContent() {
        super.initPopupContent()

        title = findViewById<TextView>(R.id.title)
        title?.text = titleContent
        search = findViewById<REditText>(R.id.search)
        search?.addTextChangedListener(this)
        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dismiss()
        }

        val recycleView = findViewById<RecyclerView>(R.id.recycler_view)
        recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listPopupAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_list_popup_item_search)
                setItems(data)
                setIcons(icons)
                setItemOnClickListener(object : ListPopupSearchAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, content: DialyProjec) {
                        itemOnClickListener?.onClick(position, content)
                        postDelayed(Runnable {
                            if (popupInfo.autoDismiss){
                                search?.text?.clear()
                                data.clear()
                                data.addAll(savedata)
                                KeyboardUtils.hideSoftInput(recycleView)
                                dismiss()
                            }
                        },100)
                    }
                })
            }
        }
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.85f).toInt()
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(bean: DialyProjec?){
        if(bean==null && data.size>0){
            listPopupAdapter?.setSelectedPosition(data[0])
        }
        data?.forEachIndexed { index, s ->
            if(bean?.id == s.id){
                listPopupAdapter.setSelectedPosition(bean)
            }
        }

    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : DialyProjec)
    }


    override fun afterTextChanged(p0: Editable?) {
        if(!p0.isNullOrBlank()){
            val datas = search(p0.toString(), savedata)
            data.clear()
            data.addAll(datas)
            listPopupAdapter.notifyDataSetChanged()
        }else{
            data.clear()
            data.addAll(savedata)
            listPopupAdapter.notifyDataSetChanged()
        }

    }



    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }


    /**
     * 模拟查询
     */
    fun search(name: String, list: List<DialyProjec>): MutableList<DialyProjec> {
        val results: MutableList<DialyProjec> = mutableListOf()
        val pattern: Pattern = Pattern.compile(name)
        for (i in list.indices) {
            if(list[i].projectName.contains(name)){
                results.add(list[i])
            }
        }
        return results
    }

}