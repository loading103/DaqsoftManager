package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.library_common.pojo.vo.Employee

import com.daqsoft.module_workbench.repository.pojo.vo.NoticeDetail
import com.daqsoft.module_workbench.repository.pojo.vo.OrganizationChildren
import com.daqsoft.module_workbench.viewmodel.OrgSelectViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 3/3/2021 下午 5:34
 * @author zp
 * @describe 组织选择 itemViewModel
 */
class OrgSelectItemViewModel  (
    private val orgSelectViewModel : OrgSelectViewModel,
    var employee : Employee
) : MultiItemViewModel<OrgSelectViewModel>(orgSelectViewModel){

    var employeeObservable = ObservableField<Employee>()

    init {
        employeeObservable.set(employee)
    }
}