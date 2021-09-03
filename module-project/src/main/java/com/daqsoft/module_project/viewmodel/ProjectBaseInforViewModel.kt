package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.repository.pojo.vo.MoneyTypeBean
import com.daqsoft.module_project.repository.pojo.vo.Processe
import com.daqsoft.module_project.repository.pojo.vo.ProjectBaseInfor
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import java.text.SimpleDateFormat
import java.util.*

class ProjectBaseInforViewModel : ToolbarViewModel<ProjectRepository> {


    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.white_ffffff)
        setTitleTextColor(R.color.color_333333)
        setTitleText("基本信息")
        setRightTextTxt("修改")
        setRightTextColor(R.color.red_fa4848)
        setRightTextVisible(View.VISIBLE)
    }


    override fun rightTextOnClick() {
        super.rightTextOnClick()
        ARouter.getInstance()
            .build( ARouterPath.Project.PAGER_PROJECT_UODATE_INFOR)
            .withParcelable("detailbean",databean)
            .withString("Id",Id)
            .navigation()
    }


    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    /**
     * 类型
     */
    val typeLiveData = MutableLiveData<List<MoneyTypeBean>>()

    /**
     * 获取基本信息
     */

    val dataObservable :ObservableField<ProjectBaseInfor> = ObservableField()

    var databean :ProjectBaseInfor ?= null

    var Id :String ?= null
    fun getProjectBaseInfor(id: String) {
        Id=id
        addSubscribe(
            model
                .getProjectBaseInfor(id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectBaseInfor>>() {
                    override fun onSuccess(t: AppResponse<ProjectBaseInfor>) {
                        t.data?.let {
                            databean = t.data
                            dataObservable.set(it)
                        }
                    }
                })
        )
    }


    /**
     * 跳转到员工详情（创建人 ）
     */
    val onCreateManClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.projectCreator?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.projectCreator?.employeeName)
                .navigation()
        }
    })
    /**
     * 跳转到员工详情（签订人）
     */
    val onSignedClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.signEmployee?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.signEmployee?.employeeName)
                .navigation()
        }
    })
    /**
     * 跳转到员工详情（定级人）
     */
    val onGradeClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.gradeEmployee?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.gradeEmployee?.employeeName)
                .navigation()
        }
    })

    /**
     * 跳转到员工详情（交底人）
     */
    val onDisclosureClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.disclosureEmployee?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.disclosureEmployee?.employeeName)
                .navigation()
        }
    })
    /**
     * 跳转到员工详情（初验人）
     */
    val onFirstCheckClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.firstCheckEmployee?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.firstCheckEmployee?.employeeName)
                .navigation()
        }
    })
    /**
     * 跳转到员工详情（终验人）
     */
    val onFinalCheckClick= BindingCommand<Unit>(BindingAction {
        dataObservable?.get()?.finalCheckEmployee?.employeeId?.let {
            ARouter
                .getInstance()
                .build(ARouterPath.Workbench.PAGER_STAFF)
                .withString("id", it)
                .withString("name", dataObservable?.get()?.finalCheckEmployee?.employeeName)
                .navigation()
        }
    })

    /**
     * 点击较低风险单
     */
    val onJdFxdClick= BindingCommand<Unit>(BindingAction {
    })
}