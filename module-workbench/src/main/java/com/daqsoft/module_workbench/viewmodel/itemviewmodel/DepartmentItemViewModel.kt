package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.viewmodel.StaffSearchViewModel
import com.daqsoft.module_workbench.viewmodel.StaffSelectViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 部门 itemViewModel
 */
class DepartmentItemViewModel (
    private val baseViewModel : BaseViewModel<*>,
    /**
     * 分类  0 员工  1 组织
     */
    val type : Int,
    val employee : Employee,
    val fromSelect:Boolean = false
) : ItemViewModel<BaseViewModel<*>>(baseViewModel){

    /**
     * 头像
     */
    val urlObservable = ObservableField<String>()

    /**
     * 占位
     */
    val placeholderObservable = ObservableField<Int>()

    /**
     * 名称
     */
    val nameObservable = ObservableField<String>()

    /**
     * 职位
     */
    val positionObservable = ObservableField<String>()

    init {
        when(type){
            0 -> {
                urlObservable.set(employee.avartar)
                placeholderObservable.set( R.mipmap.workbench_default_avatar)
                nameObservable.set(employee.name)
                positionObservable.set(employee.postFullName)
            }
            1->{
                urlObservable.set("")
                placeholderObservable.set( R.mipmap.txl_list_default)
                nameObservable.set(employee.name)
                positionObservable.set(baseViewModel.getApplication<Application>().resources.getString(R.string.total,employee.postFullName))
            }
        }
    }

    /**
     * item 点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {
        if (type == 0 && fromSelect){
            if (baseViewModel is StaffSearchViewModel){
                LiveEventBus.get(LEBKeyGlobal.EMPLOYEE_SEARCH_SELECTED).post(employee)
                baseViewModel.finish()
            }
            return@BindingAction
        }
        when(type){
            0 ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_STAFF)
                    .withString("id", employee.id.toString())
                    .withString("name", employee.name)
                    .navigation()
            }
            1 ->{
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_DEPARTMENT)
                    .withString("title", employee.name)
                    .withString("pid", employee.id.toString())
                    .navigation()
            }
        }
    })

}

