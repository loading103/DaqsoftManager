package com.daqsoft.module_main.activity

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.databinding.ActivityDialogBinding
import com.daqsoft.module_main.viewmodel.DialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@Route(path = ARouterPath.Base.DIALOG)
class DialogActivity: AppBaseActivity<ActivityDialogBinding, DialogViewModel>() {

    @Autowired
    @JvmField
    var hint : String? = ""

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_dialog
    }

    override fun initViewModel(): DialogViewModel? {
        return viewModels<DialogViewModel>().value
    }


    override fun initData() {

        hint?.let {
            if (it.isNotBlank()){
                binding.content.text = hint
            }
        }
        setFinishOnTouchOutside(false)

        binding.determine.setOnClickListener {
            ARouter
                .getInstance()
                .build(ARouterPath.Mine.PAGER_LOGIN)
                .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                .navigation()
            finish()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (event?.keyCode == KeyEvent.KEYCODE_BACK ) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }



}