package com.daqsoft.module_workbench.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityCaretheasSearchBinding
import com.daqsoft.module_workbench.databinding.ActivityNotificationSearchBinding
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.viewmodel.CareThesauruSearchViewModel
import com.daqsoft.module_workbench.viewmodel.NotificationSearchViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import dagger.hilt.android.AndroidEntryPoint

/**
 * @ClassName    NotificationSearchActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/22
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_NOTIFICATION_SEARCH)
class NotificationSearchActivity : AppBaseActivity<ActivityNotificationSearchBinding, NotificationSearchViewModel>() {


    @JvmField
    @Autowired
    var permission : MenuPermissionCover ? = null


    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_notification_search
    }

    override fun initViewModel(): NotificationSearchViewModel? {
        return viewModels<NotificationSearchViewModel>().value
    }

    override fun initView() {
        super.initView()

        initRecycleView()

        initMultipleLayoutManager()
    }

    override fun initData() {
        super.initData()

        viewModel.permission = permission
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
                    it
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
                    val position = parent.getChildAdapterPosition(view)
                    val count = state.itemCount - 1
                    outRect.top = 12.dp
                    outRect.bottom = if (position == count) 20.dp else 0.dp
                }
            })
        }
    }
}