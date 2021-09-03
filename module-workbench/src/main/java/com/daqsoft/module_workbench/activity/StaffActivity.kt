package com.daqsoft.module_workbench.activity

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.StaffAdapter
import com.daqsoft.module_workbench.databinding.ActivityStaffBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffItemUpcomingBinding
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffItemUpcomingViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.module_workbench.widget.StaffRefreshFooter
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffPagedObservableList
import timber.log.Timber
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 27/11/2020 下午 2:33
 * @author zp
 * @describe 员工详情
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_STAFF)
class StaffActivity : AppBaseActivity<ActivityStaffBinding, StaffViewModel>() {

    @Inject
    lateinit var staffAdapter : StaffAdapter

    @JvmField
    @Autowired
    var id : String = ""

    @JvmField
    @Autowired
    var name : String? = ""

    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_staff
    }

    override fun initViewModel(): StaffViewModel? {
        return viewModels<StaffViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRecycleView()
        initMascotAnimation()
        initAppBar()
        initRefreshView()

        initMultipleLayoutManager()
    }

    /**
     * 初始化状态管理
     */
    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_error,null,false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        emptyContent.text = "暂无数据"

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .setEmptyLayout(emptyView)
            .build()

        multipleLayoutManager.showEmptyLayout()
    }

    /**
     * 初始化 下拉刷新
     */
    private fun initRefreshView() {
        binding.refresh.run {
            setEnableRefresh(false)
            setEnableLoadMore(true)
            setRefreshFooter(StaffRefreshFooter(this@StaffActivity))
            setEnablePureScrollMode(true)

        }
    }

    /**
     * 初始化recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            adapter = staffAdapter.apply {
                setUpdateTaskStatus(object : StaffAdapter.UpdateTaskStatus{
                    override fun updateStatus(
                        firstStatus: Int,
                        firstItemBinding: RecyclerviewStaffItemUpcomingBinding,
                        firstItemViewModel: StaffItemUpcomingViewModel
                    ) {
                        viewModel.updateTaskStatus(firstStatus, firstItemBinding, firstItemViewModel)
                    }

                    override fun read(
                        itemBinding: RecyclerviewStaffItemUpcomingBinding,
                        itemViewModel: StaffItemUpcomingViewModel
                    ) {
                        viewModel.read(itemBinding,itemViewModel)
                    }

                })
            }
        }
    }

    /**
     * 初始化 吉祥物动画
     */
    private fun initMascotAnimation() {
        val animator = ObjectAnimator.ofFloat(binding.mascot, "translationX", 0f, -(50.dp).toFloat(), 0f, 50.dp.toFloat(), 0f)
        animator.duration = 3000
        animator.interpolator = LinearInterpolator()
        animator.repeatCount = -1
        animator.start()
    }


    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                    }
                }
            }

            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
                if(i == 0){
                    return
                }
                val recyclerViewTop = binding.refresh.top
                val minDistance = AppUtils.getStatusBarHeight() + AppUtils.getToolbarHeight()
                if (recyclerViewTop < minDistance) {
                    viewModel.setBackground(Color.WHITE)
                    viewModel.setBackIconSrc(R.mipmap.back_black)
                    viewModel.setRightIconSrc(R.mipmap.txl_list_search_black)
                    viewModel.setTitleTextColor(R.color.black_000000)

                } else {
                    viewModel.setBackground(Color.TRANSPARENT)
                    viewModel.setBackIconSrc(R.mipmap.list_top_return_white)
                    viewModel.setRightIconSrc(R.mipmap.txl_list_search_white)
                    viewModel.setTitleTextColor(R.color.white_ffffff)
                }
            }
        })
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.callLiveData.observe(this, Observer {
            callPhone(it)
        })

        viewModel.updateCompletedLiveData.observe(this, Observer {
            staffAdapter.updateCompleted(it.first, it.second)
        })

        viewModel.layoutLiveData.observe(this, Observer {
            multipleLayoutManager.showSuccessLayout()
        })

        LiveEventBus.get(LEBKeyGlobal.TASK_STATUS_CHANGE,String::class.java).observe( this,Observer {
            staffAdapter.updateTaskStatusSchedule(it.toInt())
        })
    }

    override fun initData() {
        super.initData()

        viewModel.id = id

        if (id==DataStoreUtils.getInt(DSKeyGlobal.USER_ID).toString()){
            viewModel.isOneself = true
        }

        viewModel.setTitleText(name ?: "")
        viewModel.getEmployeeInfo()
        viewModel.getUpcomingAndParticipate()
    }


    private fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        staffAdapter.stopTimer()
    }

}