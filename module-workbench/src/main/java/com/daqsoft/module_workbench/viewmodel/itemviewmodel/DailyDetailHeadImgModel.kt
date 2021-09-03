package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.Read
import com.daqsoft.module_workbench.viewmodel.DailyDetailViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class DailyDetailHeadImgModel (
    private val dailyDetailViewModel : DailyDetailViewModel,
    val read: Read
) : MultiItemViewModel<DailyDetailViewModel>(dailyDetailViewModel){


    val readPerson = ObservableField<Read>()


    init {
        readPerson.set(read)

    }

    val gotoPersonDetail = BindingCommand<Unit>(BindingAction {
        ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id",""+read.employeeId)
                .navigation()
    })

}