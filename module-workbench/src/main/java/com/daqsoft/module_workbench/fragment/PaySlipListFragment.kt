package com.daqsoft.module_workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialLabelUnit
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.sp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.ExpandableAdapter
import com.daqsoft.module_workbench.databinding.FragmentPaySlipListBinding
import com.daqsoft.module_workbench.databinding.FragmentPaySlipPasswordBinding
import com.daqsoft.module_workbench.viewmodel.PaySlipViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 9/12/2020 下午 2:56
 * @author zp
 * @describe 工资条列表页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PAY_SLIP_LIST)
class PaySlipListFragment : AppBaseFragment<FragmentPaySlipListBinding, PaySlipViewModel>() {

    @Inject
    lateinit var expandableAdapter: ExpandableAdapter

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_pay_slip_list
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): PaySlipViewModel? {
        return activity?.viewModels<PaySlipViewModel>()?.value
    }

    override fun initView() {
        initExpandableListView()
    }

    override fun initData() {
        viewModel.getPaySlipList()
    }


    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.paySlipListLiveData.observe(this, Observer {
            expandableAdapter.setGroup(it.first)
            expandableAdapter.setChildren(it.second)
            expandableAdapter.notifyDataSetChanged()
        })

    }

    /**
     * 初始化二级列表
     */
    private fun  initExpandableListView(){
        binding.expandableListView.apply {
            setGroupIndicator(null)
            setAdapter(expandableAdapter)
            expandGroup(0)
            setOnGroupClickListener { parent, v, groupPosition, id ->
                false
            }

            setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                ARouter
                    .getInstance()
                    .build(ARouterPath.Workbench.PAGER_PAY_SLIP_DETAIL)
                    .withString("id",expandableAdapter.getChildrenList()[groupPosition][childPosition].id.toString())
                    .withString("secret",viewModel.secret)
                    .navigation()
                true
            }
        }
    }

}