package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.core.view.NestedScrollingChild
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.Child
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 通讯录 头像 itemViewModel
 */
class ContactsAvatarItemViewModel (
    private val contactsViewModel : ContactsViewModel,
    val url : String
) : ItemViewModel<ContactsViewModel>(contactsViewModel){

    val urlObservable = ObservableField<String>()

    init {
        urlObservable.set(url)
    }
}