package com.daqsoft.module_home.viewmodel

import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.coverTime
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.pojo.vo.Untreated
import com.daqsoft.library_common.utils.NextMessageOpenHelper
import com.daqsoft.module_home.BR
import com.daqsoft.module_home.R
import com.daqsoft.module_home.repository.HomeRepository
import com.daqsoft.module_home.repository.pojo.vo.Message
import com.daqsoft.module_home.viewmodel.itemviewmodel.MessageItemViewModel
import com.daqsoft.module_home.viewmodel.itemviewmodel.MessageTitleViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_home.viewmodel
 * @date 1/12/2020 上午 11:45
 * @author zp
 * @describe 消息页面 viewModel
 */
class MessageViewModel : ToolbarViewModel<HomeRepository>,Paging2Utils<MultiItemViewModel<*>>{

    @ViewModelInject
    constructor(application: Application,homeRepository: HomeRepository):super(application,homeRepository)

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.ITEM -> itemBinding.set(BR.viewModel, R.layout.recyclerview_message_item)
            ConstantGlobal.TITLE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_message_title)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_message_item)
        }
    }

    override fun onCreate() {
        initToolbar()
    }

    fun initToolbar() {
        if (Untreated.INSTANCE.news <= 0){
            setTitleText(getApplication<Application>().resources.getString(R.string.message))
            return
        }
        setTitleText(getApplication<Application>().resources.getString(R.string.message) + "（${Untreated.INSTANCE.news}）")
    }



    var visibleField = ObservableField(View.GONE)

    var readAllLivaData = MutableLiveData<String?>()
    /**
     * 全部标为已读
     */
    val markReadOnClick = BindingCommand<Unit>(BindingAction {
        markAllAsRead()
    })

    /**
     * 只看未读
     */
    val unReadOnClick = BindingCommand<Unit>(BindingAction {
        readAllLivaData.value="0"
        visibleField.set(View.VISIBLE)
    })
    /**
     * 只看全部
     */
    val allReadOnClick = BindingCommand<Unit>(BindingAction {
        visibleField.set(View.GONE)
        readAllLivaData.value=null
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
    var dataSource : DataSource<Int, MultiItemViewModel<*>>?= null
    override fun createDataSource(): DataSource<Int, MultiItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, MultiItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, MultiItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getMessage(state = readAllLivaData?.value?.toInt())
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Message>>() {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onSuccess(t: AppResponse<Message>) {
                                refreshCompleteLiveData.value = true
                                t.data?.let { its->
                                    its.datas?.let {

                                        var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

                                        it.map {
                                            it.apply {
                                                day = msgTime.coverTime(
                                                    "yyyy-MM-dd HH:mm:ss",
                                                    "yyyy-MM-dd"
                                                )
                                            }
                                        }
                                            .groupBy({ it.day }, { it })
                                            .forEach { t, u ->
                                                lastTitle["day"] = t
                                                observableList.add(MessageTitleViewModel(this@MessageViewModel,t).apply { multiItemType(ConstantGlobal.TITLE) })
                                                u.forEach {
                                                    observableList.add(MessageItemViewModel(this@MessageViewModel,it,its?.extraInfo?.next).apply { multiItemType(ConstantGlobal.ITEM) })
                                                }
                                            }
                                        callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
                                    }

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
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {
                addSubscribe(
                    model
                        .getMessage(state = readAllLivaData?.value?.toInt(),page = params.key)
                        .compose (RxUtils.schedulersTransformer())
                        .compose (RxUtils.exceptionTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Message>>() {
                            @RequiresApi(Build.VERSION_CODES.N)
                            override fun onSuccess(t: AppResponse<Message>) {
                                t.data?.let {its->
                                    its.datas?.let {
                                        var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
                                        it.map {
                                            it.apply {
                                                day = msgTime.coverTime(
                                                    "yyyy-MM-dd HH:mm:ss",
                                                    "yyyy-MM-dd"
                                                )
                                            }
                                        }
                                            .groupBy({ it.day }, { it })
                                            .forEach { t, u ->
                                                if (lastTitle["day"] != t){
                                                    lastTitle["day"] = t
                                                    observableList.add(MessageTitleViewModel(this@MessageViewModel,t).apply { multiItemType(ConstantGlobal.TITLE) })
                                                }
                                                u.forEach {
                                                    observableList.add(MessageItemViewModel(this@MessageViewModel,it,its?.extraInfo?.next).apply { multiItemType(ConstantGlobal.ITEM) })
                                                }
                                            }
                                        callback.onResult(observableList,params.key+1)
                                    }

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
     * 最后一条title数据
     */
    val lastTitle = hashMapOf<String,String>()


    /**
     * 全部标记已读
     */
    private fun markAllAsRead(){
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


    /**
     * 阅读单条数据
     * @param id String
     */
    fun readSingle(id:String,item:MessageItemViewModel){
        addSubscribe(
            model
                .readSingle(id)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post{
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        item.pageJump()
                        item.statusObservable.set(0)
                        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
                    }
                })
        )
    }


    fun  getNextId(Id:String,item:MessageItemViewModel){
        addSubscribe(
            model
                .getNextDetail(Id.toInt())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NextMessage>>() {
                    override fun onSuccess(it: AppResponse<NextMessage>) {
                        it?.data?.next?.let {
                            item.nextId.set(it)
                        }
                        item.pageJumpSame()
                    }
                })
        )
    }
}