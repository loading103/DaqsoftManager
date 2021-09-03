package com.daqsoft.mvvmfoundation.base

/**
 * Author: Walk The Line
 * Date: 2020/10/25 15:33
 * Description:
 */
interface IModel {

    /**
     * ViewModel销毁时清除Model
     */
    fun onCleared()
}