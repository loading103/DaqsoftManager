package com.daqsoft.module_project.activity

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.adapter.ProjectAdapter
import com.daqsoft.module_project.adapter.ProjectDetailFlowAdapter
import com.daqsoft.module_project.adapter.ProjectFlowAdapter
import com.daqsoft.module_project.databinding.ActivityProjectFlowBinding
import com.daqsoft.module_project.databinding.RecyclerviewProjectFlowBinding
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.viewmodel.ProjectFlowViewModel
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectDetailFlowViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 19/11/2020 下午 3:27
 * @author zp
 * @describe 修改住址
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_FLOW)
class ProjectFlowActivity : AppBaseActivity<ActivityProjectFlowBinding, ProjectFlowViewModel>() {


    @JvmField
    @Autowired
    var projectId : String = ""

    @Inject
    lateinit var projectDetailAdapter : ProjectFlowAdapter

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_flow
    }

    override fun initViewModel(): ProjectFlowViewModel? {
        return viewModels<ProjectFlowViewModel>().value
    }

    override fun initView() {
        super.initView()
        viewModel.projectId=projectId
    }
    override fun initData() {
        super.initData()
//        var processJson = intent.getStringExtra("dataKey")
//        val type = object : TypeToken<List<Processe>>() {}.type
//        val coverTemplateContent = Gson().fromJson<List<Processe>>(processJson, type)
//        viewModel.addData(coverTemplateContent)

        viewModel.getRequestProjectDetail(projectId.toInt(),true)
        binding.recyclerView.adapter = projectDetailAdapter.apply {
            projectIds=projectId
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()
        // 进入网页刷新该界面
        LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).observeForever {
            viewModel.getRequestProjectDetail(projectId.toInt(),false)
        }
        viewModel.selectPosition.observeForever {
           binding.recyclerView?.scrollToPosition(it)
        }

    }

}