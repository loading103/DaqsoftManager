package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.repository.pojo.vo.*
import com.daqsoft.module_project.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_project.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 项目 viewModel
 */
class ProjectDetailViewModel : ToolbarViewModel<ProjectRepository> {

    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    /**
     * 获取登录人姓名
     */
    val employeeInfo: EmployeeInfo = Gson().fromJson(AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.USER_INFO))
        , EmployeeInfo::class.java)


    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }



    /**
     * 更多 livData
     */
    val moreLiveData = MutableLiveData<Boolean>()
    override fun rightIconOnClick() {
        moreLiveData.value = true
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.project_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("")
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.list_top_more)
    }


    val dynamicOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Project.PAGER_PROJECT_DYNAMIC)
            .withString("id", detailData.value?.id.toString())
            .navigation()
    })

    var pageList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.ITEM_PROJECT_HEAD -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_head
                )
                ConstantGlobal.ITEM_PROJECT_COUNT -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_count
                )
                ConstantGlobal.ITEM_PROJECT_FLOW -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_flow
                )
                ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_TITLE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_buildcontent_title
                )
                ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_STANDARD -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_buildcontent
                )
                ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_CUSTOM -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_buildcontent_custom
                )

                ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_EMPTY -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_project_buildcontent_empty
                )
            }
        }


    var detailData = MutableLiveData<ProjectDetailBean>()

    fun getRequestContentBuild(projectId: Int) {
        addSubscribe(
            model.getProjecBuildContentRequest(projectId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectBuildContent>>() {
                    override fun onSuccess(t: AppResponse<ProjectBuildContent>) {
                        t.data?.let {

                            // 建设内容及数目
                            var buildContentTitle = ProjectDetailBuildContentTitle(this@ProjectDetailViewModel,it.custom.size+it.standard.size)
                            buildContentTitle.multiItemType(ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_TITLE)
                            pageList.add(buildContentTitle)

                            if(it.custom.isNullOrEmpty() && it.standard.isNullOrEmpty()){
                                val empty= ProjectDetailBuildContentEmpty(this@ProjectDetailViewModel)
                                empty.multiItemType(ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_EMPTY)
                                pageList.add(empty)
                                return
                            }

                            // 红色卡片
                            pageList.addAll(it.standard.map {
                                var buildContentItem = ProjectDetailBuildContentViewModel(this@ProjectDetailViewModel,it)
                                buildContentItem.multiItemType(ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_STANDARD)
                                buildContentItem
                            })
                            // 灰色卡片
                            pageList.addAll(it.custom.map {
                                var buildContentCustom = ProjectDetailBuildContentCustom(this@ProjectDetailViewModel,it)
                                buildContentCustom.multiItemType(ConstantGlobal.ITEM_PROJECT_BUILD_CONTENT_CUSTOM)
                                buildContentCustom
                            })
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                })
        )
    }

    /**
     * isall=true  全部界面刷新，false之刷新流程
     */
    var flowViewModel :ProjectDetailFlowViewModel ?= null
    fun getRequestProjectDetail(projectId: Int,isall: Boolean) {
        addSubscribe(
            model.getProjectDetailRequest(projectId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectDetailBean>>() {
                    override fun onSuccess(t: AppResponse<ProjectDetailBean>) {
                        if(isall){
                            pageList.clear()
                            t.data?.let {

                                detailData.value = it

                                setTitleText(it.projectName)

                                var headerViewModel = ProjectDetailHeadViewModel(this@ProjectDetailViewModel, it)
                                headerViewModel.multiItemType(ConstantGlobal.ITEM_PROJECT_HEAD)

                                var countViewModel = ProjectDetailCountViewModel(this@ProjectDetailViewModel,it)
                                countViewModel.multiItemType(ConstantGlobal.ITEM_PROJECT_COUNT)


                                flowViewModel = ProjectDetailFlowViewModel(this@ProjectDetailViewModel,it)
                                flowViewModel?.multiItemType(ConstantGlobal.ITEM_PROJECT_FLOW)

                                pageList.add(headerViewModel)
                                pageList.add(countViewModel)
                                pageList.add(flowViewModel)

                                getRequestContentBuild(projectId)
                            }
                        }else{
                            t.data?.let {
                                flowViewModel?.reFreshData(it)
                            }
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                    }
                })
        )
    }



    var  menuObser:ObservableField<MenuInfo> = ObservableField()
    fun getMenus(){
        addSubscribe(
            model
                .getMenus()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Menu>>() {
                    override fun onSuccess(t: AppResponse<Menu>) {
                        t.data?.let {
                            if (!it.daily.isNullOrEmpty()){
                                it.daily.forEach { it1->
                                    if(it1.appMenuName=="团队文件"){
                                        menuObser.set(it1)
                                    }
                                }
                            }
                        }
                    }
                })
        )
    }


    /**
     * 关注/取消关注项目
     */
    fun focusProjectData(
        id: Int,
        itemBinding: ProjectDetailHeadViewModel
    ){
        addSubscribe(
            model
                .followProject(id)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<String>>() {
                    override fun onSuccess(t: AppResponse<String>) {
                        dismissLoadingDialog()

                        // 如果是关注就取消
                        if(itemBinding.ischecked.get()!!){
                            itemBinding.ischecked.set(false)
                            LiveEventBus.get(LEBKeyGlobal.PROJICT_FOLLOW_SUCCESS).post(false)
                        }else{
                            itemBinding.ischecked.set(true)
                            LiveEventBus.get(LEBKeyGlobal.PROJICT_FOLLOW_SUCCESS).post(true)
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }



    fun showGrayContentPop(bean: Custom) {

    }
    /**
     * 点击建设内容
     */
    var showRedLiveData = MutableLiveData<ProductBean>()
    fun showRedContentPop(bean: Standard) {
        addSubscribe(
            model
                .getProductDetail(bean.productId.toInt())
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProductBean>>() {
                    override fun onSuccess(t: AppResponse<ProductBean>) {
                        dismissLoadingDialog()
                        showRedLiveData.postValue(t.data)
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }



}