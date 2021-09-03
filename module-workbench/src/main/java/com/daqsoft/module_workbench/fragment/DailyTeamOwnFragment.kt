package com.daqsoft.module_workbench.fragment

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.ScaleTransitionPagerTitleView
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.activity.DailyTeamActivity
import com.daqsoft.module_workbench.adapter.MyViewPagerAdapter
import com.daqsoft.module_workbench.databinding.ActivityDailyPerosnBinding
import com.daqsoft.module_workbench.databinding.ActivityDailyTeamOtherBinding
import com.daqsoft.module_workbench.databinding.ActivityDailyTeamOwnerBinding
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.DataTime
import com.daqsoft.module_workbench.utils.AnimatorUtil
import com.daqsoft.module_workbench.viewmodel.DailyTeamOtherViewModel
import com.daqsoft.module_workbench.widget.ChooseTimePopup
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BasePopupView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_popup_choose_time.view.*
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView

/**
 * @package name：com.daqsoft.module_project.di
 * @author qql
 * @describe 项目
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_OWN)
class DailyTeamOwnFragment : AppBaseFragment<ActivityDailyTeamOwnerBinding, DailyTeamOtherViewModel>() {

    var datas :MutableList<String> = arrayListOf()

    @JvmField
    @Autowired
    var menuPermissionCover: MenuPermissionCover?=null

    lateinit var  animatorUtil: AnimatorUtil

    var startTimes:String?=""

    var endTimes:String?=""

    var fragments : MutableList<Fragment> = mutableListOf()

    var childFragment1: Fragment? = null
    var childFragment2: Fragment? = null
    var childFragment3: Fragment? = null
//    var childFragment4: Fragment? = null

    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return R.layout.activity_daily_team_owner
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): DailyTeamOtherViewModel? {
        return activity?.viewModels<DailyTeamOtherViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        viewModel.setMenuId((activity as DailyTeamActivity). Id)
        viewModel.isOwn=true
        animatorUtil=AnimatorUtil(activity)
        LiveEventBus.get(LEBKeyGlobal.SHOWTITLE).observeForever {
            val show = it as Boolean
            if(show){
                if( binding.llTop?.visibility==View.GONE){
                    animatorUtil.performAnim(true,  binding.llTop)
                }
            }else{
                if( binding.llTop?.visibility==View.VISIBLE) {
                    animatorUtil.performAnim(false,  binding.llTop)
                }
            }
        }
        binding.llTop.setOnClickListener {
            showTopPopup(binding.llTop)
        }


    }


    override fun initData() {
        super.initData()
        datas.add("全部")
        datas.add("未提交")
        datas.add("已提交")

        childFragment1 = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_OWN_ALL).withString("Id", "1").navigation() as Fragment
        fragments.add(childFragment1!!)
        childFragment2 = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_OWN_ALL).withString("Id", "2").navigation() as Fragment
        fragments.add(childFragment2!!)
        childFragment3 = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_OWN_ALL).withString("Id", "3").navigation() as Fragment
        fragments.add(childFragment3!!)

//        if(menuPermissionCover?.create!!){
//            datas.add("已发布")
//            childFragment4 = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_OWN_ALL).withString("Id", "4").navigation() as Fragment
//            fragments.add(childFragment4!!)
//        }
        binding.magicIndicator.navigator=setNaviga(activity as DailyTeamActivity,datas,binding.viewPager)
        var pagerAdapter = MyViewPagerAdapter((activity as FragmentActivity).supportFragmentManager, fragments)
        binding.viewPager.adapter = pagerAdapter
        ViewPagerHelper.bind(binding.magicIndicator, binding.viewPager)
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.finishedLiveData.observe(this, Observer {
            activity?.finish()
        })
        viewModel.chooseTimeLiveData.observe(this, Observer {
            showTopPopup(binding.llTop)


        })
    }


    fun setNaviga(context: Context, datas: List<String>,viewpager: ViewPager) : CommonNavigator {
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode=true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return datas?.size ?: 0
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.textSize = 16f
                colorTransitionPagerTitleView.normalColor =  context.resources.getColor(R.color.gray_666666)
                colorTransitionPagerTitleView.selectedColor = context.resources.getColor(R.color.color_333333)
                colorTransitionPagerTitleView.text = datas[index]
                colorTransitionPagerTitleView.setOnClickListener{
                    viewpager.setCurrentItem(index, true)
                }
                return colorTransitionPagerTitleView
            }
            override fun getIndicator(context: Context): IPagerIndicator {
                return LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                    lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
                    roundRadius = UIUtil.dip2px(context, 6.0).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(2.0f)
                    setColors(context.resources.getColor(R.color.red_fa4848))
                }
            }
        }
        return commonNavigator;
    }


    /**
     * 展示顶部类型展示
     */

    val listPopupProject : ChooseTimePopup by lazy {
        ChooseTimePopup(activity as DailyTeamActivity)
    }
    var shouldShow:Boolean=true
    fun showTopPopup(view: View){
        XPopup
            .Builder(activity)
            .atView(view)
            .asCustom(listPopupProject.apply {
                setOnChooseListerer(object : ChooseTimePopup.ChooseListerer {
                    override fun chooseTime(startTime: String, endTime: String) {
                        if (endTime.isNullOrBlank()) {
                            viewModel.chooseTag.set(startTime)
                        } else {
                            viewModel.chooseTag.set("$startTime~$endTime")
                        }
                        startTimes=startTime
                        endTimes=endTime
                        viewModel.setTimeData(startTime, endTime)
                        // 处理还没初始化的bug
                        (childFragment3 as  DailyTeamOwnChildFragment)?.startTimes=startTime
                        (childFragment3 as  DailyTeamOwnChildFragment)?.endTimes=endTime
//                        (childFragment4 as  DailyTeamOwnChildFragment)?.startTimes=startTime
//                        (childFragment4 as  DailyTeamOwnChildFragment)?.endTimes=endTime

                        LiveEventBus.get(LEBKeyGlobal.CHOOST_TIME).post(DataTime(startTime, endTime))
                        animatorUtil.performAnim1(false,  binding.llTop)
                    }
                })
                setOnDissmissListerer(object :ChooseTimePopup.DissmissListerer{
                    override fun dissmissListerer() {
                        if(!shouldShow){
                            animatorUtil.performAnim1(false,  binding.llTop)
                        }
                    }
                })
            }).show()

        if(binding.llTop.visibility==View.GONE){
            shouldShow=false
            animatorUtil.performAnim1(true,  binding.llTop)
        }else{
            shouldShow=true
        }
    }

}