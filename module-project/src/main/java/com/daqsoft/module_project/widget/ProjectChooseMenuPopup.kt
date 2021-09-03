package com.daqsoft.module_project.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_project.R
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.adapter.ListLeftPopupAdapter
import com.daqsoft.module_project.adapter.ListRightPopupAdapter
import com.daqsoft.module_project.repository.pojo.vo.ProjectChooseType
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.lxj.xpopup.impl.PartShadowPopupView
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 19/2/2021 下午 2:58
 * @author zp
 * @describe
 */
class ProjectChooseMenuPopup(context: Context) : PartShadowPopupView(context) {



    private val leftAdapter : ListLeftPopupAdapter by lazy { ListLeftPopupAdapter() }

    private val rightAdapter : ListRightPopupAdapter by lazy { ListRightPopupAdapter() }


    fun setData(data: MutableList<ProjectChooseType>){
        this.data = data
        leftAdapter.setItems(data)
        leftAdapter.notifyDataSetChanged()
    }

    private var data = mutableListOf<ProjectChooseType>()

    private var leftbean :ProjectChooseType?=null

    private var leftposition :Int=0



    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_top_two
    }

    override fun initPopupContent() {
        super.initPopupContent()
        initRecycleView()
    }

    private fun initRecycleView() {
        val recycleView1 = findViewById<RecyclerView>(R.id.recycle_view_right)
        recycleView1.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = rightAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_pro_list_popup_item_right)
                setItemOnClickListener(object : ListRightPopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, content: ProjectType) {
                        itemOnClickListener?.onClick(leftposition, leftbean!!,content)
                        rightAdapter.setLeftSelectedPosition(leftposition)
                        rightAdapter.notifyDataSetChanged()
                        postDelayed(Runnable {
                            if (popupInfo.autoDismiss){
                                dismiss()
                            }
                        },100)
                    }
                })
            }
        }

        val recycleView = findViewById<RecyclerView>(R.id.recycle_view_left)
        recycleView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = leftAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_pro_list_popup_item_left)
                setItems(data)
                leftbean=data[leftposition]
                rightAdapter.setItems(data[leftposition].datas)
                setItemOnClickListener(object : ListLeftPopupAdapter.ItemOnClickListener{
                    override fun onClick(position: Int, content: ProjectChooseType) {
                        leftbean=data[position]
                        leftposition=position
                        rightAdapter.setleftPosition(position)
                        rightAdapter.setItems(data[position].datas)
                        rightAdapter.notifyDataSetChanged()
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
        fun onClick(position: Int,content : ProjectChooseType,content1 :ProjectType)
    }

}