package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding
import retrofit2.http.Url
import timber.log.Timber
import java.lang.NullPointerException

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 员工详情  待办 头像 itemViewModel
 */
class StaffItemUpcomingAvatarViewModel (
    private val staffViewModel : StaffViewModel,
    val url: String,
    val placeholder : Int? = null
) : ItemViewModel<StaffViewModel>(staffViewModel){

    var urlObservable  = ObservableField<String>()

    var placeholderObservable  = ObservableField<Int>()

    init {
        urlObservable.set(url)
        placeholderObservable.set(placeholder?:R.mipmap.txl_details_tx_default_1)
    }

}