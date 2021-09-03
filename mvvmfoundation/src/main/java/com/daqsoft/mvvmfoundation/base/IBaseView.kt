package com.daqsoft.mvvmfoundation.base

/**
 * Author: Walk The Line
 * Date: 2020/10/25 16:23
 * Description:
 */
interface IBaseView {

    /**
     * 初始化界面传递参数
     */
    fun initParam()

    /**
     * 初始化 界面
     */
    fun initView()

    /**
     * 初始化数据
     */
    fun initData()

    /**
     * 初始化界面观察者的监听
     */
    fun initViewObservable()
}