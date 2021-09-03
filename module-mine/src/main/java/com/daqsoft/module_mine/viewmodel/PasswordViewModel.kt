package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Intent
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 20/11/2020 上午 9:28
 * @author zp
 * @describe 修改密码 viewModel
 */
class PasswordViewModel : ToolbarViewModel<MineRepository> {

    @ViewModelInject constructor(application: Application,mineRepository: MineRepository):super(application, mineRepository)

    /**
     * 是否是二级密码
     */
    val isSecondaryPassword = ObservableField<Boolean>(false)

    /**
     * 登录密码（原密码）
     */
    var oldPassword = ObservableField<String>("")

    /**
     * 登录密码（原密码）清除可见性
     */
    val oldPasswordCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 登录密码（原密码）输入监听
     */
    var oldPasswordChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            oldPasswordCleanVisible.set(View.GONE)
        }else{
            oldPasswordCleanVisible.set(View.VISIBLE)
        }
    })

    /**
     * 登录密码（原密码） 清除点击事件
     */
    val oldPasswordCleanOnClick = BindingCommand<Unit>(BindingAction {
        oldPassword.set("")
    })

    /**
     * 登录密码（原密码） 可见ICON
     */
    val oldPasswordVisibleIcon = ObservableField<Int>(R.mipmap.login_invisible)

    /**
     * 登录密码（原密码）密码样式
     */
    val oldPasswordVisible = MutableLiveData<Boolean>(false)

    /**
     * 登录密码（原密码） 可见点击事件
     */
    val oldPasswordVisibleOnClick = BindingCommand<Unit>(BindingAction {
        oldPasswordVisible.value = !oldPasswordVisible.value!!
        oldPasswordVisibleIcon.set(if (oldPasswordVisible.value!!) R.mipmap.login_visible else R.mipmap.login_invisible)
    })



    /**
     * 新密码
     */
    var newPassword = ObservableField<String>("")

    /**
     * 新密码 清除可见性
     */
    val newPasswordCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 新密码 输入监听
     */
    var newPasswordChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            newPasswordCleanVisible.set(View.GONE)
        }else{
            newPasswordCleanVisible.set(View.VISIBLE)
        }
    })

    /**
     * 新密码 清除点击事件
     */
    val newPasswordCleanOnClick = BindingCommand<Unit>(BindingAction {
        newPassword.set("")
    })

    /**
     * 新密码 可见ICON
     */
    val newPasswordVisibleIcon = ObservableField<Int>(R.mipmap.login_invisible)

    /**
     * 新密码 密码样式
     */
    val newPasswordVisible = MutableLiveData<Boolean>(false)

    /**
     * 新密码 可见点击事件
     */
    val newPasswordVisibleOnClick = BindingCommand<Unit>(BindingAction {
        newPasswordVisible.value = !newPasswordVisible.value!!
        newPasswordVisibleIcon.set(if (newPasswordVisible.value!!) R.mipmap.login_visible else R.mipmap.login_invisible)
    })



    /**
     * 确认密码
     */
    var confirmPassword = ObservableField<String>("")

    /**
     * 确认密码 清除可见性
     */
    val confirmPasswordCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 确认密码 输入监听
     */
    var confirmPasswordChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            confirmPasswordCleanVisible.set(View.GONE)
        }else{
            confirmPasswordCleanVisible.set(View.VISIBLE)
        }
    })

    /**
     * 确认密码 清除点击事件
     */
    val confirmPasswordCleanOnClick = BindingCommand<Unit>(BindingAction {
        confirmPassword.set("")
    })

    /**
     * 确认密码 可见ICON
     */
    val confirmPasswordVisibleIcon = ObservableField<Int>(R.mipmap.login_invisible)

    /**
     * 确认密码 密码样式
     */
    val confirmPasswordVisible = MutableLiveData<Boolean>(false)

    /**
     * 确认密码 可见点击事件
     */
    val confirmPasswordVisibleOnClick = BindingCommand<Unit>(BindingAction {
        confirmPasswordVisible.value = !confirmPasswordVisible.value!!
        confirmPasswordVisibleIcon.set(if (confirmPasswordVisible.value!!) R.mipmap.login_visible else R.mipmap.login_invisible)
    })


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setRightTextVisible(View.VISIBLE)
        setRightTextColor(R.color.red_fa4848)
        setRightTextTxt(getApplication<Application>().resources.getString(R.string.module_mine_save))
    }


    override fun rightTextOnClick() {
        if (isSecondaryPassword.get()!!){
            modifySecondaryPassword()
        }else{
            modifyLoginPassword()
        }
    }


    /**
     * 修改登录密码
     */
    private fun  modifyLoginPassword(){
        if (oldPassword.get().isNullOrBlank()){
            ToastUtils.showLong("请输入原密码")
            return
        }
        if (newPassword.get().isNullOrBlank()){
            ToastUtils.showLong("请输入新密码")
            return
        }
        if (confirmPassword.get().isNullOrBlank()){
            ToastUtils.showLong("请确认新密码")
            return
        }
        if(newPassword.get()!! != confirmPassword.get()!!){
            ToastUtils.showLong("两次输入密码不一致")
            return
        }

        addSubscribe(
            model
                .modifyLoginPassword(
                    oldPassword.get()!!,
                    newPassword.get()!!,
                    confirmPassword.get()!!)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        ToastUtils.showLong(getApplication<Application>().resources.getString(R.string.module_mine_password_reset_complete))
                        ARouter
                            .getInstance()
                            .build(ARouterPath.Mine.PAGER_LOGIN)
                            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            .navigation()
                    }

                    override fun onFailToast(): Boolean {
                        return true
                    }
                })
        )
    }

    /**
     * 修改二级密码
     */
    private fun  modifySecondaryPassword(){

        if (oldPassword.get().isNullOrBlank()){
            ToastUtils.showLong("请输入原密码")
            return
        }

        if (newPassword.get().isNullOrBlank()){
            ToastUtils.showLong("请输入新密码")
            return
        }

        addSubscribe(
            model
                .modifySecondaryPassword(
                    oldPassword.get()!!,
                    newPassword.get()!!,
                    newPassword.get()!!)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        ToastUtils.showLong(getApplication<Application>().resources.getString(R.string.module_mine_password_reset_complete))
                        finish()
                    }

                    override fun onFailToast(): Boolean {
                        return true
                    }
                })
        )
    }
}