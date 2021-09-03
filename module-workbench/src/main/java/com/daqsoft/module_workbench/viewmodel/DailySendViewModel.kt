package com.daqsoft.module_workbench.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DialyProjec
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEventBusCore
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

/**
 * 发送日报
 */
class DailySendViewModel: ToolbarViewModel<WorkBenchRepository> {

    companion object{
        const val DAILY_CONTENT = "daily_content"
    }


    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository) : super(application, workBenchRepository)

    var id : String = ""

//    // 用於發送成功之後重新賦值
//    var saveId : String = ""
    /**
     * 今日工作完成情况
     */
    val todayObservable = ObservableField<String>()

    /**
     * 明日工作计划
     */
    val tommodayObservable = ObservableField<String>()
    /**
     * 试试@领导并输入遇到的问题
     */
    val questObservable = ObservableField<String>()

    /**
     * 小时
     */
    val longTimeObservable = ObservableField<String>()
    /**
     * 所属项目
     */
    val nameObservable = ObservableField<String>()
    /**
     * 所属项目liveData
     */
    val nameLiveData = MutableLiveData<String>()
    /**
     * 日报时间
     */
    val timeObservable = ObservableField<String>()
    /**
     * 日报时间liveData
     */
    val timeLiveData = MutableLiveData<Unit>()

    override fun onCreate() {
        super.onCreate()
        initToolbar()
        initSpannerData()


        val dailyContent = DataStoreUtils.getString(DAILY_CONTENT)
        if (dailyContent.isNotBlank()){
            val type = object : TypeToken<HashMap<String, String?>>() {}.type
            val paramMap = Gson().fromJson<HashMap<String, String?>>(dailyContent, type)
            if (paramMap.isNotEmpty()){
                todayObservable.set(paramMap["todayInfo"])
                tommodayObservable.set(paramMap["tomorrowPlan"])
                questObservable.set(paramMap["problemForManager"])
                longTimeObservable.set(paramMap["workHour"])
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val paramMap = hashMapOf<String, String?>(
            "todayInfo" to todayObservable.get(),
            "tomorrowPlan" to tommodayObservable.get(),
            "problemForManager" to questObservable.get(),
            "workHour" to longTimeObservable.get()
        )
        DataStoreUtils.put(DAILY_CONTENT,Gson().toJson(paramMap))
    }


    private fun initToolbar() {
        setTitleText("日报")
        timeObservable.set(stampToTime(System.currentTimeMillis().toString()))
    }


    val nameSpannable = ObservableField<SpannableStringBuilder>()
    val timeSpannable = ObservableField<SpannableStringBuilder>()
    val todaySpannable = ObservableField<SpannableStringBuilder>()
    val tommoSpannable = ObservableField<SpannableStringBuilder>()
    val gsSpannable = ObservableField<SpannableStringBuilder>()
    private fun initSpannerData() {
        val nameString = SimplifySpanBuild()
            .append(SpecialTextUnit("所属项目").setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
            .append(SpecialTextUnit(" *").setTextColor(getApplication<Application>().resources.getColor(R.color.color_ff6d6b)))
            .build()
        nameSpannable.set(nameString)
        val   timeString = SimplifySpanBuild()
            .append(SpecialTextUnit("日报时间").setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
            .append(SpecialTextUnit(" *").setTextColor(getApplication<Application>().resources.getColor(R.color.color_ff6d6b)))
            .build()
        timeSpannable.set(timeString)
        val todayString = SimplifySpanBuild()
            .append(SpecialTextUnit("今日工作完成情况").setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
            .append(SpecialTextUnit(" *").setTextColor(getApplication<Application>().resources.getColor(R.color.color_ff6d6b)))
            .build()
        todaySpannable.set(todayString)
        val  tommoString = SimplifySpanBuild()
            .append(SpecialTextUnit("明日工作计划").setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
            .append(SpecialTextUnit(" *").setTextColor(getApplication<Application>().resources.getColor(R.color.color_ff6d6b)))
            .build()
        tommoSpannable.set(tommoString)
        val gsString = SimplifySpanBuild()
            .append(SpecialTextUnit("工时").setTextColor(getApplication<Application>().resources.getColor(R.color.black_333333)))
            .build()
        gsSpannable.set(gsString)
    }

    /**
     * 所属项目点击选择
     */
    @SuppressLint("NullSafeMutableLiveData")
    val nameOnClick = BindingCommand<Unit>(BindingAction {
        nameLiveData.value = null
    })

    /**
     * 日报时间点击选择
     */
    @SuppressLint("NullSafeMutableLiveData")
    val timeOnClick = BindingCommand<Unit>(BindingAction {
        timeLiveData.value = null
    })

    /**
     * 日报时间点击选择
     */
    val sendOnClick = BindingCommand<Unit>(BindingAction {
        sendDailyContent()
    })


    /**
     * 监听@
     */
    var textChangedListener = BindingCommand<String>(BindingConsumer {
        if (!it.isNullOrEmpty() && it.toString().endsWith("@") ){
        }
    })


    /**
     * 获取所属项目
     */
    val typeLiveData = MutableLiveData<MutableList<DialyProjec>>()
    fun getDailyProject() {
        addSubscribe(
            model
                .getDailyProject()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<DialyProjec>>>() {
                    override fun onSuccess(t: AppResponse<List<DialyProjec>>) {
                        typeLiveData.value=t.data?.toMutableList()
                    }
                })
        )
    }

    /**
     * 发送日报
     */

    val refreshLiveData = MutableLiveData<String>()
    fun sendDailyContent(){
        if(todayObservable.get().isNullOrEmpty()){
            ToastUtils.showShort("请输入今日工作完成情况")
            return
        }
        if(tommodayObservable.get().isNullOrEmpty()){
            ToastUtils.showShort("请输入明日工作计划")
            return
        }

        if (!longTimeObservable.get().isNullOrEmpty() && longTimeObservable.get()?.toFloat()!! >8){
            ToastUtils.showShort("最大不能超过8小时")
            return
        }
        var paramMap = hashMapOf<String, String?>(
            "projectId" to id,
            "reportDate" to timeObservable.get(),
            "todayInfo" to todayObservable.get()?.replace("\n","<br>")?.replace(" ","&nbsp;"),
            "tomorrowPlan" to tommodayObservable.get()?.replace("\n","<br>")?.replace(" ","&nbsp;"),
            "problemForManager" to questObservable.get()?.replace("\n","<br>")?.replace(" ","&nbsp;"),
            "workHour" to longTimeObservable.get()
        )

        addSubscribe(
            model
                .sendDailyData(paramMap)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post{
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        ToastUtils.showLong("发送成功")
                        LiveEventBus.get(LEBKeyGlobal.DAILY_SEND_SUCCESSFULLY).post(true)
                        todayObservable.set("")
                        tommodayObservable.set("")
                        questObservable.set("")
                        longTimeObservable.set("")
//                        id=saveId
//                        nameObservable.set("默认当前项目")
                        refreshLiveData.value=null
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }

    fun stampToTime(stamp: String): String {
        val   type = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }

}