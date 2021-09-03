package com.daqsoft.module_workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.DailyTeamActivity
import com.daqsoft.module_workbench.adapter.DailyTeamallAdapter
import com.daqsoft.module_workbench.databinding.FragmentDailyOwnBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DataTime
import com.daqsoft.module_workbench.utils.AnimatorUtil
import com.daqsoft.module_workbench.viewmodel.DailyTeamOwnViewModel
import com.daqsoft.module_workbench.widget.ChooseTimePopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * 团队日报（全部）
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_OWN_ALL)
class DailyTeamOwnChildFragment : AppBaseFragment<FragmentDailyOwnBinding, DailyTeamOwnViewModel>() {

    @JvmField
    @Autowired
    var Id : String = ""

    var startTimes:String?=""

    var endTimes:String?=""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?): Int {
        return R.layout.fragment_daily_own
    }

    override fun initViewModel(): DailyTeamOwnViewModel? {
        return ViewModelProvider(this).get(DailyTeamOwnViewModel::class.java)
    }

    override fun initView() {
        super.initView()

        viewModel.setMenuId(Id,(activity as DailyTeamActivity). Id)

        LiveEventBus.get(LEBKeyGlobal.RESH_UI).observeForever {
            val isAll = it as Boolean
            if(isAll){
                viewModel.dataSource?.invalidate()
            }else{
                // 局部刷新
                viewModel.reFreshItemData()
            }
        }

        LiveEventBus.get(LEBKeyGlobal.CHOOST_TIME).observeForever {
            val dataTime = it as DataTime
            viewModel.setTimeData(dataTime.startTime!!,dataTime.endTime!!)
        }

        endTimes?.let { viewModel.setTimeData(startTimes!!,it,false) }

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

            if(it.isEmpty()){
                binding.llNodate.visibility=View.VISIBLE
                binding.recyclerviews.visibility=View.GONE
                return@Observer
            }
            binding.llNodate.visibility=View.GONE
            binding.recyclerviews.visibility=View.VISIBLE
            binding.recyclerviews.executePaging(it, viewModel.diff)
        })

//        binding.llTop.setOnClickListener {
//            showTopPopup(binding.llTop)
//        }
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
    @Inject
    lateinit var mAdapter : DailyTeamallAdapter

    private fun initRecycleView() {
        binding?.recyclerviews.apply {
            adapter = mAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    var layoutManager =binding.recyclerviews.layoutManager as LinearLayoutManager
                    val position  = layoutManager.findFirstVisibleItemPosition()
                    val firstVisiableChildView = layoutManager.findViewByPosition(position) ?: return
                    val height = firstVisiableChildView?.height

                    if( (position) * height!! - firstVisiableChildView.top==0){
                        LiveEventBus.get(LEBKeyGlobal.SHOWTITLE).post(false)
                    }else{
                        LiveEventBus.get(LEBKeyGlobal.SHOWTITLE).post(true)
                    }
                }
            })
        }
    }

    /**
     * 展示顶部类型展示
     */

//    val listPopupProject : ChooseTimePopup by lazy {
//        ChooseTimePopup(activity as DailyTeamActivity)
//    }
//
//    fun showTopPopup(view: View){
//        XPopup
//            .Builder(activity)
//            .atView(view)
//            .asCustom(listPopupProject.apply {
//                setOnChooseListerer(object : ChooseTimePopup.ChooseListerer{
//                    override fun chooseTime(startTime: String, endTime: String) {
//                        if(endTime.isNullOrBlank()){
//                            viewModel.chooseTag.set(startTime)
//                        }else{
//                            viewModel.chooseTag.set("$startTime~$endTime")
//                        }
//                        viewModel.setTimeData(startTime,endTime)
//
//                    }
//                })
//
//            })
//            .show()
//
//    }
}