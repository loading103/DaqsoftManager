package com.daqsoft.module_workbench.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.activity.OrgOrStaffSelectActivity
import com.daqsoft.module_workbench.adapter.OrgSelectAdapter
import com.daqsoft.module_workbench.databinding.FragmentOrgSelectBinding
import com.daqsoft.module_workbench.viewmodel.BaseSelectViewModel
import com.daqsoft.module_workbench.viewmodel.OrgSelectViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.fragment
 * @date 3/3/2021 下午 5:03
 * @author zp
 * @describe 组织选择
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ORG_SELECT)
class OrgSelectFragment : AppBaseFragment<FragmentOrgSelectBinding, OrgSelectViewModel>() {

    var navArguments : ArrayList<Employee>? = null

    var current : Employee? = null

    var superIsChecked : Boolean = false

    @Inject
    lateinit var orgSelectAdapter: OrgSelectAdapter


    lateinit var multipleLayoutManager: MultipleLayoutManager

    lateinit var container : OrgOrStaffSelectActivity

    override fun initParam() {
        super.initParam()
        arguments?.let {
            navArguments = it.getParcelableArrayList("navArguments")
            navArguments?.let {
                current = it[it.size - 1]
            }
            Timber.e( "navArguments ${Gson().toJson(navArguments)}"   )
            Timber.e( "current ${Gson().toJson(current)}"   )

            superIsChecked = it.getBoolean("superIsChecked",false)
        }
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.fragment_org_select
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): OrgSelectViewModel? {
        return ViewModelProvider(this).get(OrgSelectViewModel::class.java)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun initData() {
        super.initData()
        viewModel.current = current
        if (container.getCurrentFragmentIndex(this) == 0){
            if (BaseSelectViewModel.isAllDept){
                viewModel.selectAll = true
                viewModel.setRightTextTxt("取消全选")
            }else{
                viewModel.selectAll = false
                viewModel.setRightTextTxt("全选")
            }
        }else{
            if (superIsChecked){
                viewModel.selectAll = true
                viewModel.setRightTextTxt("取消全选")
            }else{
                viewModel.selectAll = false
                viewModel.setRightTextTxt("全选")
            }
        }
    }

    override fun initView() {
        super.initView()

        container = activity  as OrgOrStaffSelectActivity
        initRecyclerView()
        initMultipleLayoutManager()

        viewModel.setTitleText(current?.name?:if (BaseSelectViewModel.type == 0)"员工选择" else "组织架构选择")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @SuppressLint("RestrictedApi")
    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.backLiveData.observe(this, Observer {
            container.finish()
        })

        viewModel.pageList.observe(this, Observer {
            binding.recyclerView.executePaging(it,viewModel.diff)
        })

        LiveEventBus.get(LEBKeyGlobal.EMPLOYEE_BOTTOM_SELECTED_CLICK,Employee::class.java).observe(this, Observer { employee ->
            orgSelectAdapter.getSelectedList().find { it.id == employee.id }?.let {
                orgSelectAdapter.removeChecked(it)
                orgSelectAdapter.setAllIsChecked(false)
                orgSelectAdapter.notifyDataSetChanged()
            }
            Handler(Looper.getMainLooper()).postDelayed({
                container.checkIfAllSelected()
            },100)
        })

        LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_ACTIVITY_ON_BACK_PRESSED,Boolean::class.java).observe(this, Observer {
            orgSelectAdapter.notifyDataSetChanged()

            Handler(Looper.getMainLooper()).postDelayed({
                container.checkIfAllSelected()
            },100)
        })

        viewModel.checkSelection.observe(this, Observer {
            orgSelectAdapter.notifyDataSetChanged()

            Handler(Looper.getMainLooper()).postDelayed({
                container.checkIfAllSelected(it)
            },100)


//            checkIfAllSelected()
        })

        viewModel.multipleLayoutLiveData.observe(this, Observer {
            when(it){
                MultipleLayoutManager.SUCCESS -> multipleLayoutManager.showSuccessLayout()
                MultipleLayoutManager.EMPTY -> multipleLayoutManager.showEmptyLayout()
                MultipleLayoutManager.ERROR -> multipleLayoutManager.showErrorLayout()
            }
        })

    }

    private fun initMultipleLayoutManager() {
        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .build()
        multipleLayoutManager.showLoadingLayout()
    }

    private fun initRecyclerView() {
        binding.recyclerView.apply {
            adapter = orgSelectAdapter.apply {
                setOrgItemOnClickListener(object : OrgSelectAdapter.OrgItemOnClickListener{
                    override fun onClick(position: Int, employee: Employee,isChecked:Boolean) {

                        BaseSelectViewModel.gradation.add(employee)

                        val bundle = Bundle()
                        if (navArguments == null){
                            navArguments = arrayListOf()
                        }
                        navArguments!!.add(employee)
                        bundle.putParcelableArrayList("navArguments",navArguments)
                        bundle.putBoolean("superIsChecked",isChecked)
                        Navigation.findNavController(requireView()).navigate(R.id.module_workbench_orgselectfragment,bundle)
                    }
                })

                setOnCheckedChangeListener(object : OrgSelectAdapter.OnCheckedChangeListener {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onStaffCheckedChanged(
                        position: Int,
                        isChecked: Boolean,
                        employee: Employee
                    ) {
                        val finder =
                            BaseSelectViewModel.selectedStaffSet.find { it.id == employee.id }
                        if (isChecked) {
                            if (finder == null) {
                                BaseSelectViewModel.selectedStaffSet.add(employee)
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK)
                                    .post("add")
                            }
                        } else {
                            if (finder != null) {
                                BaseSelectViewModel.selectedStaffSet.removeIf {
                                    it.id == employee.id
                                }
                                LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK)
                                    .post("remove")
                            }
                        }
                        Handler(Looper.getMainLooper()).postDelayed({
                            container.checkIfAllSelected()
                        },100)
                    }

                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onOrgCheckedChanged(
                        position: Int,
                        isChecked: Boolean,
                        employee: Employee
                    ) {
                        // 单选时
                        if (BaseSelectViewModel.type == 1 && BaseSelectViewModel.orgSingleChoice) {
                            if (isChecked) {
                                LiveEventBus.get(LEBKeyGlobal.ORG_SINGLE_CHOICE).post(employee)
                            }
                            return
                        }

                        val finder = BaseSelectViewModel.selectedOrgSet.find { it.id == employee.id }
                        if (isChecked) {
                            if (finder == null) {
                                BaseSelectViewModel.selectedOrgSet.add(employee)
                                if (BaseSelectViewModel.type == 1) {
                                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK)
                                        .post("add")
                                }
                            }
                        } else {
                            if (finder != null) {
                                BaseSelectViewModel.selectedOrgSet.remove(finder)
                                if (BaseSelectViewModel.type == 1) {
                                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK)
                                        .post("remove")
                                }
                            }
                        }

                        if (isChecked) {
                            if (BaseSelectViewModel.type == 0) {
                                viewModel.addOrgAllStaff(employee)
                            }
                            if (BaseSelectViewModel.type == 1) {
                                viewModel.addOrgAllOrg(employee)
                            }
                        } else {
                            if (BaseSelectViewModel.type == 0) {
                                viewModel.removeOrgAllStaff(employee)
                            }
                            if (BaseSelectViewModel.type == 1) {
                                viewModel.removeOrgAllOrg(employee)
                            }
                        }
                    }


                })
            }
        }
    }


//    @SuppressLint("RestrictedApi")
//    @RequiresApi(Build.VERSION_CODES.N)
//    fun checkIfAllSelected(){
//        val finder = BaseSelectViewModel.selectedOrgSet.find { it.id == current?.id }
//        val selectAll = orgSelectAdapter.getSelectedList().size == orgSelectAdapter.itemCount
//        if (selectAll){
//            if (finder == null && orgSelectAdapter.itemCount !=0){
//                current?.let {
//                    BaseSelectViewModel.selectedOrgSet.add(it)
//                    if (BaseSelectViewModel.type == 1){
//                        LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("add")
//                    }
//                }
//            }
//        }else{
//            if (finder != null && orgSelectAdapter.itemCount != 0) {
//                BaseSelectViewModel.selectedOrgSet.removeIf{
//                    it.id == current?.id
//                }
//                if (BaseSelectViewModel.type == 1){
//                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("remove")
//                }
//            }
////
////            BaseSelectViewModel.selectedOrgSet.removeIf{
////                navArguments?.map { it.id }?.contains(it.id)?:false
////            }
//        }
//
//
//        orgSelectAdapter.notifyDataSetChanged()
//        Timber.e("selectedOrgSet ${Gson().toJson(BaseSelectViewModel.selectedOrgSet)}")
////
////        viewModel.selectAll = selectAll
////        viewModel.setRightTextTxt(if (selectAll) "取消全选" else "全选")
//
//    }


    @RequiresApi(Build.VERSION_CODES.N)
    fun checkAllSelected(fromSelectAll : Boolean = false){
        val selectAll = orgSelectAdapter.getSelectedList().size == orgSelectAdapter.itemCount
        Timber.e("selectAll  ${selectAll}")
        val finder = BaseSelectViewModel.selectedOrgSet.find { it.id == current?.id }
        if (selectAll){
            if (finder == null && orgSelectAdapter.itemCount !=0){
                current?.let {
                    BaseSelectViewModel.selectedOrgSet.add(it)
                    if (BaseSelectViewModel.type == 1){
                        LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("add")
                    }
                }
            }
        }else{
            if (finder != null && orgSelectAdapter.itemCount != 0) {
                BaseSelectViewModel.selectedOrgSet.removeIf{
                    it.id == current?.id
                }
                if (BaseSelectViewModel.type == 1){
                    LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK).post("remove")
                }
            }
//
//            BaseSelectViewModel.selectedOrgSet.removeIf{
//                navArguments?.map { it.id }?.contains(it.id)?:false
//            }
        }

        if (!fromSelectAll){
            if (selectAll){
                viewModel.selectAll = true
                viewModel.setRightTextTxt("取消全选")
            }else{
                viewModel.selectAll = false
                viewModel.setRightTextTxt("全选")
            }

        }
    }


}