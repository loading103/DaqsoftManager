package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.viewmodel.PaySlipDetailViewModel
import com.daqsoft.module_workbench.viewmodel.PaySlipViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import java.lang.Exception

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 10/12/2020 上午 11:28
 * @author zp
 * @describe
 */
class PaySlipDetailItemViewModel (
    private val paySlipDetailViewModel : PaySlipDetailViewModel,
    val content : String,
    val contrast : Boolean = false
) : MultiItemViewModel<PaySlipDetailViewModel>(paySlipDetailViewModel){

    val contentObservable = ObservableField<String>()

    val drawableObservable = ObservableField<Int?>()

    init {
        contentObservable.set(content)

        if (contrast){
            if (content.isNotBlank()){
                try {
                    val number = content.toDouble()
                    drawableObservable.set(
                        when(number.compareTo(0)){
                            -1 -> R.mipmap.gzt_xq_down
                            1-> R.mipmap.gzt_xq_up
                            else -> null
                        }
                    )
                }catch (e : Exception){
                    drawableObservable.set(null)
                }
            }else{
                drawableObservable.set(null)
            }

        }
    }
}