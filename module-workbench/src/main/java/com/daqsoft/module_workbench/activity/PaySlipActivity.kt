package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityPaySlipBinding
import com.daqsoft.module_workbench.viewmodel.PaySlipViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 9/12/2020 下午 2:15
 * @author zp
 * @describe 工资条
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PAY_SLIP)
class PaySlipActivity : AppBaseActivity<ActivityPaySlipBinding, PaySlipViewModel>() {
    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_pay_slip
    }

    override fun initViewModel(): PaySlipViewModel? {
        return viewModels<PaySlipViewModel>().value
    }

    override fun initView() {
        super.initView()
        initFrame()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.verificationLiveData.observe(this, Observer {
            val listFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PAY_SLIP_LIST).navigation() as Fragment
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.frame_layout, listFragment)
                setMaxLifecycle(listFragment, Lifecycle.State.RESUMED)
                commit()
            }
        })

    }

    private fun initFrame(){
        val passwordFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PAY_SLIP_PASSWORD).navigation() as Fragment
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, passwordFragment)
            setMaxLifecycle(passwordFragment, Lifecycle.State.RESUMED)
            commit()
        }
    }
}