package com.daqsoft.module_workbench.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.OrgOrStaffSelectActivity
import com.daqsoft.module_workbench.adapter.StaffSelectAdapter
import com.daqsoft.module_workbench.databinding.FragmentStaffSelectBinding

import com.daqsoft.module_workbench.viewmodel.BaseSelectViewModel
import com.daqsoft.module_workbench.viewmodel.StaffSelectViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffSelectItemViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 3/3/2021 下午 5:03
 * @author zp
 * @describe 员工选择
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_STAFF_SELECT)
class StaffSelectFragment : AppBaseFragment<FragmentStaffSelectBinding, StaffSelectViewModel>() {


    var container : OrgOrStaffSelectActivity? = null

    @Inject
    lateinit var staffSelectAdapter: StaffSelectAdapter

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_staff_select
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): StaffSelectViewModel? {
        return requireActivity().viewModels<StaffSelectViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.searchLiveData.observe(this, Observer {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF_SEARCH)
                .withBoolean("fromSelect",true)
                .navigation()
        })

        LiveEventBus.get(LEBKeyGlobal.EMPLOYEE_SEARCH_SELECTED,Employee::class.java).observe(this, Observer { employee ->
            val have = BaseSelectViewModel.selectedStaffSet.find { it.id == employee.id  }
            if (have == null){
                BaseSelectViewModel.selectedStaffSet.add(employee)
                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("add")
                handleSelected(employee,0)
            }
        })

        viewModel.pageList.observe(this, Observer {
            binding.recyclerView.executePaging(it,viewModel.diff)
        })


        LiveEventBus.get(LEBKeyGlobal.EMPLOYEE_BOTTOM_SELECTED_CLICK,Employee::class.java).observe(this, Observer { employee ->
            handleSelected(employee,1)
        })

        viewModel.backLiveData.observe(this, Observer {
            container?.finish()
        })

        LiveEventBus.get(LEBKeyGlobal.REFRESH_THE_EMPLOYEE_SELECTION_PAGE,Boolean::class.java).observe(this,
            Observer {
                staffSelectAdapter.notifyDataSetChanged()
            })


        viewModel.rightTextLiveData.observe(this, Observer {
            if (it){
                BaseSelectViewModel.selectedStaffSet.addAll(viewModel.pageList.value?.map {(it as StaffSelectItemViewModel).employee }?: arrayListOf())
            }else{
                BaseSelectViewModel.selectedStaffSet.clear()
            }
            staffSelectAdapter.notifyDataSetChanged()
            container?.staffSelectedAll()
        })
    }


    override fun initView() {
        super.initView()
        activity?.let {
            container = it  as OrgOrStaffSelectActivity
        }
        initRecyclerView()

        if(BaseSelectViewModel.staffSingleChoice){
            binding.appbarLayout.visibility = View.GONE
        }else{
            binding.appbarLayout.visibility = View.VISIBLE
        }


        binding.staff.setOnClickListener{
            val bundle = Bundle()
            bundle.putInt("pid",0)
            Navigation.findNavController(requireView()).navigate(R.id.module_workbench_orgselectfragment,bundle)
        }

    }


    /**
     * 初始化 recycleView
     */
    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = staffSelectAdapter.apply {
                setOnCheckedChangeListener(object : StaffSelectAdapter.OnCheckedChangeListener{
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onCheckedChanged(
                        position: Int,
                        isChecked: Boolean,
                        employee: Employee
                    ) {

                        // 单选时
                        if (BaseSelectViewModel.type == 0 && BaseSelectViewModel.staffSingleChoice) {
                            if (isChecked) {
                                LiveEventBus.get(LEBKeyGlobal.STAFF_SINGLE_CHOICE).post(employee)
                            }
                            return
                        }

                        val finder = BaseSelectViewModel.selectedStaffSet.find { it.id == employee.id }
                        if (isChecked){
                            if (finder == null){
                                BaseSelectViewModel.selectedStaffSet.add(employee)
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("add")
                            }
                        }else{
                            if (finder != null) {
                                BaseSelectViewModel.selectedStaffSet.removeIf {
                                    it.id == employee.id
                                }
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK).post("remove")
                            }
                        }

                    }

                })
            }
        }
    }

    /**
     *
     * @param sender ObservableList<Employee>
     * @param type Int 0 插入 1 移除
     */
    fun handleSelected(item: Employee,type : Int){
//        when(type){
//            0 ->{
//                staffSelectAdapter.addChecked(item)
//            }
//            1 ->{
//                staffSelectAdapter.removeChecked(item)
//            }
//        }
        staffSelectAdapter.notifyDataSetChanged()
    }
}