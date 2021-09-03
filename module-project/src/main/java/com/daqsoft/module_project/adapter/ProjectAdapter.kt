package com.daqsoft.module_project.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.widget.flowlayout.FlowLayout
import com.daqsoft.library_base.widget.flowlayout.TagAdapter
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.ItemProjectListBinding
import com.daqsoft.module_project.repository.pojo.vo.Construct
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

class ProjectAdapter @Inject constructor() : BindingRecyclerViewAdapter<ItemViewModel<*>>() {

    var  types:List<ProjectType> = mutableListOf()
    lateinit  var itemviewModel: ProjectItemViewModel
    override fun onBindBinding(binding: ViewDataBinding, variableId: Int, layoutRes: Int, position: Int, item: ItemViewModel<*>?) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as ItemProjectListBinding
        val itemViewModel = item as ProjectItemViewModel

        itemViewModel.datas?.get()?.construct?.let {
            if(it.isNotEmpty()){
                itemBinding.caringCl.visibility = View.VISIBLE
            }
            itemBinding.caring.setMaxLine(4)
            itemBinding.caring .setExceedingMaxLimitListener {index->
                itemBinding.caring.getChildAt(index-1).findViewById<TextView>(R.id.label).text="....."
            }
            itemBinding.caring.adapter = object : TagAdapter<Construct>(it){
                override fun getView(parent: FlowLayout, position: Int, careWord: Construct): View {
                    val mInflater = LayoutInflater.from(parent.context)
                    val view = mInflater.inflate(R.layout.item_project_tag, parent, false)
                    val tv = view.findViewById<TextView>(R.id.label)
                    tv.text = careWord.productName
                    return view
                }
            }
        }
        var typeColor= ""
        // 项目类型的颜色
        if(types.isNotEmpty()){
            types.forEachIndexed { index, bean ->
                if(bean.name==itemViewModel.datas.get()?.projectTypeName && !TextUtils.isEmpty(bean.color)){
                    itemBinding.tvType.setBackgroundColor(Color.parseColor(bean.color))
                    typeColor= bean.color.toString()
                }
            }
        }
        //  流程状态
        if( TextUtils.isEmpty(itemViewModel.datas?.get()?.delayDays)){
            itemBinding.tvState.visibility=View.GONE
            itemBinding.tvTian.visibility=View.GONE
            itemBinding.tvTime.visibility=View.GONE
            itemBinding.tvDays.visibility=View.GONE
        }else{
            val split = itemViewModel.datas?.get()?.delayDays?.split(" ")
            if(split?.size!! >1){
                itemBinding.tvState.visibility=View.GONE
                itemBinding.tvTian.visibility=View.VISIBLE
                itemBinding.tvTime.visibility=View.VISIBLE
                itemBinding.tvDays.visibility=View.VISIBLE
                itemBinding.tvTime.text=split[0]
                itemBinding.tvDays.text=split[1].substring(0,split[1].length-1)
            }else{
                itemBinding.tvState.visibility=View.VISIBLE
                itemBinding.tvState.text=itemViewModel.datas?.get()?.delayDays
                itemBinding.tvTian.visibility=View.GONE
                itemBinding.tvTime.visibility=View.GONE
                itemBinding.tvDays.visibility=View.GONE
            }

        }

//        itemBinding.root.setOnClickListener {
//
//            itemviewModel=itemViewModel
//            ARouter
//                .getInstance()
//                .build(ARouterPath.Project.PAGER_PROJECT_DETAIL)
//                .withString("id",itemViewModel.datas.get()?.id.toString())
//                .withString("typeColor",typeColor)
//                .navigation()
//        }
    }

    fun setListType(it: List<ProjectType>?) {
        if (it != null) {
            this.types=it
        }
    }
}