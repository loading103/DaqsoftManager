package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
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
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.*
import com.daqsoft.module_workbench.utils.IMTimeUtils
import com.daqsoft.module_workbench.utils.MenuPermissionCoverUtils
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.NotificationItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*

/**
 * @ClassName    NotificationViewModel
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/19
 */
class NotificationViewModel : ToolbarViewModel<WorkBenchRepository> ,
    Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(
        application,
        workBenchRepository
    )

    override fun onCreate() {
        super.onCreate()
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
        setTitleText("????????????")
        setRightIcon2Visible(View.VISIBLE)
        setRightIcon2Src(R.mipmap.bmwj_search)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.ghck_list_add)
    }

    override fun rightIconOnClick() {
        if (menuPermissionCover?.create == false){
            ToastUtils.showShort("??????????????????????????????")
            return
        }
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_NOTIFICATION_ADD).navigation()
    }
    override fun rightIcon2OnClick() {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_NOTIFICATION_SEARCH)
            .withParcelable("permission",menuPermissionCover)
            .navigation()
    }


    /**
     * ???RecyclerView??????ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_notification_item
    )

    // ????????????
    val chooseType = ObservableField<NoticeType>(NoticeType(false,null,"??????"))

    // ????????????
    val noticeCountObservable = ObservableField<NoticeCount>()

    // ??????
    val thisYear: ObservableField<String>
        get() = ObservableField<String>(
           Calendar.getInstance().apply {
               timeZone = TimeZone.getTimeZone("GMT+8:00")
           }.get(Calendar.YEAR).toString()
        )

    // ??????
    val today = ObservableField<String>(IMTimeUtils.StringData())


    // ??????
    val typeLiveData = MutableLiveData<List<NoticeType>>()

    // ??????/?????? ??????
    val submitOnClick = MutableLiveData<NoticeDetail>()
    
    
    /**
     * ??????????????????
     */
    fun getNumberOfNotify(){
        addSubscribe(
            model
                .getNoticeCount()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NoticeCount>>() {
                    override fun onSuccess(t: AppResponse<NoticeCount>) {
                        t.data?.let {
                            noticeCountObservable.set(it)
                        }
                    }

                })
        )
    }

    /**
     * ??????????????????
     */
    fun getNoticeType(){
        addSubscribe(
            model
                .getNoticeType()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<NoticeType>>>() {
                    override fun onSuccess(t: AppResponse<List<NoticeType>>) {
                        t.data?.let {
                            typeLiveData.value = it.filter { !it.deleted }.toMutableList().apply {
                                add(
                                    0,
                                    chooseType.get()!!
                                )
                            }
                        }
                    }

                })
        )
    }


    /**
     * ?????? ?????????
     */
    var diff = createDiff()

    /**
     * ?????? ????????????
     */
    var pageList = createPagedList()

    /**
     * ?????? ?????????
     */
    var dataSource : DataSource<Int, ItemViewModel<*>>?= null

    var refreshLiveData = MutableLiveData<Boolean>()

    override fun createDataSource(): DataSource<Int, ItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, ItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>
            ) {

                addSubscribe(
                    model
                        .getNoticeList(
                            noticeStatus = chooseStatus.get()!!.value?.toInt()
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Notice>>() {
                            override fun onSuccess(t: AppResponse<Notice>) {
                                refreshLiveData.value = true
                                t.data?.let {
                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = NotificationItemViewModel(this@NotificationViewModel,it)
                                        observableList.add(itemViewModel)
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
                        .getNoticeList(
                            page = params.key,
                            noticeStatus = chooseStatus.get()!!.value?.toInt()
                        )
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<Notice>>() {
                            override fun onSuccess(t: AppResponse<Notice>) {
                                t.data?.let {

                                    val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                    it.records.forEach {
                                        val itemViewModel = NotificationItemViewModel(this@NotificationViewModel,it)
                                        observableList.add(itemViewModel)
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
     * ????????????
     * @param id String
     */
    fun submitReview(id:String){

        if (menuPermissionCover?.create == false){
            ToastUtils.showShort("??????????????????????????????")
            return
        }

        addSubscribe(
            model
                .submitNoticeReview(id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dataSource?.invalidate()
                    }

                })
        )
    }

    /**
     * ??????
     */
    fun approve(result: Boolean,id:String){
        addSubscribe(
            model
                .approveNotice(result,"",id)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dataSource?.invalidate()
                        getNumberOfNotify()

                        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
                    }
                })
        )
    }


    var menuPermissionCover: MenuPermissionCover?=null
    /**
     * ??????????????????
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






    /**
     * ????????????
     */
    val auditStatusLiveData = MutableLiveData<List<NoticeAuditStatus>>()

    /**
     * ????????????
     */
    val chooseStatus = ObservableField<NoticeAuditStatus>(NoticeAuditStatus("??????",null))

    /**
     * ????????????????????????
     */
    fun getAllAuditStatus(){
        addSubscribe(
            model
                .getAllAuditStatus()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<NoticeAuditStatus>>>(){
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onSuccess(t: AppResponse<List<NoticeAuditStatus>>) {
                        t.data?.let {
                            auditStatusLiveData.value = it.toMutableList().apply {
                                replaceAll {
                                    if (it.desc == "?????????"){
                                        NoticeAuditStatus("???????????????",it.value)
                                    }else{
                                        it
                                    }
                                }
                                add(0,chooseStatus.get()!!)
                            }
                        }
                    }

                })
        )
    }
}