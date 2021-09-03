package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.module_project.repository.pojo.vo.ProjectFlow
import com.daqsoft.module_project.repository.pojo.vo.ProjectOwnerBean
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.module_project.widget.ProjectTypePopup
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.lxj.xpopup.XPopup
import timber.log.Timber
import java.util.*

class ProjectAddViewModel : ToolbarViewModel<ProjectRepository> {


    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.white_ffffff)
        setTitleTextColor(R.color.color_333333)
        setTitleText("添加项目")
    }


    /**
     * 业主
     */
    val ownerObservable = ObservableField<ProjectOwnerBean>()
    /**
     * 合作伙伴
     */
    val patnerObservable = ObservableField<ProjectOwnerBean>()
    /**
     * 选中的类型
     */
    val typeObservable = ObservableField<ProjectType>()

    /**
     * 选中的流程
     */
    val flowObservable = ObservableField<ProjectFlow>()
    /**
     * 项目名称
     */
    var detailedName = ObservableField<String>("")

    /**
     * 项目简称
     */
    var CommonName = ObservableField<String>("")
    /**
     * 项目等级
     */
    val levelLiveData = ObservableField<ProjectType>()
    /**
     * 获取类型
     */
    val typeLiveData = MutableLiveData<List<ProjectType>>()

    /**
     * 项目金额
     */
    var projectMoney = ObservableField<String>("")

    /**
     * 项目背景
     */
    val projectBgObservable = ObservableField<String>("")
    /**
     * 项目概况
     */
    val projectGkObservable = ObservableField<String>("")


    /**
     * 项目背景监
     */
    var maxBgNumber = ObservableField<String>("0/500")
    var maxGkNumber = ObservableField<String>("0/500")
    var textChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            maxBgNumber.set("0/500")
        }else{
            maxBgNumber.set(it.length.toString()+"/500")
        }
    })
    /**
     * 项目概况
     */
    var textGkChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            maxGkNumber.set("0/500")
        }else{
            maxGkNumber.set(it.length.toString()+"/500")
        }
    })



    fun getProjectType(){
        addSubscribe(
            model
                .getProjectType()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectType>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectType>>) {
                        t.data?.let {
                            typeLiveData.value = it
                        }
                    }
                })
        )
    }

    /**
     * 获取流程
     */
    val followLiveData = MutableLiveData<List<ProjectFlow>>()
    fun getProjectFlow(){
        addSubscribe(
            model
                .getProjectFlow()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectFlow>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectFlow>>) {
                        t.datas?.let {
                            followLiveData.value = it
                        }
                    }
                })
        )
    }


    val levelDatas :MutableList<ProjectType> = mutableListOf(
        ProjectType("五重一大",1),
        ProjectType("重大",2),
        ProjectType("一般",3)
    )


    /**
     * 选择业主
     */
    val onChooseOwner= BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Project.PAGER_CHOOSE_YEZHU).withBoolean("isOwner",true).navigation()
    })
    /**
     * 选择伙伴
     */
    val onChoosePater = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Project.PAGER_CHOOSE_YEZHU).withBoolean("isOwner",false).navigation()
    })
    /**
     * 确认修改
     */
    val onUndateData = BindingCommand<Unit>(BindingAction {
        if(typeObservable.get()?.name.isNullOrBlank()){
            ToastUtils.showLong("请选择项目类型")
            return@BindingAction
        }
        if(flowObservable.get()?.processName.isNullOrBlank()){
            ToastUtils.showLong("请选择项目流程")
            return@BindingAction
        }
        if (detailedName.get().isNullOrBlank()) {
            ToastUtils.showLong("请输入项目名称")
            return@BindingAction
        }
        if (CommonName.get().isNullOrBlank()) {
            ToastUtils.showLong("请输入项目简称")
            return@BindingAction
        }

        if(ownerObservable.get()?.customerName.isNullOrBlank()){
            ToastUtils.showLong("请选择业主")
            return@BindingAction
        }
        if(levelLiveData.get()?.name.isNullOrBlank()){
            ToastUtils.showLong("请选择项目等级")
            return@BindingAction
        }
        val param = HashMap<String, String>()
        if(!patnerObservable.get()?.partnerName.isNullOrBlank()){
            param["projectPartner"] = patnerObservable.get()?.id.toString()
        }

        param["projectGrade"] = levelLiveData.get()?.id.toString()
        param["processId"] = flowObservable.get()?.id.toString()
        param["projectCustomer"] = ownerObservable.get()?.id.toString()
        param["projectName"] = detailedName.get().toString()
        param["simpleName"] =  CommonName.get().toString()
        param["projectType"] = typeObservable.get()?.id.toString()
        param["projectAmount"] = projectMoney.get().toString()

        param["projectOverview"] = projectGkObservable.get().toString()
        param["projectBackgroud"] = projectBgObservable.get().toString()
        addSubscribe(
            model
                .SaveNewProject(param)
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        ToastUtils.showLong("添加成功")
                        LiveEventBus.get(LEBKeyGlobal.ADD_PROJECT_SUCESS).post(true)
                        finish()

                    }

                    override fun onFailT(t: AppResponse<Any>) {
                        super.onFailT(t)
                        dismissLoadingDialog()
                    }
                })
        )
    })
}