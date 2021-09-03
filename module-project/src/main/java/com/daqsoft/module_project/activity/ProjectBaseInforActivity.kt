package com.daqsoft.module_project.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.ActivityProjectAccountBinding
import com.daqsoft.module_project.viewmodel.ProjectBaseInforViewModel
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint


/**
 * @describe 基本信息
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_BASE_INFOR)
class ProjectBaseInforActivity : AppBaseActivity<ActivityProjectAccountBinding, ProjectBaseInforViewModel>() {

    @JvmField
    @Autowired
    var  Id : String? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_base_infor
    }

    override fun initViewModel(): ProjectBaseInforViewModel? {
        return viewModels<ProjectBaseInforViewModel>().value
    }

    override fun initView() {
        super.initView()
        initClickEvent()
    }

    /**
     * 获取费用类型点击事件
     */
    override fun initData() {
        super.initData()
        viewModel.getProjectBaseInfor(Id!!)

    }

    override fun initViewObservable() {
        super.initViewObservable()
        // 修改项目成功
        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_SUCESS, Boolean::class.java).observe(this,
            Observer {
                viewModel.getProjectBaseInfor(Id!!)
            })
    }
    /**
     * 点击监听事件
     */
    private fun initClickEvent() {
    }


}