package com.daqsoft.module_workbench.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.AnnouncementAdapter
import com.daqsoft.module_workbench.databinding.ActivityAnnouncementBinding
import com.daqsoft.module_workbench.viewmodel.AnnouncementViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementItemViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 4/12/2020 下午 2:16
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ANNOUNCEMENT)
class AnnouncementActivity : AppBaseActivity<ActivityAnnouncementBinding, AnnouncementViewModel>() {

    @Inject
    lateinit var announcementAdapter: AnnouncementAdapter

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_announcement
    }

    override fun initViewModel(): AnnouncementViewModel? {
        return viewModels<AnnouncementViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRecycleView()
        initRefreshLayout()
    }

    override fun initData() {
        binding.setVariable(BR.untreated, Untreated.INSTANCE)
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refresh.finishRefresh(it)
        })

        viewModel.pageList.observe(this, Observer {
            binding.refresh.finishRefresh(true)
            binding.recyclerView.executePaging(it, viewModel.diff)
        })

        Untreated.INSTANCE.addOnPropertyChangedCallback(object :
            Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                viewModel.initToolbar()
            }
        })

        // 消息数量改变
        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED, String::class.java).observe(
            this,
            Observer {
                val itInt = it.toInt()
                if (itInt > 0) {
                    val item = viewModel.pageList.value!![itInt] as AnnouncementItemViewModel
                    val itemRead = item.statusObservable.get()!!
                    if (itemRead == 0) {
                        return@Observer
                    }
                    item.statusObservable.set(0)
                }
            })

        LiveEventBus.get(LEBKeyGlobal.ANNOUNCEMENT_ACTIVITY_REFRESH, HashMap::class.java).observe(this,
            Observer {
                viewModel.onClickItemViewModel?.let {itemViewModel ->

                    itemViewModel.readAmount.set( AppUtils.numberFormatGroupingUsed(it["readAmount"].toString().toInt()) )
                    itemViewModel.commentAmount.set(AppUtils.numberFormatGroupingUsed(it["commentAmount"].toString().toInt()) )
                    itemViewModel.likeAmount.set(AppUtils.numberFormatGroupingUsed(it["likeAmount"].toString().toInt()) )

                }

            })

    }

    /**
     * 初始化下拉刷新
     */
    private fun initRefreshLayout() {
        binding.refresh.setOnRefreshListener {
            viewModel.dataSource?.invalidate()
        }
    }

    //RecyclerView的第一个可视item的position
    private var lastPosition = 0
    //与RecyclerView顶部的偏移量
    private var lastOffset = 0


    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            adapter = announcementAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == 0) {
                        outRect.top = 12.dp
                    }
                }
            })

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (layoutManager != null) {
                        val mLayoutManager = layoutManager as LinearLayoutManager
                        val topView = mLayoutManager.getChildAt(0)
                        if (topView != null) {
                            lastOffset = topView.top
                            lastPosition = mLayoutManager.getPosition(topView);
                        }
                    }
                }
            })
        }
    }

}


