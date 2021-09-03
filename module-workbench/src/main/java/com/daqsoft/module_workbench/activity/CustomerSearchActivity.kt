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
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityCustomerSearchBinding
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerType
import com.daqsoft.module_workbench.viewmodel.CustomerSearchViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CUSTOMER_SEARCH)
class CustomerSearchActivity : AppBaseActivity<ActivityCustomerSearchBinding, CustomerSearchViewModel>() {

    @JvmField
    @Autowired
    var colorJson : String = ""


    lateinit var multipleLayoutManager: MultipleLayoutManager

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_customer_search
    }

    override fun initViewModel(): CustomerSearchViewModel? {
        return viewModels<CustomerSearchViewModel>().value
    }

    override fun initView() {
        super.initView()
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
        initMultipleLayoutManager()
    }
    override fun initData() {
        if(!colorJson.isNullOrBlank()){
            viewModel.typeDatas.value = Gson().fromJson(colorJson, object : TypeToken<ArrayList<CustomerType>>() {}.type)
        }
        viewModel.getdatas(viewModel.searchObservable.get().toString())

    }

    private fun initMultipleLayoutManager() {
        val emptyView = LayoutInflater.from(this).inflate(R.layout.layout_no_add,null,false)
        val emptyContent =  emptyView!!.findViewById<TextView>(R.id.content)
        val addContent =  emptyView!!.findViewById<TextView>(R.id.retry)
        addContent.visibility=View.GONE
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

    override fun initViewObservable() {
        super.initViewObservable()

        // 进入网页刷新该界面
        LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).observeForever {
            viewModel.getdatas(viewModel.searchObservable.get().toString())
        }
        viewModel.changedLiveData.observe(this, Observer {
            if(it){
                multipleLayoutManager.showSuccessLayout()
            }else{
                multipleLayoutManager.showEmptyLayout()
            }
        })
        viewModel.callLiveData.observe(this, Observer {
            callPhone(it)
        })

        LiveEventBus.get(LEBKeyGlobal.CUSTOMER_SENT_SUCCESSFULLY,Boolean::class.java).observe(this, Observer {
            viewModel.getdatas(viewModel.searchObservable.get().toString())
        })
    }

    private fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }

}


