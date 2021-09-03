package com.daqsoft.module_home.activity

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.module_home.BR
import com.daqsoft.module_home.R
import com.daqsoft.module_home.databinding.ActivityMessageBinding
import com.daqsoft.module_home.viewmodel.MessageViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint


/**
 * @package name：com.daqsoft.module_home.fragment
 * @date 1/12/2020 上午 11:43
 * @author zp
 * @describe 消息页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Home.PAGER_MESSAGE)
class MessageActivity : AppBaseActivity<ActivityMessageBinding, MessageViewModel>() {

    //    override fun outermostId() = R.id.outermost
//    override fun retry() {
//        initData()
//        viewModel.dataSource?.invalidate()
//    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_message
    }

    override fun initViewModel(): MessageViewModel? {
        return viewModels<MessageViewModel>().value
    }

    override fun initView() {
        super.initView()
        initRecycleView()
        initRefreshLayout()
    }

    override fun initData() {
        binding.setVariable(BR.untreated, Untreated.INSTANCE)
    }


    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refresh.finishRefresh(it)
        })

        viewModel.pageList.observe(this, Observer {
            if(it.isEmpty()){
                binding.recyclerView.visibility=View.GONE
                binding.llEmpty.llRoot.visibility=View.VISIBLE
                binding.llEmpty.content.text="暂无数据"
                return@Observer
            }
            binding.recyclerView.visibility=View.VISIBLE
            binding.llEmpty.llRoot.visibility=View.GONE
            binding.refresh.finishRefresh(true)
            binding.recyclerView.executePaging(it,viewModel.diff)
        })

        viewModel.readAllLivaData.observe(this, Observer {
            viewModel.dataSource?.invalidate()
        })



        Untreated.INSTANCE.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                viewModel.initToolbar()
            }
        })

        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED,String::class.java).observe(this, Observer {
            // 此处是未读的情况下才显示下一条消息
            if(viewModel.readAllLivaData.value=="0"){
                viewModel.dataSource?.invalidate()
            }
            //如果点击进去详情，点击下一条也要刷新界面
            if(it=="refresh"){
                viewModel.dataSource?.invalidate()
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
                    if (position == 0) {
                        outRect.top = 12.dp
                    }
                }
            })
        }
    }


    /**
     * 初始化下拉刷新
     */
    private fun initRefreshLayout() {
        binding.refresh.setOnRefreshListener {
            viewModel.dataSource?.invalidate()
        }
    }
}