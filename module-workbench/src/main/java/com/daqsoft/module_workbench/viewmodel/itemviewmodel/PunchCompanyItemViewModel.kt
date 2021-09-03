package com.daqsoft.module_workbench.viewmodel.itemviewmodel

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
import com.daqsoft.module_workbench.viewmodel.PaySlipDetailViewModel
import com.daqsoft.module_workbench.viewmodel.PunchCompanyViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 8/1/2021 下午 4:17
 * @author zp
 * @describe
 */
class PunchCompanyItemViewModel(
    private val punchCompanyViewModel : PunchCompanyViewModel
) : ItemViewModel<PunchCompanyViewModel>(punchCompanyViewModel){

    var disposable:Disposable? = null

    val timeObservable = ObservableField<String>()


    /**
     * 打卡
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {
        Timber.e("点击打卡")
    })


    init {
        startTimer()
    }

    /**
     * 开始计时
     */
    fun startTimer(){
        Observable
            .interval(1, TimeUnit.SECONDS)
            .compose(RxUtils.schedulersTransformer())
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable?) {
                    disposable = d
                }

                override fun onNext(t: Long?) {
                    val calendar = Calendar.getInstance()
                    calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)
                    timeObservable.set(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
                }
                override fun onError(e: Throwable?) {
                }
                override fun onComplete() {
                }
            })
    }

    fun stopTimer(){
        disposable?.let {
            if (!it.isDisposed){
                it.dispose()
            }
        }
    }
}