package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.bo.BirthdayCalendar
import com.daqsoft.module_mine.repository.pojo.vo.EmployeeInfoUpdate
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 18/11/2020 下午 4:28
 * @author zp
 * @describe 修改生日 viewModel
 */
class BirthdayViewModel : ToolbarViewModel<MineRepository>{

    @ViewModelInject constructor(application: Application,mineRepository: MineRepository):super(application, mineRepository)

    val calendarLiveData = MutableLiveData<BirthdayCalendar>()


    /**
     * 农历点击事件
     */
    val lunarCalendarOnClick = BindingCommand<Unit>(BindingAction {
        calendarLiveData.value = BirthdayCalendar.LUNAR_CALENDAR
    })

    /**
     * 国历点击事件
     */
    val nationalCalendarOnClick = BindingCommand<Unit>(BindingAction {
        calendarLiveData.value = BirthdayCalendar.NATIONAL_CALENDAR
    })

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar(){
        setTitleText(getApplication<Application>().resources.getString(R.string.module_mine_birthday))
    }

    val update = ObservableField<EmployeeInfoUpdate>()

    fun getEmployeeToUpdate(){
        addSubscribe(
            model
                .getEmployeeToUpdate()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeInfoUpdate>>(){
                    override fun onSuccess(t: AppResponse<EmployeeInfoUpdate>) {
                        t.data?.let {eu->
                            update.set(eu)
                        }
                    }
                })
        )
    }

    fun updateEmployeeInfo(){
        val json = Gson().toJson(update.get()!!)
        val type = object : TypeToken<Map<String, String>>() {}.type
        val params = Gson().fromJson<Map<String,String>>(json, type)
        addSubscribe(
            model
                .updateEmployeeInfo(params)
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        LiveEventBus.get(LEBKeyGlobal.MODIFY_BIRTHDAY_KEY).post("${calendarLiveData.value!!.text}-${update.get()!!.employeeBirthday}")
                        finish()
                    }
                })
        )

    }

}