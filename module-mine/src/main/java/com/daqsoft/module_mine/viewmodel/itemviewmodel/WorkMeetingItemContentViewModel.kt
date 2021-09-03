package com.daqsoft.module_mine.viewmodel.itemviewmodel

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.pojo.vo.MeetingInfo
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作会议内容 itemViewModel
 */
class WorkMeetingItemContentViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel,
    val meetingInfo:MeetingInfo,
    val first : Boolean? = false,
    val last : Boolean? = false
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel){

    val meetingInfoObservable = ObservableField<MeetingInfo>()

    val firstObservable = ObservableField<Boolean>(false)
    val lastObservable = ObservableField<Boolean>(false)


    val selectedObservable = ObservableField<Boolean>(false)

    init {
        meetingInfoObservable.set(meetingInfo)

        firstObservable.set(first)
        lastObservable.set(last)
    }


    /**
     * item  点击事件
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Web.PAGER_WEB).withString("url",meetingInfo.url).navigation()
    })
}


