package com.daqsoft.module_workbench.fragment

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.WorkBenchAdapter
import com.daqsoft.module_workbench.databinding.FragmentWorkbenchBinding
import com.daqsoft.module_workbench.viewmodel.WorkBenchViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @ClassName    WorkBenchFragment
 * @Description  工作台
 * @Author       yuxc
 * @CreateDate   2020/11/17
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_WORKBENCH)
class WorkBenchFragment : AppBaseFragment<FragmentWorkbenchBinding, WorkBenchViewModel>() {


    @Inject
    lateinit var workBenchAdapter: WorkBenchAdapter


    override fun initData() {
        super.initData()
        viewModel.createItem()
    }

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_workbench
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): WorkBenchViewModel? {
        return activity?.viewModels<WorkBenchViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        if (!NetworkUtil.isNetworkAvailable(this.context)) {
           errorLayout(R.id.coordinator_layout,callback = {initView()})
        } else {
            normalLayout()
            initRecycleView()
        }
    }

    override fun retry() {
        super.retry()
        initData()
    }
    override fun initViewObservable() {
        super.initViewObservable()


//        /**
//         * 统计日报浏览时间长度
//         */
//        LiveEventBus.get(LEBKeyGlobal.SCAN_TIME,HashMap::class.java).observe(this, Observer {
//            if (it != null) {
//                val id = it["id"] as String
//                val scanTime = it["scanTime"] as String
//                viewModel.getScanTime(id,scanTime)
//            }
//        })
    }

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.recyclerView.apply {
            adapter = workBenchAdapter
            addItemDecoration(object : RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position == 0){
                        outRect.top - 12.dp
                    }
                    outRect.left = 12.dp
                    outRect.right = 12.dp
                    outRect.bottom = 12.dp
                }
            })

        }
    }
}