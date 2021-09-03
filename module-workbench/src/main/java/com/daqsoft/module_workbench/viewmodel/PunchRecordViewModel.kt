package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_common.pojo.vo.WelcomeMessage
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 3/2/2021 上午 10:46
 * @author zp
 * @describe
 */
class PunchRecordViewModel : ToolbarViewModel<WorkBenchRepository>{

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application, workBenchRepository)


    /**
     * 是否是外出
     */
    var outside = ObservableField<Boolean>(false)


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(R.color.red_fa4848)
        setBackIconSrc(R.mipmap.list_top_return_white)
        setTitleTextColor(R.color.white_ffffff)
    }


    /**
     * 获取月数据
     */
    fun getMonthData(){

    }


    /**
     * 获取天数据
     */
    fun getDayData(){

    }




}