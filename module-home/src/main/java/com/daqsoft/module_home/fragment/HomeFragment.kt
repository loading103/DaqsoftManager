package com.daqsoft.module_home.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.module_home.BR
import com.daqsoft.module_home.R
import com.daqsoft.module_home.databinding.FragmentHomeBinding
import com.daqsoft.module_home.viewmodel.HomeViewModel
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
 * @package name：com.daqsoft.module_mine.fragment
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 首页
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Home.PAGER_HOME)
class HomeFragment : AppBaseFragment<FragmentHomeBinding, HomeViewModel>() {

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_home
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): HomeViewModel? {
        return activity?.viewModels<HomeViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        if (!NetworkUtil.isNetworkAvailable(this.context)) {
            errorLayout(R.id.outermost,callback = {initView()})
        } else {
            normalLayout()
            initRefreshLayout()
        }
    }

    override fun retry() {
        super.retry()
        initData()
    }

    override fun initData() {
        viewModel.getAllNewsList()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.refreshCompleteLiveData.observe(this, Observer {
            binding.refresh.finishRefresh(it)
        })

        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED,String::class.java).observe(this, Observer {
            viewModel.getAllNewsList()
        })


        /**
         * 统计日报浏览时间长度
         */
        LiveEventBus.get(LEBKeyGlobal.SCAN_TIME,HashMap::class.java).observe(this, Observer {
            if (it != null) {
                val id = it["id"] as String
                val scanTime = it["scanTime"] as String
                viewModel.getScanTime(id,scanTime)
            }
        })
    }

    /**
     * 初始化下拉刷新
     */
    private fun initRefreshLayout() {
        binding.refresh.setOnRefreshListener {
            initData()
        }

    }
}
