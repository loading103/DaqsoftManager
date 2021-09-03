package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewProFileItemBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DeptType
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding

class ListFilePopupAdapter : BindingRecyclerViewAdapter<DeptType>() {

    //    private var selectedPosition= -1
    var listFilePopupAdapter: ListFilePopupAdapter?=null
    var datas = mutableListOf<DeptType>()
    //  保存上一级的状态
    var lastTopDatas :DeptType?=null
    //  保存第一级选中的id
    var lastAdapter :ListFilePopupAdapter?=null


    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: DeptType
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding1 = binding as RecyclerviewProFileItemBinding
        itemBinding1.content.isSelected = item.havechoose == true

        // 有次级目录的item
        if(!item.childs.isNullOrEmpty()){
            //初始化下级recycleView
            listFilePopupAdapter=  ListFilePopupAdapter().apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_pro_file_item)
                setItems(item.childs)
            }
            listFilePopupAdapter?.datas=datas
            listFilePopupAdapter?.lastTopDatas=item
            listFilePopupAdapter?.lastAdapter=this

            itemBinding1.fileRecyclerView.adapter=listFilePopupAdapter

            // 去掉有次级目录的右边的勾勾
            itemBinding1.content.setCompoundDrawables(null, null, null, null)
            // 去掉有次级目录的左边的箭头
            itemBinding1.ivHead.visibility=View.VISIBLE
            // 是否展示下级菜单（默认的都不展开）
            if(item.isVisible == true){
                itemBinding1.fileRecyclerView.visibility=View.VISIBLE
                itemBinding1.ivHead.setImageResource(R.mipmap.rbxq_arrow_down)
            }else{
                itemBinding1.fileRecyclerView.visibility=View.GONE
                itemBinding1.ivHead.setImageResource(R.mipmap.add_tzgg_arrow)
            }

            // 点击内容选中
            itemBinding1.llHead.setOnClickListener {

                datas?.let { cleacData(it) }
                item.havechoose= true
                lastTopDatas?.havechoose=true
                lastTopDatas?.isVisible=true
                lastVisible(true)

                if( itemBinding1.fileRecyclerView.visibility==View.VISIBLE){
                    item.isVisible= false
                    itemBinding1.fileRecyclerView.visibility=View.GONE
                    itemBinding1.ivHead.setImageResource(R.mipmap.add_tzgg_arrow)
                }else{
                    item.isVisible=true
                    itemBinding1.fileRecyclerView.visibility=View.VISIBLE
                    itemBinding1.ivHead.setImageResource(R.mipmap.rbxq_arrow_down)
                }
                notifyDataSetChanged()
                listFilePopupAdapter?.notifyDataSetChanged()
            }
            // 点击内容选中
            itemBinding1.content.setOnClickListener {
                datas?.let { cleacData(it) }
                item.havechoose= true
                lastTopDatas?.havechoose=true
                lastTopDatas?.isVisible=true
                lastVisible(true)
                notifyDataSetChanged()
                LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_FILED).post(item)
            }


        }else{
            itemBinding1.fileRecyclerView.visibility=View.GONE
            itemBinding1.ivHead.visibility=View.INVISIBLE

            //无下级目录才显示勾勾
            if ( item.havechoose == true ){
                val drawable = itemBinding1.content.context.resources.getDrawable(R.mipmap.mine_sr_selected)
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                itemBinding1.content.setCompoundDrawables(null, null, drawable, null)

            }else{
                itemBinding1.content.setCompoundDrawables(null, null, null, null)
            }

            itemBinding1.content.setOnClickListener {
                datas?.let { cleacData(it) }
                item.havechoose= true
                lastTopDatas?.havechoose=true
                lastTopDatas?.isVisible=true
                lastVisible(true)
                notifyDataSetChanged()
                LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_FILED).post(item)
            }
        }
    }


    fun cleacData( ss : MutableList<DeptType> ){
        ss?.forEach {
            it.havechoose=false
            it.isVisible=false
            if(!it.childs.isNullOrEmpty()){
                cleacData(it.childs!!.toMutableList())
            }
        }
    }



    fun lastVisible( show:Boolean){
        lastAdapter?.lastTopDatas?.isVisible=show
        lastAdapter?.lastTopDatas?.havechoose=show
        lastAdapter?.lastVisible(show)
    }



    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }
}