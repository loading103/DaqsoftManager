package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.InvoiceInfo
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 30/3/2021 下午 2:05
 * @author zp
 * @describe 发票详情 viewModel
 */
class InvoiceDetailViewModel: ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    override fun onCreate() {
        super.onCreate()
        initToolbar()

    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("发票信息")
    }




    /**
     * 税号复制点击事件
     */
    val taxCodeOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(invoiceInfo.get()?.taxCode?:"")
    })

    /**
     * 注册地址复制点击事件
     */
    val addressOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(invoiceInfo.get()?.address?:"")
    })

    /**
     * 电话号码复制点击事件
     */
    val phoneOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(invoiceInfo.get()?.phone?:"")
    })

    /**
     * 开户银行复制点击事件
     */
    val bankOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(invoiceInfo.get()?.bank?:"")
    })

    /**
     * 银行账号复制点击事件
     */
    val bankAccountOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(invoiceInfo.get()?.bankAccount?:"")
    })



    /**
     * 发票信息
     */
    val invoiceInfo = ObservableField<InvoiceInfo>()

    /**
     * 获取发票信息
     */
    fun getInvoiceInfo(){
        addSubscribe(
            model
                .getInvoiceInfo()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<InvoiceInfo>>(){
                    override fun onSuccess(t: AppResponse<InvoiceInfo>) {
                        t.data?.let {
                            invoiceInfo.set(it)
                        }
                    }
                })
        )
    }

}

