package com.daqsoft.module_workbench.viewmodel.itemviewmodel

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
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:11
 * @author zp
 * @describe 通知公告 item viewModel
 */
class DailyListDateViewModel(
    private val dailyListViewModel: DailyListViewModel,
    val date: DialyRecord
) : MultiItemViewModel<DailyListViewModel>(dailyListViewModel){


    val mDate = ObservableField<String>()

    val day = ObservableField<String>()
    val month = ObservableField<String>()
    val today = ObservableField<String>()
    val todayEdit = ObservableField<Int>(View.GONE)
    init {
        mDate.set(date.date)

        var sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        var cal: Calendar = Calendar.getInstance()
        cal.time = sdf.parse(date.date)

        //获取今天的日期
        val now = Date()
        val nowDay = sdf.format(now)
        if(date.date == nowDay){
            day.set("今天")
            month.set(" ")

            val noSubmit = TextUtils.isEmpty(date.todayInfo) && TextUtils.isEmpty(date.tomorrowPlan) && TextUtils.isEmpty(date.leaderHelp)

            //已经提交了才显示修改
            if(date.reportMode!="AUTO" && !noSubmit){
                todayEdit.set(View.VISIBLE)
            }else{
                todayEdit.set(View.GONE)
            }
        }else{
            day.set("${cal.get(Calendar.DAY_OF_MONTH)}")
            month.set("/${cal.get(Calendar.MONTH)+1}月")
        }
    }
    /**
     * 编辑今天的日报
     */
    val callOnClick = BindingCommand<String>(BindingAction {

        var website:String= HttpGlobal.DAILY_ADD_EDIT
        ARouter
            .getInstance()
            .build(ARouterPath.Web.PAGER_WEB)
            .withString("url",website)
            .navigation()
    })
}