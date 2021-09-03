package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import androidx.databinding.ObservableField
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocViewModel
import com.daqsoft.module_workbench.viewmodel.ProjectDeptDocViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel

/**
 * @package name：com.daqsoft.module_workbench.viewmodel.itemviewmodel
 * @date 20/2/2021 下午 5:36
 * @author zp
 * @describe
 */
class AbbreviatedFolderItem1ViewModel (
    val deptDocViewModel : ProjectDeptDocViewModel,
    val folderInfo: DeptFolderInfo
) : MultiItemViewModel<ProjectDeptDocViewModel>(deptDocViewModel){

    val folderInfoObservable = ObservableField<DeptFolderInfo>()


    init {
        folderInfoObservable.set(folderInfo)
    }
}
