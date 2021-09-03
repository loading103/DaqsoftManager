package com.daqsoft.module_mine.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.adapter.WorkCalendarAdapter
import com.daqsoft.module_mine.databinding.FragmentWorkCalendarBinding
import com.daqsoft.module_mine.repository.pojo.vo.DateInfo
import com.daqsoft.module_mine.repository.pojo.vo.Day
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_mine.fragment
 * @date 13/11/2020 下午 1:39
 * @author zp
 * @describe 工作日历
 */
@AndroidEntryPoint
class WorkCalendarFragment : AppBaseFragment<FragmentWorkCalendarBinding, WorkCalendarViewModel>(){


    @Inject
    lateinit var workCalendarAdapter : WorkCalendarAdapter

    companion object{
        private const val WORK_CALENDAR = "workCalendar"
        private const val DAY = "day"

        fun newInstance(workCalendar: String,day : Day):WorkCalendarFragment {
            val args = Bundle()
            args.putString(WORK_CALENDAR, workCalendar)
            args.putParcelable(DAY, day)
            val fragment = WorkCalendarFragment()
            fragment.arguments = args
            return fragment
        }
    }

    private var workCalendar:String? = null
    var day:Day? = null


    override fun initParam() {
        super.initParam()
        workCalendar = arguments?.getString(WORK_CALENDAR, "")
        day = arguments?.getParcelable(DAY)
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_work_calendar
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): WorkCalendarViewModel? {
        // 这个地方不需要共享viewModel
//        return activity?.viewModels<WorkCalendarViewModel>()?.value
        return ViewModelProvider(this).get(WorkCalendarViewModel::class.java)
    }

    override fun initData() {
        viewModel.initData(workCalendar,day)
        viewModel.getMeetingInfo(workCalendar!!,day!!)
        viewModel.getAttendanceInfo(workCalendar!!)
        viewModel.getTaskInfo(workCalendar!!,day!!)
    }

    override fun initView() {
        initRecycleView()
    }

    private fun initRecycleView(){
        binding.recycleView.apply {
            adapter = workCalendarAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 8.dp
                }
            })

        }
    }
}