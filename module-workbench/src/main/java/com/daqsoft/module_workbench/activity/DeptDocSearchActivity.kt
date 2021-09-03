package com.daqsoft.module_workbench.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.ObservableList
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.DeptDocSearchAdapter
import com.daqsoft.module_workbench.databinding.ActivityDeptDocSearchBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFileBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFolderBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocContainerViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocSearchViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 7/2/2021 上午 11:10
 * @author zp
 * @describe 部门文件搜索
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH)
class DeptDocSearchActivity : AppBaseActivity<ActivityDeptDocSearchBinding, DeptDocSearchViewModel>() {

    @JvmField
    @Autowired
    var type:String? = null

    @JvmField
    @Autowired
    var deptId:String? = null

    @Inject
    lateinit var searchAdapter : DeptDocSearchAdapter

    lateinit var multipleLayoutManager: MultipleLayoutManager


    val deptDocContainerViewModel : DeptDocContainerViewModel by viewModels()


    var clickFile : DeptFileInfo ? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_dept_doc_search
    }

    override fun initViewModel(): DeptDocSearchViewModel? {
        return viewModels<DeptDocSearchViewModel>().value
    }


    override fun initView() {
        super.initView()

        initRecycleView()
        initMultipleLayoutManager()
    }


    override fun initData() {
        super.initData()

        viewModel.type = type
        viewModel.deptId = deptId

    }

    private fun initRecycleView() {
        binding.recyclerView.adapter = searchAdapter.apply {
            setItemOnClickListener(object : DeptDocSearchAdapter.ItemOnClickListener{
                override fun fileOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFileBinding,
                    itemViewModel: DeptDocFileItemViewModel
                ) {
                    clickFile = itemViewModel.fileInfo

                    if(FileUtils.fileExist(itemViewModel.fileInfo.fileName)){
                        val file = File(FileUtils.getAppDirPath(), itemViewModel.fileInfo.fileName)
                        FileUtils.previewFile(this@DeptDocSearchActivity,file)
                    }else{

                        if(!deptDocContainerViewModel.downloadPermission()){
                            return
                        }

                        viewModel.downloadFile(itemViewModel.fileInfo)
                    }
                }

                override fun folderOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFolderBinding,
                    itemViewModel: DeptDocFolderItemViewModel
                ) {

                    ARouter
                        .getInstance()
                        .build(ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH_FOLDER)
                        .withString("type",type)
                        .withString("deptId",deptId)
                        .withParcelable("folderInfo", itemViewModel.folderInfo)
                        .navigation()
                }

            })
        }
    }

    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_error,null,false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        emptyContent.text = "暂无数据"

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .setEmptyLayout(emptyView)
            .build()

        multipleLayoutManager.showEmptyLayout()
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.observableList.addOnListChangedCallback(object : ObservableList.OnListChangedCallback<ObservableList<MultiItemViewModel<*>>>() {
            override fun onChanged(sender: ObservableList<MultiItemViewModel<*>>?) {

            }

            override fun onItemRangeChanged(
                sender: ObservableList<MultiItemViewModel<*>>?,
                positionStart: Int,
                itemCount: Int
            ) {

            }

            override fun onItemRangeInserted(
                sender: ObservableList<MultiItemViewModel<*>>?,
                positionStart: Int,
                itemCount: Int
            ) {
                multipleLayoutManager.showSuccessLayout()
            }

            override fun onItemRangeMoved(
                sender: ObservableList<MultiItemViewModel<*>>?,
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {


            }

            override fun onItemRangeRemoved(
                sender: ObservableList<MultiItemViewModel<*>>?,
                positionStart: Int,
                itemCount: Int
            ) {
                if(viewModel.observableList.isEmpty()){
                    multipleLayoutManager.showEmptyLayout()
                }
            }

        })

        LiveEventBus.get(LEBKeyGlobal.PREVIEW_FILE,Boolean::class.java).observe(this,
            Observer {
                clickFile?.let {
                    viewModel.previewFile(it.id.toString())
                }
            })
    }

}