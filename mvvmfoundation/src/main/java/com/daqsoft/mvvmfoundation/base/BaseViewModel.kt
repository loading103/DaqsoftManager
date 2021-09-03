package com.daqsoft.mvvmfoundation.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.trello.rxlifecycle4.LifecycleProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import java.lang.ref.WeakReference

/**
 * Author: Walk The Line
 * Date: 2020/10/25 15:34
 * Description:
 */
open class BaseViewModel<M : BaseModel> : AndroidViewModel,IBaseViewModel, Consumer<Disposable> {


    lateinit var model : M

    constructor(application: Application):super(application){
        BaseViewModel(application,BaseModel())
    }
    constructor(application: Application, model: M):super(application){
        this.model = model
    }

    private var lifecycle: WeakReference<LifecycleProvider<*>>? = null
    private var mCompositeDisposable: CompositeDisposable? = null


    fun injectLifecycleProvider(lifecycle: LifecycleProvider<*>) {
        this.lifecycle = WeakReference(lifecycle)
    }

    protected fun getLifecycleProvider(): LifecycleProvider<*>? {
        return lifecycle?.get()
    }


    override fun onAny(owner: LifecycleOwner?, event: Lifecycle.Event?) {
    }

    override fun onCreate() {
    }

    override fun onDestroy() {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun accept(t: Disposable?) {
        addSubscribe(t)
    }


    override fun onCleared() {
        model.onCleared()
        mCompositeDisposable?.clear()
    }


    protected fun addSubscribe(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }


    val finishLiveData = MutableLiveData<Unit>()

    /**
     * 关闭界面
     */
    open fun finish() {
        finishLiveData.value = null
    }


    /**
     * 显示加载中 dialog
     */
    val showLoadingDialogLiveData by lazy {  MutableLiveData<String?>() }
    fun showLoadingDialog(title:String? = null){
        showLoadingDialogLiveData.value = title
    }

    /**
     * 取消 加载中 dialog
     */
    val dismissLoadingDialogLiveData by lazy {  MutableLiveData<Void>() }
    fun dismissLoadingDialog(){
        dismissLoadingDialogLiveData.value = null
    }

}