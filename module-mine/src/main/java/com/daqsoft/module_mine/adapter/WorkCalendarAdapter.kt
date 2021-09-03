package com.daqsoft.module_mine.adapter

import android.app.Application
import android.graphics.Paint
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.px
import com.daqsoft.library_base.utils.sp
import com.daqsoft.library_base.widget.flowlayout.FlowLayout
import com.daqsoft.library_base.widget.flowlayout.TagAdapter
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.RecyclerviewWorkAttendanceItemBinding
import com.daqsoft.module_mine.databinding.RecyclerviewWorkCalendarItemBinding
import com.daqsoft.module_mine.repository.pojo.vo.CareWord
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.module_mine.viewmodel.itemviewmodel.WorkAttendanceItemViewModel
import com.daqsoft.module_mine.viewmodel.itemviewmodel.WorkCalendarItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.ruffian.library.widget.RTextView
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.adapter
 * @date 31/12/2020 上午 10:30
 * @author zp
 * @describe
 */
class WorkCalendarAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when(item?.itemType){
            WorkCalendarViewModel.CALENDAR ->{
                val itemBinding = binding as RecyclerviewWorkCalendarItemBinding
                val itemViewModel = item as WorkCalendarItemViewModel

                binding.caringCl.visibility = View.GONE
                itemViewModel.day.restDay?.careWord?.let {
                    val list = arrayListOf<CareWord>()
                    if (it.size > 5 ){
                        list.addAll(it.subList(0,4))
                    }else{
                        list.addAll(it)
                    }
                    binding.caringCl.visibility = View.VISIBLE
                    binding.caring.adapter = object : TagAdapter<CareWord>(list){
                        override fun getView(parent: FlowLayout, position: Int, careWord: CareWord): View {
                            val mInflater = LayoutInflater.from(parent.context)
                            val view = mInflater.inflate(R.layout.flowlayout_work_calendar_item, parent, false)
                            val tv = view.findViewById<TextView>(R.id.label)
                            tv.text = careWord.careInfo
                            if (careWord.careUrl.isNotBlank()){
                                tv.paint.flags = Paint.UNDERLINE_TEXT_FLAG
                                tv.paint.isAntiAlias = true
                                tv.setOnClickListener {
                                    ARouter
                                        .getInstance()
                                        .build(ARouterPath.Web.PAGER_WEB)
                                        .withString("url",careWord.careUrl)
                                        .navigation()
                                }
                            }
                            return view
                        }
                    }
                }
            }
            WorkCalendarViewModel.CLOCK ->{}
            WorkCalendarViewModel.MEETING ->{}
            WorkCalendarViewModel.MISSION ->{}
            WorkCalendarViewModel.ATTENDANCE ->{
                val itemBinding = binding as RecyclerviewWorkAttendanceItemBinding
                val itemViewModel = item as WorkAttendanceItemViewModel

                itemBinding.leaveRecyclerView.apply {
                    layoutManager = LinearLayoutManager(ContextUtils.getContext(), LinearLayoutManager.HORIZONTAL,false)
                    addItemDecoration(object : RecyclerView.ItemDecoration(){
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            val position = parent.getChildAdapterPosition(view)
                            val count = state.itemCount - 1
                            outRect.right = if (position == count) 0 else 6.dp

                        }
                    })
                }

                itemBinding.abnormalRecyclerView.apply {
                    layoutManager = LinearLayoutManager(ContextUtils.getContext(), LinearLayoutManager.HORIZONTAL,false)
                    addItemDecoration(object : RecyclerView.ItemDecoration(){
                        override fun getItemOffsets(
                            outRect: Rect,
                            view: View,
                            parent: RecyclerView,
                            state: RecyclerView.State
                        ) {
                            val position = parent.getChildAdapterPosition(view)
                            val count = state.itemCount - 1
                            outRect.right = if (position == count) 0 else 6.dp

                        }
                    })
                }}
        }

    }

}