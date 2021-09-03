package com.daqsoft.mvvmfoundation.base

import android.os.Bundle
import android.os.PersistableBundle
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import timber.log.Timber
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Author: Walk The Line
 * Date: 2020/10/25 16:12
 * Description:
 */
abstract class BaseActivity<V : ViewDataBinding,VM: BaseViewModel<*>> : RxAppCompatActivity() ,IBaseView{

    protected lateinit var binding: V
    protected lateinit var viewModel: VM
    private var viewModelId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 参数接收
        initParam()
        // 初始化dataBinding和ViewModel
        initViewDataBinding(savedInstanceState)
        // 界面初始化
        initView()
        // 页面事件监听
        initViewObservable()
        // 数据获取
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    /**
     * 注入绑定
     */
    private fun initViewDataBinding(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, initContentView(savedInstanceState))
        viewModelId = initVariableId()
        viewModel = createViewModel()
        //关联ViewModel
        binding.setVariable(viewModelId, viewModel)
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this)
    }

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int


    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(savedInstanceState: Bundle?): Int


    override fun initParam() {

    }

    override fun initView() {

    }

    override fun initData() {

    }

    override fun initViewObservable() {
        viewModel.finishLiveData.observe(this, Observer {
            finish()
        })
    }


    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    open fun initViewModel(): VM? {
        return null
    }


    private fun createViewModel():VM{
        val viewModel = initViewModel()
        if (viewModel == null){
            val modelClass: Class<*>
            val type: Type? = javaClass.genericSuperclass
            modelClass = if (type is ParameterizedType) {
                type.actualTypeArguments[1] as Class<*>
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                BaseViewModel::class.java
            }
           return ViewModelProvider.AndroidViewModelFactory(application).create(modelClass as Class<ViewModel> ) as VM
        }
        return viewModel
    }
}