package com.daqsoft.module_workbench.activity

import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityCustmerListBinding
import com.daqsoft.module_workbench.viewmodel.CustomerListViewModel
import com.daqsoft.module_workbench.widget.CustomeTopPopup
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint

/**
 * 客户管理详情界面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CUSTOMER_LIST)
class CustomerListActivity : AppBaseActivity<ActivityCustmerListBinding, CustomerListViewModel>() {
    val lecftChoosePopup : CustomeTopPopup by lazy {
        CustomeTopPopup(this)
    }
    val rightChoosePopup : CustomeTopPopup by lazy {
        CustomeTopPopup(this)
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_custmer_list
    }

    override fun initViewModel(): CustomerListViewModel? {
        return viewModels<CustomerListViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRefreshLayout()
        initMultipleLayoutManager()
    }

    override fun initData() {
        viewModel.getTypeList()
        viewModel.getLevelList()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        // 进入网页刷新该界面
        LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).observeForever {
            viewModel.dataSource?.invalidate()
        }

        LiveEventBus.get(LEBKeyGlobal.CUSTOMER_SENT_SUCCESSFULLY,Boolean::class.java).observe(this, Observer {
            viewModel.refreshItemClick()
        })

        viewModel.pageList.observe(this, Observer {
            binding.refresh.finishRefresh(true)
            if(it.isNullOrEmpty()){
                multipleLayoutManager.showEmptyLayout()
                return@Observer
            }
            multipleLayoutManager.showSuccessLayout()
            binding.recyclerView.executePaging(it, viewModel.diff)
        })

        viewModel.levelDatas.observe(this, Observer {
            rightChoosePopup.setData(it.map { it.getNameString() }.toMutableList())
        })
        viewModel.typeDatas.observe(this, Observer {
            lecftChoosePopup.setData(it.map { it.typeName!!}.toMutableList())
        })

        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refresh.finishRefresh()
        })
        viewModel.leftLiveData.observe(this, Observer {
            showLeftTopPopup(binding.tvPx)
        })

        viewModel.rightLiveData.observe(this, Observer {
            showRightTopPopup(binding.tvChoose)
        })
        viewModel.callLiveData.observe(this, Observer {
            callPhone(it)
        })
    }


    private fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }
    /**
     * 初始化下拉刷新
     */
    private fun initRefreshLayout() {
        binding.refresh.setOnRefreshListener {
            viewModel.dataSource?.invalidate()
        }
        binding.recyclerView.apply {
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    outRect.bottom = 12.dp
                }
            })
        }
    }


    lateinit var multipleLayoutManager: MultipleLayoutManager
    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_no_add,null,false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        val addContent =  emptyView!!.findViewById<TextView>(R.id.retry)
        emptyContent.text = "你暂无可管理的客户"
        addContent.setOnClickListener {
            var website:String= HttpGlobal.DAILY_ADD_CUSTOMER
            ARouter
                .getInstance()
                .build(ARouterPath.Web.PAGER_WEB)
                .withString("url",website)
                .navigation()
        }
        multipleLayoutManager =  MultipleLayoutManager
            .Builder(binding.recyclerView)
            .setEmptyLayout(emptyView)
            .build()

        multipleLayoutManager.showEmptyLayout()
    }


    /**
     * 展示顶部类型展示
     */
    var selectedLeftPosition = 0
    fun showLeftTopPopup(view: View){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .atView(view)
            .asCustom(lecftChoosePopup.apply {
                setItemOnClickListener(object : CustomeTopPopup.ItemOnClickListener{
                    override fun onClick(position: Int, content: String) {
                        selectedLeftPosition=position
                        viewModel.leftTextObserFild.set(content)
                        viewModel.chooseType.value=viewModel.typeDatas?.value?.get(position)
                        viewModel.dataSource?.invalidate()
                    }
                })
            })
            .show()
        lecftChoosePopup.setSelectedPosition(selectedLeftPosition)
    }
    /**
     * 展示顶部类型展示
     */
    var selectedRightPosition = 0
    fun showRightTopPopup(view: View){
        XPopup
            .Builder(this)
            .isDestroyOnDismiss(false)
            .atView(view)
            .asCustom(rightChoosePopup.apply {
                setItemOnClickListener(object : CustomeTopPopup.ItemOnClickListener{
                    override fun onClick(position: Int, content: String) {
                        selectedRightPosition=position
                        viewModel.rightTextObserFild.set(content)
                        viewModel.chooseLevel.value=viewModel.levelDatas?.value?.get(position)
                        viewModel.dataSource?.invalidate()
                    }
                })
            })
            .show()
        rightChoosePopup.setSelectedPosition(selectedRightPosition)
    }
}


