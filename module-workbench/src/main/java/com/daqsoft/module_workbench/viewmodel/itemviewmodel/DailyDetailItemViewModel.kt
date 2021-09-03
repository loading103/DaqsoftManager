package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DialyDetailDiscussItem
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
class DailyDetailItemViewModel(
        private val dailyDetailViewModel: DailyDetailViewModel,
        it: DialyDetailDiscussItem
) : MultiItemViewModel<DailyDetailViewModel>(dailyDetailViewModel){


    val mDisItemBean = ObservableField<DialyDetailDiscussItem>()


    init {
        mDisItemBean.set(it)
    }


    /**
     * item 点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DETAIL).navigation()
    })
}