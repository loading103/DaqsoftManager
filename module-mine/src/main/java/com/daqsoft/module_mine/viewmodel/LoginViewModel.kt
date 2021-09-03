package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Looper
import android.view.View
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.BuildConfig
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.MD5
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_common.pojo.vo.CompanyInfo
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.http.ResponseThrowable
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.RequestBody

import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.util.logging.Handler

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 下午 2:02
 * @author zp
 * @describe 登录viewModel
 */
class LoginViewModel : BaseViewModel<MineRepository> {

    @ViewModelInject
    constructor(application: Application,mineRepository: MineRepository):super(application,mineRepository)

    /**
     * 账号
     */
    val accountNumber = ObservableField<String>("")

    /**
     * 密码
     */
    val password = ObservableField<String>("")

    /**
     * 验证码
     */
    val verifyCode = ObservableField<String>("")

    /**
     * 验证码可见性
     */
    val verifyCodeVisible =  ObservableField<Int>(View.GONE)


    /**
     * 清除账号可见性
     */
    val accountNumberCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 清除密码可见性
     */
    val passwordCleanVisible =  ObservableField<Int>(View.GONE)

    /**
     * 登陆失败提示可见性
     */
    val  errorMessageVisible = ObservableField<Int>(View.INVISIBLE)

    /**
     * 登陆失败提示信息
     */
    val errorMessage = ObservableField<String>()

    /**
     * 清除账号点击事件
     */
    val accountNumberCleanOnClick = BindingCommand<Unit>(BindingAction {
        accountNumber.set("")
    })

    /**
     * 清除密码点击事件
     */
    val passwordCleanOnClick = BindingCommand<Unit>(BindingAction {
        password.set("")
    })

    /**
     * 密码可见性
     */
    val passwordVisible = MutableLiveData<Boolean>(false)

    /**
     * 密码可见ICON
     */
    val passwordVisibleIcon = ObservableField<Int>(R.mipmap.login_invisible)

    /**
     * 密码可见点击事件
     */
    val passwordVisibleOnClick = BindingCommand<Unit>(BindingAction {
        passwordVisible.value = !passwordVisible.value!!
        passwordVisibleIcon.set(if (passwordVisible.value!!) R.mipmap.login_visible else R.mipmap.login_invisible)
    })

    /**
     * 账号密码是否都有数据
     */
    val bothHaveData = MutableLiveData<Pair<Boolean,Boolean>>(Pair(false,false))
    
    /**
     * 账号输入监听
     */
    var accountNumberChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            accountNumberCleanVisible.set(View.GONE)
            bothHaveData.value = Pair(false,bothHaveData.value!!.second)
        }else{
            accountNumberCleanVisible.set(View.VISIBLE)
            bothHaveData.value = Pair(true,bothHaveData.value!!.second)
        }
    })

    /**
     * 密码输入监听
     */
    var passwordChangedListener = BindingCommand<String>(BindingConsumer {
        if (it.isNullOrEmpty()){
            passwordCleanVisible.set(View.GONE)
            bothHaveData.value = Pair(bothHaveData.value!!.first,false)
        }else{
            passwordCleanVisible.set(View.VISIBLE)
            bothHaveData.value = Pair(bothHaveData.value!!.first,true)
        }
    })


    /**
     * 登录点击事件
     */
    val logInOnClick = BindingCommand<Unit>(BindingAction {
        if(!NetworkUtil.isNetworkAvailable(getApplication())){
            errorMessageVisible.set(View.VISIBLE)
            errorMessage.set(getApplication<Application>().resources.getString(R.string.network_connection_failed))
            return@BindingAction
        }
        logIn()
    })


    override fun onCreate() {
        super.onCreate()

        accountNumber.set(AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.ACCOUNT_NUMBER)))
        password.set(AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.PASSWORD)))

    }

    /**
     * 登录
     */
    private fun logIn(){
        addSubscribe(
            model
                .login(accountNumber.get()!!, MD5.hexdigest(MD5.hexdigest(password.get()!!)),verifyCode.get(),verifySession = verifySession)
                .doOnSubscribe{
                    android.os.Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        // 报讯用户信息
                        DataStoreUtils.put(DSKeyGlobal.TOKEN,AesCryptUtils.encrypt(t.token?:""))
                        DataStoreUtils.put(DSKeyGlobal.ACCOUNT_NUMBER,AesCryptUtils.encrypt(accountNumber.get()!!))
                        DataStoreUtils.put(DSKeyGlobal.PASSWORD,AesCryptUtils.encrypt(password.get()!!))

                        ARouter
                            .getInstance()
                            .build(ARouterPath.Main.PAGER_MAIN)
                            .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            .navigation()
                    }

                    override fun onFail(e: Throwable) {
                        dismissLoadingDialog()
                        errorMessageVisible.set(View.VISIBLE)
                        if (e is ResponseThrowable){
                            errorMessage.set(e.errMessage)
                        }
//                        errorMessage.set( getApplication<Application>().resources.getString(R.string.module_mine_login_failed_tips))
                    }

                    override fun onFailT(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        errorMessage.set(t.message)

//                        if (verifyCodeFirst){
//                            return
//                        }

                        if (t.data == null || (t.data != null && t.data.toString().toFloat() > 3)){
                            errorMessage.set(t.message)
                            verifyCodeVisible.set(View.VISIBLE)
                            getVerifyCode()
                            verifyCodeFirst = true
                        }
                    }
                })

        )
    }

    var verifyCodeFirst = false

    /**
     * 验证码点击事件
     */
    val verifyCodeOnClick = BindingCommand<Unit>(BindingAction {
        getVerifyCode()
    })

    var verifyBitmap = MutableLiveData<ByteArray>()

    var verifySession = ""

    /**
     * 获取验证码
     */
    fun getVerifyCode(){
        addSubscribe(
            model
                .getVerifyCode()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>(){
                    override fun onNext(t: Response<ResponseBody>?) {
                        verifySession = t?.headers()?.get("XQSESSION")?:""
                        val body = t?.body()?.bytes()
                        if(body == null || body.isEmpty()){
                            return
                        }
                        verifyBitmap.value = body
                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onComplete() {
                    }
                })

        )
    }


    /**
     * 公司信息
     */
    val companyInfo = ObservableField<CompanyInfo>()

    /**
     * 获取公司信息
     */
    fun getCompanyInfo(){
        addSubscribe(
            model
                .getCompanyInfo()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<CompanyInfo>>(){
                    override fun onSuccess(t: AppResponse<CompanyInfo>) {
                        t.data?.let {
                            companyInfo.set(it)
                        }
                    }
                })
        )
    }

}