package com.daqsoft.module_workbench.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.widget.FullyGridLayoutManager
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.FragmentMemberDailyBinding
import com.daqsoft.module_workbench.viewmodel.DailyListViewModel
import com.daqsoft.module_workbench.viewmodel.DailyMemberViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * @describe 成员日报
 */
@AndroidEntryPoint
@Route(path =ARouterPath.Workbench.PAGER_DAILY_MEMBER)
class DailyMemberFragment : AppBaseFragment<FragmentMemberDailyBinding, DailyMemberViewModel>() {


    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_member_daily
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): DailyMemberViewModel? {
        return activity?.viewModels<DailyMemberViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        initRefresh()
        initAppBar()
        initRecycleView()
        initMultipleLayoutManager()
        binding.title.setOnClickListener {
            showDatePicker()
        }
    }


    override fun initData() {
        super.initData()
        viewModel.getNoSubmitMember()
        viewModel.getyReportStatistic()
    }
    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerTop.apply {
            layoutManager = FullyGridLayoutManager(context, 6, GridLayoutManager.VERTICAL, false)
        }
        binding.recyclerView.apply {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if(position == count){
                        outRect.bottom = 15.dp
                    }
                }
            })
        }
    }
    /**
     * 初始化 AppBar(处理下来和下滑冲突)
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                        binding.refreshLayout.setEnableRefresh(true)
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                        binding.refreshLayout.setEnableRefresh(false)
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                        binding.refreshLayout.setEnableRefresh(false)
                    }
                }
            }
        })
    }


    private fun initRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            viewModel.getNoSubmitMember()
            viewModel.getyReportStatistic()
            viewModel.dataSource?.invalidate()
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refreshLayout.finishRefresh(it)
        })
        viewModel.finishedLiveData.observe(this, Observer {
            activity?.finish()
        })
        viewModel.pageList.observe(this, Observer {
            if(it.isEmpty()){
                multipleLayoutManager.showEmptyLayout()
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()
            binding.recyclerView.executePaging(it,viewModel.diff)
        })

    }


    /**
     * 展示顶部类型展示
     */


    private fun initMultipleLayoutManager() {

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .build()

        multipleLayoutManager.showLoadingLayout()
    }

    /**
     * 时间选择器
     */
    lateinit var timePicker : TimePickerView
    fun showDatePicker() {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance() //系统当前时间
        // 正确设置方式 原因：注意事项有说明
        startDate[1970, 1] = 1
        endDate[2030, 12] = 31
        timePicker = TimePickerBuilder(activity,
            OnTimeSelectListener { date, v ->
                val stampToTime = viewModel.stampToTime(date.time.toString())
                viewModel.timeData.set(stampToTime)
                if(viewModel.stampToTime(System.currentTimeMillis().toString())==stampToTime){
                    viewModel.chooseTag.set("今天")
                }else{
                    viewModel.chooseTag.set(stampToTime)
                }
                viewModel.getNoSubmitMember()
                viewModel.getyReportStatistic()
                viewModel.dataSource?.invalidate()
            }).setTimeSelectChangeListener { }
            .addOnCancelClickListener { }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isDialog(true)
            // 默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5)
            // 若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setCancelText("取消")
            // 取消按钮文字
            .setSubmitText("确认")
            .setDate(selectedDate)
            // 确认按钮文字
            .setTitleSize(20)
            // 标题文字大小
            .setOutSideCancelable(true)
            // 点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true)
            // 是否循环滚动
            .setTitleBgColor(-0xa0a0b)
            // 标题背景颜色 Night mode
            .setSubmitColor(
                resources.getColor(R.color.red_fa4848)
            ) //确定按钮文字颜色
            .setCancelColor(
                resources.getColor(R.color.color_333333)
            )
            // 取消按钮文字颜色
            .setRangDate(startDate, endDate)
            // 起始终止年月日设定
            .setLabel("年", "月", "日", null, null, null)
            // 是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isCenterLabel(false)
            .build()
        val mDialog = timePicker.dialog
        if (mDialog != null) {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            params.leftMargin = 0
            params.rightMargin = 0
            timePicker.dialogContainerLayout.layoutParams = params
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim)
                dialogWindow.setGravity(Gravity.BOTTOM)
                dialogWindow.setDimAmount(0.3f)
            }
        }
        timePicker?.show()
    }



}