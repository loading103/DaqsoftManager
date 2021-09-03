package com.daqsoft.module_mine.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daqsoft.library_base.BuildConfig
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityLoginBinding
import com.daqsoft.module_mine.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 6/11/2020 下午 2:01
 * @author zp
 * @describe 登录
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_LOGIN)
class LoginActivity : AppBaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_login
    }

    override fun initViewModel(): LoginViewModel? {
        return viewModels<LoginViewModel>().value
    }

    override fun initData() {
        viewModel.getCompanyInfo()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.passwordVisible.observe(this, Observer {
            if (it) {
                binding.password.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.password.setSelection(binding.password.text.length)
        })

        viewModel.bothHaveData.observe(this, Observer {
            if (it.first && it.second) {
                binding.logIn.isEnabled = true
                binding.logIn.helper.backgroundColorNormal = resources.getColor(R.color.red_fa4848)
            } else {
                binding.logIn.isEnabled = false
                binding.logIn.helper.backgroundColorNormal = resources.getColor(R.color.red_ffb0b0)
            }
        })

        viewModel.verifyBitmap.observe(this, Observer {
            Glide
                .with(this)
                .load(it)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.verifyCodeImage)
        })
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed()
            true
        } else {// 如果不是back键正常响应
            super.onKeyDown(keyCode, event)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if(binding.accountNumber.text.isNotBlank() && binding.password.text.isNotBlank()){
            binding.logIn.isEnabled = true
        }
    }
}