package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.Participate
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
 * @describe 员工详情  参与 itemViewModel
 */
class StaffItemParticipateViewModel (
    private val staffViewModel : StaffViewModel,
    val participate : Participate,
    val line:Boolean = true
) : MultiItemViewModel<StaffViewModel>(staffViewModel){

    val lineVisible = ObservableField<Boolean>(true)

    val participateObservable = ObservableField<Participate>()

    init {
        lineVisible.set(line)
        participateObservable.set(participate)
    }
}