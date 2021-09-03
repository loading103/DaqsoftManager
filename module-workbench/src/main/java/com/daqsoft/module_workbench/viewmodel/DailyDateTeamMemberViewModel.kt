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
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListDateViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.TeamHeadItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.lang.StringBuilder
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 */
class DailyDateTeamMemberViewModel :  BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var isActivity:Boolean=true

    val chooseTag = ObservableField<String>("未知年份")


    val finishedLiveData = MutableLiveData<Boolean>()


    override fun onCreate() {
        initTopTime()

    }

    private fun initTopTime() {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)+1
        chooseTag.set(year.toString()+"年"+month+"月")
    }



    /**
     * 获取当月的月季统计数据
     */
    var selectedData :String ?= ""

    /**
     * 选中当天的数据
     */
    val dailyData = MutableLiveData<DailyTeamDataBean>()

    val dailyDataBean = MutableLiveData<MutableList<DailyTeamDataBean>>()

    val dailyDataLiveData = ObservableField<MutableList<DailyTeamDataBean>>()

    val tvColorLiveData = ObservableField<Int>()

    fun getMonthData(curYear: Int, curMonth: Int) {
        var month= StringBuilder()
        if(curMonth.toString().length==1){
            month.append("$curYear-0$curMonth")
        }else{
            month.append("$curYear-$curMonth")
        }

        addSubscribe(
            model.getMemberCalender(month.toString())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<DailyTeamDataBean>>>() {
                    override fun onSuccess(t: AppResponse<List<DailyTeamDataBean>>) {
                        dailyDataBean.value=t?.data?.toMutableList()
                        dailyDataLiveData.set(t?.data?.toMutableList())
                        unDataTodayReport()
                    }
                })
        )
    }

    // 1显示进度条 2显示周末 3显示已提交 4.显示未提交（需求修改 只显示进度条）
    val visibleData = ObservableField<Int>(1)
    val tvStateLiveData = ObservableField<String>("--")
    val tvYtjLiveData = ObservableField<String>("0")
    val tvNtjLiveData = ObservableField<String>("0")

    fun unDataTodayReport() {
        dailyDataLiveData.get()?.forEachIndexed { index, bean ->
            //  获取当日日报提交状态
            if(bean?.date==selectedData){
                dailyData.value=bean
                if(bean.isReported!!){
                    visibleData.set(1)
                    val totle = bean.reported?.toInt()!! + bean.unreported?.toInt()!!
                    tvStateLiveData.set(totle.toString())
                    tvYtjLiveData.set(bean.reported.toString())
                    tvNtjLiveData.set(bean.unreported.toString())
                }else{
                    if(bean.isWeekend!!){
                        visibleData.set(2)
                        tvStateLiveData.set("--")
                        tvYtjLiveData.set("--")
                        tvNtjLiveData.set("--")
                    }else{
                        visibleData.set(1)
                        val totle = bean.reported?.toInt()!! + bean.unreported?.toInt()!!
                        tvStateLiveData.set(totle.toString())
                        tvYtjLiveData.set(bean.reported.toString())
                        tvNtjLiveData.set(bean.unreported.toString())
                    }
                }
            }
        }
    }

    /**
     * 日报时间点击选择
     */
    var showOnClickLiveData  = MutableLiveData<Boolean>()
    val showOnClick = BindingCommand<Unit>(BindingAction {
        showOnClickLiveData.value=true
    })
}

