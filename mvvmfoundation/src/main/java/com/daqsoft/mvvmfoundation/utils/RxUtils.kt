package com.daqsoft.mvvmfoundation.utils

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import com.daqsoft.mvvmfoundation.http.ExceptionHandle
import com.trello.rxlifecycle4.LifecycleProvider
import com.trello.rxlifecycle4.LifecycleTransformer
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableTransformer
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers

object RxUtils {

    /**
     * 线程切换
     */
    fun <T> schedulersTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        }
    }

    /**
     * 网络错误的异常转换
     */
    fun <T> exceptionTransformer(): ObservableTransformer<T, T> {
        return ObservableTransformer { upstream ->
            upstream.onErrorResumeNext(Function<Throwable, Observable<T>> { t ->
                Observable.error(
                    ExceptionHandle.handleException(t)
                )
            })
        }
    }


    /**
     * 绑定到生命周期
     * @param lifecycle Context
     * @return LifecycleTransformer<T>
     */
    @SuppressLint("CheckResult")
    fun <T> bindToLifecycle(@NonNull lifecycle: LifecycleProvider<*>): LifecycleTransformer<T> {
        return lifecycle.bindToLifecycle<T>()
    }
}