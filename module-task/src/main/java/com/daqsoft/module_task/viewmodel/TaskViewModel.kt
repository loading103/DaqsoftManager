package com.daqsoft.module_task.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_task.repository.TaskRepository
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.BaseViewModel

/**
 * @package name：com.daqsoft.module_task.viewmodel
 * @date 10/11/2020 上午 11:33
 * @author zp
 * @describe
 */
class TaskViewModel : ToolbarViewModel<TaskRepository> {

    @ViewModelInject
    constructor(application: Application,taskRepository : TaskRepository):super(application,taskRepository)
}