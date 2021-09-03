package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.module_workbench.viewmodel.WorkBenchViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 工作台 itemViewModel
 */
class WorkBenchItemViewModel (
    private val workBenchViewModel : WorkBenchViewModel,
    val title : String,
    val data : List<ItemViewModel<*>>
) : MultiItemViewModel<WorkBenchViewModel>(workBenchViewModel){

    /**
     * 标题
     */
    val titleObservable = ObservableField<String>()

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recycleview_workbench_item_label)

    init {

        titleObservable.set(title)

        observableList.addAll(data)

    }
}
