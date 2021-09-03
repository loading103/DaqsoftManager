package com.daqsoft.module_workbench.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDailySearchBinding
import com.daqsoft.module_workbench.databinding.ActivityDailyTeamSearchBinding
import com.daqsoft.module_workbench.databinding.ActivityDailyTeamSearchOwnBinding
import com.daqsoft.module_workbench.viewmodel.DailyMemberViewModel
import com.daqsoft.module_workbench.viewmodel.DailySearchTeamOwnViewModel
import com.daqsoft.module_workbench.viewmodel.DailySearchTeamViewModel
import com.daqsoft.module_workbench.viewmodel.DailySearchViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 4/12/2020 下午 2:16
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_TEAM_SEARCH_OWN)
class DailySearchTeamOwnActivity : AppBaseActivity<ActivityDailyTeamSearchOwnBinding, DailySearchTeamOwnViewModel>() {



    @JvmField
    @Autowired
    var menuId:String? = null


    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_daily_team_search_own
    }

    override fun initViewModel(): DailySearchTeamOwnViewModel? {
        return viewModels<DailySearchTeamOwnViewModel>().value
    }

    override fun initView() {
        super.initView()
        viewModel.menuIds=menuId
        initMultipleLayoutManager()
    }

    override fun initData() {

    }

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

    override fun initViewObservable() {
        super.initViewObservable()

        LiveEventBus.get(LEBKeyGlobal.RESH_UI).observeForever {
            val isAll = it as Boolean
            if(isAll){
                viewModel.dataSource?.invalidate()
            }else{
                // 局部刷新
                viewModel.reFreshItemData()
            }
        }

        viewModel.changedLiveData.observe(this, Observer {
            if(it){
                viewModel.pageList.observe(this, Observer {
                    if(it.isEmpty()){
                        multipleLayoutManager.showEmptyLayout()
                        return@Observer
                    }
                    multipleLayoutManager.showSuccessLayout()
                    binding.recyclerView.executePaging(it,viewModel.diff)
                })
            }
        })

    }

}


