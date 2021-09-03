package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel

class DailyTeamViewModel : BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workRepository: WorkBenchRepository):super(application,workRepository)






}