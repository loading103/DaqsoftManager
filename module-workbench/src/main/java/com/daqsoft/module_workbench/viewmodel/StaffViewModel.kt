package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.paging.PageHelper
import com.daqsoft.library_base.paging.Paging2Utils
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.coverTime
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewStaffItemUpcomingBinding
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.EmployeeDetail
import com.daqsoft.module_workbench.repository.pojo.vo.Participate
import com.daqsoft.module_workbench.repository.pojo.vo.Upcoming
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.*
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding
import okhttp3.MultipartBody
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 27/11/2020 下午 2:37
 * @author zp
 * @describe 员工详情 viewModel
 */
class StaffViewModel : ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository) : super(
        application,
        workBenchRepository
    )

    /**
     * 记录上次添加的位置
     */
    var index = 0

    /**
     * 待办分页工具类
     */
    var pageHelper : PageHelper? = null

    /**
     * 用户id
     */
    var id = ""

    /**
     * 是否自己
     */
    var isOneself = false

    /**
     * 员工信息
     */
    val employeeInfoObservable = ObservableField<EmployeeDetail>()

    val employeeDetailObservable = ObservableField<String>()

    /**
     * 待办头部
     */
    var upcomingHeadViewModel: StaffHeadViewModel? = null

    // 2021/03/30 修改
//    /**
//     * 参与头部
//     */
//    var participateHeadViewModel: StaffHeadViewModel? = null

    /**
     * 底部
     */
    var footerViewModel: StaffFooterViewModel? = null

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()

    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =
        ItemBinding.of { itemBinding, position, item ->
            when (item.itemType as String) {
                ConstantGlobal.HEAD -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_staff_head
                )
                ConstantGlobal.UPCOMING -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_staff_item_upcoming
                )
                ConstantGlobal.PARTICIPATE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_staff_item_participate
                )
                ConstantGlobal.FOOTER -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_staff_footer
                )
                ConstantGlobal.MORE -> itemBinding.set(
                    BR.viewModel,
                    R.layout.recyclerview_staff_item_more
                )
                else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_staff_item_upcoming)
            }
        }

    /**
     * 电话长按事件
     */
    val phoneLongClick = BindingCommand<Unit>(BindingAction {
        if (employeeInfoObservable.get() == null){
            return@BindingAction
        }
        AppUtils.primaryClip(employeeInfoObservable.get()?.employeeMobile?:"")
    })

    /**
     * 邮箱长按事件
     */
    val emailLongClick = BindingCommand<Unit>(BindingAction {
        if (employeeInfoObservable.get() == null){
            return@BindingAction
        }
        AppUtils.primaryClip(employeeInfoObservable.get()?.employeeEmail?:"")
    })

    /**
     * 打电话
     */
    val callLiveData = MutableLiveData<String>()

    val prepareData = mutableListOf<MultiItemViewModel<*>>()


    override fun onCreate() {
        initToolbar()

        upcomingHeadViewModel = StaffHeadViewModel(
            this,
            getApplication<Application>().resources.getString(R.string.upcoming_tasks),
            true
        ).apply { multiItemType(ConstantGlobal.HEAD) }
        prepareData.add(upcomingHeadViewModel!!)

        // 2021/03/30 修改
//        participateHeadViewModel = StaffHeadViewModel(
//            this@StaffViewModel,
//            getApplication<Application>().resources.getString(R.string.key_projects_involved),
//            true
//        ).apply { multiItemType(ConstantGlobal.HEAD) }
//        prepareData.add(participateHeadViewModel!!)
//
//        getParticipatingProjectsTotal()


        footerViewModel = StaffFooterViewModel(this).apply { multiItemType(ConstantGlobal.FOOTER) }
        prepareData.add(footerViewModel!!)
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
    }


    val onClick = BindingCommand<Unit>(BindingAction {
        Timber.e("onClick  ")
    })


    /**
     * 员工信息
     */
    fun getEmployeeInfo() {
        addSubscribe(
            model
                .getEmployeeInfo(id)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeDetail>>() {
                    override fun onSuccess(t: AppResponse<EmployeeDetail>) {
                        t.data?.let {
                            setTitleText(it.employeeName)
                            employeeInfoObservable.set(it)
                            setOrganizationStr(it)
                        }
                    }
                })
        )
    }

    /**
     * 设置职位
     */
    private fun setOrganizationStr(it: EmployeeDetail) {
        val sb = StringBuilder()
        it.employeeOrganizationStr?.takeIf {
            it.isNotBlank()
        }?.apply {
            sb.append(this)
        }
        it.employeePostStr?.takeIf {
            it.isNotBlank()
        }?.apply {
            if (!sb.isEmpty()){
                sb.append("-")
            }
            sb.append(this)
        }
        employeeDetailObservable.set(sb.toString())
    }


    val layoutLiveData = MutableLiveData<String>()
    val employeeDetail = MutableLiveData<EmployeeDetail>()

    /**
     * 获取待办任务和参与项目
     */
    fun getUpcomingAndParticipate() {
        addSubscribe(
            model
                .getUpcomingTasks(id = id)
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<AppResponse<Upcoming>, Observable<AppResponse<List<Participate>>>> {
                    Handler(Looper.getMainLooper()).post {
                        it.data?.let {
                            upcomingHeadViewModel?.totalObservable?.set(it.total)
                            it.datas?.let {
                                if (it.isEmpty()) {
                                    return@post
                                }
                                val tempList = arrayListOf<MultiItemViewModel<*>>()
                                it.forEach {
                                    it.totalSeconds = (it.costHours * 3600).toLong()

                                    tempList.add(StaffItemUpcomingViewModel(this, it).apply {
                                        multiItemType(
                                            ConstantGlobal.UPCOMING
                                        )
                                    })
                                }
                                prepareData.addAll(1, tempList)
                            }
                            if (it.pageCurr !=  it.totalPages) {
//                                prepareData.add(prepareData.size - 2, StaffItemMoreViewModel(this).apply { multiItemType(ConstantGlobal.MORE) })
                                // 2021/03/30 修改
                                prepareData.add(prepareData.size - 1, StaffItemMoreViewModel(this).apply { multiItemType(ConstantGlobal.MORE) })

                                // 如果有分页就创建  并且讲页数 +1
                                pageHelper = PageHelper()
                                pageHelper?.add()
                            }

                        }
                    }
                    return@Function model.getParticipatingProjects(id)
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                    observableList.addAll(prepareData)
                    layoutLiveData.value = MultipleLayoutManager.SUCCESS
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<Participate>>>() {
                    override fun onSuccess(t: AppResponse<List<Participate>>) {
                        // 2021/03/30 修改
//                        t.data?.let {
//                            if (it.isEmpty()) {
//                                return
//                            }
//                            participateHeadViewModel?.totalObservable?.set(it.size)
//
//                            val tempList = arrayListOf<MultiItemViewModel<*>>()
//                            it.forEachIndexed { index, participate ->
//                                tempList.add(
//                                    StaffItemParticipateViewModel(
//                                        this@StaffViewModel,
//                                        participate,
//                                        index != it.size - 1
//                                    ).apply { multiItemType(ConstantGlobal.PARTICIPATE) })
//                            }
//                            prepareData.addAll(prepareData.size - 1, tempList)
//                        }
                    }
                })
        )

    }

    /**
     * 状态更新完成 liveData
     */
    val updateCompletedLiveData =
        MutableLiveData<Pair<RecyclerviewStaffItemUpcomingBinding, StaffItemUpcomingViewModel>>()


    /**
     * 更新任务状态
     */
    fun updateTaskStatus(
        firstStatus: Int,
        firstItemBinding: RecyclerviewStaffItemUpcomingBinding,
        firstItemViewModel: StaffItemUpcomingViewModel
    ) {
        addSubscribe(
            model
                .updateTaskStatus(firstStatus, firstItemViewModel.missionObservable.get()!!.taskId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        firstItemViewModel.missionObservable.get()!!.workState = firstStatus
                        firstItemViewModel.missionObservable.notifyChange()
                        updateCompletedLiveData.value = Pair(firstItemBinding, firstItemViewModel)
                    }
                })
        )
    }

    /**
     * 已阅
     */
    fun read(itemBinding: RecyclerviewStaffItemUpcomingBinding, itemViewModel: StaffItemUpcomingViewModel) {
        addSubscribe(
            model
                .read(itemViewModel.missionObservable.get()!!.taskId)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        itemViewModel.missionObservable.get()!!.workState = 3
                        itemViewModel.missionObservable.notifyChange()
                        updateCompletedLiveData.value = Pair(itemBinding, itemViewModel)
                    }
                })
        )
    }


    /**
     * 获取待办任务
     */
   fun getUpcomingTasks(){
       addSubscribe(
           model
               .getUpcomingTasks(id = id,page = pageHelper?.getPage()?:ConstantGlobal.INITIAL_PAGE)
               .compose (RxUtils.schedulersTransformer())
               .compose (RxUtils.exceptionTransformer())
               .subscribeWith(object : AppDisposableObserver<AppResponse<Upcoming>>() {
                   @RequiresApi(Build.VERSION_CODES.N)
                   override fun onSuccess(t: AppResponse<Upcoming>) {
                       t.data?.let {
                           if (it.pageCurr != it.totalPages){
                               pageHelper?.add()
                           }else{
                               observableList.removeAt(index)
                           }
                           it.datas?.let {
                               val tempList = arrayListOf<MultiItemViewModel<*>>()
                               it.forEach {
                                   it.totalSeconds = (it.costHours * 3600).toLong()
                                   tempList.add(StaffItemUpcomingViewModel(this@StaffViewModel,it).apply {
                                       multiItemType(
                                           ConstantGlobal.UPCOMING
                                       )
                                   })
                               }
                               observableList.addAll(index,tempList)
                           }
                       }

                   }
               })
       )
   }

    /**
     * 待办临时存储对象
     */
    private val upcomingTempList by lazy { mutableListOf<MultiItemViewModel<*>>() }
    /**
     * 参与临时存储对象
     */
    val participateTempList by lazy { mutableListOf<MultiItemViewModel<*>>() }


    /**
     * titleItem moreOnClick
     */
    fun moreOnClick(item:StaffHeadViewModel){
        when(item){
            // 待办
            upcomingHeadViewModel ->{
                val startIndex = 1
                if (item.moreSelected.get()!!){
                    observableList.addAll(startIndex,upcomingTempList)
                    return
                }
//                val endIndex = observableList.indexOf(participateHeadViewModel)
                // 2021/03/30 修改
                val endIndex = observableList.indexOf(footerViewModel)
                upcomingTempList.addAll(observableList.subList(startIndex,endIndex))
                observableList.removeAll(upcomingTempList)
            }
            // 2021/03/30 修改
//            // 参与
//            participateHeadViewModel ->{
//                val startIndex = observableList.indexOf(participateHeadViewModel) + 1
//                if (item.moreSelected.get()!!){
//                    observableList.addAll(startIndex,participateTempList)
//                    return
//                }
//                val endIndex = observableList.size -1
//                participateTempList.addAll(observableList.subList(startIndex,endIndex))
//                observableList.removeAll(participateTempList)
//            }
        }
    }

    /**
     * 获取参与项目总数
     */
    fun getParticipatingProjectsTotal(){
        addSubscribe(
            model
                .getParticipatingProjectsTotal(id)
                .compose (RxUtils.schedulersTransformer())
                .compose (RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onSuccess(t: AppResponse<Any>) {
                        var value = 0
                        t.data?.let {
                            value = (it as Double).toInt()
                        }
//                        participateHeadViewModel?.totalObservable?.set(value)
                    }
                })
        )
    }

}
