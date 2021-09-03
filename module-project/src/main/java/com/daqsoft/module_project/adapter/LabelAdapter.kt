package com.daqsoft.module_project.adapter

import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.module_project.databinding.RecyclerviewInputPopupItemBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectLabel
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter

/**
 * @package name：com.daqsoft.module_project.adapter
 * @date 8/4/2021 上午 11:55
 * @author zp
 * @describe
 */
class LabelAdapter : BindingRecyclerViewAdapter<ProjectLabel>() {

    private var selectedList = mutableListOf<Int>()

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        super.onBindViewHolder(holder, position, payloads)

        val itemBinding = DataBindingUtil.getBinding<ViewDataBinding>(holder.itemView) as RecyclerviewInputPopupItemBinding
        itemBinding.label.tag = position
        getAdapterItem(position).isCheck=  selectedList.contains(position)
        itemBinding.label.isChecked = selectedList.contains(position)


        itemBinding.label.setOnClickListener {
           val  checked = !getAdapterItem(position).isCheck
            itemOnClickListener?.onClick(position,getAdapterItem(position),checked)
            val tag = itemBinding.label.tag
            getAdapterItem(position).isCheck=checked
            if (checked){
                if (!selectedList.contains(tag)) {
                    selectedList.add(position)
                }
            }else{
                if (selectedList.contains(tag)) {
                    selectedList.remove(position)
                }
            }
        }


        itemBinding.label.text = getAdapterItem(position).name
    }



    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,item: ProjectLabel,isChecked : Boolean)
    }


    /**
     * 检查是否包含
     * @param content String
     */
    fun checkLabelContains(content:String){
        if(content.isBlank()){
            return
        }
        val tags = selectedList.map {
            getAdapterItem(it).name
        }
        tags.forEachIndexed { index, s ->
            if (content.indexOf(s) == -1){
                selectedList.removeAt(index)
            }
        }
        Handler().postDelayed({
            notifyDataSetChanged()
        },100)

    }


    fun getSelectedList():List<ProjectLabel>{
        if (selectedList.isEmpty()){
            return arrayListOf()
        }
        return selectedList.map { getAdapterItem(it) }
    }


    /**
     * 重置
     */
    fun reset(){
        selectedList.clear()
        Handler().postDelayed({
            notifyDataSetChanged()
        },100)
    }

}