package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_workbench.repository.WorkBenchRepository

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 4/3/2021 下午 5:55
 * @author zp
 * @describe 组织或者员员工选择
 */
class OrgOrStaffSelectViewModel :  BaseSelectViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

}