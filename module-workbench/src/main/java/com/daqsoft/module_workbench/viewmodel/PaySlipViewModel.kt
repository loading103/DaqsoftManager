package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.MD5
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.PaySlip
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 9/12/2020 下午 2:15
 * @author zp
 * @describe
 */
class PaySlipViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)

    /**
     * 密钥
     */
    var secret = ""

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText(getApplication<Application>().resources.getString(R.string.pay_slip))
    }


    /**
     * 密码
     */
    val password = ObservableField<String>("")

    /**
     * 确定点击事件
     */
    val determineOnClick = BindingCommand<Unit>(BindingAction {
        if (password.get().isNullOrBlank()){
            ToastUtils.showLong(getApplication<Application>().resources.getString(R.string.please_enter_password))
            return@BindingAction
        }
        verifySecondPassword()
    })


    val verificationLiveData = MutableLiveData<Unit>()
    /**
     * 验证二级密码
     */
    private fun  verifySecondPassword(){
        addSubscribe(
            model
                .verifySecondPassword(MD5.hexdigest(MD5.hexdigest(password.get()!!))   ,"WAGE")
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        t.data?.let {
                            secret = it.toString()
                            verificationLiveData.value = null
                        }
                    }

                    override fun onFailToast(): Boolean {
                        return true
                    }
                })
        )
    }


    val paySlipListLiveData = MutableLiveData<Pair<List<String>,List<List<PaySlip>>>>()

    fun getPaySlipList(){
        addSubscribe(
            model
                .getPaySlipList(secret)
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<PaySlip>>>(){
                    @RequiresApi(Build.VERSION_CODES.N)
                    override fun onSuccess(t: AppResponse<List<PaySlip>>) {
                        t.data?.let {
                            val group = arrayListOf<String>()
                            val children = arrayListOf<List<PaySlip>>()
                            it
                                // 变换为 Pair 以便分组
                                .map {
                                    val split = it.wageMonth.split("年")
                                    Pair(split[0],it)
                                }
                                // 根据 key 分组
                                .groupBy({ it.first }, { it.second })
                                // 分离数据 重新组装
                                .forEach { t, u ->
                                    group.add(t)
                                    children.add(u)
                                }
                            paySlipListLiveData.value = Pair(group,children)
                        }
                    }
                })
        )
    }
}