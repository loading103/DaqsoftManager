package com.daqsoft.module_workbench.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityCompanyDetailBinding
import com.daqsoft.module_workbench.viewmodel.CompanyDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 30/3/2021 上午 11:19
 * @author zp
 * @describe 公司详情
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_COMPANY_DETAIL)
class CompanyDetailActivity : AppBaseActivity<ActivityCompanyDetailBinding, CompanyDetailViewModel>() {
    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_company_detail
    }

    override fun initViewModel(): CompanyDetailViewModel? {
        return viewModels<CompanyDetailViewModel>().value
    }

    override fun initData() {
        super.initData()
        viewModel.getCompanyInfo()
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.callLiveData.observe(this, Observer {
            callPhone(it)
        })
    }



    private fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }
}