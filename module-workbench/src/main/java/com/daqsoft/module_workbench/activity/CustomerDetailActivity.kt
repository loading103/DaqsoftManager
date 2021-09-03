package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.CutomerFlowAdapter
import com.daqsoft.module_workbench.databinding.ActivityCustmerDetailBinding
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerType
import com.daqsoft.module_workbench.viewmodel.CustomerDetailViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * 客户管理列表界面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CUSTOMER_DETAIL)
class CustomerDetailActivity : AppBaseActivity<ActivityCustmerDetailBinding, CustomerDetailViewModel>() {

    @JvmField
    @Autowired
    var id: String? = null

    @JvmField
    @Autowired
    var colorJson : String = ""

    @Inject
    lateinit var projectDetailAdapter : CutomerFlowAdapter

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_custmer_detail
    }

    override fun initViewModel(): CustomerDetailViewModel? {
        return viewModels<CustomerDetailViewModel>().value
    }

    override fun initView() {
        super.initView()

        binding.recyclerView.adapter = projectDetailAdapter
    }

    override fun initData() {
        if(!colorJson.isNullOrBlank()){
            viewModel.typeDatas.value = Gson().fromJson(colorJson, object : TypeToken<ArrayList<CustomerType>>() {}.type)
        }
        viewModel.getdatas(id!!)
        viewModel.getRecorddatas(id!!)
    }

    override fun initViewObservable() {
        super.initViewObservable()
        // 进入网页刷新该界面
        LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).observeForever {
            viewModel.getdatas(id!!)
            viewModel.getRecorddatas(id!!)
        }

        LiveEventBus.get(LEBKeyGlobal.CUSTOMER_SENT_SUCCESSFULLY,Boolean::class.java).observe(this, Observer {
            viewModel.getdatas(id!!)
            viewModel.getRecorddatas(id!!)
        })
    }
}


