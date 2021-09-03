package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.DialyRuleBean
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils

/**
 * 日报规则
 */
class DailyRuleViewModel: ToolbarViewModel<WorkBenchRepository> {

    companion object{
        const val DAILY_CONTENT = "daily_content"
    }

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository) : super(application, workBenchRepository)

    var id : String = ""

    override fun onCreate() {
        super.onCreate()
        initToolbar()
        initSpannerData()
    }

    val nameSpannable = ObservableField<SpannableStringBuilder>()
    private fun initSpannerData() {
        val nameString = SimplifySpanBuild()
            .append(
                SpecialTextUnit("当前日报为【").setTextColor(getApplication<Application>().resources.getColor(
                    R.color.black_333333)))
            .append(
                SpecialTextUnit("自动生成模式").setTextColor(getApplication<Application>().resources.getColor(
                    R.color.color_ff6d6b)))
            .append(
                SpecialTextUnit("】").setTextColor(getApplication<Application>().resources.getColor(
                    R.color.black_333333)))
            .build()
        nameSpannable.set(nameString)
    }

    private fun initToolbar() {
        setTitleText("自动生成模式规则")
    }


    /**
     * 日报时间点击选择
     */
    val sendOnClick = BindingCommand<Unit>(BindingAction {
    })


    val ruleBean =ObservableField<DialyRuleBean>()
    fun getDayReportSet(){
        addSubscribe(
            model
                .getDayReportSet()
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post{
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DialyRuleBean>>() {
                    override fun onSuccess(t: AppResponse<DialyRuleBean>) {
                        ruleBean.set(t?.data)
                        dismissLoadingDialog()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }

}