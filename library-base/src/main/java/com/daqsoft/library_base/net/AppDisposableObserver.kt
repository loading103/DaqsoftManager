package com.daqsoft.library_base.net

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.mvvmfoundation.http.BaseResponse
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.http.ResponseThrowable
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.rxjava3.observers.DisposableObserver


/**
 * @package name：com.daqsoft.mvvmfoundation.http
 * @date 26/10/2020 下午 3:38
 * @author zp
 * @describe
 */
abstract class AppDisposableObserver<T> : DisposableObserver<T>() {

    open fun onSuccessToast():Boolean = false

    abstract fun onSuccess(t:T)

    open fun onFailToast():Boolean = true
    
    open fun onFail(e: Throwable) {}

    open fun onFailT(t:T){}

    override fun onStart() {
        super.onStart()
        if (!NetworkUtil.isNetworkAvailable(ContextUtils.getContext())) {
//            Handler(Looper.getMainLooper()).post {
//                ToastUtils.showLong("网络连接不可用，请检查网络设置")
//            }
            onComplete()
        }
    }

    override fun onNext(t: T) {
        if (t is BaseResponse<*>){
            when(t.code){
                CodeRule.CODE_0 -> {
                    onSuccess(t)
                    if (onSuccessToast()){
                        ToastUtils.showLongSafe(t.message)
                    }
                }
                CodeRule.CODE_302 ->{
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Base.DIALOG)
                        .withString("hint",t.message)
                        .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .navigation()
                }
                else->{
                    onFail(Throwable("网络请求成功，服务器接口请求失败，错误信息： ${t.toString()}"))
                    if (onFailToast()){
                        ToastUtils.showLongSafe(t.message)
                    }
                    onFailT(t)
                }
            }
        }
    }


    override fun onError(e: Throwable) {
        e.printStackTrace()
        onFail(e)
        if (e is ResponseThrowable){
            if (onFailToast()){
                ToastUtils.showLongSafe(e.errMessage)
            }
            when(e.code){
                401 ->{
                    ARouter
                        .getInstance()
                        .build(ARouterPath.Base.DIALOG)
                        .withString("hint",e.message)
                        .withFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        .navigation()
                }
            }
            return
        }
    }

    override fun onComplete() {
    }

    object CodeRule {
        //请求成功, 正确的操作方式
        internal const val CODE_0 = 0

        //  token 过期
        internal const val CODE_302 = 302
    }

}