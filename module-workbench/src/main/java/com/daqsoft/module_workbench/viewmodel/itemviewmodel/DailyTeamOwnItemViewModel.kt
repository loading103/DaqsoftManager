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
import com.daqsoft.module_workbench.viewmodel.DailyTeamOwnViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 */
class DailyTeamOwnItemViewModel(
    private val dailyListViewModel: DailyTeamOwnViewModel,
    it: DialyRecord
) : MultiItemViewModel<DailyTeamOwnViewModel>(dailyListViewModel){

    val mDate = ObservableField<String>()
    val day = ObservableField<String>()
    val month = ObservableField<String>()

    val record = ObservableField<DialyRecord>()
    var  show =  ObservableField<Boolean>(true)

    var mTodyPlan = ObservableField<Spanned>()
    var mTomorrowPlan = ObservableField<Spanned>()
    var mLeaveQuestion = ObservableField<Spanned>()
    var dayCheck = ObservableField<Spanned>()


    val todayContent = ObservableField<String>()
    val todayEdit = ObservableField<Int>(View.GONE)
    val dataVisible = ObservableField<Boolean>(true)
    val noSubmitVisible = ObservableField<Boolean>(true)
    val todayAdd = ObservableField<Int>(View.GONE)
    val maxLine = ObservableField<Int>(3)
    val headurl = ObservableField<String>("")
    init {
        headurl.set(it.employeeAvatar)
        handleData(it)
    }
    /**
     * 刷新单个item
     */
    fun notifyItemData(date: DialyRecord) {
        handleData(date)
    }


    /**
     * 展示内容
     * showUndate是不是可以修改
     */
    fun showDataView(showUndate:Boolean){
        if(showUndate){
            todayEdit.set(View.VISIBLE)
        }else{
            todayEdit.set(View.GONE)
        }
        noSubmitVisible.set(false)
        todayAdd.set(View.GONE)
        dataVisible.set(true)
    }

    /**
     * 展示ADDView
     */
    fun showAddView(it: DialyRecord) {
        todayEdit.set(View.GONE)
        todayContent.set(it.getTeamContent())
        dataVisible.set(false)
        noSubmitVisible.set(false)
        todayAdd.set(View.VISIBLE)
    }


    /**
     * 团队日报未提交
     */
    val onNoSubmitClick = BindingCommand<Unit>(BindingAction {
        dailyListViewModel.setItemViewModel(this@DailyTeamOwnItemViewModel)
        goAddView()
    })



    /**
     * 赶快去编辑(新增)
     */
    val onSubmitClick = BindingCommand<Unit>(BindingAction {
        dailyListViewModel.setItemViewModel(this@DailyTeamOwnItemViewModel)
        goAddView()
    })
    /**
     * 赶快去编辑(修改)
     */
    val callOnClick = BindingCommand<String>(BindingAction {
        dailyListViewModel.setItemViewModel(this@DailyTeamOwnItemViewModel)
        goAddView()
    })



    /**
     * item 点击事件
     * todo qql
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

        dailyListViewModel.setItemViewModel(this@DailyTeamOwnItemViewModel)
        //撤回的
        if(!record.get()?.publishStatus!! && !record.get()?.submitStatus!!){
            goAddView()
            return@BindingAction
        }

        //已发布的 本地原生
        if(record.get()?.publishStatus!!  ){
            record.get()?.id?.let { it1 ->
                ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DETAIL).withInt("id", it1).navigation()
            }
            return@BindingAction
        }

        //已提交的
        if(record.get()?.submitStatus!!  ){
            var website:String= HttpGlobal.DAILY_TEAM_DETAIL+record.get()?.id+"/"+record.get()?.reportDate
            ARouter
                .getInstance()
                .build(ARouterPath.Web.PAGER_WEB)
                .withString("url",website)
                .navigation()
        }

        //三者都没 跳转到新增修改界面
        if(!record.get()?.recallStatus!! &&  !record.get()?.publishStatus!! && !record.get()?.submitStatus!!){
            goAddView()
        }

    })


    /**
     * 跳转到新增修改界面去
     */
    fun goAddView(){
        var website:String= HttpGlobal.DAILY_TEAM_ADD+record.get()?.id+"/"+record.get()?.reportDate
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    }


    private fun handleData(it: DialyRecord) {
        record.set(it)
        mDate.set(it.reportDate)
        if (!TextUtils.isEmpty(it.dailyCheck)) {
            dayCheck.set(Html.fromHtml(it.dailyCheck))
        } else {
            dayCheck.set(Html.fromHtml("无"))
        }
        if (TextUtils.isEmpty(it.todayInfo)) {
            mTodyPlan.set(Html.fromHtml("无"))
        } else {
            mTodyPlan.set(Html.fromHtml(it.todayInfo))
        }
        if (TextUtils.isEmpty(it.tomorrowPlan)) {
            mTomorrowPlan.set(Html.fromHtml("无"))
        } else {
            mTomorrowPlan.set(Html.fromHtml(it.tomorrowPlan))
        }

        if (TextUtils.isEmpty(it.needHelp)) {
            mLeaveQuestion.set(Html.fromHtml(""))
        } else {
            mLeaveQuestion.set(Html.fromHtml(it.needHelp))
        }
        var cal: Calendar = Calendar.getInstance()
        cal.time = SimpleDateFormat("yyyy-MM-dd").parse(it?.reportDate)

        // 获取今天的日期，是今天判断有没有提交 有显示内容 如果是手动编辑的 还要显示编辑按钮，没提交显示未提交
        val currentData = SimpleDateFormat("yyyy-MM-dd").format(Date())

        if (it.reportDate == currentData) {
            //  顶部时间
            day.set("今天")
            month.set(" ")
            maxLine.set(3)

            //三个为false 显示新增
            if (!it.publishStatus && !it.recallStatus && !it.submitStatus) {
                showAddView(it)
            }
            // 只要是撤回就是修改,没有撤回都是显示内容
            else if (it.recallStatus) {
                showDataView(true)
            } else
                showDataView(false)
        }else {
            //  顶部时间
            maxLine.set(3)
            day.set("${cal.get(Calendar.DAY_OF_MONTH)}")
            month.set("/${cal.get(Calendar.MONTH) + 1}月")

            val haveSubmit = it.dailyCheck.isNullOrEmpty() && it.todayInfo.isNullOrEmpty() && it.tomorrowPlan.isNullOrEmpty() && it.needHelp.isNullOrEmpty()
            if(!haveSubmit){
                todayEdit.set(View.GONE)
                noSubmitVisible.set(false)
                todayAdd.set(View.GONE)
                dataVisible.set(true)
            }else{
                todayEdit.set(View.GONE)
                todayAdd.set(View.GONE)
                dataVisible.set(false)
                noSubmitVisible.set(true)
            }
            //三个为false 显示新增
            if (!it.publishStatus && !it.recallStatus && !it.submitStatus) {
                todayEdit.set(View.GONE)
                todayAdd.set(View.GONE)
                dataVisible.set(false)
                noSubmitVisible.set(true)
            }

        }

    }

}