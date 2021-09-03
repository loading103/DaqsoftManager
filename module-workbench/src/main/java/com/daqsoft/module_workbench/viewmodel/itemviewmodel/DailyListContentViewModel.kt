package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.DailyListViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
 class DailyListContentViewModel(
    private val dailyListViewModel: DailyListViewModel,
    it: DialyRecord
) : MultiItemViewModel<DailyListViewModel>(dailyListViewModel){


    val record = ObservableField<DialyRecord>()
    var  show =  ObservableField<Boolean>(true)

    var mTodyPlan = ObservableField<Spanned>()
    var mTomorrowPlan = ObservableField<Spanned>()
    var mLeaveQuestion = ObservableField<Spanned>()
    var dayCheck = ObservableField<Spanned>()


    val todayContent = ObservableField<String>()
    val todayEdit = ObservableField<Int>(View.GONE)
    val dataVisible = ObservableField<Int>(View.VISIBLE)

    //日报未提交 且不是今天
    val noSubmitVisible = ObservableField<Int>(View.GONE)

    val maxline = ObservableField<Int>(3)
    val headurl = ObservableField<String>("")
    init {
        headurl.set(it.employeeAvatar)
        handleData(it)
    }



    fun notifyItemData(date: DialyRecord) {
        handleData(date)
    }



    /**
     * item 点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

        if(noSubmitVisible.get()==View.VISIBLE){
            return@BindingAction
        }
        dailyListViewModel.setItemViewModel(this@DailyListContentViewModel)
        var website:String=""
        if(it.reportMode=="AUTO"){
            if(it.date.isNullOrBlank()){
                website=HttpGlobal.DAILY_AUTO_DETAIL+"null"
            }else{
                website=HttpGlobal.DAILY_AUTO_DETAIL+it.date
            }
        }else{
            if(it.date.isNullOrBlank()){
                website=HttpGlobal.DAILY_DETAIL+"null"
            }else{
                website=HttpGlobal.DAILY_DETAIL+it.date
            }
        }
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()

    })

    /**
     * item 点击事件
     */
    val onSubmitClick = BindingCommand<Unit>(BindingAction {


        if(it.reportMode=="AUTO"){
            ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_RULE).withInt("id", it.id).navigation()
        }else{
            dailyListViewModel.setItemViewModel(this@DailyListContentViewModel)
            var website:String=HttpGlobal.DAILY_ADD_EDIT
            ARouter
                .getInstance()
                .build(ARouterPath.Web.PAGER_WEB)
                .withString("url",website)
                .navigation()
        }
    })


    private fun handleData(it: DialyRecord) {
        record.set(it)
        if (it.read == 0) {
            show.set(true)
        } else {
            show.set(false)
        }
        if (!TextUtils.isEmpty(it.dailyCheck )) {
            dayCheck.set(Html.fromHtml(it.dailyCheck))
        } else {
            dayCheck.set(Html.fromHtml("无"))
        }
        if (TextUtils.isEmpty(it.todayInfo )) {
            mTodyPlan.set(Html.fromHtml("无"))
        } else {
            mTodyPlan.set(Html.fromHtml(it.todayInfo))
        }
        if (TextUtils.isEmpty(it.tomorrowPlan )) {
            mTomorrowPlan.set(Html.fromHtml("无"))
        } else {
            mTomorrowPlan.set(Html.fromHtml(it.tomorrowPlan))
        }

        if (TextUtils.isEmpty(it.leaderHelp )) {
            mLeaveQuestion.set(Html.fromHtml(""))
        } else {
            mLeaveQuestion.set(Html.fromHtml(it.leaderHelp))
        }

        // 判断今天是不是有数据
        var haveTodayData:Boolean= TextUtils.isEmpty(it.todayInfo ) &&  TextUtils.isEmpty(it.tomorrowPlan ) &&  TextUtils.isEmpty(it.leaderHelp )


        //获取今天的日期
        val now = Date()
        var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        val nowDay = sdf.format(now)
        if(it.date == nowDay){
            maxline.set(100)
            if(haveTodayData){
                todayEdit.set(View.VISIBLE)
                todayContent.set(it.getTopContent())
                dataVisible.set(View.GONE)
                noSubmitVisible.set(View.GONE)
            }else{
                todayEdit.set(View.GONE)
                noSubmitVisible.set(View.GONE)
                dataVisible.set(View.VISIBLE)
            }
        }else{
            maxline.set(3)
            if(haveTodayData){
                todayEdit.set(View.GONE)
                noSubmitVisible.set(View.VISIBLE)
                dataVisible.set(View.GONE)
            }else{
                todayEdit.set(View.GONE)
                noSubmitVisible.set(View.GONE)
                dataVisible.set(View.VISIBLE)
            }

        }
    }

}