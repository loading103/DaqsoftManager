package com.daqsoft.module_workbench.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.base.IMMyViewPagerAdapter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDailyDateTeamBinding
import com.daqsoft.module_workbench.viewmodel.DailyDateTeamViewModel
import com.luck.picture.lib.tools.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * 日报数据
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_DATA_TEAM)
class DailyTeamDateFragment : AppBaseFragment<ActivityDailyDateTeamBinding, DailyDateTeamViewModel>(){

    @JvmField
    @Autowired
    var haveTeamReports: Boolean?=false
    @JvmField
    @Autowired
    var haveMemberReports: Boolean?=false


    private var mFragments: ArrayList<Fragment> = arrayListOf()

    private  var mAdapter:IMMyViewPagerAdapter ?= null

    override fun initVariableId(): Int {
        return BR.viewModel
    }


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.activity_daily_date_team
    }

    override fun initViewModel(): DailyDateTeamViewModel? {
        return ViewModelProvider(this).get(DailyDateTeamViewModel::class.java)
    }

    override fun initView() {
        super.initView()
        binding.ivBack.setOnClickListener {
            activity?.finish()
        }
        binding.ivRightIcon.setOnClickListener {
            ToastUtils.s(activity,"敬请期待")
        }
        binding.tvLeft.isSelected=true
        binding.tvLeft.setOnClickListener {
            binding.tvLeft.isSelected=true
            binding.tvMiddle.isSelected=false
            binding.tvRight.isSelected=false
            binding.viewpager.currentItem=0

        }
        binding.tvMiddle.setOnClickListener {
            binding.tvLeft.isSelected=false
            binding.tvMiddle.isSelected=true
            binding.tvRight.isSelected=false
            binding.viewpager.currentItem=1
        }
        binding.tvRight.setOnClickListener {
            binding.tvLeft.isSelected=false
            binding.tvMiddle.isSelected=false
            binding.tvRight.isSelected=true
            binding.viewpager.currentItem=2
        }
        // 只有成员日报
        if(!haveTeamReports!! && haveMemberReports!!){
            binding.tvMiddle.visibility=View.GONE
            binding.tvMiddleLine.visibility=View.GONE
        }
        // 只有团队日报
        if(haveTeamReports!! && !haveMemberReports!!){
            binding.tvMiddle.visibility=View.GONE
            binding.tvMiddleLine.visibility=View.GONE
            binding.tvRight.text="团队"
        }
    }

    override fun initData() {
        mFragments.add(DailyTeamDateChild1Fragment())
        if(haveTeamReports!!){
            mFragments.add(DailyTeamDateChild2Fragment())
        }
        if(haveMemberReports!!){
            mFragments.add(DailyTeamDateChild3Fragment())
        }


        mAdapter = IMMyViewPagerAdapter(activity?.supportFragmentManager, mFragments)
        binding.viewpager.offscreenPageLimit=3
        binding.viewpager.adapter = mAdapter
    }


}