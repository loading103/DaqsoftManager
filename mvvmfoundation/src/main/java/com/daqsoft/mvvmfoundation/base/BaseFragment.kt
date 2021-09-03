package com.daqsoft.mvvmfoundation.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trello.rxlifecycle4.components.support.RxFragment
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


/**
 * Author: Walk The Line
 * Date: 2020/10/25 17:08
 * Description:
 */
abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel<*>> : RxFragment(), IBaseView {

    private var isLoaded = false

    protected lateinit var binding: V
    protected lateinit var viewModel: VM
    private var viewModelId = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initParam()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, initContentView(
                inflater,
                container,
                savedInstanceState
            ), container, false
        )
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (needLazy()) {
            if (!isLoaded && !isHidden) {
                initData()
                isLoaded = true
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        isLoaded = false
        binding.unbind()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化Databinding和ViewModel
        initViewDataBinding()
        // 界面初始化
        initView()
        // 页面事件监听
        initViewObservable()
        // 数据获取
        if(!needLazy()){
            initData()
        }


    }


    /**
     * 注入绑定
     */
    private fun initViewDataBinding() {
        viewModelId = initVariableId()
        viewModel = createViewModel()
        binding.setVariable(viewModelId, viewModel)
        //让ViewModel拥有View的生命周期感应
        lifecycle.addObserver(viewModel)
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this)
    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    abstract fun initContentView(
        inflater: LayoutInflater?,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): Int

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    abstract fun initVariableId(): Int

    override fun initParam() {
    }

    override fun initView() {
    }

    override fun initData() {
    }

    override fun initViewObservable() {
    }

    open fun isBackPressed(): Boolean {
        return false
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
            return ViewModelProvider(this)[modelClass as Class<ViewModel>] as VM
        }
        return viewModel
    }

    open fun needLazy() = true
}