package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityRuleDailyBinding
import com.daqsoft.module_workbench.viewmodel.DailyRuleViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @describe  日报生成规则
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_RULE)
class DailyRuleActivity : AppBaseActivity<ActivityRuleDailyBinding, DailyRuleViewModel>() {

    @JvmField
    @Autowired
    var id : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_rule_daily
    }

    override fun initViewModel(): DailyRuleViewModel? {
        return viewModels<DailyRuleViewModel>().value
    }

    override fun initView() {
        super.initView()
    }

    override fun initData() {
        super.initData()
        viewModel.id=id
        viewModel.getDayReportSet()
    }
    /**
     * 点击监听事件
     */
    override fun initViewObservable() {
        super.initViewObservable()
    }


}