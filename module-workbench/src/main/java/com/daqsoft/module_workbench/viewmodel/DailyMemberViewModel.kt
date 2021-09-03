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
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyStatisticBean
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyTeamContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.TeamHeadItemViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailyMemberViewModel :  ToolbarViewModel<WorkBenchRepository>,
    Paging2Utils<ItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    val chooseTag = ObservableField<String>("今天")


    val topVisible = ObservableField<Int>(View.VISIBLE)

    val startTimeLiveData = ObservableField<String>("")

    val endTimeLiveData = ObservableField<String>("")

    val finishedLiveData = MutableLiveData<Boolean>()

    val timeData = ObservableField<String>("")
    /**
     * 未提交成员
     */
    var itemHeadBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_team_head_icon
    )

    var itemHeadObservableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()


    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_daily_member
    )

    override fun onCreate() {
        initToolbar()
        initTime()
    }

    private fun initTime() {
        val currentTimeMillis = System.currentTimeMillis()
        timeData.set(stampToTime(currentTimeMillis.toString()))
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setTitleText("日报")
        setTitleTextColor(R.color.white)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.VISIBLE)
        setBackIconSrc(R.mipmap.list_top_return)
        setRightIconSrc(R.mipmap.list_top_ss1)
    }
    override fun rightIconOnClick() {
        ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_SEARCH).navigation()
    }

    override fun backOnClick() {
        super.backOnClick()
        finishedLiveData.value=true
    }
    /**
     * 刷新完成
     */
    val refreshCompleteLiveData = MutableLiveData<Boolean>()

    /**
     * 顶部数据
     */
    val topDataField = ObservableField<DialyStatisticBean>()
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
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, ItemViewModel<*>>) {

                var paramMap = hashMapOf<String, String>("name" to searchObservable.get()!!,"isReported" to "true",
                    "date" to timeData.get().toString())
                addSubscribe(
                    model.getDailyMemberRequest(paramMap)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<List<DialyMemberBean>>>() {
                            override fun onSuccess(t: AppResponse<List<DialyMemberBean>>) {
                                refreshCompleteLiveData.value = true
                                val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
                                t.data?.forEach {
                                    var dailyListDate = DailyTeamContentViewModel(this@DailyMemberViewModel, it)
                                    observableList.add(dailyListDate)
                                }
                                callback.onResult(observableList, ConstantGlobal.INITIAL_PAGE, ConstantGlobal.INITIAL_PAGE+1)
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
//
//                var paramMap = hashMapOf<String, String>(
//                    "name" to "",
//                    "date" to ""
//                )
//
//                addSubscribe(
//                    model.getDailyMemberRequest(paramMap)
//                        .compose(RxUtils.exceptionTransformer())
//                        .compose(RxUtils.schedulersTransformer())
//                        .subscribeWith(object : AppDisposableObserver<AppResponse<List<DialyMemberBean>>>() {
//                            override fun onSuccess(t: AppResponse<List<DialyMemberBean>>) {
//
//                                val observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
//                                t.data?.forEach {
//                                    var dailyListDate = DailyTeamContentViewModel(this@DailyMemberViewModel, it)
//                                    observableList.add(dailyListDate)
//                                }
//                                callback.onResult(observableList,params.key+1)
//                            }
//
//                        })
//                )

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
     * 获取未提交成员
     */

    fun getNoSubmitMember() {
        addSubscribe(
            model.getDailyNoSubMemberRequest(date = timeData.get())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<DialyMemberBean>>>() {
                    override fun onSuccess(t: AppResponse<List<DialyMemberBean>>) {
                        itemHeadObservableList.clear()
                        if(t.data.isNullOrEmpty()){
                            topVisible.set(View.GONE)
                        }else{
                            topVisible.set(View.VISIBLE)
                        }
                        t.data?.forEach {
                            var dailyListDate = TeamHeadItemViewModel(this@DailyMemberViewModel, it)
                            itemHeadObservableList.add(dailyListDate)
                        }
                    }
                })
        )
    }

    /**
     * 获取主管团队日报提交统计
     */
    fun getyReportStatistic() {
        addSubscribe(
            model.getDailyReportData(timeData.get())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DialyStatisticBean>>() {
                    override fun onSuccess(t: AppResponse<DialyStatisticBean>) {
                        t.data?.let {
                            topDataField.set(it)
                        }
                    }
                })
        )
    }







    fun setTimeData(startTime: String, endTime: String) {
        if(!endTime.isNullOrBlank()){
            startTimeLiveData.set(startTime.replace(".","-"))
            endTimeLiveData.set(endTime.replace(".","-"))
        }else{
            val split = startTime.split(".")
            startTimeLiveData.set(startTime.replace(".","-")+"-01")
            val monthOfDay = getMonthOfDay(split[0].toInt(), split[1].toInt())
            endTimeLiveData.set(startTime.replace(".","-")+"-${monthOfDay}")
        }
        dataSource?.invalidate()
    }


    var chooseTimeLiveData = MutableLiveData<Boolean>()
    val onChooseClick = BindingCommand<Unit>(BindingAction {
        chooseTimeLiveData.value=true
    })



    fun getMonthOfDay(year: Int, month: Int): Int {
        var day = 0
        day = if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
            29
        } else {
            28
        }
        when (month) {
            1, 3, 5, 7, 8, 10, 12 -> return 31
            4, 6, 9, 11 -> return 30
            2 -> return day
        }
        return 0
    }


    fun stampToTime(stamp: String): String {
        val   type = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
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
}

