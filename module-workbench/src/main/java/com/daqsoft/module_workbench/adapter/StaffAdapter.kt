package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.graphics.ImageFormat
import android.graphics.Rect
import android.view.View
import androidx.core.view.isVisible
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.model.layer.NullLayer
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffItemUpcomingBinding
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.StaffItemUpcomingViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.viewadapter.view.ViewAdapter
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jakewharton.rxbinding4.view.clicks
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.runBlocking
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import timber.log.Timber
import java.math.BigDecimal
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 30/11/2020 下午 5:19
 * @author zp
 * @describe 员工详情 adapter
 */
class StaffAdapter @Inject constructor() : BindingRecyclerViewAdapter<MultiItemViewModel<*>>() {

    companion object{
        const val PROCESSING = 1
        const val PAUSE = 2
        const val COMPLETE = 3
    }

    /**
     * 当前点击  item
     */
    private var clickItem : HashMap<String,Any>? = null

    /**
     * 当前计时器
     */
    private var currentTimer : HashMap<String,Any?> ? = null

    /**
     * 状态更新接口
     */
    private var updateTaskStatus:UpdateTaskStatus? = null

    fun setUpdateTaskStatus(updateTaskStatus: UpdateTaskStatus){
        this.updateTaskStatus = updateTaskStatus
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: MultiItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when (item?.itemType) {
            ConstantGlobal.HEAD -> {}
            ConstantGlobal.UPCOMING -> {
                val itemBinding = binding as RecyclerviewStaffItemUpcomingBinding
                val itemViewModel = item as StaffItemUpcomingViewModel
                itemBinding.recycleViewPersonnel.apply {
                    layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
                    if (itemDecorationCount == 0) {
                        addItemDecoration(object : RecyclerView.ItemDecoration(){
                            override fun getItemOffsets(
                                outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val position = parent.getChildAdapterPosition(view)
                                if (position != 0){
                                    outRect.left = -(3.dp)
                                }
                            }
                        })
                    }
                    handleStatusUI(itemBinding, itemViewModel)
                }
                itemBinding.root
                    .clicks()
                    .throttleFirst(ViewAdapter.CLICK_INTERVAL, TimeUnit.SECONDS)
                    .subscribe {
                        if (clickItem == null){
                            clickItem = hashMapOf()
                        }
                        clickItem!!["clickItemBinding"] = itemBinding
                        clickItem!!["clickItemViewModel"] = itemViewModel

                        ARouter
                            .getInstance()
                            .build(ARouterPath.Web.PAGER_WEB)
                            .withString("url", HttpGlobal.TASK_DETAIL + itemViewModel.missionObservable.get()!!.taskId)
                            .navigation()
                    }
            }
            ConstantGlobal.PARTICIPATE -> {}
            ConstantGlobal.FOOTER -> {}
            ConstantGlobal.MORE -> {}
        }
    }

    /**
     * 状态更新完成
     * @param itemBinding RecyclerviewStaffItemUpcomingBinding
     * @param itemViewModel StaffItemUpcomingViewModel
     */
    fun updateCompleted(itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel){
        if (itemViewModel.missionObservable.get()!!.workType == 1){
            handleStatusUI(itemBinding, itemViewModel)
            return
        }
        currentTimer?.let {
            var observable = it["observable"]
            var subscribe= it["subscribe"]
            if (observable != null && subscribe != null){
                observable = observable as Observable<Int>
                subscribe = subscribe as Observer<Int>
                observable.subscribe(subscribe)
            }
        }
        stopTimer()
        handleStatusUI(itemBinding, itemViewModel)
    }

    /**
     * 处理 转态 UI
     * @param itemBinding RecyclerviewStaffItemUpcomingBinding
     * @param itemViewModel StaffItemUpcomingViewModel
     */
    private fun handleStatusUI(itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel){
        val isOneself = itemViewModel.staffViewModel.isOneself
        val mission = itemViewModel.missionObservable.get()!!
        // 是否延期
        var isExtension =  false
        var day : String  = "0"
        var hour : String = "0"
        if (!mission.delayDays.isNullOrBlank()){
            val time = mission.delayDays.split("_")
            day = time[0]
            hour = time[1]
            if (day.toInt() >0 && hour.toInt() > 0){
                isExtension = true
            }
        }
        itemBinding.extension.isVisible = false
        if(isExtension && !isOneself){
            itemBinding.extension.apply {
                isVisible = true
                text = resources.getString(R.string.extension,day,hour)
            }
        }
        // 0-未开始 1-进行中 2- 暂停 3-完成 5-中止
        itemBinding.status.setOnClickListener(null)
        when(mission.workState){
            0 ->{
                if (isOneself){
                    itemBinding.complete.isVisible = false
                    itemBinding.extension.isVisible = false
                    itemBinding.status.apply {
                        setOnClickListener {
                            if(mission.workType == 1){
                                read(itemBinding,itemViewModel)
                            }else{
                                updateTaskStatusSchedule(PROCESSING,itemBinding,itemViewModel)
                            }

                        }
                        isVisible = true
                        text =  if(mission.workType == 1) resources.getString(R.string.read) else resources.getString(R.string.start_task)
                        helper.apply {
                            textColorNormal = resources.getColor(R.color.green_23c070)
                            backgroundColorNormal = resources.getColor(R.color.green_23c070_alpha90)
                            iconNormal =  if(mission.workType == 1) resources.getDrawable(R.mipmap.rw_list_zt_yy) else resources.getDrawable(R.mipmap.rw_list_zt_ks)

                        }
                    }
                }else{
                    itemBinding.status.isVisible = false
                    itemBinding.complete.apply {
                        isVisible = true
                        helper.apply {
                            text = resources.getString(R.string.not_started)
                            textColorNormal = resources.getColor(R.color.yellow_f7c54e)
                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_wks)
                        }
                    }
                }
            }
            1 ->{
                if (isOneself){
                    startTimer(itemBinding, itemViewModel)
                    itemBinding.complete.isVisible = false
                    itemBinding.extension.isVisible = false
                    itemBinding.status.apply {
                        isVisible = true
                        helper.apply {
                            setOnClickListener{
                                updateTaskStatusSchedule(PAUSE,itemBinding,itemViewModel)
                            }
                            text = mission.coverDuration()
                            textColorNormal = resources.getColor(R.color.red_fa4848)
                            backgroundColorNormal = resources.getColor(R.color.red_fa4848_alpha90)
                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_jxz)
                        }
                    }
                }else{
                    itemBinding.status.isVisible = false
                    itemBinding.complete.apply {
                        isVisible = true
                        helper.apply {
                            text = resources.getString(R.string.started)
                            textColorNormal = resources.getColor(R.color.red_fa4848)
                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_yks)
                        }
                    }
                }
            }
            2 ->{
                if (isOneself){
                    itemBinding.complete.isVisible = false
                    itemBinding.extension.isVisible = false
                    itemBinding.status.apply {
                        isVisible = true
                        helper.apply {
                            setOnClickListener{
                                updateTaskStatusSchedule(PROCESSING,itemBinding,itemViewModel)
                            }
                            text = if (isExtension) resources.getString(R.string.extension,day,hour) else mission.coverDuration()
                            textColorNormal = resources.getColor(R.color.red_fa4848)
                            backgroundColorNormal = resources.getColor(R.color.red_fa4848_alpha90)
                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_zt)
                        }
                    }
                }else{
                    itemBinding.status.isVisible = false
                    itemBinding.complete.apply {
                        isVisible = true
                        helper.apply {
//                            text = resources.getString(R.string.paused)
//                            textColorNormal = resources.getColor(R.color.red_fa4848)
//                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_yks)

                            text = resources.getString(R.string.not_started)
                            textColorNormal = resources.getColor(R.color.yellow_f7c54e)
                            iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_wks)
                        }
                    }
                }
            }
            3 ->{
                itemBinding.status.isVisible = false
                itemBinding.complete.apply {
                    isVisible = true
                    helper.apply {
                        text = if (isOneself){
                            (if(mission.workType == 1) resources.getString(R.string.read) else resources.getString(R.string.completed)) + if (mission.finishTime == null) "" else "  |  " + mission.finishTime
                        }else{
                            resources.getString(R.string.completed)
                        }
                        textColorNormal = resources.getColor(R.color.green_23c070)
                        iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_ywc)
                    }
                }
            }
            5 ->{
                itemBinding.status.isVisible = false
                itemBinding.complete.apply {
                    isVisible = true
                    helper.apply {
                        text = resources.getString(R.string.not_started)
                        textColorNormal = resources.getColor(R.color.yellow_f7c54e)
                        iconNormal = resources.getDrawable(R.mipmap.rw_list_zt_wks)
                    }
                }

            }
        }
    }

    /**
     * 停止计时器
     */
    fun stopTimer(){
        if (currentTimer == null){
            return
        }
        var disposable = currentTimer!!["disposable"]
        if (disposable == null){
            return
        }
        disposable = disposable as Disposable
        if (!disposable.isDisposed){
            disposable.dispose()
        }
    }

    /**
     * 开始计时
     * @param initialValue Int  初始值
     */
    private fun startTimer(itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel){
        Observable
            .interval(1, TimeUnit.SECONDS)
            .doOnSubscribe {
                if (currentTimer == null){
                    currentTimer = hashMapOf()
                }
                currentTimer!!.run {
                    this["disposable"] =   it
                    this["itemBinding"] =   itemBinding
                    this["itemViewModel"] =   itemViewModel
                }
            }
            .compose(RxUtils.schedulersTransformer())
            .subscribe(object : Observer<Long>{
                override fun onSubscribe(d: Disposable?) {
                }

                override fun onNext(t: Long?) {
                    itemViewModel.missionObservable.get()!!.totalSeconds++
                    itemBinding.status.text = itemViewModel.missionObservable.get()!!.coverDuration()
                    itemViewModel.missionObservable.notifyChange()
                }
                override fun onError(e: Throwable?) {
                }
                override fun onComplete() {
                }
            })
    }

    /**
     * 已阅
     * @param itemBinding RecyclerviewStaffItemUpcomingBinding
     * @param itemViewModel StaffItemUpcomingViewModel
     */
    private fun  read(itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel){
        updateTaskStatus?.read(itemBinding, itemViewModel)
    }


    /**
     * 任务状态更新
     * @param status Int
     * @param itemBinding RecyclerviewStaffItemUpcomingBinding
     * @param itemViewModel StaffItemUpcomingViewModel
     */
    private fun updateTaskStatusSchedule(status: Int,itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel){

        if (currentTimer != null){
            var lastItemViewModel = currentTimer!!["itemViewModel"]
            var lastItemBinding = currentTimer!!["itemBinding"]
            if (lastItemViewModel != null && lastItemBinding != null){
                lastItemViewModel = lastItemViewModel as StaffItemUpcomingViewModel
                lastItemBinding = lastItemBinding as RecyclerviewStaffItemUpcomingBinding
                if (itemViewModel != lastItemViewModel && itemBinding != lastItemBinding){
                    if (lastItemViewModel.missionObservable.get()!!.workState == PROCESSING ){
                        updateTaskStatus?.updateStatus(PAUSE, lastItemBinding, lastItemViewModel)
                        val observable = Observable.just(1)
                        val subscribe = object : Observer<Int> {
                            override fun onSubscribe(d: Disposable?) {
                            }

                            override fun onNext(t: Int?) {
                                updateTaskStatus?.updateStatus(
                                    status, itemBinding, itemViewModel
                                )
                                currentTimer!!["observable"] = null
                                currentTimer!!["subscribe"] = null
                            }

                            override fun onError(e: Throwable?) {
                            }

                            override fun onComplete() {
                            }

                        }
                        currentTimer!!["observable"] = observable
                        currentTimer!!["subscribe"] = subscribe
                        return
                    }
                }
            }
        }

        updateTaskStatus?.updateStatus(
            status, itemBinding, itemViewModel
        )
    }


    /**
     * 详情任务状态改变
     */
    fun updateTaskStatusSchedule (status: Int){
        if (clickItem == null){
            return
        }
        val itemBinding = clickItem!!["clickItemBinding"]!! as RecyclerviewStaffItemUpcomingBinding
        val itemViewModel = clickItem!!["clickItemViewModel"]!! as StaffItemUpcomingViewModel

        if (itemViewModel.missionObservable.get()!!.workType == 0) {
            if (currentTimer != null) {
                var lastItemViewModel = currentTimer!!["itemViewModel"]
                var lastItemBinding = currentTimer!!["itemBinding"]
                if (lastItemViewModel != null && lastItemBinding != null) {
                    lastItemViewModel = lastItemViewModel as StaffItemUpcomingViewModel
                    lastItemBinding = lastItemBinding as RecyclerviewStaffItemUpcomingBinding
                    if (itemViewModel != lastItemViewModel && itemBinding != lastItemBinding) {
                        if (lastItemViewModel.missionObservable.get()!!.workState == PROCESSING) {
                            lastItemViewModel.missionObservable.get()!!.workState = PAUSE
                            lastItemViewModel.missionObservable.notifyChange()
                            updateCompleted(lastItemBinding, lastItemViewModel)
                        }
                    }
                }
            }
        }
        itemViewModel.missionObservable.get()!!.workState = status
        itemViewModel.missionObservable.notifyChange()
        updateCompleted(itemBinding,itemViewModel)
    }


    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
    }

    interface UpdateTaskStatus{

        fun updateStatus(
            firstStatus: Int,
            firstItemBinding:RecyclerviewStaffItemUpcomingBinding,
            firstItemViewModel:StaffItemUpcomingViewModel)


        fun  read(itemBinding:RecyclerviewStaffItemUpcomingBinding,itemViewModel:StaffItemUpcomingViewModel)
    }
}