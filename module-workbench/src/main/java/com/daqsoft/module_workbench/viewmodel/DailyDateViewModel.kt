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
import com.daqsoft.module_workbench.repository.pojo.vo.DailyDataBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyListBeanItem
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListContentViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.DailyListDateViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.TeamHeadItemViewModel
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
class DailyDateViewModel :  ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)

    var isActivity:Boolean=true

    val chooseTag = ObservableField<String>("未知年份")


    val finishedLiveData = MutableLiveData<Boolean>()


    override fun onCreate() {
        initToolbar()
        initTopTime()

    }

    private fun initTopTime() {
        val cal: Calendar = Calendar.getInstance()
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH)+1
        chooseTag.set(year.toString()+"年"+month+"月")
    }

    private fun initToolbar() {
        setBackground(Color.WHITE)
        setTitleText("日报数据")
        setTitleTextColor(R.color.black_333333)
        setRightIcon2Visible(View.INVISIBLE)
        setRightIconVisible(View.INVISIBLE)
//        setRightIconSrc(R.mipmap.rbsj_card)
    }

    override fun backOnClick() {
        super.backOnClick()
        finishedLiveData.value=true
    }


    override fun rightIconOnClick() {
    }


    /**
     * 获取当月的月季统计数据
     */
    var selectedData :String ?= ""

    val dailyDataBean = MutableLiveData<DailyDataBean>()

    val dailyDataLiveData = ObservableField<DailyDataBean>()

    val tvStateLiveData = ObservableField<String>("--")

    val tvColorLiveData = ObservableField<Int>()

    fun getMonthData(curYear: Int, curMonth: Int) {
        var month= StringBuilder()
        if(curMonth.toString().length==1){
            month.append("$curYear-0$curMonth")
        }else{
            month.append("$curYear-$curMonth")
        }

        addSubscribe(
            model.getPersonCalender(month.toString())
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DailyDataBean>>() {
                    override fun onSuccess(t: AppResponse<DailyDataBean>) {
                        dailyDataBean.value=t?.data
                        dailyDataLiveData.set(t?.data)
                        unDataTodayReport()
                    }
                })
        )
    }

    // 1显示进度条 2显示周末 3显示已提交 4.显示未提交（需求修改 只显示进度条）
    val visibleData = ObservableField<Int>()

    fun unDataTodayReport() {
        dailyDataLiveData.get()?.dates?.forEachIndexed { index, bean ->
            //  获取当日日报提交状态
            if(bean?.date==selectedData){

                if(bean.isWeekend!!){
                    visibleData.set(1)
                    tvStateLiveData.set("--")
                    tvColorLiveData.set(Color.parseColor("#333333"))
                }else{
                    if(bean.isReported==true){
                        visibleData.set(1)
                        tvStateLiveData.set("已提交")
                        tvColorLiveData.set(Color.parseColor("#23c070"))
                    }else{
                        visibleData.set(1)
                        tvStateLiveData.set("未提交")
                        tvColorLiveData.set(Color.parseColor("#ff9e05"))
                    }
                }

                if(dailyDataLiveData.get()?.isShowDate==true){
                    visibleData.set(1)
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

