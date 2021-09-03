package com.daqsoft.module_workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialLabelUnit
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.px
import com.daqsoft.library_base.utils.sp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.FragmentPaySlipPasswordBinding
import com.daqsoft.module_workbench.viewmodel.PaySlipViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 9/12/2020 下午 2:56
 * @author zp
 * @describe 工资条密码 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PAY_SLIP_PASSWORD)
class PaySlipPasswordFragment : AppBaseFragment<FragmentPaySlipPasswordBinding, PaySlipViewModel>() {
    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_pay_slip_password
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): PaySlipViewModel? {
        return activity?.viewModels<PaySlipViewModel>()?.value
    }

    override fun initView() {

        binding.tips.text = SimplifySpanBuild()
            .append(
                SpecialTextUnit("如果忘了密码可通过进入")
                    .setTextSize(12.px.toFloat())
                    .setTextColor(resources.getColor(R.color.gray_666666))
            )
            .append(
                SpecialTextUnit("【我的-二级密码】")
                    .setTextSize(12.px.toFloat())
                    .setTextColor(resources.getColor(R.color.red_fa4848))

            )
            .append(
                SpecialTextUnit("找回")
                    .setTextSize(12.px.toFloat())
                    .setTextColor(resources.getColor(R.color.gray_666666))
            )
            .build()
    }
}