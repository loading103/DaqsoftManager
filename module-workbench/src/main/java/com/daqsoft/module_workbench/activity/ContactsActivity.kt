package com.daqsoft.module_workbench.activity

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialLabelUnit
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.sp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.ContactsAdapter
import com.daqsoft.module_workbench.databinding.ActivityContactsBinding
import com.daqsoft.module_workbench.viewmodel.ContactsViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.google.android.material.appbar.AppBarLayout
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 25/11/2020 下午 2:04
 * @author zp
 * @describe 通讯录
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_CONTACTS)
class ContactsActivity : AppBaseActivity<ActivityContactsBinding, ContactsViewModel>() {


    @Inject
    lateinit var contactsAdapter: ContactsAdapter

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_contacts
    }

    override fun initViewModel(): ContactsViewModel? {
        return viewModels<ContactsViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.callLiveData.observe(this, Observer {
            callPhone(it)
        })

    }

    override fun initView() {
        super.initView()
        initAppBar()
        initRecycleView()
    }

    override fun initData() {
        viewModel.getCompanyInfo()
        viewModel.getMember(0)
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
                    val count = state.itemCount - 1
                    if (position != count) {
                        outRect.top = 10.dp
                    }
                    if (position == 0){
                        outRect.top = 20.dp
                    }
                }
            })
            adapter = contactsAdapter
        }
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
                val cardViewTop = binding.cardView.top
                val appbarTop = binding.appbar.top
                val minDistance = AppUtils.getStatusBarHeight() + AppUtils.getToolbarHeight()
                if ((cardViewTop + appbarTop) < minDistance) {
                    viewModel.setBackground(Color.WHITE)
                    viewModel.setBackIconSrc(R.mipmap.back_black)
                    viewModel.setRightIconSrc(R.mipmap.txl_list_search_black)
                    viewModel.setTitleTextColor(R.color.black_000000)
                    binding.includeLine.visibility = View.VISIBLE
                } else {
                    viewModel.setBackground(Color.TRANSPARENT)
                    viewModel.setBackIconSrc(R.mipmap.list_top_return_white)
                    viewModel.setRightIconSrc(R.mipmap.txl_list_search_white)
                    viewModel.setTitleTextColor(R.color.white_ffffff)
                    binding.includeLine.visibility = View.GONE
                }
            }
        })
    }

    private fun callPhone(phoneNum: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        val data = Uri.parse("tel:$phoneNum")
        intent.data = data
        startActivity(intent)
    }
}