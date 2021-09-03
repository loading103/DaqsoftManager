package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.isWebsite
import com.daqsoft.library_base.utils.toWebsite
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 30/3/2021 上午 11:23
 * @author zp
 * @describe 公司详情 viewModel
 */
class CompanyDetailViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    override fun onCreate() {
        super.onCreate()
        initToolBar()
    }

    private fun initToolBar(){
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
    }


    val callLiveData = MutableLiveData<String>()

    /**
     * 拨号点击事件
     */
    val callOnclick = BindingCommand<Unit>(BindingAction {
        callLiveData.value = companyInfo.get()!!.companyPhone
    })

    /**
     * 官网 点击事件
     */
    val websiteOnClick = BindingCommand<Unit>(BindingAction {
        if (companyInfo.get() == null){
            return@BindingAction
        }
        var website = companyInfo.get()!!.companyWebsite
        if (!website.isWebsite()){
            website = website.toWebsite()
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    })

    /**
     * 大旗云 点击事件
     */
    val cloudOnClick = BindingCommand<Unit>(BindingAction {
        if (companyInfo.get() == null){
            return@BindingAction
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",companyInfo.get()!!.getCloud().toWebsite())
            .navigation()
    })



    /**
     * 复制点击事件
     */
    val copyOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(companyInfo.get()?.companyAddress?:"")
    })

    /**
     * 发票点击事件
     */
    val invoiceOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_INVOICE_DETAIL).navigation()
    })



    /**
     * 公司信息
     */
    val companyInfo = ObservableField<CompanyInfo>()

    /**
     * 获取公司信息
     */
    fun  getCompanyInfo(){
        addSubscribe(
            model
                .getCompanyInfo()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CompanyInfo>>(){
                    override fun onSuccess(t: AppResponse<CompanyInfo>) {
                        t.data?.let {
                            companyInfo.set(it)
                            setTitleText(it.companyName)
                        }
                    }
                })
        )
    }
}