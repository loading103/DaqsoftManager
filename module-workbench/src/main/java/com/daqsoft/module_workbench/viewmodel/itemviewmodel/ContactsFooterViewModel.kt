package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideApplicationFactory
import kotlinx.android.synthetic.main.activity_contacts.view.*

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 通讯录 底部 itemViewModel
 */
class ContactsFooterViewModel (
    private val contactsViewModel : ContactsViewModel,
    val totalCount : Int
) : MultiItemViewModel<ContactsViewModel>(contactsViewModel){

    val totalCountObservable = ObservableField<String>()

    init {
        totalCountObservable.set(contactsViewModel.getApplication<Application>().resources.getString(R.string.total,totalCount))
    }
}