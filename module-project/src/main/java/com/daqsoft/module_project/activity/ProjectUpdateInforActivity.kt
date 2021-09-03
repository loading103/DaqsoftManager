package com.daqsoft.module_project.activity

import android.os.Bundle
import android.text.TextUtils
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
import com.daqsoft.module_project.databinding.ActivityProjectUpdateBaseInforBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectBaseInfor
import com.daqsoft.module_project.repository.pojo.vo.ProjectFlow
import com.daqsoft.module_project.repository.pojo.vo.ProjectOwnerBean
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.ProjectBaseInforViewModel
import com.daqsoft.module_project.viewmodel.ProjectUndateInforViewModel
import com.daqsoft.module_project.widget.ProjectTypePopup
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


/**
 * @describe 修改基本信息
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_UODATE_INFOR)
class ProjectUpdateInforActivity : AppBaseActivity<ActivityProjectUpdateBaseInforBinding, ProjectUndateInforViewModel>() {

    @JvmField
    @Autowired
    var detailbean : ProjectBaseInfor? = null

    @JvmField
    @Autowired
    var Id : String? = null

    lateinit var flowPopup : ProjectTypePopup

    lateinit var levelPopup : ProjectTypePopup

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_update_base_infor
    }

    override fun initViewModel(): ProjectUndateInforViewModel? {
        return viewModels<ProjectUndateInforViewModel>().value
    }

    override fun initView() {
        super.initView()
        flowPopup=ProjectTypePopup(this@ProjectUpdateInforActivity, "选择项目流程")
        levelPopup=ProjectTypePopup(this@ProjectUpdateInforActivity, "选择项目等级")
        initClickEvent()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        LiveEventBus.get(LEBKeyGlobal.CHOOSE_OWNER, ProjectOwnerBean::class.java).observe(this, Observer {
            if(it.isOwner!!){
                viewModel.ownerObservable.set(it)
            }else{
                viewModel.patnerObservable.set(it)
            }
        })
    }
    /**
     * 获取费用类型点击事件
     */
    override fun initData() {
        super.initData()
        detailbean?.let {
            if(!TextUtils.isEmpty(it.processName)){
                viewModel.flowObservable.set(ProjectFlow(id = 0,processName = it.processName))
            }
            if(!TextUtils.isEmpty(it.projectName)){
                viewModel.detailedName.set(it.projectName)
            }

            if(!TextUtils.isEmpty(it.customerName)){
                viewModel.ownerObservable.set(ProjectOwnerBean(customerName = it.customerName,contactUser = it.customerUser))
            }
            if(!TextUtils.isEmpty(it.processName)){
                viewModel.levelLiveData.set(ProjectType(it.projectGrade!!,0))
            }
            if(!TextUtils.isEmpty(it.projectAmount)){
                viewModel.projectMoney.set(it.projectAmount)
            }
            if(!TextUtils.isEmpty(it.projectBackgroud) && it.projectBackgroud!="null"){
                viewModel.projectBgObservable.set(it.projectBackgroud)
            }
            if(!TextUtils.isEmpty(it.projectOverview) && it.projectBackgroud!="null"){
                viewModel.projectGkObservable.set(it.projectOverview)
            }
            viewModel.getProjectBaseInfor(Id)
        }
        viewModel.getProjectFlow()
    }
    /**
     * 点击监听事件
     */
    private fun initClickEvent() {
        binding.tvFlow.setOnClickListener {
            showProjectFlowPopup()
        }
        binding.tvLevelRight.setOnClickListener {
            showProjectLevelPopup()
        }
    }

    /**
     * 选择项目流程
     */
    private fun showProjectFlowPopup() {
        XPopup
            .Builder(this)
            .asCustom(flowPopup.apply {
                setItemOnClickListener(object : ProjectTypePopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        viewModel.flowObservable.set( viewModel.followLiveData.value!![position])
                        setSelectedPosition(position)
                    }
                })
                setData(viewModel.followLiveData.value?.map { it.processName!! }?.toMutableList() ?: arrayListOf())
            })
            .show()
    }

    /**
     * 选择项目等级
     */
    private fun showProjectLevelPopup() {
        XPopup
            .Builder(this)
            .asCustom(levelPopup.apply {
                setItemOnClickListener(object : ProjectTypePopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        viewModel.levelLiveData.set( viewModel.levelDatas[position])
                        setSelectedPosition(position)
                    }
                })
                setData(viewModel.levelDatas?.map { it.name!! }?.toMutableList())
            })
            .show()
    }
}