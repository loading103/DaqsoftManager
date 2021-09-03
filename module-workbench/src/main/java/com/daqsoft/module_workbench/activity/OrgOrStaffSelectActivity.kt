package com.daqsoft.module_workbench.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.StaffSelectedAdapter
import com.daqsoft.module_workbench.databinding.ActivityOrgOrStaffSelectBinding
import com.daqsoft.module_workbench.fragment.OrgSelectFragment
import com.daqsoft.module_workbench.viewmodel.BaseSelectViewModel
import com.daqsoft.module_workbench.viewmodel.OrgOrStaffSelectViewModel
import com.daqsoft.module_workbench.widget.MyNavHostFragment
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 3/3/2021 下午 4:22
 * @author zp
 * @describe 组织或者员工共选择
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ORG_OR_STAFF_SELECT)
class OrgOrStaffSelectActivity : AppBaseActivity<ActivityOrgOrStaffSelectBinding, OrgOrStaffSelectViewModel>() {


    @Inject
    lateinit var staffSelectedAdapter : StaffSelectedAdapter


    /**
     * 组织单选
     */
    @JvmField
    @Autowired
    var orgSingleChoice : Boolean? = false

    /**
     * 选择类型 0 员工  1 组织
     */
    @JvmField
    @Autowired
    var type : Int = 0

    /**
     * 员工单选
     */
    @JvmField
    @Autowired
    var staffSingleChoice : Boolean? = false

    /**
     * 项目id
     */
    @JvmField
    @Autowired
    var projectId : String? = null

    @JvmField
    @Autowired
    var selected : ArrayList<Employee>? = null

    @JvmField
    @Autowired
    var isAllDept : Boolean? = false



    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_org_or_staff_select
    }

    override fun initViewModel(): OrgOrStaffSelectViewModel? {
        return viewModels<OrgOrStaffSelectViewModel>().value
    }

    override fun onSupportNavigateUp(): Boolean {
        return Navigation.findNavController(this, R.id.fragment).navigateUp()
    }

    override fun initView() {
        super.initView()

        BaseSelectViewModel.type = type
        BaseSelectViewModel.orgSingleChoice = orgSingleChoice?:false
        BaseSelectViewModel.selectedOrgSet.addAll(selected ?: arrayListOf())
        BaseSelectViewModel.modify = !selected.isNullOrEmpty()
        if(BaseSelectViewModel.modify){
            handleSelected()
        }
        BaseSelectViewModel.isAllDept = isAllDept?:false
        BaseSelectViewModel.staffSingleChoice = staffSingleChoice?:false
        BaseSelectViewModel.projectId = projectId

        initNavController()
        initRecyclerView()

        binding.staffDetermine.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", Gson().toJson(BaseSelectViewModel.selectedStaffSet))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }


        binding.orgDetermine.setOnClickListener {
            val intent = Intent()
            intent.putExtra("result", Gson().toJson(BaseSelectViewModel.selectedOrgSet))
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()


        LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK, String::class.java).observe(
            this,
            Observer {
                handleSelected()
            })

        LiveEventBus.get(LEBKeyGlobal.ORG_SELECT_LIST_CHANGED_CALLBACK, String::class.java).observe(
            this,
            Observer {
                handleSelected()
            })


        LiveEventBus.get(LEBKeyGlobal.ORG_SINGLE_CHOICE, Employee::class.java).observe(
            this,
            Observer {
                val intent = Intent()
                intent.putExtra("result", Gson().toJson(arrayListOf(it)))
                setResult(Activity.RESULT_OK, intent)
                finish()
            })

        // 员工单选
        LiveEventBus.get(LEBKeyGlobal.STAFF_SINGLE_CHOICE, Employee::class.java).observe(
            this,
            Observer {
                val intent = Intent()
                intent.putExtra("result", Gson().toJson(arrayListOf(it)))
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
    }

    private fun initNavController() {
        val navController = Navigation.findNavController(this, R.id.fragment)
        val navGraph = navController.navInflater.inflate(R.navigation.nav_staff_org_select)
        navGraph.startDestination = if(type == 0) R.id.module_workbench_staffselectfragment else R.id.module_workbench_orgselectfragment
        navController.graph = navGraph
    }

    /**
     * 初始化选中状态员工 recyclerView
     */
    fun initRecyclerView(){
        binding.recycleViewSelected.apply {
            layoutManager = LinearLayoutManager(this@OrgOrStaffSelectActivity).apply {
                orientation = RecyclerView.HORIZONTAL
            }
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    var position = parent.getChildAdapterPosition(view)
                    var count = state.itemCount - 1
                    outRect.left = if (position == 0) 0.dp else 6.dp
                    outRect.right = if (position == count) 0.dp else 6.dp
                }
            })

            adapter = staffSelectedAdapter.apply {
                itemBinding = ItemBinding.of(BR.employee, R.layout.recyclerview_staff_selected_item)
                setItems(BaseSelectViewModel.selectedStaffSet.toMutableList())
                setItemOnClickListener(object : StaffSelectedAdapter.ItemOnClickListener {
                    override fun onClick(position: Int, item: Employee) {
                        BaseSelectViewModel.selectedStaffSet.remove(item)
                        LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_LIST_CHANGED_CALLBACK)
                            .post(
                                "remove"
                            )
                        LiveEventBus.get(LEBKeyGlobal.EMPLOYEE_BOTTOM_SELECTED_CLICK).post(item)
                    }
                })
            }
        }
    }


    @SuppressLint("SetTextI18n")
    fun handleSelected(){
        if(type == 0){
            if(BaseSelectViewModel.selectedStaffSet.isEmpty()){
                binding.selectedCl.visibility = View.GONE
            }else{
                binding.selectedCl.visibility = View.VISIBLE
                binding.staffGroup.visibility = View.VISIBLE
                binding.staffDetermine.text = "确认（${BaseSelectViewModel.selectedStaffSet.size}）"
            }
            staffSelectedAdapter.setItems(BaseSelectViewModel.selectedStaffSet.toMutableList())
            staffSelectedAdapter.notifyDataSetChanged()
            if(BaseSelectViewModel.selectedStaffSet.isNotEmpty()){
                binding.recycleViewSelected.smoothScrollToPosition(BaseSelectViewModel.selectedStaffSet.size - 1)
            }
        }

        if (type ==1){
            if (BaseSelectViewModel.modify){
                binding.selectedCl.visibility = View.VISIBLE
                binding.orgDetermine.visibility = View.VISIBLE
                binding.orgDetermine.text = "确认（${BaseSelectViewModel.selectedOrgSet.size}）"
                return
            }


            if (BaseSelectViewModel.selectedOrgSet.isEmpty()){
                binding.selectedCl.visibility = View.GONE
            }else{
                binding.selectedCl.visibility = View.VISIBLE
                binding.orgDetermine.visibility = View.VISIBLE
                binding.orgDetermine.text = "确认（${BaseSelectViewModel.selectedOrgSet.size}）"
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        BaseSelectViewModel.selectedStaffSet.clear()
        BaseSelectViewModel.selectedOrgSet.clear()
        BaseSelectViewModel.type = 0
        BaseSelectViewModel.orgSingleChoice = false
        BaseSelectViewModel.modify = false
        BaseSelectViewModel.isAllDept = false
        BaseSelectViewModel.staffSingleChoice = false
        BaseSelectViewModel.projectId = null
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {

        LiveEventBus.get(LEBKeyGlobal.ORG_OR_STAFF_SELECT_ACTIVITY_ON_BACK_PRESSED).post(true)

        Timber.e(
            "Navigation.findNavController(this, R.id.fragment).backStack.size ${
                Navigation.findNavController(
                    this,
                    R.id.fragment
                ).backStack.size
            }"
        )
        if (Navigation.findNavController(this, R.id.fragment).backStack.size == 3){
            LiveEventBus.get(LEBKeyGlobal.REFRESH_THE_EMPLOYEE_SELECTION_PAGE).post(true)
        }

        super.onBackPressed()
    }



    @RequiresApi(Build.VERSION_CODES.N)
    fun checkIfAllSelected(fromSelectAll : Boolean = false){
        val fragments = supportFragmentManager.fragments
        if (!fragments.isNullOrEmpty()){
            val myNavHostFragment = fragments[0] as MyNavHostFragment
            val childFragments: List<Fragment> = myNavHostFragment.childFragmentManager.fragments
            if (!childFragments.isNullOrEmpty()){
                childFragments.reversed().forEach {
                    if (it is OrgSelectFragment){
                        it.checkAllSelected(fromSelectAll)
                    }
                }
            }
        }
    }

    /**
     * 获取位置
     * @param current OrgSelectFragment
     * @return Int
     */
    fun getCurrentFragmentIndex(current : OrgSelectFragment):Int{
        val fragments = supportFragmentManager.fragments
        if (!fragments.isNullOrEmpty()){
            val myNavHostFragment = fragments[0] as MyNavHostFragment
            val childFragments: List<Fragment> = myNavHostFragment.childFragmentManager.fragments
            if (!childFragments.isNullOrEmpty()){
                childFragments.reversed().forEachIndexed { index, fragment ->
                    if (fragment is OrgSelectFragment){
                        return index
                    }
                }
            }
        }
        return -1
    }


    fun staffSelectedAll(){
        val intent = Intent()
        intent.putExtra("selectAll",true)
        intent.putExtra("result", Gson().toJson(BaseSelectViewModel.selectedStaffSet))
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}

