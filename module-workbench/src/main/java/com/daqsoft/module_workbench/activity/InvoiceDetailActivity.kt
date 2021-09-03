package com.daqsoft.module_workbench.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityInvoiceDetailBinding
import com.daqsoft.module_workbench.viewmodel.InvoiceDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 30/3/2021 下午 2:04
 * @author zp
 * @describe 发票详情
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_INVOICE_DETAIL)
class InvoiceDetailActivity : AppBaseActivity<ActivityInvoiceDetailBinding, InvoiceDetailViewModel>() {
    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_invoice_detail
    }

    override fun initViewModel(): InvoiceDetailViewModel? {
        return viewModels<InvoiceDetailViewModel>().value
    }

    override fun initData() {
        super.initData()
        viewModel.getInvoiceInfo()
    }
}