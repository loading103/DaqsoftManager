//package com.daqsoft.module_workbench.viewmodel.itemviewmodel
//
//import androidx.databinding.ObservableField
//import com.daqsoft.library_base.global.ConstantGlobal
//import com.daqsoft.module_workbench.R
//import com.daqsoft.module_workbench.repository.pojo.vo.CareThesausuBean
//import com.daqsoft.module_workbench.viewmodel.CareThesaurusViewModel
//import com.daqsoft.mvvmfoundation.base.ItemViewModel
//import timber.log.Timber
//
///**
// * @package name：com.daqsoft.module_workbench.viewmodel
// * @date 25/11/2020 下午 2:08
// * @author zp
// * @describe 部门文件  文件 viewModel
// */
//class CareThesausuViewModel(val deptDocViewModel: CareThesaurusViewModel, val fileType: CareThesausuBean) : ItemViewModel<CareThesaurusViewModel>(deptDocViewModel){
//
//    /**
//     * 图标
//     */
//    val iconObservable = ObservableField<Int>()
//
//    /**
//     * 是否是文件夹
//     */
//    val folderFlag = ObservableField<Boolean>(false)
//
//    init {
//        when(fileType){
//            ConstantGlobal.FOLDER ->{
//                folderFlag.set(true)
//                iconObservable.set(R.mipmap.bmwj_folder_big)
//            }
//            ConstantGlobal.FILE->{
//                folderFlag.set(false)
//
//            }
//        }
//    }
//
//}