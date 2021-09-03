package com.daqsoft.module_main.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.adapter.WelcomeBannerAdapter
import com.daqsoft.module_main.databinding.ActivityWelcomeBinding
import com.daqsoft.module_main.repository.pojo.vo.MyNotificationExtra
import com.daqsoft.module_main.viewmodel.WelcomeViewModel
import com.daqsoft.module_main.widget.MyRoundLinesIndicator
import com.jakewharton.rxbinding4.view.visibility
import com.youth.banner.config.IndicatorConfig
import com.youth.banner.indicator.RoundLinesIndicator
import com.youth.banner.listener.OnPageChangeListener
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_main.activity
 * @date 21/12/2020 上午 11:00
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Main.PAGER_WELCOME)
class WelcomeActivity : AppBaseActivity<ActivityWelcomeBinding, WelcomeViewModel>() {

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_welcome
    }

    override fun initViewModel(): WelcomeViewModel? {
        return viewModels<WelcomeViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.adapterLiveData.observe(this, Observer {
            if (it){
                initBanner()
                return@Observer
            }
            jumpPage()
        })

    }

    /**
     * 初始化  banner
     */
    private fun initBanner() {
        val data = arrayListOf<Int>(
            R.mipmap.welcome_image_1,
            R.mipmap.welcome_image_2,
            R.mipmap.welcome_image_3,
            R.mipmap.welcome_image_4,
            R.mipmap.welcome_image_5
        )

        binding.banner.apply {
            setAdapter(WelcomeBannerAdapter(data).apply {
                setJoinOnClickListener(object : WelcomeBannerAdapter.JoinOnClickListener{
                    override fun joinOnClick() {
                        DataStoreUtils.put(DSKeyGlobal.FIRST,false)
                        jumpPage()
                    }
                })
            },false)
            isAutoLoop(false)
            indicator = MyRoundLinesIndicator(this@WelcomeActivity)
            setIndicatorRadius(4.dp)
            setIndicatorNormalColor(resources.getColor(R.color.gray_d4d4d4))
            setIndicatorSelectedColor(resources.getColor(R.color.red_fa4848))
            setIndicatorNormalWidth(8.dp)
            setIndicatorSelectedWidth(16.dp)
            setIndicatorHeight(8.dp)
            setIndicatorSpace(6.dp)
            setIndicatorMargins(IndicatorConfig.Margins(0,0,0,130.dp))

            addOnPageChangeListener(object : OnPageChangeListener{
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                        if (position == data.size -1){
                            (indicator as MyRoundLinesIndicator).visibility = View.GONE
                        }else{
                            (indicator as MyRoundLinesIndicator).visibility = View.VISIBLE
                        }
                }

                override fun onPageScrollStateChanged(state: Int) {
                }

            })
        }

    }


    /**
     * 页面跳转
     */
    private fun jumpPage(){
        val usrId = DataStoreUtils.getInt(DSKeyGlobal.USER_ID)
        if (usrId != 0){
            ARouter
                .getInstance()
                .build(ARouterPath.Main.PAGER_MAIN)
                .withBundle("notifyBundle",intent.getBundleExtra("notifyBundle"))
                .navigation()
        }else{
            ARouter
                .getInstance()
                .build(ARouterPath.Mine.PAGER_LOGIN)
                .navigation()
        }
        finish()
    }
}