package com.daqsoft.module_workbench.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.DailyDetailAdapter
import com.daqsoft.module_workbench.databinding.ActivityDailyDetailBinding
import com.daqsoft.module_workbench.viewmodel.DailyDetailViewModel
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
@Route(path = ARouterPath.Workbench.PAGER_DAILY_DETAIL)
class DailyDetailActivity : AppBaseActivity<ActivityDailyDetailBinding, DailyDetailViewModel>() {

    @Inject
    lateinit var dailyDetailAdapter: DailyDetailAdapter

    @Autowired
    @JvmField
    var nextId: String? = null
    /**
     * 浏览时间
     */
    var scanTime:Int=0

    var stopTime:Boolean=true

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_daily_detail
    }

    override fun initViewModel(): DailyDetailViewModel? {
        return viewModels<DailyDetailViewModel>().value
    }

    override fun initView() {
        super.initView()
        if(nextId.isNullOrBlank()){
            viewModel.nextVisible.set(View.GONE)
        }else{
            viewModel.nextVisible.set(View.VISIBLE)
            viewModel.getNextDetailData(nextId!!)
        }
        addTimeData()
    }
//"next":114033,
    override fun initData() {
        viewModel.getDailyDetailRequest(intent.getIntExtra("id", 0 ))
        viewModel.getReadDialyDetail(intent.getIntExtra("id", 0 ))

        binding.recyclerView.adapter = dailyDetailAdapter

        LiveEventBus.get("updateDiscuss", String::class.java).observe(this, Observer {
            viewModel.getDailyDetailRequest(intent.getIntExtra("id", 0 ))
        })


    }


    override fun initViewObservable() {
        super.initViewObservable()
    }

    /**
     * 统计浏览的时间长度
     */
    private fun addTimeData() {
        Thread {
            while (stopTime){
                Thread.sleep(1000)
                scanTime++
            }
        }.start()
    }



    override fun onDestroy() {
        super.onDestroy()
        stopTime=false
        LiveEventBus.get(LEBKeyGlobal.SCAN_TIME).post(
            hashMapOf(
                "id" to intent.getIntExtra("id", 0 ).toString(),
                "scanTime" to scanTime.toString()
            )
        )

    }
}


