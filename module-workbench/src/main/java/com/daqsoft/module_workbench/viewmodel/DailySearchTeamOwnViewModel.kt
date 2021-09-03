package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailySearchTeamOwnViewModel :  ToolbarViewModel<WorkBenchRepository>, Paging2Utils<MultiItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var  datas : MutableList<DialyRecord> = mutableListOf()

    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.DAILY_LIST_CONTENT -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_team_own_search
                )
                ConstantGlobal.DAILY_LIST_DATE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_search_date
                )
            }
        }

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("日报搜索")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
        setRightIconSrc(R.mipmap.list_top_ss)
    }

    /**
     * 搜索内容
     */
    val searchObservable = ObservableField<String>("")
    val changedLiveData = MutableLiveData<Boolean>(false)

    /**
     * 输入框监听
     */
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


    init {

    }

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
    var dataSource : DataSource<Int, MultiItemViewModel<*>>?= null

    var menuIds : String ?  ="0"
    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    override fun createDataSource(): DataSource<Int, MultiItemViewModel<*>> {
            dataSource = object : PageKeyedDataSource<Int, MultiItemViewModel<*>>(){
                override fun loadInitial(
                    params: LoadInitialParams<Int>,
                    callback: LoadInitialCallback<Int, MultiItemViewModel<*>>
                ) {

                    var paramMap = hashMapOf<String, String>(
                        "publishStatus" to "",
                        "submitStatus" to "",
                        "startDate" to "",
                        "endDate" to "",
                        "page" to ConstantGlobal.INITIAL_PAGE.toString(),
                        "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                        "menuId" to menuIds.toString(),
                        "key" to searchObservable.get()!!.toString()
                    )
                    addSubscribe(

                        model.getAllMyTeamReport(paramMap)
                            .compose(RxUtils.exceptionTransformer())
                            .compose(RxUtils.schedulersTransformer())
                            .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                                override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                    datas.clear()
                                    t.data?.let {
                                        it.records?.let {   datas.addAll(it)}
                                        val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
                                        it.records?.forEach{
                                            var dailyListContent = DailyTeamSearchOwnItemViewModel(this@DailySearchTeamOwnViewModel, it)
                                            dailyListContent.multiItemType(ConstantGlobal.DAILY_LIST_CONTENT)
                                            observableList.add(dailyListContent)
                                        }
                                        callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                    }
                                }

                            })
                    )
                }

                override fun loadAfter(
                    params: LoadParams<Int>,
                    callback: LoadCallback<Int, MultiItemViewModel<*>>
                ) {

                    var paramMap = hashMapOf<String, String>(
                        "publishStatus" to "",
                        "submitStatus" to "",
                        "startDate" to "",
                        "endDate" to "",
                        "page" to params.key.toString(),
                        "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                        "menuId" to menuIds.toString(),
                        "key" to searchObservable.get()!!.toString()
                    )
                    addSubscribe(

                        model.getAllMyTeamReport(paramMap)
                            .compose(RxUtils.exceptionTransformer())
                            .compose(RxUtils.schedulersTransformer())
                            .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                                override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                    t.data?.let {
                                        val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
                                        it.records?.let {   datas.addAll(it)}
                                        it.records?.forEach{
                                            var dailyListContent = DailyTeamSearchOwnItemViewModel(this@DailySearchTeamOwnViewModel, it)
                                            dailyListContent.multiItemType(ConstantGlobal.DAILY_LIST_CONTENT)
                                            observableList.add(dailyListContent)
                                        }
                                        callback.onResult(observableList,params.key+1)
                                    }
                                }

                            })
                    )

                }

                override fun loadBefore(
                    params: LoadParams<Int>,
                    callback: LoadCallback<Int, MultiItemViewModel<*>>
                ) {
                }

            }
            return dataSource!!
        }




    /**
     * 保存点击的Item信息
     */
    var itemListContentViewModel: DailyTeamSearchOwnItemViewModel ? = null
    fun setItemViewModel(dailyListContentViewModel: DailyTeamSearchOwnItemViewModel) {
        itemListContentViewModel = dailyListContentViewModel
    }

    fun reFreshItemData() {
        if(itemListContentViewModel!=null){
            val date = itemListContentViewModel?.record?.get()?.date.toString() ?:return
            val id = itemListContentViewModel?.record?.get()?.id.toString() ?:return
            addSubscribe(
                model.getDailyTeamDayRequest(id)
                    .compose(RxUtils.exceptionTransformer())
                    .compose(RxUtils.schedulersTransformer())
                    .subscribeWith(object : AppDisposableObserver<AppResponse<DialyRecord>>() {
                        override fun onSuccess(t: AppResponse<DialyRecord>) {
                            t.data?.let {
                                itemListContentViewModel?.notifyItemData(it)
                            }
                        }
                    })
            )
        }
    }
}

