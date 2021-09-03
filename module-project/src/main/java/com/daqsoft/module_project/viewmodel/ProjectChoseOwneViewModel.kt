package com.daqsoft.module_project.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.module_project.repository.pojo.vo.ProjectOwnerBean
import com.daqsoft.module_project.repository.pojo.vo.ProjectType
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectItemOwnerViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @describe ProjectViewModel
 */
class ProjectChoseOwneViewModel : ToolbarViewModel<ProjectRepository>  {


    var  isOwner : Boolean? = true

    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)


    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        getItemLayout()

    )

    private fun getItemLayout(): Int {
        return  R.layout.item_project_list_yz
    }

    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 以下是搜索界面的内容
     * 搜索内容（输入框监听）
     */

    val searchObservable = ObservableField<String>("")
    var searchTextChanged = BindingCommand<String>(BindingConsumer {
        searchObservable.set(it)
        getShowData()
    })


    val haveObservable = MutableLiveData<Boolean>(false)
    val typeLiveData = MutableLiveData<List<ProjectOwnerBean>>()
    fun getShowData() {
        if(isOwner!!){
            getShowOwnerData()
        }else{
            getShowPaterData()
        }
    }


    /**
     *获取客户信息简易列表
     */
    fun getShowOwnerData() {
        addSubscribe(
            model
                .getOwnerList(key = searchObservable.get())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectOwnerBean>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectOwnerBean>>) {
                        observableList.clear()
                        t.data?.forEach {
                            it.isOwner=true
                            observableList.add(ProjectItemOwnerViewModel(this@ProjectChoseOwneViewModel,it ))
                        }
                        haveObservable.value=true
                    }
                })
        )
    }

    /**
     *获取 合作伙伴列表
     */
    fun getShowPaterData() {
        addSubscribe(
            model
                .getPartnerList(key = searchObservable.get())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectOwnerBean>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectOwnerBean>>) {
                        observableList.clear()
                        t.data?.forEach {
                            it.isOwner=false
                            observableList.add(ProjectItemOwnerViewModel(this@ProjectChoseOwneViewModel,it ))
                        }
                        haveObservable.value=true
                    }
                })
        )
    }


    fun setItemClick(data: ProjectOwnerBean) {
        data.isOwner = isOwner
        LiveEventBus.get(LEBKeyGlobal.CHOOSE_OWNER).post(data)
        finish()
    }

    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })
}