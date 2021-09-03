package com.daqsoft.module_mine.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityInterestBinding
import com.daqsoft.module_mine.viewmodel.InterestViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 19/11/2020 下午 5:37
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_INTEREST)
class InterestActivity : AppBaseActivity<ActivityInterestBinding, InterestViewModel>() {


    @JvmField
    @Autowired
    var content : String ? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_interest
    }

    override fun initViewModel(): InterestViewModel? {
        return viewModels<InterestViewModel>().value
    }

    override fun initData() {

        viewModel.interest.set(content)

        viewModel.getEmployeeToUpdate()
    }
}