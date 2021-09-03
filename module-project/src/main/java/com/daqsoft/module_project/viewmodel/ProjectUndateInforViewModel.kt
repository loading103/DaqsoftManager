package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.repository.pojo.vo.*
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import org.jsoup.select.Evaluator
import java.text.SimpleDateFormat
import java.util.*

class ProjectUndateInforViewModel : ToolbarViewModel<ProjectRepository> {


    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.white_ffffff)
        setTitleTextColor(R.color.color_333333)
        setTitleText("修改基本信息")
    }


    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()


    /**
     * 业主
     */
    val ownerObservable = ObservableField<ProjectOwnerBean>()
    /**
     * 合作伙伴
     */
    val patnerObservable = ObservableField<ProjectOwnerBean>()

    /**
     * 选中的流程
     */
    val flowObservable = ObservableField<ProjectFlow>()
    /**
     * 项目名称
     */
    var detailedName = ObservableField<String>("")
    /**
     * 项目等级
     */
    val levelLiveData = ObservableField<ProjectType>()

    /**
     * 项目简称
     */
    val simpleNameLiveData = ObservableField<String>()

    var ProjectId :String ?=null

    /**
     * 项目金额
     */
    var projectMoney = ObservableField<String>("")

    /**
     * 项目背景
     */
    val projectBgObservable = ObservableField<String>()

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
    val projectGkObservable = ObservableField<String>()

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

    fun getProjectBaseInfor(id: String?) {
        ProjectId=id
        addSubscribe(
            model
                .getProjectBaseInfors(id!!)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectBaseInfors>>() {
                    override fun onSuccess(t: AppResponse<ProjectBaseInfors>) {
                        t.data?.let {
                            if(!it.processId.isNullOrBlank()){
                                val processName = flowObservable.get()?.processName
                                flowObservable.set(ProjectFlow(id = it.processId.toInt(),processName = processName))
                            }
                            if(!TextUtils.isEmpty(it.projectPartner)){
                                patnerObservable.set(ProjectOwnerBean(id=it.projectPartner,partnerName =it.partnerName ))
                            }
                            if(!TextUtils.isEmpty(it.projectCustomer)){
                                val customerName = ownerObservable.get()?.customerName
                                ownerObservable.set(ProjectOwnerBean(id=it.projectCustomer,customerName = customerName ))
                            }
                            if(!TextUtils.isEmpty(it.projectGrade)){
                                val levelname = levelLiveData.get()?.name
                                levelLiveData.set(ProjectType( name = levelname!!,id = it.projectGrade.toInt()))
                            }
                            if(!TextUtils.isEmpty(it.simpleName)){
                                simpleNameLiveData.set(it.simpleName)
                            }

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
        if(flowObservable.get()?.processName.isNullOrBlank()){
            ToastUtils.showLong("请选择项目流程")
            return@BindingAction
        }
        if (detailedName.get().isNullOrBlank()) {
            ToastUtils.showLong("请输入项目名称")
            return@BindingAction
        }

        if (simpleNameLiveData.get().isNullOrBlank()) {
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
        param["id"] = ProjectId.toString()
        param["projectGrade"] = levelLiveData.get()?.id.toString()
        param["processId"] = flowObservable.get()?.id.toString()
        param["projectCustomer"] = ownerObservable.get()?.id.toString()
        param["projectName"] = detailedName.get().toString()
        param["simpleName"] = simpleNameLiveData.get().toString()
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
                        ToastUtils.showLong("修改成功")
                        dismissLoadingDialog()
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