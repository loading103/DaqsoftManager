package com.daqsoft.module_workbench.activity

import android.content.Intent
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
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityStaffSearchBinding
import com.daqsoft.module_workbench.viewmodel.StaffSearchViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 30/12/2020 上午 9:13
 * @author zp
 * @describe 员工搜索 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_STAFF_SEARCH)
class StaffSearchActivity : AppBaseActivity<ActivityStaffSearchBinding, StaffSearchViewModel>() {

    @JvmField
    @Autowired
    var fromSelect : Boolean = false

    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_staff_search
    }

    override fun initViewModel(): StaffSearchViewModel? {
        return viewModels<StaffSearchViewModel>().value
    }

    override fun initView() {
        super.initView()

        initRecycleView()

        initMultipleLayoutManager()
    }


    override fun initData() {
        super.initData()

        viewModel.fromSelect = fromSelect
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
                    outRect.top = 20.dp
                }
            })
        }
    }
}