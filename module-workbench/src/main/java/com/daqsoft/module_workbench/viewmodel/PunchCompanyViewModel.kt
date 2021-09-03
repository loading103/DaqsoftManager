package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.PunchCompanyItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 8/1/2021 下午 4:17
 * @author zp
 * @describe
 */
class PunchCompanyViewModel : BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)

    // 是在围栏内
    var insideFence = MutableLiveData<Boolean>(false)

    // 点击提示
    val clickTips = ObservableField<String>(getApplication<Application>().getString(R.string.module_workbench_click_to_clock_in_at_work))

    // 工作时间 （默认 9 - 18）
    val work = Pair(9,18)

    // 上班
    var startWorkItemViewModel : PunchCompanyItemViewModel ? = null
    // 下班
    var offWorkItemViewModel : PunchCompanyItemViewModel ? = null

    val companyPunchInfo = MutableLiveData<Unit>()

    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: ObservableList<ItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> =  ItemBinding.of(BR.viewModel, R.layout.viewpager2_punch_item)


    override fun onCreate() {
        observableList.clear()

        // 上班
        startWorkItemViewModel = PunchCompanyItemViewModel(this)
        observableList.add(startWorkItemViewModel)

        // 下班
        offWorkItemViewModel = PunchCompanyItemViewModel(this)
        observableList.add(offWorkItemViewModel)

    }

    /**
     * 获取公司定位信息
     */
    fun getCompanyPunchInfo(){
        companyPunchInfo.value = null
    }


    /**
     * 上班打卡
     */
    fun startWork(){
        //
    }

    // 下班打卡
    fun offWork(){
        //
    }

}