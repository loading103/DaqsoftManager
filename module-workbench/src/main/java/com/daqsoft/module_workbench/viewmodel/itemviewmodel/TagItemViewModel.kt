package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.graphics.Color
import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerTagListBean
import com.daqsoft.module_workbench.viewmodel.CustomerListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel

class TagItemViewModel ( val customerViewModel: CustomerListViewModel, val data: CustomerTagListBean
) : ItemViewModel<CustomerListViewModel>(customerViewModel){

    val dataObservable = ObservableField<String>()

    val colorObservable = ObservableField<Int>()
    val textColorObservable = ObservableField<Int>()
    init {
        dataObservable.set(data.content)
        if(data.type==1){
            colorObservable.set(Color.parseColor("#1Afa4848"))
            textColorObservable.set(Color.parseColor("#fa4848"))
        }else{
            textColorObservable.set(Color.parseColor("#ffffff"))
            if(data.color.isNullOrBlank()){
                colorObservable.set(Color.parseColor("#fa4848"))
            }else{
                colorObservable.set(Color.parseColor("#${data.color}"))
            }
        }
    }
}
