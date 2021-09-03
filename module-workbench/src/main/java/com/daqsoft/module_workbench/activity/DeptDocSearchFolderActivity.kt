package com.daqsoft.module_workbench.activity

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDeptDocSearchFolderBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocSearchFolderContainerViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocSearchFolderViewModel
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 19/3/2021 上午 9:30
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH_FOLDER)
@SuppressLint("RestrictedApi")
class DeptDocSearchFolderActivity : AppBaseActivity<ActivityDeptDocSearchFolderBinding, DeptDocSearchFolderContainerViewModel>() {

    @JvmField
    @Autowired
    var type:String? = null

    @JvmField
    @Autowired
    var deptId:String? = null

    @JvmField
    @Autowired
    var folderInfo : DeptFolderInfo ? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_dept_doc_search_folder
    }

    override fun initViewModel(): DeptDocSearchFolderContainerViewModel? {
        return viewModels<DeptDocSearchFolderContainerViewModel>().value
    }

    override fun initData() {
        super.initData()

        viewModel.type = type
        viewModel.deptId = deptId
        viewModel.folderInfo = folderInfo
    }

}