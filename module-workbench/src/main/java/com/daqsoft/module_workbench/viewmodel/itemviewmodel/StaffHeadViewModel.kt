package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 员工详情 头部 viewModel
 */
class StaffHeadViewModel (
    private val staffViewModel : StaffViewModel,
    val title : String,
    val more : Boolean = false
) : MultiItemViewModel<StaffViewModel>(staffViewModel){

    /**
     * 更多展示
     */
    val moreVisible = ObservableField<Boolean>(false)

    /**
     * 总数
     */
    val totalObservable = ObservableField<Int>(0)

    /**
     * 更多按钮的状态   false展开 true收起
     */
    var moreSelected = ObservableField<Boolean>(false)

    /**
     * 更多点击事件
     */
    val moreOnClick = BindingCommand<Unit>(BindingAction {
        staffViewModel.moreOnClick(this)
        moreSelected.set(!moreSelected.get()!!)
    })

    /**
     * title
     */
    val titleObservable = ObservableField<String>("")

    init {
        titleObservable.set(title)
        moreVisible.set(more)
    }
}