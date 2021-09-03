package com.daqsoft.module_mine.viewmodel.itemviewmodel

import android.app.Application
import androidx.databinding.Observable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.viewmodel.MineViewModel
import com.daqsoft.module_mine.viewmodel.WorkCalendarViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作会议 itemViewModel
 */
class WorkMeetingItemViewModel(
    private val workCalendarViewModel : WorkCalendarViewModel,
    val today : Boolean = true
) : MultiItemViewModel<WorkCalendarViewModel>(workCalendarViewModel) {

    /**
     * 会议总数
     */
    var total = ObservableField<Int>(0)

    // 给RecyclerView添加ObservableList
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()

    // 给RecyclerView添加ItemBinding
    var itemBinding: ItemBinding<ItemViewModel<*>> =
        ItemBinding.of(BR.viewModel, R.layout.recyclerview_work_meeting_item_content)


    val todayObservable = ObservableField<Boolean>(true)

    init {
        todayObservable.set(today)
    }
}

