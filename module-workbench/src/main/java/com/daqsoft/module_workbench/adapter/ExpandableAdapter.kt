package com.daqsoft.module_workbench.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.PaySlip
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 9/12/2020 下午 3:52
 * @author zp
 * @describe
 */
class ExpandableAdapter @Inject constructor() : BaseExpandableListAdapter () {

    private var mGroup : List<String> = arrayListOf()
    private var mChildren : List<List<PaySlip>> = arrayListOf()


    override fun getGroupCount(): Int {
        return mGroup.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return if(mGroup.isEmpty()) 0 else mChildren[groupPosition].size
    }

    override fun getGroup(groupPosition: Int): Any {
        return mGroup[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return  mChildren[groupPosition][childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val groupViewHolder: GroupViewHolder
        if (view == null) {
            view = LayoutInflater.from(ContextUtils.getContext()).inflate(
                R.layout.expandablelistview_pay_slip_group,
                parent,
                false
            )
            groupViewHolder = GroupViewHolder()
            groupViewHolder.title = view.findViewById<TextView>(R.id.title)
            groupViewHolder.arrow = view.findViewById<ImageView>(R.id.arrow)
            view.tag = groupViewHolder
        }else{
            groupViewHolder = view.tag as GroupViewHolder
        }
        groupViewHolder.title!!.text = mGroup[groupPosition]
        if (isExpanded){
            groupViewHolder.arrow!!.setImageResource(R.mipmap.txl_details_arrow_up)
        }else{
            groupViewHolder.arrow!!.setImageResource(R.mipmap.txl_details_arrow_down)
        }
        return view!!
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        val childrenViewHolder: ChildrenViewHolder
        if (view == null) {
            view = LayoutInflater.from(ContextUtils.getContext()).inflate(
                R.layout.expandablelistview_pay_slip_children,
                parent,
                false
            )
            childrenViewHolder = ChildrenViewHolder()
            childrenViewHolder.title = view.findViewById<TextView>(R.id.title)
            childrenViewHolder.line = view.findViewById<View>(R.id.line)
            childrenViewHolder.lineBold = view.findViewById<TextView>(R.id.line_bold)
            view.tag = childrenViewHolder
        }else{
            childrenViewHolder = view.tag as ChildrenViewHolder
        }
        childrenViewHolder.title!!.text = mChildren[groupPosition][childPosition].wageMonth.split("年")[1]

        if (childPosition == mChildren[groupPosition].size-1){
            childrenViewHolder.line!!.visibility =View.GONE
            childrenViewHolder.lineBold!!.visibility =View.VISIBLE
        }else{
            childrenViewHolder.line!!.visibility =View.VISIBLE
            childrenViewHolder.lineBold!!.visibility =View.GONE
        }
        return view!!
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    internal class GroupViewHolder {
        var title: TextView? = null
        var arrow: ImageView? = null
    }

    internal class ChildrenViewHolder {
        var title: TextView? = null
        var arrow: ImageView? = null
        var line:View?= null
        var lineBold:View?= null
    }


    fun setGroup(list: List<String>){
        this.mGroup = list
    }

    fun setChildren(list: List<List<PaySlip>>){
        this.mChildren = list
    }

    fun getGroupList() : List<String>{
        return this.mGroup
    }

    fun getChildrenList() : List<List<PaySlip>>{
        return this.mChildren
    }
}