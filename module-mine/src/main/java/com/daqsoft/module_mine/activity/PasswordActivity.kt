package com.daqsoft.module_mine.activity

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityPasswordBinding
import com.daqsoft.module_mine.viewmodel.PasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 20/11/2020 上午 9:26
 * @author zp
 * @describe
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_PASSWORD)
class PasswordActivity : AppBaseActivity<ActivityPasswordBinding, PasswordViewModel>() {

    @JvmField
    @Autowired
    var type : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_password
    }

    override fun initViewModel(): PasswordViewModel? {
        return viewModels<PasswordViewModel>().value
    }

    override fun initData() {
        viewModel.isSecondaryPassword.set(type == resources.getString(R.string.module_mine_secondary_password))
        viewModel.setTitleText(type)

    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.oldPasswordVisible.observe(this, Observer {
            if (it){
                binding.oldPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.oldPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.oldPassword.setSelection(binding.oldPassword.text.length)
        })

        viewModel.newPasswordVisible.observe(this, Observer {
            if (it){
                binding.newPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.newPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.newPassword.setSelection(binding.newPassword.text.length)
        })

        viewModel.confirmPasswordVisible.observe(this, Observer {
            if (it){
                binding.confirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }else{
                binding.confirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.confirmPassword.setSelection(binding.confirmPassword.text.length)
        })
    }
}