package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.util.Log
import android.widget.Toast
import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.CareThesausuBean
import com.daqsoft.module_workbench.viewmodel.CareThesaurusViewModel
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class CareThesausuItemViewModel(
    private val baseViewModel : BaseViewModel<*>,
    val bean : CareThesausuBean
) : MultiItemViewModel<BaseViewModel<*>>(baseViewModel){

    val careThesausuBean = ObservableField<CareThesausuBean>()

    // 禁用状态
    val tvStatus = ObservableField<String>()

    init {
        careThesausuBean.set(bean)

        tvStatus.set(getStatue(bean))
    }


    /**
     * 获取数据状态
     */
    private fun getStatue(bean: CareThesausuBean): String? {
        when(bean.enable){
            true-> return ""
            false->return "已禁用"
        }
        return ""
    }

}

