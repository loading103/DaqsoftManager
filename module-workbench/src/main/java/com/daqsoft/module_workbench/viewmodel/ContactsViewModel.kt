package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.module_workbench.repository.pojo.vo.Organization
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.ContactsFooterViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.ContactsItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.isWebsite
import com.daqsoft.library_base.utils.toWebsite
import com.daqsoft.module_workbench.repository.pojo.vo.Member
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 25/11/2020 下午 2:08
 * @author zp
 * @describe 通讯录 viewModel
 */
class ContactsViewModel : ToolbarViewModel<WorkBenchRepository> {

    /**
     * 公司信息
     */
    val companyInfo = ObservableField<CompanyInfo>()


    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.ITEM -> itemBinding.set(BR.viewModel, R.layout.recyclerview_contacts_item)
            ConstantGlobal.FOOTER -> itemBinding.set(BR.viewModel, R.layout.recyclerview_contacts_footer)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_contacts_item)
        }
    }

    /**
     * 公司详情
     */
    val companyDetailOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_COMPANY_DETAIL).navigation()
    })


    val callLiveData = MutableLiveData<String>()
    /**
     * 拨号点击事件
     */
    val callOnclick = BindingCommand<Unit>(BindingAction {
        callLiveData.value = companyInfo.get()!!.companyPhone
    })

    /**
     * 复制点击事件
     */
    val copyOnclick = BindingCommand<Unit>(BindingAction {
        AppUtils.primaryClip(companyInfo.get()?.companyAddress?:"")
    })


    /**
     * 官网 点击事件
     */
    val websiteOnClick = BindingCommand<Unit>(BindingAction {
        if (companyInfo.get() == null){
            return@BindingAction
        }
        var website = companyInfo.get()!!.companyWebsite
        if (!website.isWebsite()){
            website = website.toWebsite()
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    })

    /**
     * 大旗云 点击事件
     */
    val cloudOnClick = BindingCommand<Unit>(BindingAction {
        if (companyInfo.get() == null){
            return@BindingAction
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",companyInfo.get()!!.getCloud().toWebsite())
            .navigation()
    })


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.txl_list_search_white)
        setTitleText(getApplication<Application>().resources.getString(R.string.contacts))
        setTitleTextColor(R.color.white_ffffff)
    }

    override fun rightIconOnClick() {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_STAFF_SEARCH).navigation()
    }

    /**
     * 获取公司信息
     */
    fun  getCompanyInfo(){
        addSubscribe(
            model
                .getCompanyInfo()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CompanyInfo>>(){
                    override fun onSuccess(t: AppResponse<CompanyInfo>) {
                        t.data?.let {
                            companyInfo.set(it)
                        }
                    }
                })
        )
    }


    /**
     * 获取组织架构
     */
    fun getOrganization(){
        addSubscribe(
            model
                .getOrganization()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Organization>>(){
                    override fun onSuccess(t: AppResponse<Organization>) {

//                        t.data?.let {
//                            it.teams.forEach{
//                                observableList.add(ContactsItemViewModel(this@ContactsViewModel,it).apply {
//                                    multiItemType(ConstantGlobal.ITEM)
//                                })
//                            }
//
//                            observableList.add(ContactsFooterViewModel(this@ContactsViewModel,it.totalCount).apply {
//                                multiItemType(ConstantGlobal.FOOTER)
//                            })
//                        }
                    }
                })
        )
    }


    /**
     * 获取顶层成员
     * @param pid Int
     */
    fun getMember(pid: Int){
        addSubscribe(
            model
                .getMember(pid)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<AppResponse<Member>, Observable<AppResponse<Int>>>{
                    it.data?.let {
                        Handler(Looper.getMainLooper()).post {
                            it.org.forEach{
                                observableList.add(
                                    ContactsItemViewModel(this@ContactsViewModel,it).apply {
                                        multiItemType(ConstantGlobal.ITEM)
                                    }
                                )
                            }
                        }
                    }
                    return@Function model.getCompanyNumberOfEmployees()
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Int>>() {
                    override fun onSuccess(t: AppResponse<Int>) {
                        t.data?.let {
                            observableList.add(ContactsFooterViewModel(this@ContactsViewModel,it).apply {
                                multiItemType(ConstantGlobal.FOOTER)
                            })
                        }
                    }
                })
        )
    }
}