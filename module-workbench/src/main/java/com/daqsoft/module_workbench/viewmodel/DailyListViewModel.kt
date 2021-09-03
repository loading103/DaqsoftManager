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
import com.daqsoft.module_workbench.repository.pojo.vo.DialyListBeanItem
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListDateViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailyListViewModel :  ToolbarViewModel<WorkBenchRepository>, Paging2Utils<MultiItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var isActivity:Boolean=true

    val chooseTag = ObservableField<String>("未知年份")

    val startTimeLiveData = ObservableField<String>("")

    val endTimeLiveData = ObservableField<String>("")

    val finishedLiveData = MutableLiveData<Boolean>()

    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.DAILY_LIST_CONTENT -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_list_content
                )
                ConstantGlobal.DAILY_LIST_DATE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_list_date
                )
            }
        }


    override fun onCreate() {
        initToolbar()
        initTopTime()

    }

    private fun initTopTime() {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        chooseTag.set(year.toString()+"年")
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("日报")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.VISIBLE)
        setRightIconSrc(R.mipmap.rb_top_rl)
    }

    override fun backOnClick() {
        super.backOnClick()
        finishedLiveData.value=true
    }

    fun setRightIconGone() {
        isActivity=false
    }



    override fun rightIconOnClick() {
        if(isActivity){
//            ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_SEARCH).navigation()
            chooseTimeLiveData.value=true
        }else{
            chooseTimeLiveData.value=true
        }
    }

    /**
     * 刷新完成
     */
    val refreshCompleteLiveData = MutableLiveData<Boolean>()

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

    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()



    override fun createDataSource(): DataSource<Int, MultiItemViewModel<*>> {
        dataSource = object : PageKeyedDataSource<Int, MultiItemViewModel<*>>(){
            override fun loadInitial(
                params: LoadInitialParams<Int>,
                callback: LoadInitialCallback<Int, MultiItemViewModel<*>>
            ) {

                var paramMap = hashMapOf<String, String>(
                    "page" to "1",
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString()
                )
                addSubscribe(

                    model.getDailyPersonRequest(paramMap)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                            override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                refreshCompleteLiveData.value = true

                                t.data?.let {
                                    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

                                    it?.records.forEach{ it1->
                                        var dailyListDate = DailyListDateViewModel(this@DailyListViewModel, it1)
                                        dailyListDate.multiItemType(ConstantGlobal.DAILY_LIST_DATE)
                                        observableList.add(dailyListDate)

                                        var dailyListContent = DailyListContentViewModel(this@DailyListViewModel, it1)
                                        dailyListContent.multiItemType(ConstantGlobal.DAILY_LIST_CONTENT)
                                        observableList.add(dailyListContent)
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
                callback: LoadCallback<Int, MultiItemViewModel<*>>
            ) {

                var paramMap = hashMapOf<String, String>(
                    "page" to params.key.toString(),
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString()
                )

                addSubscribe(
                    model.getDailyPersonRequest(paramMap)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                            override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                t.data?.let {
                                    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

                                    it?.records.forEach{ it1->
                                        var dailyListDate = DailyListDateViewModel(this@DailyListViewModel,it1)
                                        dailyListDate.multiItemType(ConstantGlobal.DAILY_LIST_DATE)
                                        observableList.add(dailyListDate)

                                        var dailyListContent = DailyListContentViewModel(this@DailyListViewModel, it1)
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

    /**
     * 保存点击的Item信息
     */
    var itemListContentViewModel: DailyListContentViewModel ? = null
    fun setItemViewModel(dailyListContentViewModel: DailyListContentViewModel) {
        itemListContentViewModel = dailyListContentViewModel
    }

    fun reFreshItemData() {
        if(itemListContentViewModel!=null){
            val date = itemListContentViewModel?.record?.get()?.date.toString() ?:return
            addSubscribe(
                model.getDailyPersonDayRequest(date)
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

