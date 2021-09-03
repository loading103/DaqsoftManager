package com.daqsoft.module_workbench.activity

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.databinding.ActivityPaySlipDetailBinding
import com.daqsoft.module_workbench.viewmodel.PaySlipDetailViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 10/12/2020 上午 10:01
 * @author zp
 * @describe  工资条详情 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PAY_SLIP_DETAIL)
class PaySlipDetailActivity : AppBaseActivity<ActivityPaySlipDetailBinding, PaySlipDetailViewModel>() {


    @JvmField
    @Autowired
    var id : String = ""

    @JvmField
    @Autowired
    var secret : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_pay_slip_detail
    }

    override fun initViewModel(): PaySlipDetailViewModel? {
        return viewModels<PaySlipDetailViewModel>().value
    }

    override fun initView() {
        super.initView()
        initAppBar()

        initRecycleView()
    }

    /**
     * 初始化 recycleView
     */
    private fun initRecycleView() {
        binding.titleRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val count = state.itemCount - 1
                if (position == count){
                    outRect.bottom = 47.dp
                }
            }
        })

        binding.currentRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val count = state.itemCount - 1
                if (position == count){
                    outRect.bottom = 47.dp
                }
            }
        })

        binding.beforeRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration(){
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val position = parent.getChildAdapterPosition(view)
                val count = state.itemCount - 1
                if (position == count){
                    outRect.bottom = 47.dp
                }
            }
        })
    }

    override fun initData() {
        viewModel.getPaySlipDetail(id, secret)
    }


    /**
     * 初始化 AppBar
     */
    private fun initAppBar() {
        binding.appbar.addOnOffsetChangedListener(object : AppBarStateChangeListener() {
            override fun onStateChanged(appBarLayout: AppBarLayout, state: State) {
                when (state) {
                    //展开状态
                    State.EXPANDED -> {
                        Timber.e("展开")
                    }
                    //折叠状态
                    State.COLLAPSED -> {
                        Timber.e("折叠")
                    }
                    //中间状态
                    else -> {
                        Timber.e("中间")
                    }
                }
            }

            override fun onOffsetChanged(appBarLayout: AppBarLayout, i: Int) {
                if(i == 0){
                    return
                }
                val cardViewTop = binding.timeCl.top
                val appbarTop = binding.appbar.top
                val minDistance = AppUtils.getStatusBarHeight() + AppUtils.getToolbarHeight()
                if ((cardViewTop + appbarTop) < minDistance) {
                    viewModel.setBackground(Color.WHITE)
                    viewModel.setBackIconSrc(R.mipmap.back_black)
                    viewModel.setTitleTextColor(R.color.black_000000)
                    binding.includeLine.visibility = View.VISIBLE
                } else {
                    viewModel.setBackground(Color.TRANSPARENT)
                    viewModel.setBackIconSrc(R.mipmap.list_top_return_white)
                    viewModel.setTitleTextColor(R.color.white_ffffff)
                    binding.includeLine.visibility = View.GONE
                }
            }
        })
    }

}