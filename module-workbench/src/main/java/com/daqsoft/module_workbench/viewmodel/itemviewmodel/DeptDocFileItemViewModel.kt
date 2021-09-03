package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.os.Bundle
import androidx.databinding.ObservableField
import androidx.navigation.Navigation
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.FileUtils
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
 * @describe 部门文件  文件 viewModel
 */
class DeptDocFileItemViewModel (
    val deptDocViewModel : BaseViewModel<*>,
    val fileInfo: DeptFileInfo
) : MultiItemViewModel<BaseViewModel<*>>(deptDocViewModel){

    /**
     * 图标
     */
    val iconObservable = ObservableField<Int>()

    val urlObservable = ObservableField<String>("")

    /**
     * 是否是文件夹
     */
    val fileInfoObservable = ObservableField<DeptFileInfo>()

    init {
        fileInfoObservable.set(fileInfo)
        urlObservable.set("")
        iconObservable.set(
            when {
                FileUtils.isPDF(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_pdf
                }
                FileUtils.isPPT(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_ppt
                }
                FileUtils.isCompressed(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_zip
                }
                FileUtils.isExcel(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_excel
                }
                FileUtils.isWord(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_word
                }
                FileUtils.isVideo(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_video
                }
                FileUtils.isImage(fileInfo.fileType) -> {
//                    urlObservable.set(fileInfo.fileUrl)
//                    R.mipmap.bmwj_folder_big
                    R.mipmap.bmwj_list_jpg
                }
                FileUtils.isTxt(fileInfo.fileType) -> {
                    R.mipmap.bmwj_list_txt
                }
                else -> {
                    R.mipmap.bmwj_list_default
                }
            }
        )



    }

}