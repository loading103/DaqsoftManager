package com.daqsoft.module_project.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.ProjectRepository
import com.daqsoft.module_project.repository.pojo.vo.*
import com.daqsoft.module_project.viewmodel.itemviewmodel.ProjectItemViewModel
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
class ProjectViewModel : ToolbarViewModel<ProjectRepository> , Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application, projectRepository : ProjectRepository):super(application,projectRepository)

    // 选择标签
    val chooseTag = ObservableField<ProjectType>(ProjectType("全部项目",null))


    // 选择标签
    val projecttype = ObservableField<String?>()

    val stageId = ObservableField<String?>()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.item_project_list
    )


    /**
     * 点击事件
     */
    val addOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build( ARouterPath.Project.PAGER_PROJECT_ADD).navigation()
    })

    val researchOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Project.PAGER_PROJECT_SEARCH).navigation()
    })

    /**
     * 项目等级
     */
    val leveLiveData = MutableLiveData<Int>()
    /**
     * 是否关注
     */
    val onlyCaresLiveData = MutableLiveData<Boolean>()
    /**
     * 获取类型
     */
    val typeLiveData = MutableLiveData<List<ProjectType>>()

    /**
     * 获取类型
     */
    val typeStateLiveData = MutableLiveData<Int>()


    val firstTypeLiveData :MutableList<ProjectChooseType> = mutableListOf(
        ProjectChooseType(id = 1,name = "按项目类型"),
        ProjectChooseType(id = 2,name = "按执行阶段")
    )
    val haveTypeLiveData = MutableLiveData<Boolean>()
    fun getProjectType(){
        addSubscribe(
            model
                .getProjectType()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectType>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectType>>) {
                        t.data?.let {
                            typeLiveData.value = it.toMutableList().apply {
                                add(0, ProjectType("全部",null,null))
                            }
                            firstTypeLiveData[0].datas= it?.toMutableList().apply {
                                add(0, ProjectType("全部",null,null))
                            }
                        }
                        getProjectTypeStageTable()
                    }
                })
        )
    }
    fun getProjectTypeStageTable(){
        addSubscribe(
            model
                .getProjectStageTable()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<ProjectType>>>() {
                    override fun onSuccess(t: AppResponse<List<ProjectType>>) {
                        t.data?.let {
                            firstTypeLiveData[1].datas= it
                            firstTypeLiveData[1].datas?.forEach { it1->
                                it1.id=it1.ID
                                it1.name=it1.typeName
                            }
                        }
                        haveTypeLiveData.value=true
                    }
                })
        )
    }



    /**
     * 获取头部信息
     */
    val headLiveData = ObservableField<ProjectHeadBean>()
    fun getHeadInforType(){
        addSubscribe(
            model
                .getProjectHeadInfor()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectHeadBean>>() {
                    override fun onSuccess(t: AppResponse<ProjectHeadBean>) {
                        headLiveData.set(t.data)
                    }
                })
        )
    }

    /**
     * 关注/取消关注项目
     */
    fun focusProjectData(
        id: Int,
        itemBinding: ProjectItemViewModel
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

    var refreshLiveData = MutableLiveData<Boolean>()
    /**
     * 分页 差分器
     */
    var diff = createDiff()

    /**
     * 分页 数据监听
     */
    var pageList = createPagedList()
    /**
     * 分页 数据源
     */
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {


        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ItemViewModel<*>>) {
                addSubscribe(
                    model
                        .getProjectListData(projectName=searchObservable.get(),projectType = projecttype?.get()?.toInt(),stage = stageId?.get()?.toInt()
                            ,projectGrade = leveLiveData?.value,onlyCares = onlyCaresLiveData?.value,state = typeStateLiveData?.value)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectListBean>>() {
                            override fun onSuccess(t: AppResponse<ProjectListBean>) {
                                refreshLiveData.value = true
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.datas.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = ProjectItemViewModel(this@ProjectViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
                                    }
                                    callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                                refreshLiveData.value = false
                            }
                        })
                )
            }

            override fun loadAfter(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getProjectListData(pageCurr = params.key,projectName=searchObservable.get(),projectType = projecttype?.get()?.toInt(),stage = stageId?.get()?.toInt()
                            ,projectGrade = leveLiveData?.value,onlyCares = onlyCaresLiveData?.value,state = typeStateLiveData?.value)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectListBean>>() {
                            override fun onSuccess(t: AppResponse<ProjectListBean>) {
                                t.data?.let { it ->
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.datas.let { it1 ->
                                        observableList.clear()
                                        it1.forEach {
                                            val itemViewModel = ProjectItemViewModel(this@ProjectViewModel,it)
                                            observableList.add(itemViewModel)
                                        }
                                    }
                                    callback.onResult(observableList,params.key+1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ItemViewModel<*>>) {}
        }
        return dataSource!!
    }



    /**
     * 以下是搜索界面的内容
     * 搜索内容（输入框监听）
     */

    val changedLiveData = MutableLiveData<Boolean>(false)
    val searchObservable = ObservableField<String>("")
    var searchTextChanged = BindingCommand<String>(BindingConsumer {
        if (!changedLiveData.value!!){
            changedLiveData.value = true
        }

        if(it.isNullOrBlank()){
            return@BindingConsumer
        }
        dataSource?.invalidate()
    })
    /**
     * 取消点击事件
     */
    val cancelOnClick = BindingCommand<Unit>(BindingAction {
        finish()
    })


    /**
     * 保存点击item
     */
    val clickItem = hashMapOf<String, Any>()
    fun saveItemClick(itemViewModel: ProjectItemViewModel) {
        clickItem["itemViewModel"] = itemViewModel
    }
    /**
     * 刷新单条数据
     */
    fun refreshItemClick() {
        clickItem?.run {
            val itemViewModel = this["itemViewModel"] as ProjectItemViewModel
            addSubscribe(
                model.getProjectSingleData(itemViewModel.datas.get()?.id)
                    .compose(RxUtils.exceptionTransformer())
                    .compose(RxUtils.schedulersTransformer())
                    .subscribeWith(object : AppDisposableObserver<AppResponse<ProjectData>>() {
                        override fun onSuccess(t: AppResponse<ProjectData>) {
                            t?.data.let {
                                refreshItem(it!!)
                            }

                        }
                    })
            )
        }
    }


    /**
     * 刷新单个
     * @param item ProjectDynamic
     */
    fun refreshItem(item: ProjectData){
        if (clickItem.isEmpty()){
            return
        }
        try {
            clickItem.run {
                val itemViewModel = this["itemViewModel"] as ProjectItemViewModel
                itemViewModel.datas.set(item)
                itemViewModel.ischecked.set(item.isFocus=="1")
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


}