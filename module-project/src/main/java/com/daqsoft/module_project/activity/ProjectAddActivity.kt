package com.daqsoft.module_project.activity

import android.app.Application
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.ObservableField
import androidx.lifecycle.Observer
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.ActivityProjectAccountBinding
import com.daqsoft.module_project.databinding.ActivityProjectAddBinding
import com.daqsoft.module_project.repository.pojo.vo.ProjectOwnerBean
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.ProjectAddViewModel
import com.daqsoft.module_project.viewmodel.ProjectBaseInforViewModel
import com.daqsoft.module_project.viewmodel.ProjectUndateInforViewModel
import com.daqsoft.module_project.widget.AccountTypePopup
import com.daqsoft.module_project.widget.ProjectTypePopup
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview_project_dynamics_item.view.*
import timber.log.Timber


/**
 * @describe 新增项目
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_ADD)
class ProjectAddActivity : AppBaseActivity<ActivityProjectAddBinding, ProjectAddViewModel>() {

    @JvmField
    @Autowired
    var  Id : String? = null

    /**
     * 选择项目类型
     */
    lateinit var accountTypePopup : ProjectTypePopup

    lateinit var flowPopup : ProjectTypePopup

    lateinit var levelPopup : ProjectTypePopup

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_add
    }

    override fun initViewModel(): ProjectAddViewModel? {
        return viewModels<ProjectAddViewModel>().value
    }

    override fun initView() {
        super.initView()
        accountTypePopup=ProjectTypePopup(this@ProjectAddActivity, "选择项目类型")
        flowPopup=ProjectTypePopup(this@ProjectAddActivity, "选择项目流程")
        levelPopup=ProjectTypePopup(this@ProjectAddActivity, "选择项目等级")
        initClickEvent()
    }



    /**
     * 获取费用类型点击事件
     */
    override fun initData() {
        super.initData()
        viewModel.getProjectType()
        viewModel.getProjectFlow()
    }
    /**
     * 点击监听事件
     */
    private fun initClickEvent() {
        binding.tvType.setOnClickListener {
            showProjectTypePopup()
        }
        binding.tvFlow.setOnClickListener {
            showProjectFlowPopup()
        }
        binding.tvLevelRight.setOnClickListener {
            showProjectLevelPopup()
        }
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
     * 选择项目类型
     */
    private fun showProjectTypePopup() {
        XPopup
            .Builder(this)
            .asCustom(accountTypePopup.apply {
                setItemOnClickListener(object : ProjectTypePopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        viewModel.typeObservable.set( viewModel.typeLiveData.value!![position])
                        setSelectedPosition(position)
                    }
                })
                setData(viewModel.typeLiveData.value?.map { it.name!! }?.toMutableList() ?: arrayListOf())
            })
            .show()
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