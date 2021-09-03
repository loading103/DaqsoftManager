package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData

import androidx.paging.*
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.Announcement
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.AnnouncementItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 4/12/2020 下午 2:30
 * @author zp
 * @describe 通知公告 viewmodel
 */
class AnnouncementViewModel :  ToolbarViewModel<WorkBenchRepository>, Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    var onClickItemViewModel : AnnouncementItemViewModel ? = null

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(BR.viewModel, R.layout.recyclerview_announcement_item)


    override fun onCreate() {
        initToolbar()
    }

    fun initToolbar() {
        if (Untreated.INSTANCE.notice <= 0){
            setTitleText(getApplication<Application>().resources.getString(R.string.announcement))
            return
        }
        setTitleText(getApplication<Application>().resources.getString(R.string.announcement) + "（${Untreated.INSTANCE.notice}）")
    }


    /**
     * 全部标为已读
     */
    val markReadOnClick = BindingCommand<Unit>(BindingAction {
        markAllAsRead()
    })

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
    val refreshCompleteLiveData = MutableLiveData<Boolean>()
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getAnnouncement()
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Announcement>>() {
                            override fun onSuccess(t: AppResponse<Announcement>) {
                                refreshCompleteLiveData.value = true
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        observableList.add(AnnouncementItemViewModel(this@AnnouncementViewModel, it))
                                    }
                                    callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                }
                            }

                            override fun onFail(e: Throwable) {
                                super.onFail(e)
                                refreshCompleteLiveData.value = false
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
                        .getAnnouncement(current = params.key)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Announcement>>() {
                            override fun onSuccess(t: AppResponse<Announcement>) {
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        observableList.add(AnnouncementItemViewModel(this@AnnouncementViewModel, it))
                                    }
                                    callback.onResult(observableList,params.key+1)
                                }
                            }
                        })
                )
            }

            override fun loadBefore(
                params: LoadParams<Int>,
                callback: LoadCallback<Int, ItemViewModel<*>>
            ) {
            }
        }
        return dataSource!!
    }


    /**
     * 全部已读
     */
    fun markAllAsRead(){
        addSubscribe(
            model
                .markAllAsRead()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
                    }
                })
        )
    }
}

