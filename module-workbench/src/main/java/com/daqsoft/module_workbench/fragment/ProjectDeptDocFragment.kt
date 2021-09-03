package com.daqsoft.module_workbench.fragment

import android.annotation.SuppressLint
import android.app.Service
import android.graphics.Rect
import android.os.Bundle
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.Observable
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.ProjectDeptDocActivity
import com.daqsoft.module_workbench.adapter.ProAbbreviatedAdapter
import com.daqsoft.module_workbench.adapter.ProDeptDocAdapter
import com.daqsoft.module_workbench.databinding.*
import com.daqsoft.module_workbench.repository.pojo.vo.DeptFileInfo
import com.daqsoft.module_workbench.repository.pojo.vo.DeptType
import com.daqsoft.module_workbench.viewmodel.ProjectDeptDocContainerViewModel
import com.daqsoft.module_workbench.viewmodel.ProjectDeptDocViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 7/2/2021 下午 5:31
 * @author zp
 * @describe 部门文件  内容列表
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DEPT_DOC_FRAGMENT_PRO)
class ProjectDeptDocFragment : AppBaseFragment<FragmentDeptDocProBinding, ProjectDeptDocViewModel>() {

    lateinit var multipleLayoutManager: MultipleLayoutManager

    val containerViewModel : ProjectDeptDocContainerViewModel by activityViewModels()

    /**
     * 父文件夹 id
     */
    var pId : Int? = null

    /**
     * 项目编号	 id
     */
    var projectId : String? = null

    // 筛选Id
    var docId : String? = ""
    /**
     * 点击文件
     */
    var clickFile : DeptFileInfo ? = null


    /**
     * 默认列表模式 adapter
     */
    @Inject
    lateinit var deptDocAdapter : ProDeptDocAdapter

    /**
     * 缩略模式 adapter
     */
    @Inject
    lateinit var abbreviatedAdapter : ProAbbreviatedAdapter

    private val itemDecoration = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val count = state.itemCount - 1
            outRect.top = 10.dp
            if (position < 2) {
                outRect.top = 15.dp
            }
            if (count == position) {
                outRect.bottom = 15.dp
            }
            outRect.left = if (position % 2 == 0) 20.dp else 5.dp
            outRect.right = if (position % 2 == 0) 5.dp else 20.dp
        }
    }

    lateinit var container : ProjectDeptDocActivity

    override fun initParam() {
        super.initParam()

        arguments?.let {
            pId = it.getInt("pid")
        }

    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_dept_doc_pro
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    @SuppressLint("RestrictedApi")
    override fun initView() {
        super.initView()
        Timber.e("initView")
        container = (activity as ProjectDeptDocActivity)


        initRefresh()
        initMultipleLayoutManager()
        initAppBar()


        binding.title.setOnClickListener {
            container.showTopMenuPopup()
        }
    }

    private fun initRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    override fun initData() {
        super.initData()

        projectId=(activity as  ProjectDeptDocActivity).projectId

        viewModel.containerViewModel = containerViewModel

        refreshData()

        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_FILED,DeptType::class.java).observe(this, Observer {
            viewModel.containerViewModel.dirTypeObservable.set(it.baseName?:"")
            docId=it.id.toString()
            refreshData()
        })

    }

    fun refreshData() {
        if(docId.isNullOrBlank() ||  docId=="null"  ){
            dataProcessing(projectId?.toInt())
        }else{
            dataProcessing(projectId?.toInt(),docId =docId?.toInt())
        }
    }
    private fun initMultipleLayoutManager() {
        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .build()
        appBarScroll(false)
    }

    /**
     * 初始化 列表 recycleView
     */
    private fun initListRecycleView() {
        deptDocAdapter.apply {
            containerViewModel = this@ProjectDeptDocFragment.containerViewModel
            setItemOnClickListener(object : ProDeptDocAdapter.ItemOnClickListener {
                override fun fileOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFileBinding,
                    item: DeptDocFileItemViewModel
                ) {
                    clickFile = item.fileInfo

                    if(FileUtils.fileExist(item.fileInfo.fileName)){
                        val file = File(FileUtils.getAppDirPath(), item.fileInfo.fileName)
                        FileUtils.previewFile(container,file)
                    }else{

                        if (!this@ProjectDeptDocFragment.containerViewModel.downloadPermission()){
                            return
                        }

                        viewModel.downloadFile(item.fileInfo)
                    }
                }
                override fun fileOnLongClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFileBinding,
                    item: DeptDocFileItemViewModel
                ) {
                    val vibrator = requireActivity().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(30)
                    container.showBottomModifyPopup(item.fileInfo) {
                        restore()
                    }
                }
                override fun folderOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFolderBinding,
                    itemViewModel: DeptDocFolderItemViewModel
                ) {
                    this@ProjectDeptDocFragment.containerViewModel.catalogList.add(itemViewModel.folderInfo.folderName)
                    val bundle = Bundle()
                    bundle.putInt("pid",itemViewModel.folderInfo.id)
                    Navigation.findNavController(requireView()).navigate(R.id.module_workbench_deptdocfragment,bundle)
                }
                override fun folderOnLongClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocFolderBinding,
                    itemViewModel: DeptDocFolderItemViewModel
                ) {
                    val vibrator = requireActivity().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(30)
                    container.showBottomModifyPopup(itemViewModel.folderInfo) {
                        restore()
                    }
                }
            })

            itemBinding = ItemBinding.of{ itemBinding, position, item ->
                when (item.itemType as String) {
                    ConstantGlobal.FOLDER -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_folder)
                    ConstantGlobal.FILE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_file)
                    else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_dept_doc_folder)
                }
            }

            val observableList = arrayListOf<MultiItemViewModel<*>>()
            viewModel.deptFileLiveData.value?.let {
                // 干掉文件夹
//                it.folders.forEach {
//                    val itemViewModel = DeptDocFolderItemViewModel(viewModel,it)
//                    itemViewModel.multiItemType(ConstantGlobal.FOLDER)
//                    observableList.add(itemViewModel)
//                }

                it.forEach {
                    val itemViewModel = DeptDocFileItemViewModel(viewModel,it)
                    itemViewModel.multiItemType(ConstantGlobal.FILE)
                    observableList.add(itemViewModel)
                }
            }
            setItems(observableList)
        }

        binding.recyclerView.apply {
            removeItemDecoration(itemDecoration)
            setBackgroundColor(resources.getColor(R.color.white_ffffff))
            layoutManager = LinearLayoutManager(context)
            adapter = deptDocAdapter
        }
    }

    /**
     * 初始化 缩略 recycleView
     */
    private fun initAbbreviatedRecycleView() {
        abbreviatedAdapter.apply {
            containerViewModel = this@ProjectDeptDocFragment.containerViewModel
            setItemOnClickListener(object : ProAbbreviatedAdapter.ItemOnClickListener {
                override fun fileOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocCurtPhotoBinding,
                    itemViewModel: AbbreviatedPhotoItemViewModel
                ) {

                    clickFile = itemViewModel.fileInfo

                    if(FileUtils.fileExist(itemViewModel.fileInfo.fileName)){
                        val file = File(FileUtils.getAppDirPath(), itemViewModel.fileInfo.fileName)
                        FileUtils.previewFile(container,file)
                    }else{
                        if (!this@ProjectDeptDocFragment.containerViewModel.downloadPermission()){
                            return
                        }
                        viewModel.downloadFile(itemViewModel.fileInfo)
                    }
                }

                override fun fileOnLongClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocCurtPhotoBinding,
                    itemViewModel: AbbreviatedPhotoItemViewModel
                ) {
                    val vibrator = requireActivity().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(30)
                    container.showBottomModifyPopup(itemViewModel.fileInfo) {
                        restore()
                    }
                }

                override fun folderOnClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocCurtFolderBinding,
                    itemViewModel: AbbreviatedFolderItemViewModel
                ) {
                    this@ProjectDeptDocFragment.containerViewModel.catalogList.add(itemViewModel.folderInfo.folderName)
                    val bundle = Bundle()
                    bundle.putInt("pid",itemViewModel.folderInfo.id)
                    Navigation.findNavController(requireView()).navigate(R.id.module_workbench_deptdocfragment,bundle)
                }

                override fun folderOnLongClick(
                    position: Int,
                    itemBinding: RecyclerviewDeptDocCurtFolderBinding,
                    itemViewModel: AbbreviatedFolderItemViewModel
                ) {
                    val vibrator = requireActivity().getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
                    vibrator.vibrate(30)
                    container.showBottomModifyPopup (itemViewModel.folderInfo){
                        restore()
                    }
                }

            })

            itemBinding = ItemBinding.of{ itemBinding, position, item ->
                when (item.itemType as String) {
                    ConstantGlobal.FOLDER -> itemBinding.set(
                        BR.viewModel,
                        R.layout.recyclerview_dept_doc_curt_folder1
                    )
                    ConstantGlobal.FILE -> itemBinding.set(
                        BR.viewModel,
                        R.layout.recyclerview_dept_doc_curt_photo1
                    )
                    else -> itemBinding.set(
                        BR.viewModel,
                        R.layout.recyclerview_dept_doc_curt_folder1
                    )
                }
            }

            val observableList = arrayListOf<MultiItemViewModel<*>>()
            viewModel.deptFileLiveData.value?.let {
//                it.folders.forEach {
//                    val itemViewModel = AbbreviatedFolderItem1ViewModel(viewModel,it)
//                    itemViewModel.multiItemType(ConstantGlobal.FOLDER)
//                    observableList.add(itemViewModel)
//                }

                it.forEach {
                    val itemViewModel = AbbreviatedPhotoItem1ViewModel(viewModel,it)
                    itemViewModel.multiItemType(ConstantGlobal.FILE)
                    observableList.add(itemViewModel)
                }

            }
            setItems(observableList)
        }

        binding.recyclerView.apply {
            if(this.itemDecorationCount == 0 ){
                addItemDecoration(itemDecoration)
            }

            setBackgroundColor(resources.getColor(R.color.white_ffffff))
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = abbreviatedAdapter
        }
    }

    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                        binding.date.visibility = View.VISIBLE
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                        binding.date.visibility = View.GONE
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                        binding.date.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    override fun initViewModel(): ProjectDeptDocViewModel? {
        return ViewModelProvider(this).get(ProjectDeptDocViewModel::class.java)

    }


    @SuppressLint("RestrictedApi")
    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshLiveData.observe(this, Observer {
            binding.refreshLayout.finishRefresh()
        })

        LiveEventBus.get(LEBKeyGlobal.DEPT_DOC_LAYOUT_MANAGER, Int::class.java).observe(
            requireActivity(),
            Observer {
                Timber.e("DEPT_DOC_LAYOUT_MANAGER $it")
                // 0 列表   1 缩略图
                when (it) {
                    0 -> initListRecycleView()
                    1 -> initAbbreviatedRecycleView()
                }
            })

        viewModel.deptFileLiveData.observe(this, Observer {

            if (it.isEmpty()){
                multipleLayoutManager.showEmptyLayout()
                appBarScroll(false)
                return@Observer
            }

            multipleLayoutManager.showSuccessLayout()
            appBarScroll(true)

            when(containerViewModel.modeLiveData.value){
                0 -> initListRecycleView()
                1 -> initAbbreviatedRecycleView()
                else -> initListRecycleView()
            }
        })

        viewModel.netErrorLiveData.observe(this, Observer {
            multipleLayoutManager.showErrorLayout()
        })


        LiveEventBus.get(LEBKeyGlobal.DEPT_DOC_TYPE_SWITCH, DeptType::class.java).observe(this,
            Observer {
                refreshData()
            })


        viewModel.downloadLiveData.observe(this, Observer {
            FileUtils.previewOrDownloadFile(container,it.first.fileName,it.second)
        })


        LiveEventBus.get(LEBKeyGlobal.FILE_DOWNLOADED_SUCCESSFULLY,Boolean::class.java).observe(this,
            Observer {
                refreshData()
            })


        LiveEventBus.get(LEBKeyGlobal.PREVIEW_FILE,Boolean::class.java).observe(this,
            Observer {
                clickFile?.let {
                    viewModel.previewFile(it.id.toString())
                }
            })

        viewModel.previewLiveData.observe(this, Observer {
            refreshData()
        })

    }


    fun dataProcessing(projectId: Int?,docId: Int?=null,startTime: String?=null,endTime: String?=null,fileName:String?=null,userName:String?=null){
        viewModel.getDeptOrPublicFileInfo(projectId,docId,startTime,endTime,fileName,userName)
    }


    /**
     * 控制appbar的滑动
     * @param isScroll true 允许滑动 false 禁止滑动
     */
    private fun appBarScroll(isScroll: Boolean) {
        val mAppBarChildAt: View = binding.appbar.getChildAt(0)
        val mAppBarParams = mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams
        if (isScroll) {
            mAppBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
            mAppBarChildAt.layoutParams = mAppBarParams
        } else {
            mAppBarParams.scrollFlags = 0
            mAppBarChildAt.layoutParams = mAppBarParams
        }
    }
}