package com.daqsoft.module_workbench.activity

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.base.BaseFragmentPagerAdapter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.px
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityPunchBinding
import com.daqsoft.module_workbench.utils.MyAMapUtils
import com.daqsoft.module_workbench.viewmodel.PunchViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import timber.log.Timber


/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 7/1/2021 下午 3:27
 * @author zp
 * @describe 打卡 页面
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_PUNCH)
class PunchActivity : AppBaseActivity<ActivityPunchBinding, PunchViewModel>() {

    private val fragmentList = arrayListOf<Fragment>()

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_punch
    }

    override fun initViewModel(): PunchViewModel? {
        return viewModels<PunchViewModel>().value
    }

    override fun initView() {
        super.initView()
        initIndicator()
        initViewPager()
    }

    override fun initData() {
        super.initData()
        MyAMapUtils.init(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        MyAMapUtils.destroy()
    }

    /**
     * 初始化 指示器
     */
    private fun initIndicator() {
        val pagerTitles = arrayListOf(
            resources.getString(R.string.module_workbench_company_punch),
            resources.getString(R.string.module_workbench_outside_punch)
        )

        val commonNavigator = CommonNavigator(this)
        commonNavigator.isAdjustMode = false
        commonNavigator.adapter = object : CommonNavigatorAdapter() {

            override fun getCount(): Int {
                return pagerTitles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val simplePagerTitleView = SimplePagerTitleView(context)
                simplePagerTitleView.text = pagerTitles[index]
                simplePagerTitleView.textSize = 16.px.toFloat()
                simplePagerTitleView.normalColor = context.resources.getColor(R.color.black_333333)
                simplePagerTitleView.selectedColor = context.resources.getColor(R.color.red_fa4848)
                simplePagerTitleView.setOnClickListener {
                    binding.viewPager.currentItem = index
                }
                return simplePagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_EXACTLY
                indicator.lineWidth = 16.dp.toFloat()
                indicator.lineHeight = 2.dp.toFloat()
                indicator.roundRadius = 1.dp.toFloat()
                indicator.setColors(context.resources.getColor(R.color.red_fa4848))
                return indicator
            }
        }
        binding.indicator.navigator = commonNavigator
        ViewPagerHelper.bind(binding.indicator, binding.viewPager)
        binding.indicator.onPageSelected(0)
    }

    /**
     * 初始化 viewPager
     */
    private fun initViewPager() {
        if (fragmentList.isNotEmpty()){
            return
        }

        val  companyPunch = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PUNCH_COMPANY).navigation() as Fragment
        val outsidePunch = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_PUNCH_OUTSIDE).navigation() as Fragment
        fragmentList.add(companyPunch)
        fragmentList.add(outsidePunch)

        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.adapter = BaseFragmentPagerAdapter(supportFragmentManager, fragmentList)
        binding.viewPager.currentItem = 0
    }
}