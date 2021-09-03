package com.daqsoft.module_workbench.viewmodel.itemviewmodel

import android.text.Html
import android.text.Spanned
import android.text.TextUtils
import android.view.View
import androidx.databinding.ObservableField
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_workbench.repository.pojo.vo.DialyMemberBean
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRecord
import com.daqsoft.module_workbench.viewmodel.DailySearchViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 */
class DailySearchContentViewModel(
    private val dailySearchViewModel: DailySearchViewModel,
    it: DialyMemberBean
) : MultiItemViewModel<DailySearchViewModel>(dailySearchViewModel){


    val datas = ObservableField<DialyMemberBean>()

    var  show =  ObservableField<Boolean>(true)

    var mTodyPlan = ObservableField<Spanned>()
    var mTomorrowPlan = ObservableField<Spanned>()
    var mLeaveQuestion = ObservableField<Spanned>()
    var dayCheck = ObservableField<Spanned>()

    var  haveSubmit =  ObservableField<Boolean>(true)

    init {
        datas.set(it)
        if (TextUtils.isEmpty(it?.todayInfo )) {
            mTodyPlan.set(Html.fromHtml("无"))
        } else {
            mTodyPlan.set(Html.fromHtml(it?.todayInfo))
        }
        if (TextUtils.isEmpty(it?.tomorrowPlan )) {
            mTomorrowPlan.set(Html.fromHtml("无"))
        } else {
            mTomorrowPlan.set(Html.fromHtml(it?.tomorrowPlan))
        }

        if (TextUtils.isEmpty(it?.leaderHelp )) {
            mLeaveQuestion.set(Html.fromHtml(""))
        } else {
            mLeaveQuestion.set(Html.fromHtml(it?.leaderHelp))
        }

        if(TextUtils.isEmpty(it?.todayInfo ) && TextUtils.isEmpty(it?.tomorrowPlan ) && TextUtils.isEmpty(it?.leaderHelp )){
            haveSubmit.set(false)
        }else{
            haveSubmit.set(true)
        }
    }



    /**
     * 搜索出来的不进去详情界面  直接显示完成
     */
    val onItemClick = BindingCommand<Unit>(BindingAction {

    })
}