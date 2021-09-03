package com.daqsoft.module_project.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.adapter.ProjectAdapter
import com.daqsoft.module_project.databinding.FragmentProjectBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectChooseType
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.ProjectViewModel
import com.daqsoft.module_project.widget.AppBarStateChangeListener
import com.daqsoft.module_project.widget.ProjectChooseMenuPopup
import com.daqsoft.module_project.widget.ProjectTopMenuPopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_project.view.*
import kotlinx.android.synthetic.main.recyclerview_project_dynamics_item.view.*
import kotlinx.android.synthetic.main.recyclerview_project_head.view.*
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * @describe 项目
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT)
class ProjectFragment : AppBaseFragment<FragmentProjectBinding, ProjectViewModel>() {


    lateinit var multipleLayoutManager: MultipleLayoutManager

    val typeChoosePopup : ProjectChooseMenuPopup by lazy {
        ProjectChooseMenuPopup(
            activity as Context
        )
    }
    @Inject
    lateinit var mAdapter : ProjectAdapter

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_project
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): ProjectViewModel? {
        return activity?.viewModels<ProjectViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        initOnclick()
        initRefresh()
        initAppBar()
        initRecycleView()
        initMultipleLayoutManager()

    }

    private fun initOnclick() {
        binding.title.setOnClickListener {
            showTopPopup(binding.title)
        }
        binding.tvForce.setOnClickListener {
            if(it.isSelected){
                it.isSelected=false
                viewModel.onlyCaresLiveData.value=null
            }else{
                it.isSelected=true
                binding.tvFive.isSelected=false
                binding.tvZd.isSelected=false

                viewModel.onlyCaresLiveData.value=true
                viewModel.leveLiveData.value=null
            }
            bottomViewGone(-1)
            viewModel.dataSource?.invalidate()
        }
        binding.tvFive.setOnClickListener {
            if(it.isSelected){
                it.isSelected=false
                viewModel.leveLiveData.value=null
            }else{
                it.isSelected=true
                binding.tvForce.isSelected=false
                binding.tvZd.isSelected=false

                viewModel.leveLiveData.value=1
                viewModel.onlyCaresLiveData.value=null
            }
            bottomViewGone(-1)
            viewModel.dataSource?.invalidate()
        }
        binding.tvZd.setOnClickListener {
            if(it.isSelected){
                it.isSelected=false
                viewModel.leveLiveData.value=null

            }else{
                it.isSelected=true
                binding.tvForce.isSelected=false
                binding.tvFive.isSelected=false

                viewModel.leveLiveData.value=2
                viewModel.onlyCaresLiveData.value=null
            }
            bottomViewGone(-1)
            viewModel.dataSource?.invalidate()
        }
        binding.llYlx.setOnClickListener {
            if(it.isSelected){
                bottomViewGone(-1)
            }else{
                topChooseFalse()
                bottomViewGone(0)
                viewModel.typeStateLiveData.value=0
            }
            viewModel.dataSource?.invalidate()
        }
        binding.llDcy.setOnClickListener {
            if(it.isSelected){
                bottomViewGone(-1)
            }else{
                topChooseFalse()
                bottomViewGone(1)
                viewModel.typeStateLiveData.value=1
            }
            viewModel.dataSource?.invalidate()
        }
        binding.llDzy.setOnClickListener {
            if(it.isSelected){
                bottomViewGone(-1)
            }else{
                topChooseFalse()
                bottomViewGone(2)
                viewModel.typeStateLiveData.value=2
            }
            viewModel.dataSource?.invalidate()
        }
        binding.llCqyz.setOnClickListener {
            if(it.isSelected){
                bottomViewGone(-1)
            }else{
                topChooseFalse()
                bottomViewGone(3)
                viewModel.typeStateLiveData.value=3
            }
            viewModel.dataSource?.invalidate()
        }
    }

    private fun topChooseFalse() {
        viewModel.onlyCaresLiveData.value=null
        viewModel.leveLiveData.value=null
        binding.tvForce.isSelected=false
        binding.tvFive.isSelected=false
        binding.tvZd.isSelected=false
    }

    private fun bottomViewGone(index:Int) {
        binding.tvYlx.isSelected=index==0
        binding.tvYlxNumber.isSelected=index==0
        binding.tvDcy.isSelected=index==1
        binding.tvDcyNumber.isSelected=index==1
        binding.tvDzy.isSelected=index==2
        binding.tvDzyNumber.isSelected=index==2
        binding.tvCqyz.isSelected=index==3
        binding.tvCqyzNumber.isSelected=index==3
        viewModel.typeStateLiveData.value=null
    }

    override fun initData() {
        super.initData()
        viewModel.getProjectType()
        viewModel.getHeadInforType()
    }

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            adapter = mAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {

                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    if(position == count){
                        outRect.bottom = 15.dp
                    }
                }
            })

        }
    }
    /**
     * 初始化 AppBar(处理下来和下滑冲突)
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                        binding.refreshLayout.setEnableRefresh(true)
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                        binding.refreshLayout.setEnableRefresh(false)
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                        binding.refreshLayout.setEnableRefresh(false)
                    }
                }
            }
        })
    }


    private fun initRefresh() {
        binding.refreshLayout.setOnRefreshListener {
            initData()
            viewModel.dataSource?.invalidate()
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()
        // 类型选择
        viewModel.haveTypeLiveData.observe(this, Observer {
            typeChoosePopup.setData(viewModel.firstTypeLiveData)
            mAdapter.setListType(viewModel.firstTypeLiveData[0].datas)
        })


        // 刷新完成
        viewModel.refreshLiveData.observe(this, Observer {
            binding.refreshLayout.finishRefresh()
            binding.recyclerView.visibility=View.VISIBLE
        })


        viewModel.pageList.observe(this, Observer {
            if(it.isEmpty()){
                multipleLayoutManager.showLoadingLayout()
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()
            binding.recyclerView.executePaging(it,viewModel.diff)
        })


        // 详情关注或是取消刷新
        LiveEventBus.get(LEBKeyGlobal.PROJICT_FOLLOW_SUCCESS, Boolean::class.java).observe(this,
            Observer {
//                mAdapter.itemviewModel.ischecked.set(it)
                viewModel.refreshItemClick()
            })

        // 刷新单个item
        LiveEventBus.get(LEBKeyGlobal.PROJECT_DYNAMIC_SENT_SUCCESSFULLY,Boolean::class.java).observe(this, Observer {
            viewModel.refreshItemClick()
        })
        // 添加项目成功
        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_SUCESS, Boolean::class.java).observe(this,
            Observer {
                initData()
                viewModel.dataSource?.invalidate()
            })
    }


    /**
     * 展示顶部类型展示
     */
    fun showTopPopup(view: View){
        XPopup
            .Builder(activity)
            .isDestroyOnDismiss(false)
            .atView(view)
            .asCustom(typeChoosePopup.apply {
                setItemOnClickListener(object : ProjectChooseMenuPopup.ItemOnClickListener{
                    override fun onClick(position: Int, content: ProjectChooseType, content1: ProjectType) {
                        if(content.name.contains("项目类型")){
                            viewModel.projecttype.set(content1.id?.toString())
                            viewModel.stageId.set(null)
                        }else{
                            viewModel.projecttype.set(null)
                            viewModel.stageId.set(content1.id?.toString())

                        }
                        viewModel.chooseTag.set(ProjectType(name=content.name+"—"+content1.name,id = content1.id))
                        viewModel.dataSource?.invalidate()
                    }
                })
            })
            .show()
    }


    private fun initMultipleLayoutManager() {

        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .build()

        multipleLayoutManager.showLoadingLayout()
    }





}