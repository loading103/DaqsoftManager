package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 员工详情 底部 viewModel
 */
class StaffFooterViewModel (
    private val staffViewModel : StaffViewModel
) : MultiItemViewModel<StaffViewModel>(staffViewModel){


    /**
     * 故事 点击事件
     */
    val storyOnClick = BindingCommand<Unit>(BindingAction {
        if (staffViewModel.employeeInfoObservable.get() == null){
            return@BindingAction
        }
        ARouter.getInstance().build(ARouterPath.Web.PAGER_WEB).withString("url",HttpGlobal.STAFF_STORY).navigation()

    })

    /**
     * 发消息 点击事件
     */
    val messageOnClick = BindingCommand<Unit>(BindingAction {
        ToastUtils.showShort("敬请期待")
    })

    /**
     * 打电话点击事件
     */
    val callOnClick = BindingCommand<String>(BindingAction {

        if (staffViewModel.employeeInfoObservable.get() == null){
            return@BindingAction
        }

        staffViewModel.callLiveData.value = staffViewModel.employeeInfoObservable.get()?.employeeMobile
    })
}