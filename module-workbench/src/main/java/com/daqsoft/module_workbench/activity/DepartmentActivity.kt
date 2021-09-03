package com.daqsoft.module_workbench.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDepartmentBinding
import com.daqsoft.module_workbench.viewmodel.DepartmentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DepartmentItemViewModel
import com.daqsoft.module_workbench.widget.SideBar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 26/11/2020 下午 2:52
 * @author zp
 * @describe 部门
 */
@AndroidEntryPoint

@Route(path = ARouterPath.Workbench.PAGER_DEPARTMENT)
class DepartmentActivity : AppBaseActivity<ActivityDepartmentBinding, DepartmentViewModel>() {

    @JvmField
    @Autowired
    var title : String? = ""

    @JvmField
    @Autowired
    var pid : String? = null


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_department
    }

    override fun initViewModel(): DepartmentViewModel? {
        return viewModels<DepartmentViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRecycleView()
        initSideBar()
    }

    override fun initData() {

        viewModel.setTitleText(title?:"")
        viewModel.getMember(pid?.toInt() ?: 0)
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.pinyinLiveData.observe(this, Observer {
            binding.sideBar.setIndexItemsArray(it.toTypedArray())
        })

    }


    /**
     * 初始化 侧边栏
     */
    private fun initSideBar() {
        binding.sideBar.setOnSelectIndexItemListener(object : SideBar.OnSelectIndexItemListener{
            override fun onSelectIndexItem(index: String?) {
                val  first = viewModel.observableList.first {
                    var itemViewModel =  it as DepartmentItemViewModel
                    itemViewModel.employee.initials.equals(index)
                }
                binding.recyclerView.smoothScrollToPosition(viewModel.observableList.indexOf(first))
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