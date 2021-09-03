package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.Record
import com.daqsoft.module_workbench.viewmodel.AnnouncementViewModel
import com.daqsoft.mvvmfoundation.base.BaseModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 9/2/2021 上午 10:12
 * @author zp
 * @describe
 */
class BottomPopupItemViewModel(
    val baseViewModel: BaseViewModel<BaseModel>,
    val pair : Pair<Int,String>
) : ItemViewModel<BaseViewModel<BaseModel>>(baseViewModel){


    val iconObservable = ObservableField<Int>()

    val titleObservable = ObservableField<String>()


    init {

        iconObservable.set(pair.first)
        titleObservable.set(pair.second)

    }

}