package com.daqsoft.module_workbench.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityDailyTeamBinding
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.viewmodel.DailyTeamViewModel
import com.daqsoft.module_workbench.widget.BottomTab
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import me.majiajie.pagerbottomtabstrip.PageNavigationView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectedListener
import kotlin.properties.Delegates


@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_TEAM)
class DailyTeamActivity : AppBaseActivity<ActivityDailyTeamBinding, DailyTeamViewModel>(){


    @JvmField
    @Autowired
    var Id:String? = null
    @JvmField
    @Autowired
    var menuPermissionCover: MenuPermissionCover?=null
    @JvmField
    @Autowired
    var haveMemberReports: Boolean?=false
    @JvmField
    @Autowired
    var haveTeamReports: Boolean?=false




    private var mFragments: ArrayList<Fragment> = arrayListOf()

    private var bottomTabItemMap : HashMap<String,BaseTabItem> ? = null

    var currentIndex by Delegates.notNull<Int>()

    var initdex: Int=0

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): DailyTeamViewModel? {
        return viewModels<DailyTeamViewModel>().value
    }
    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_daily_team
    }

    override fun initView() {
        super.initView()

        initBottomTab()
        initFragment()
    }

    override fun initData() {
    }

    override fun initViewObservable() {
        super.initViewObservable()

        // 当时是否处于 任务页面
        LiveEventBus.get(LEBKeyGlobal.CURRENT_IN_TASK_LIST,Boolean::class.java).observe(this, Observer {
            binding.pageNavigation.visibility = if (it) View.VISIBLE else View.GONE
        })


    }

    /**
     * 初始化页面
     */
    private fun initFragment() {
        if (mFragments.isNotEmpty()){
            return
        }
        //这里需要通过ARouter获取，不能直接new,因为在组件独立运行时，宿主app是没有依赖其他组件，所以new不到其他组件的Fragment)
        val homeFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_PERSON).navigation() as Fragment
        mFragments.add(homeFragment)

        // 可以查看别人
        if(this!!.haveTeamReports!!) {
            if(menuPermissionCover?.update!!){
                val taskFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_ALL).navigation() as Fragment
                mFragments.add(taskFragment)
            }else{
                val taskFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_OWN).withParcelable("menuPermissionCover",menuPermissionCover).navigation() as Fragment
                mFragments.add(taskFragment)
            }
        }

        if(this!!.haveMemberReports!!){
            val projectFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_MEMBER).navigation() as Fragment
            mFragments.add(projectFragment)
        }

        // 个人数据和团队数据统计
        if(!this!!.haveTeamReports!! && !this!!.haveMemberReports!!){
            val dataFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DATA).navigation() as Fragment
            mFragments.add(dataFragment)
        }else{
            val dataFragment = ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DATA_TEAM)
                .withBoolean("haveTeamReports" ,this!!.haveTeamReports!!)
                .withBoolean("haveMemberReports" ,this!!.haveMemberReports!!)
                .navigation() as Fragment
            mFragments.add(dataFragment)
        }

        // 默认选中第一个
        supportFragmentManager.beginTransaction().apply {
            add(R.id.frame_layout, mFragments[initdex])
            commit()
            currentIndex =initdex
        }
    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomTab() {
        if(bottomTabItemMap == null){
            bottomTabItemMap = hashMapOf()
        }
        bottomTabItemMap?.clear()

        val bottomTabItemHome = createBottomTabItem(
            R.mipmap.rb_tab_grrb_normal,
            R.mipmap.rb_tab_grrb_selected,
            "个人日报"
        )
        bottomTabItemMap!!["home"] = bottomTabItemHome
        val bottomTabItemMission = createBottomTabItem(
            R.mipmap.rb_tab_tdrb_normal,
            R.mipmap.rb_tab_tdrb_selected,
            "团队日报"
        )
        bottomTabItemMap!!["mission"] = bottomTabItemMission

        val bottomTabItemProject = createBottomTabItem(
            R.mipmap.rb_tab_cyrb_normal,
            R.mipmap.rb_tab_cyrb_selected,
            "成员日报"
        )
        bottomTabItemMap!!["member"] = bottomTabItemMission

        val bottomTabItemData = createBottomTabItem(
            R.mipmap.rb_tab_rbsj_normal,
            R.mipmap.rb_tab_rbsj_selected,
            "日报数据"
        )
        bottomTabItemMap!!["data"] = bottomTabItemData


        val customBuilder : PageNavigationView.CustomBuilder = binding.pageNavigation.custom()
        customBuilder.addItem(bottomTabItemHome)

        if(this!!.haveTeamReports!!){

            customBuilder.addItem(bottomTabItemMission)
        }

        if(this!!.haveMemberReports!!){
            customBuilder.addItem(bottomTabItemProject)
        }
        customBuilder.addItem(bottomTabItemData)

        if(this!!.haveTeamReports!! && this!!.haveMemberReports!!){
            initdex=1
        }else{
            initdex=0
        }



        customBuilder
            .build()
            .apply {
                setSelect(initdex)
                addTabItemSelectedListener(object : OnTabItemSelectedListener {
                    override fun onSelected(index: Int, old: Int) {
                        try {
                            currentIndex = index
                            val previousFragment = mFragments[old]
                            val currentFragment = mFragments[index]
                            val transaction = supportFragmentManager.beginTransaction()
                            transaction
                                .hide(previousFragment)
                            if (!currentFragment.isAdded) {
                                transaction
                                    .add(R.id.frame_layout, currentFragment)
                                    .commit()
                            } else {
                                transaction
                                    .show(currentFragment)
                                    .setMaxLifecycle(currentFragment, Lifecycle.State.RESUMED)
                                    .commit()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun onRepeat(index: Int) {}
                })
            }

    }

    /**
     * 创建底部导航item
     * @param defaultDrawable Int
     * @param checkedDrawable Int
     * @param text String
     * @param selectSingle Boolean
     * @return BaseTabItem
     */
    private fun createBottomTabItem(defaultDrawable: Int, checkedDrawable: Int, text: String,selectSingle:Boolean? = false,size:Int? = 0): BaseTabItem {
        val normalItemView = BottomTab(this)
        normalItemView.initialize(defaultDrawable, checkedDrawable)
        normalItemView.title = text
        selectSingle?.let {
            normalItemView.setSelectedSingleDrawableFlag(selectSingle,size?:0)
        }
        return normalItemView
    }

}


