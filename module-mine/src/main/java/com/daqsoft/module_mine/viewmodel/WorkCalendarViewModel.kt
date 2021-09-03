package com.daqsoft.module_mine.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.bo.WorkCalendar
import com.daqsoft.module_mine.repository.pojo.vo.AttendanceInfo
import com.daqsoft.module_mine.repository.pojo.vo.Day
import com.daqsoft.module_mine.repository.pojo.vo.MeetingInfo
import com.daqsoft.module_mine.repository.pojo.vo.TaskInfo
import com.daqsoft.module_mine.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.utils.RxUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.text.SimpleDateFormat
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 13/11/2020 下午 1:41
 * @author zp
 * @describe 工作日历 viewModel
 */
class WorkCalendarViewModel : BaseViewModel<MineRepository> {
    @ViewModelInject constructor(application: Application,mineRepository: MineRepository):super(application, mineRepository)

    companion object{
        const val CALENDAR = "calendar" // 日历
        const val CLOCK = "clock" // 打卡
        const val MEETING = "meeting" // 会议
        const val MISSION = "mission" // 任务
        const val ATTENDANCE = "attendance" // 考勤

        val notHoliday = arrayListOf("工作日","休息日")
    }


    // 给RecyclerView添加ObservableList
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    // 给RecyclerView添加ItemBinding
    var itemBinding: ItemBinding<MultiItemViewModel<*>> = ItemBinding.of{ itemBinding, position, item ->
        when (item.itemType as String) {
            CALENDAR -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_calendar_item)
            CLOCK -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_clock_item)
            MEETING -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_meeting_item)
            MISSION -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_mission_item)
            ATTENDANCE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_attendance_item)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_work_calendar_item)
        }
    }


    var calendar : WorkCalendarItemViewModel ? = null
    var clock : WorkClockItemViewModel ? = null
    var meeting : WorkMeetingItemViewModel ? = null
    var mission : WorkMissionItemViewModel ? = null
    var attendance : WorkAttendanceItemViewModel ? = null


    fun initData(workCalendar:String?,day: Day?){

        observableList.clear()

        if (day != null && day.dayInfo != "工作日"){
            calendar = WorkCalendarItemViewModel(this,day).apply { multiItemType(CALENDAR) }
            observableList.add(calendar)
        }

        when(workCalendar){
            WorkCalendar.TODAY.text->{
                if (day != null && day.dayInfo == "工作日"){
                    clock = WorkClockItemViewModel(this).apply { multiItemType(CLOCK) }
                    observableList.add(clock)
                }

                meeting = WorkMeetingItemViewModel(this).apply { multiItemType(MEETING) }
                observableList.add(meeting)

                mission = WorkMissionItemViewModel(this,true).apply {
                    multiItemType(MISSION)
                    if (day != null && day.dayInfo != "工作日"){
                        holidaysObservable.set(true)
                    }
                }
                observableList.add(mission)

                if (day != null && day.dayInfo == "工作日"){
                    attendance = WorkAttendanceItemViewModel(this).apply { multiItemType(ATTENDANCE) }
                    observableList.add(attendance)
                }
            }
            WorkCalendar.TOMORROW.text->{
                meeting = WorkMeetingItemViewModel(this,false).apply { multiItemType(MEETING) }
                observableList.add(meeting)
                mission = WorkMissionItemViewModel(this,false).apply { multiItemType(MISSION) }
                observableList.add(mission)

                if (day != null && day.dayInfo == "工作日"){
                    attendance = WorkAttendanceItemViewModel(this,false).apply { multiItemType(ATTENDANCE) }
                    observableList.add(attendance)
                }
            }
        }
    }


    /**
     * 获取会议信息
     */
    fun getMeetingInfo(workCalendar:String,day: Day){
        addSubscribe(
            model
                .getMeetingInfo(day.dateForParam)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<MeetingInfo>>>() {
                    @SuppressLint("SimpleDateFormat")
                    override fun onSuccess(t: AppResponse<List<MeetingInfo>>) {
                        t.data?.let { list ->
                            meeting?.run {
                                observableList.clear()
                                total.set(list.size)
                                if (list.isNotEmpty()) {
                                    // 当前时间
                                    val currentTime = Date().time
                                    val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                                    format.timeZone = TimeZone.getTimeZone("GMT+8")
                                    // 最小差异索引
                                    var minDifferenceIndex = -1
                                    // 最小差异 item
                                    var minDifferenceItem: WorkMeetingItemContentViewModel? = null
                                    // 是否找到
                                    var find = false
                                    list.forEachIndexed { index, meetingInfo ->
                                        val item = WorkMeetingItemContentViewModel(
                                            this@WorkCalendarViewModel,
                                            meetingInfo,
                                            first = index == 0,
                                            last = index == list.size - 1
                                        )
                                        observableList.add(item)
                                        val startDate = format.parse("${day.dateForParam} ${meetingInfo.startTime}")
                                        meetingInfo.timeBegin = startDate?.time ?: 0L

                                        val endDate = format.parse("${day.dateForParam} ${meetingInfo.endTime}")
                                        meetingInfo.timeEnd = endDate?.time ?: 0L

                                        // 如果是今天且未找到最小差异的item
                                        if (workCalendar == WorkCalendar.TODAY.text && !find) {
                                            if (startDate != null && endDate != null) {
                                                val startTime = startDate.time
                                                val endTime = endDate.time

                                                // 如果大于当前时间记录索引
                                                if (startTime > currentTime) {
                                                    minDifferenceIndex = index
                                                }
                                                // 如果当前时间是在 会议范围内
                                                else if (currentTime in startTime..endTime){
                                                    minDifferenceItem = item
                                                    find = true
                                                }
                                            }
                                        }
                                    }

                                    if (minDifferenceItem != null){
                                        minDifferenceItem!!.selectedObservable.set(true)
                                    }else{
                                        if (minDifferenceIndex == -1) {
                                            // 如果没有记录  则取最后一个
                                            minDifferenceItem = observableList.last() as WorkMeetingItemContentViewModel
                                            if (currentTime > minDifferenceItem!!.meetingInfo.timeEnd){
                                                minDifferenceItem!!.selectedObservable.set(false)
                                            }
                                        } else if (minDifferenceIndex == 0) {
                                            minDifferenceItem = observableList.first() as WorkMeetingItemContentViewModel
                                            if (currentTime > minDifferenceItem!!.meetingInfo.timeEnd){
                                                minDifferenceItem!!.selectedObservable.set(false)
                                            }
                                        } else {
                                            // 比较前后之差  取差值小的
                                            val previous = observableList[minDifferenceIndex - 1] as WorkMeetingItemContentViewModel
                                            val index = observableList[minDifferenceIndex] as WorkMeetingItemContentViewModel
                                            if (currentTime - previous.meetingInfo.timeEnd < index.meetingInfo.timeBegin - currentTime) {
                                                minDifferenceItem = previous
                                            } else {
                                                minDifferenceItem = index
                                            }
                                            minDifferenceItem!!.selectedObservable.set(true)
                                        }
                                    }
                                }
                            }
                        }
                    }
                })
        )
    }


    /**
     * 获取考勤信息
     */
    fun getAttendanceInfo(workCalendar:String){
        val offset = when(workCalendar){
            WorkCalendar.TODAY.text -> 0
            WorkCalendar.TOMORROW.text ->1
            else -> 0
        }
        addSubscribe(
            model
                .getAttendanceInfo(offset)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<AttendanceInfo>>(){
                    override fun onSuccess(t: AppResponse<AttendanceInfo>) {
                        t.data?.let { it ->
                            // 我的考勤
                            clock?.run {
                                startWork.set(if (it.myArriveTime.isBlank()) "--" else it.myArriveTime)
                                offWork.set(if (it.myLeaveTime.isBlank()) "--" else it.myLeaveTime)
                            }

                            // 部门考勤
                            attendance?.run {

                                earliest.set(it.firstInOrg)
                                latest.set(it.lastInOrg)

                                leaveTotal.set(it.holidayList.size)
                                leaveSlogan.set(if (it.holidayList.isEmpty()) "上班使我快乐，请假有种失恋的感受！" else "有我在，你们放心去吧！")

                                leaveObservableList.clear()
                                it.holidayList.forEach {
                                    val item = WorkAttendanceItemContentViewModel(this@WorkCalendarViewModel,staff = it)
                                    leaveObservableList.add(item)
                                }
                                if (leaveObservableList.isEmpty()){
                                    val item = WorkAttendanceItemContentViewModel(this@WorkCalendarViewModel,placeholder = R.mipmap.mine_bmkq_smile)
                                    leaveObservableList.add(item)
                                }

                                abnormalTotal.set(it.exceptionList.size)
                                abnormalSlogan.set(if (it.exceptionList.isEmpty()) "按时打卡是我们的人生信条！" else "快去对TA们说，明天考勤不异常我请喝奶茶！")

                                abnormalObservableList.clear()
                                it.exceptionList.forEach {
                                    val item = WorkAttendanceItemContentViewModel(this@WorkCalendarViewModel,staff = it)
                                    abnormalObservableList.add(item)
                                }
                                if (abnormalObservableList.isEmpty()){
                                    val item = WorkAttendanceItemContentViewModel(this@WorkCalendarViewModel,placeholder = R.mipmap.mine_bmkq_smile)
                                    abnormalObservableList.add(item)
                                }
                            }
                        }
                    }
                })
        )
    }


    /**
     * 获取任务信息
     */
    fun getTaskInfo(workCalendar:String,day: Day){
        addSubscribe(
            model
                .getTaskInfo(day.dateForParam)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<TaskInfo>>(){
                    override fun onSuccess(t: AppResponse<TaskInfo>) {
                        t.data?.let {
                            mission?.run {
                                taskInfoObservable.set(it)
                            }
                        }
                    }
                })
        )
    }
}