package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.viewmodel.PaySlipDetailViewModel
import com.daqsoft.module_workbench.viewmodel.PaySlipViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 10/12/2020 上午 11:28
 * @author zp
 * @describe
 */
class PaySlipDetailTitleViewModel (
    private val paySlipDetailViewModel : PaySlipDetailViewModel,
    val content : String
) : MultiItemViewModel<PaySlipDetailViewModel>(paySlipDetailViewModel){

    val contentObservable = ObservableField<String>()

    init {
        contentObservable.set(content)
    }
}