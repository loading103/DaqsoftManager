package com.daqsoft.module_workbench.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.DeptDocSearchFolderActivity
import com.daqsoft.module_workbench.adapter.DeptDocSearchAdapter
import com.daqsoft.module_workbench.databinding.FragmentDeptDocSearchFolderBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFileBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewDeptDocFolderBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFolderInfo
import com.daqsoft.module_workbench.viewmodel.DeptDocContainerViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocSearchFolderContainerViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocSearchFolderViewModel
import com.daqsoft.module_workbench.viewmodel.DeptDocViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFileItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DeptDocFolderItemViewModel
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 19/3/2021 上午 9:38
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC_SEARCH_FOLDER_FRAGMENT)
class DeptDocSearchFolderFragment : AppBaseFragment<FragmentDeptDocSearchFolderBinding, DeptDocSearchFolderViewModel>() {

    lateinit var container : DeptDocSearchFolderActivity

    /**
     * 父文件夹
     */
    var folderInfo : DeptFolderInfo? = null

    @Inject
    lateinit var searchAdapter : DeptDocSearchAdapter


    val  containerViewModel : DeptDocSearchFolderContainerViewModel by activityViewModels()

    val deptDocContainerViewModel : DeptDocContainerViewModel by viewModels()


    var clickFile : DeptFileInfo? = null


    override fun initParam() {
        super.initParam()

        arguments?.let {
            folderInfo = it.getParcelable("folderInfo")
        }

    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_dept_doc_search_folder
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): DeptDocSearchFolderViewModel? {
        return ViewModelProvider(this).get(DeptDocSearchFolderViewModel::class.java)
    }

    override fun initView() {
        super.initView()

        container = activity as DeptDocSearchFolderActivity

        initRecycleView()
    }

    override fun initData() {
        super.initData()

        viewModel.setTitleText(folderInfo?.folderName?:containerViewModel.folderInfo?.folderName?:"")

        dataProcessing(containerViewModel.type?:"部门文件",folderInfo?.id?:containerViewModel.folderInfo?.id?:0)

    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.backLiveData.observe(this, Observer {
            container.finish()
        })

        LiveEventBus.get(LEBKeyGlobal.PREVIEW_FILE,Boolean::class.java).observe(this,
            Observer {
                clickFile?.let {
                    viewModel.previewFile(it.id.toString())
                }
            })

        viewModel.downloadLiveData.observe(this, Observer {
            FileUtils.previewOrDownloadFile(container,it.first.fileName,it.second)
        })

        viewModel.previewLiveData.observe(this, Observer {
            dataProcessing(containerViewModel.type?:"部门文件",folderInfo?.id?:containerViewModel.folderInfo?.id?:0)
        })
    }


    fun  initRecycleView(){
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
                        FileUtils.previewFile(container,file)
                    }else{

                        if (!deptDocContainerViewModel.downloadPermission()){
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
                    val bundle = Bundle()
                    bundle.putParcelable("folderInfo",itemViewModel.folderInfo)
                    Navigation.findNavController(requireView()).navigate(R.id.module_workbench_deptdocsearchfolderfragment,bundle)
                }

            })
        }
    }


    fun dataProcessing(type:String,pId:Int){
        when(type){
            "部门文件" ->{
                viewModel.getDeptOrPublicFileInfo(pId,null,0,null)
            }
            "公共文件" ->{
                viewModel.getDeptOrPublicFileInfo(pId,null,1,null)
            }
            "共享文件" ->{
                viewModel.getShareFileInfo(pId ,containerViewModel.deptId?.toInt(),null)
            }
        }
    }
}