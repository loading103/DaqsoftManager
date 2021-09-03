package com.daqsoft.module_project.activity

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.adapter.ProjectDetailAdapter
import com.daqsoft.module_project.databinding.ActivityProjectDetailBinding
import com.daqsoft.module_project.repository.pojo.vo.ProductBean
import com.daqsoft.module_project.viewmodel.ProjectDetailViewModel
import com.daqsoft.module_project.widget.ProjectDetailPopup
import com.daqsoft.module_project.widget.ProjectRedPopup
import com.daqsoft.module_project.widget.WaterMarkBgView
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_project_detail.*
import javax.inject.Inject


/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 19/11/2020 下午 3:27
 * @author zp
 * @describe 修改住址
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_DETAIL)
class ProjectDetailActivity : AppBaseActivity<ActivityProjectDetailBinding, ProjectDetailViewModel>() {

    @Inject
    lateinit var projectDetailAdapter: ProjectDetailAdapter

    @JvmField
    @Autowired
    var id : String = ""

    @JvmField
    @Autowired
    var typeColor : String = ""


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_detail
    }

    override fun initViewModel(): ProjectDetailViewModel? {
        return viewModels<ProjectDetailViewModel>().value
    }

    override fun initData() {
        super.initData()

        viewModel.getRequestProjectDetail(id.toInt(),true)
        viewModel.getMenus()
        // 添加水印
        binding.recyclerView.background = WaterMarkBgView(this,viewModel.employeeInfo.employeeName, -30, 14)
        binding.recyclerView.adapter = projectDetailAdapter
        // 为产品类型添加颜色
        projectDetailAdapter.typeColor=typeColor
        projectDetailAdapter.projectId=id
    }

    override fun initViewObservable() {
        super.initViewObservable()
        // 进入网页刷新该界面
        LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).observeForever {
            viewModel.getRequestProjectDetail(id.toInt(),it as Boolean)
        }

        viewModel.detailData?.observe(this, Observer {


            ll_header.setData(it)
            // ==1为对外业务，才会有顶部流程
            if(it.projectType==1){
                ll_header.visibility=View.VISIBLE
                ll_scroll.visibility=View.VISIBLE
            }
            if(it.lastNote.isNullOrBlank() || it.lastNote=="0"){
                tv_project_dynamic.setText("项目动态(${it.noteTotalCount})")
            }else{
                tv_project_dynamic.setText("项目动态(${it.noteTotalCount}),已有${it.lastNote}天未更新动态")
            }

        })


        viewModel.moreLiveData.observe(this, Observer {
            showBottomMenuPopup()
        })

        viewModel.showRedLiveData.observe(this, Observer {
            if(it==null){
                return@Observer
            }
            showRedPopup(it)
        })

        LiveEventBus.get(LEBKeyGlobal.DAILY_SEND_SUCCESSFULLY,Boolean::class.java).observe(this,
            Observer {
                initData()
            })

        LiveEventBus.get(LEBKeyGlobal.PROJECT_DYNAMIC_SENT_SUCCESSFULLY,Boolean::class.java).observe(this, Observer {
            initData()
        })

        // 修改项目成功
        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_SUCESS, Boolean::class.java).observe(this,
            Observer {
                initData()
            })
    }


    /**
     * 展示 底部  菜单
     */
    var projectRedPopup : ProjectRedPopup ?=null

    private fun showRedPopup(it: ProductBean) {
        projectRedPopup=ProjectRedPopup(this@ProjectDetailActivity,it!!)
        XPopup
            .Builder(this)
            .asCustom(projectRedPopup)
            .show()
    }


    /**
     * 展示 底部  菜单
     */
    val projectMorePopup : ProjectDetailPopup by lazy { ProjectDetailPopup(this, "项目详情") }

    fun showBottomMenuPopup(){
        val datas = arrayListOf<Pair<Int,String>>()
        datas.add(Pair(R.mipmap.xmxq_more_add, "添加任务"))
        datas.add(Pair(R.mipmap.xmxq_more_jbxx, "基本信息"))
        datas.add(Pair(R.mipmap.xmxq_more_xmcy, "项目成员"))
        datas.add(Pair(R.mipmap.xmxq_more_wjk, "文件库"))
        XPopup
            .Builder(this)
            .asCustom(projectMorePopup.apply {
                setItemOnClickListener(object : ProjectDetailPopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        when(position){
                            0->{
                                var website:String= HttpGlobal.DAILY_ADD_TASK+viewModel.detailData.value?.id.toString()
                                ARouter
                                    .getInstance()
                                    .build(ARouterPath.Web.PAGER_WEB)
                                    .withString("url",website)
                                    .navigation()
                            }
                            1->{
                                ARouter.getInstance().build( ARouterPath.Project.PAGER_PROJECT_BASE_INFOR).withString("Id",
                                    viewModel.detailData.value?.id.toString()
                                ).navigation()
                            }
                            2->{
                                var website:String= HttpGlobal.DAILY_TASK_MEMBER+viewModel.detailData.value?.id.toString()
                                ARouter
                                    .getInstance()
                                    .build(ARouterPath.Web.PAGER_WEB)
                                    .withString("url",website)
                                    .navigation()
                            }
                            3->{
                                ARouter.getInstance().build( ARouterPath.Workbench.PAGER_DEPT_DOC_PRO)
                                    .withString("Id",viewModel.menuObser.get()?.id.toString())
                                    .withString("projectId",  viewModel.detailData.value?.id.toString())
                                    .navigation()
                            }
                        }
                    }
                })
                setData(datas)
            })
            .show()
    }

}