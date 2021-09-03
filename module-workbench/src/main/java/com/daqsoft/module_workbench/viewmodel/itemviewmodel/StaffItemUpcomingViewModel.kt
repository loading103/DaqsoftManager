package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.Mission
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 员工详情  待办 itemViewModel
 */
class StaffItemUpcomingViewModel (
    val staffViewModel : StaffViewModel,
    private val mission: Mission
) : MultiItemViewModel<StaffViewModel>(staffViewModel){

    val missionObservable = ObservableField<Mission>()

    /**
     * 参与人总数
     */
    val totalObservable = ObservableField<Int>(0)

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recyclerview_staff_item_avatar)


    init {
        missionObservable.set(mission)
        mission.avatars.forEach{
            observableList.add(StaffItemUpcomingAvatarViewModel(staffViewModel,it))
        }
        val workUsers = mission.workUsers.split(",")
        totalObservable.set(workUsers.size)
        if (workUsers.size > 7){
            observableList.add(StaffItemUpcomingAvatarViewModel(staffViewModel,"",R.mipmap.rw_list_tx_more))
        }
    }

}