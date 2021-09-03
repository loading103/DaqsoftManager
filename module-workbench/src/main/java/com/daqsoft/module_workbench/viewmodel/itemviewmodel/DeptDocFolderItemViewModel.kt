package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.navigation.Navigation
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocViewModel
import com.daqsoft.module_workbench.viewmodel.StaffViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 部门文件  文件夹 viewModel
 */
class DeptDocFolderItemViewModel (
    val baseViewModel : BaseViewModel<*>,
    val folderInfo: DeptFolderInfo
) : MultiItemViewModel<BaseViewModel<*>>(baseViewModel){

    /**
     * 图标
     */
    val iconObservable = ObservableField<Int>()


    val folderInfoObservable = ObservableField<DeptFolderInfo>()

    init {

        folderInfoObservable.set(folderInfo)

        iconObservable.set(R.mipmap.bmwj_folder_big)
    }

}