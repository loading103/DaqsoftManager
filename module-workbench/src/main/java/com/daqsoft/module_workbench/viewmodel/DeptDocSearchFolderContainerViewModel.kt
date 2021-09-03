package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 19/3/2021 上午 9:33
 * @author zp
 * @describe
 */
class DeptDocSearchFolderContainerViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)



    var type:String? = null


    var deptId:String? = null


    var folderInfo : DeptFolderInfo? = null
}