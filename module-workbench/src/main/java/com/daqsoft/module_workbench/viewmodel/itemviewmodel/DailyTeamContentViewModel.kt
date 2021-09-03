package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.DailyMemberViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

class DailyTeamContentViewModel(
    private val dailyListViewModel: DailyMemberViewModel,
    it: DialyMemberBean
) : MultiItemViewModel<DailyMemberViewModel>(dailyListViewModel){

    val record = ObservableField<DialyRecord>()

    val datas = ObservableField<DialyMemberBean>()

    var  show =  ObservableField<Boolean>(true)

    var mTodyPlan = ObservableField<Spanned>()
    var mTomorrowPlan = ObservableField<Spanned>()
    var mLeaveQuestion = ObservableField<Spanned>()
    var dayCheck = ObservableField<Spanned>()

    init {
        datas.set(it)
        record.set(it.dayReport)
        if (TextUtils.isEmpty(it.dayReport?.todayInfo )) {
            mTodyPlan.set(Html.fromHtml("无"))
        } else {
            mTodyPlan.set(Html.fromHtml(it.dayReport?.todayInfo))
        }
        if (TextUtils.isEmpty(it.dayReport?.tomorrowPlan )) {
            mTomorrowPlan.set(Html.fromHtml("无"))
        } else {
            mTomorrowPlan.set(Html.fromHtml(it.dayReport?.tomorrowPlan))
        }

        if (TextUtils.isEmpty(it.dayReport?.leaderHelp )) {
            mLeaveQuestion.set(Html.fromHtml(""))
        } else {
            mLeaveQuestion.set(Html.fromHtml(it.dayReport?.leaderHelp))
        }
    }



    /**
     * item 点击事件
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {
//        record.get()?.id?.let { it1 ->
//            ARouter.getInstance().build(ARouterPath.Workbench.PAGER_DAILY_DETAIL).withInt("id", it1).navigation()
//        }

//        var website:String=""
//        if(it.dayReport?.reportMode=="AUTO"){
//            if(dailyListViewModel?.timeData?.get().isNullOrBlank()){
//                website=HttpGlobal.DAILY_AUTO_DETAIL+"null"
//            }else{
//                website=HttpGlobal.DAILY_AUTO_DETAIL+dailyListViewModel?.timeData?.get()
//            }
//        }else{
//            if(dailyListViewModel?.timeData?.get().isNullOrBlank()){
//                website=HttpGlobal.DAILY_DETAIL+"null"
//            }else{
//                website=HttpGlobal.DAILY_DETAIL+dailyListViewModel?.timeData?.get()
//            }
//        }
//        ARouter
//            .getInstance()
//            .build(ARouterPath.Web.PAGER_WEB)
//            .withString("url",website)
//            .navigation()

    })

}