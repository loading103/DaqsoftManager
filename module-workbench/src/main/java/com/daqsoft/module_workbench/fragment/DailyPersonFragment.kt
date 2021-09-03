package com.daqsoft.module_workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.DailyTeamActivity
import com.daqsoft.module_workbench.databinding.ActivityDailyPerosnBinding
import com.daqsoft.module_workbench.utils.AnimatorUtil
import com.daqsoft.module_workbench.viewmodel.DailyListViewModel
import com.daqsoft.module_workbench.widget.ChooseTimePopup
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * 日报中的  个人日报（管理端的）
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_PERSON)
class DailyPersonFragment : AppBaseFragment<ActivityDailyPerosnBinding, DailyListViewModel>() {


    lateinit var  animatorUtil: AnimatorUtil

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?): Int {
        return R.layout.activity_daily_perosn
    }

    override fun initViewModel(): DailyListViewModel? {
        return ViewModelProvider(this).get(DailyListViewModel::class.java)
    }

    override fun initView() {
        super.initView()
        animatorUtil=AnimatorUtil(activity)
        LiveEventBus.get(LEBKeyGlobal.RESH_UI).observeForever {
            val isAll = it as Boolean
            if(isAll){
                viewModel.dataSource?.invalidate()
            }else{
                // 局部刷新
                viewModel.reFreshItemData()
            }
        }
        viewModel.setRightIconGone()
        initRefreshLayout()
        initRecycleView()
    }



    override fun initData() {

    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refresh.finishRefresh(it)
        })


        viewModel.finishedLiveData.observe(this, Observer {
           activity?.finish()
        })

        viewModel.pageList.observe(this, Observer {
            binding.recyclerView.executePaging(it, viewModel.diff)
        })
        viewModel.chooseTimeLiveData.observe(this, Observer {
            showTopPopup(binding.llTop)
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

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding?.recyclerView.apply {
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    var layoutManager =binding.recyclerView.layoutManager as LinearLayoutManager
                    val position  = layoutManager.findFirstVisibleItemPosition()
                    val firstVisiableChildView = layoutManager.findViewByPosition(position) ?: return
                    val height = firstVisiableChildView?.height

                    if( (position) * height!! - firstVisiableChildView.top==0){
                        if( binding.llTop?.visibility==View.VISIBLE) {
                            animatorUtil.performAnim(false,  binding.llTop)
                        }
                    }else{
                        if( binding.llTop?.visibility==View.GONE){
                            animatorUtil.performAnim(true,  binding.llTop)
                        }
                    }
                }
            })
        }
    }

    /**
     * 展示顶部类型展示
     */

    val listPopupProject : ChooseTimePopup by lazy {
        ChooseTimePopup(activity as DailyTeamActivity)
    }

    fun showTopPopup(view: View){
        XPopup
            .Builder(activity)
            .atView(view)
            .asCustom(listPopupProject.apply {
                setOnChooseListerer(object : ChooseTimePopup.ChooseListerer{
                    override fun chooseTime(startTime: String, endTime: String) {
                        if(endTime.isNullOrBlank()){
                            viewModel.chooseTag.set(startTime)
                        }else{
                            viewModel.chooseTag.set("$startTime~$endTime")
                        }
                        viewModel.setTimeData(startTime,endTime)

                    }
                })
            })
            .show()
    }
}