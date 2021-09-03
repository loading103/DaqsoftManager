package com.daqsoft.module_project.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_project.viewmodel.ProjectViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import com.daqsoft.module_project.R
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.adapter.ProjectAdapter
import com.daqsoft.module_project.databinding.ActivityProjectSearchBinding
import com.jeremyliao.liveeventbus.LiveEventBus
import javax.inject.Inject

/**
 * @describe 项目搜索 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_SEARCH)
class ProjectSearchActivity : AppBaseActivity<ActivityProjectSearchBinding, ProjectViewModel>() {

    @Inject
    lateinit var mAdapter : ProjectAdapter

    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_search
    }

    override fun initViewModel(): ProjectViewModel? {
        return viewModels<ProjectViewModel>().value
    }

    override fun initView() {
        super.initView()

        initRecycleView()

        initMultipleLayoutManager()
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

        viewModel.changedLiveData.observe(this, Observer {
            if(it){
                viewModel.pageList.observe(this, Observer {
                    if(it.isEmpty()){
                        multipleLayoutManager.showEmptyLayout()
                        return@Observer
                    }
                    multipleLayoutManager.showSuccessLayout()
                    binding.recyclerView.executePaging(it,viewModel.diff)
                })
            }
        })

        // 类型选择
        viewModel.typeLiveData.observe(this, Observer {
            mAdapter.setListType(it)
        })

        // 详情关注或是取消刷新
        LiveEventBus.get(LEBKeyGlobal.PROJICT_FOLLOW_SUCCESS, Boolean::class.java).observe(this,
            Observer {
                mAdapter.itemviewModel.ischecked.set(it)
            })
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
                        outRect.bottom = 20.dp
                    }
                }
            })
        }
    }
}