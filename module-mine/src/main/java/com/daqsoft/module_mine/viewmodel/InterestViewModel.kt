package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.vo.EmployeeInfoUpdate
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 19/11/2020 下午 5:38
 * @author zp
 * @describe
 */
class InterestViewModel: ToolbarViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application, mineRepository: MineRepository) : super(
        application,
        mineRepository
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText(getApplication<Application>().resources.getString(R.string.module_mine_interest))
        setRightTextVisible(View.VISIBLE)
        setRightTextColor(R.color.red_fa4848)
        setRightTextTxt(getApplication<Application>().resources.getString(R.string.module_mine_save))

    }

    override fun rightTextOnClick() {
        updateEmployeeInfo()
    }

    /**
     * 兴趣爱好
     */
    var interest = ObservableField<String>("")


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

        update.get()!!.employeeBirthdayType = update.get()!!.coverBirthdayType()
        update.get()!!.employeeHobby = interest.get()!!

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
                        LiveEventBus.get(LEBKeyGlobal.MODIFY_INTEREST_KEY).post(interest.get())
                        finish()
                    }

                    override fun onFailToast(): Boolean {
                        return true
                    }
                })
        )

    }

}