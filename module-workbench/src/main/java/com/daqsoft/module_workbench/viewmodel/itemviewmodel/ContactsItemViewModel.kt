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
import com.daqsoft.module_workbench.repository.pojo.vo.Org
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
 * @describe 通讯录 itemViewModel
 */
class ContactsItemViewModel (
    private val contactsViewModel : ContactsViewModel,
    val child: Org
) : MultiItemViewModel<ContactsViewModel>(contactsViewModel){



    val childObservable = ObservableField<Org>()

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel,R.layout.recyclerview_contacts_item_avatar)

    /**
     * item 点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_DEPARTMENT)
            .withString("pid",child.id.toString())
            .withString("title",child.organizationName)
            .navigation()
    })

    init {

        childObservable.set(child)

        for (i in 0 until 9){
            observableList.add( ContactsAvatarItemViewModel(contactsViewModel, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606819881212&di=96594b829970b2134d526ecce10d1025&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201809%2F01%2F20180901190625_wmpeq.thumb.700_0.jpeg"))
        }
    }
}