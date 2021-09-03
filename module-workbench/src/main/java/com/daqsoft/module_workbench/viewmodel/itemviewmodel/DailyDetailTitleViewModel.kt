package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.viewmodel.DailyDetailViewModel
import com.daqsoft.module_workbench.viewmodel.DailyListViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class DailyDetailTitleViewModel (
    private val dailyDetailViewModel : DailyDetailViewModel,
    val count: Int,
    val id: Int
) : MultiItemViewModel<DailyDetailViewModel>(dailyDetailViewModel){


    val mCount = ObservableField<String>()


    init {
       mCount.set("(${count})")

    }


    /**
     * item 点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_COMMENT).withString("id", ""+id).navigation()
    })
}