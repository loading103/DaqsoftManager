package com.daqsoft.module_project.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.ActivityProjectChooseYzBinding
import com.daqsoft.module_project.viewmodel.ProjectChoseOwneViewModel
import com.daqsoft.module_project.widget.AccountTypePopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @describe 选择业主
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_CHOOSE_YEZHU)
class ProjectChoseOwnerActivity : AppBaseActivity<ActivityProjectChooseYzBinding, ProjectChoseOwneViewModel>() {

    @JvmField
    @Autowired
    var  isOwner : Boolean? = true

    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_choose_yz
    }

    override fun initViewModel(): ProjectChoseOwneViewModel? {
        return viewModels<ProjectChoseOwneViewModel>().value
    }

    override fun initView() {
        super.initView()
        viewModel.isOwner=isOwner
        initRecycleView()
        initMultipleLayoutManager()
    }

    override fun initData() {
        super.initData()
        viewModel.getShowData()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.haveObservable.observe(this, Observer {
            if(viewModel.observableList.isNullOrEmpty()){
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()

        })
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

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
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