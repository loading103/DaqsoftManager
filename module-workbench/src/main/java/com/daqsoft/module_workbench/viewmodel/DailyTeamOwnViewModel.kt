package com.daqsoft.module_workbench.viewmodel

import android.app.Application
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
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DialyListBeanItem
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyTeamOtherItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyTeamOwnItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.util.*
import kotlin.collections.HashMap

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailyTeamOwnViewModel :  BaseViewModel<WorkBenchRepository>, Paging2Utils<MultiItemViewModel<*>> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var fragmentId : String ?  ="1"
    var menuIds : String ?  ="0"

    val chooseTag = ObservableField<String>("未知年份")

    val startTimeLiveData = ObservableField<String>("")

    val endTimeLiveData = ObservableField<String>("")

    val finishedLiveData = MutableLiveData<Boolean>()

    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.DAILY_LIST_CONTENT -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_team_own_content
                )
                ConstantGlobal.DAILY_LIST_DATE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_daily_list_date
                )
            }
        }


    override fun onCreate() {
        initTopTime()
    }

    private fun initTopTime() {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        chooseTag.set(year.toString()+"年")
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

                var paramMap =  getParamMap("1")
                addSubscribe(

                    model.getAllMyTeamReport(paramMap!!)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                            override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                refreshCompleteLiveData.value = true

                                t.data?.let {
                                    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
                                    it.records?.forEach{
                                        var dailyListContent = DailyTeamOwnItemViewModel(this@DailyTeamOwnViewModel, it)
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

                var paramMap =  getParamMap( params.key.toString())
                addSubscribe(
                    model.getAllMyTeamReport(paramMap!!)
                        .compose(RxUtils.exceptionTransformer())
                        .compose(RxUtils.schedulersTransformer())
                        .subscribeWith(object : AppDisposableObserver<AppResponse<DialyListBeanItem>>() {
                            override fun onSuccess(t: AppResponse<DialyListBeanItem>) {
                                t.data?.let {
                                    val observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
                                    it.records?.forEach{
                                        var dailyListContent =DailyTeamOwnItemViewModel(this@DailyTeamOwnViewModel, it)
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


    fun setTimeData(startTime: String, endTime: String,needload:Boolean?=true) {
        if(startTime.isNullOrBlank()){
            return
        }
        if(!endTime.isNullOrBlank()){
            startTimeLiveData.set(startTime.replace(".","-"))
            endTimeLiveData.set(endTime.replace(".","-"))
        }else{
            val split = startTime.split(".")
            startTimeLiveData.set(startTime.replace(".","-")+"-01")
            val monthOfDay = getMonthOfDay(split[0].toInt(), split[1].toInt())
            endTimeLiveData.set(startTime.replace(".","-")+"-${monthOfDay}")
        }
        if(needload!!){
            dataSource?.invalidate()
        }
    }




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
     * 不同的fragment组装不同数据
     */
    private fun getParamMap(page: String): HashMap<String, String>? {
        var paramMap : HashMap<String, String>? =null
        when(fragmentId){
            "1"->{
                paramMap = hashMapOf<String, String>("publishStatus" to "", "submitStatus" to "",
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString(),
                    "page" to page,
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "menuId" to menuIds.toString()
                )
            }
            "2"->{
                paramMap = hashMapOf<String, String>("publishStatus" to "", "submitStatus" to false.toString(),
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString(),
                    "page" to page,
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "menuId" to menuIds.toString()
                )
            }
            "3"->{
                paramMap = hashMapOf<String, String>("publishStatus" to "", "submitStatus" to true.toString(),
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString(),
                    "page" to page,
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "menuId" to menuIds.toString()
                )
            }
            "4"->{
                paramMap = hashMapOf<String, String>("publishStatus" to true.toString(), "submitStatus" to "",
                    "startDate" to startTimeLiveData.get().toString(),
                    "endDate" to endTimeLiveData.get().toString(),
                    "page" to page,
                    "size" to ConstantGlobal.INITIAL_PAGE_SIZE.toString(),
                    "menuId" to menuIds.toString()
                )
            }
        }

        return paramMap
    }

    fun setMenuId(id: String?, id1: String?) {
        fragmentId=id
        menuIds=id
    }



    /**
     * 保存点击的Item信息
     */
    var itemListContentViewModel: DailyTeamOwnItemViewModel? = null
    fun setItemViewModel(dailyListContentViewModel: DailyTeamOwnItemViewModel) {
        itemListContentViewModel = dailyListContentViewModel
    }

    fun reFreshItemData() {
        if(itemListContentViewModel!=null){
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

