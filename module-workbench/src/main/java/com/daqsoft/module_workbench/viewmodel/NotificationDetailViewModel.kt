package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.MenuPermission
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeAuditStatus
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeDetail
import com.daqsoft.module_workbench.utils.MenuPermissionCoverUtils
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @ClassName    NotificationDetailViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/22
 */
class NotificationDetailViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    var  permission : MenuPermissionCover ? = null

    var id = ""

    /**
     * 详情
     */
    val detail = ObservableField<NoticeDetail>()

    /**
     * 审核状态
     */
    val auditStatus = arrayListOf<NoticeAuditStatus>()

    /***
     * 修改点击事件
     */
    val modifyOnClick = BindingCommand<Unit>(BindingAction {

        if (permission?.update == false){
            ToastUtils.showShort("对不起，您无权操作！")
            return@BindingAction
        }


        if (detail.get() == null){
            return@BindingAction
        }
        if (detail.get()!!.oldStats){
            ToastUtils.showShort("当前数据不支持修改")
            return@BindingAction
        }

        modify()
    })


    /**
     * 审批 liveData
     */
    val approveLiveData = MutableLiveData<Unit>()

    /**
     * 确定点击事件(根据类型  提交、审核、撤回)
     */
    val determineOnClick = BindingCommand<Unit>(BindingAction {
        if (detail.get() == null){
            return@BindingAction
        }
        when(detail.get()!!.noticeStatus){
            "待提交" ->{

                if (permission?.create == false){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return@BindingAction
                }

                submit(id)
            }
            "待审核" ->{

                if (permission?.approve == false){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return@BindingAction
                }

                approveLiveData.value = null
            }
            "已发布" ->{

                if (permission?.update == false){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return@BindingAction
                }

                withdraw()
            }
            "已撤回" ->{
                if (permission?.update == false){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return@BindingAction
                }

                modify()
            }
            "已退回" ->{

                if (permission?.update == false){
                    ToastUtils.showShort("对不起，您无权操作！")
                    return@BindingAction
                }

                modify()
            }
        }

    })

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText("通知公告详情")
    }

    /**
     * 获取详情
     * @param id String
     */
    fun getNoticeDetail(id:String){
        addSubscribe(
           model
               .getNoticeDetail(id,true)
               .compose(RxUtils.schedulersTransformer())
               .compose(RxUtils.exceptionTransformer())
               .subscribeWith(object : AppDisposableObserver<AppResponse<NoticeDetail>>(){
                   override fun onSuccess(t: AppResponse<NoticeDetail>) {
                       t.data?.let {
                           detail.set(it)
                       }
                   }

               })
        )
    }


    /**
     * 获取所有审核状态
     */
    fun getAllAuditStatus(){
        addSubscribe(
            model
                .getAllAuditStatus()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<NoticeAuditStatus>>>(){
                    override fun onSuccess(t: AppResponse<List<NoticeAuditStatus>>) {
                        t.data?.let {
                            auditStatus.addAll(it)
                        }
                    }

                })
        )
    }


    /**
     * 提交
     */
    fun submit(id:String){
        addSubscribe(
            model
                .submitNoticeReview(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        getNoticeDetail(id)
                        LiveEventBus.get(LEBKeyGlobal.NOTICE_STATUS_CHANGE).post(true.toString())
                    }
                })
        )
    }


    /**
     * 修改
     */
    fun modify(){
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_NOTIFICATION_ADD)
            .withString("id",detail.get()!!.id.toString())
            .navigation()
    }

    /**
     * 撤回
     */
    fun withdraw(){
        addSubscribe(
            model
                .withdrawNotice(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        getNoticeDetail(id)
                        LiveEventBus.get(LEBKeyGlobal.NOTICE_STATUS_CHANGE).post(true.toString())
                    }
                })
        )
    }


    /**
     * 审批
     */
    fun approve(result: Boolean,content:String){
        addSubscribe(
            model
                .approveNotice(result,content,id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        getNoticeDetail(id)
                        LiveEventBus.get(LEBKeyGlobal.NOTICE_STATUS_CHANGE).post(true.toString())
                        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
                    }
                })
        )
    }


    var menuPermissionCover: MenuPermissionCover?=null
    /**
     * 获取操作权限
     * @param id Int
     */
    fun getMenuPermission(id:Int){
        addSubscribe(
            model
                .getMenuPermission(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MenuPermission>>>() {
                    override fun onSuccess(t: AppResponse<List<MenuPermission>>) {
                        t.data?.let {
                            menuPermissionCover = MenuPermissionCoverUtils.coverNoticePermission(it)
                        }
                    }
                })
        )
    }
}