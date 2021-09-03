package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 3/3/2021 下午 5:34
 * @author zp
 * @describe 员工选择 itemViewModel
 */
class StaffSelectItemViewModel  (
    private val baseViewModel : BaseViewModel<*>,
    var employee: Employee
) : MultiItemViewModel<BaseViewModel<*>>(baseViewModel){

    var employeeObservable = ObservableField<Employee>()

    init {
        employeeObservable.set(employee)
    }
}