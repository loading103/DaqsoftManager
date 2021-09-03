package com.daqsoft.mvvmfoundation.base

import android.app.Activity
import androidx.fragment.app.Fragment
import java.util.*

/**
 * Author: Walk The Line
 * Date: 2020/10/25 15:05
 * Description: activity堆栈管理
 */
object AppManager {

    private var activityStack: Stack<Activity>? = null

    /**
     * 获取activity堆栈
     */
    fun getActivityStack(): Stack<Activity>? {
        return activityStack
    }

    /**
     * 添加Activity到堆栈
     */
    fun addActivity(activity: Activity?) {
        if (activityStack == null) {
            activityStack = Stack()
        }
        activityStack?.add(activity)
    }


    /**
     * 移除指定的Activity
     */
    fun removeActivity(activity: Activity?) {
        activityStack?.remove(activity)
    }

    /**
     * 是否有activity
     */
    fun isActivity(): Boolean {
        return if (activityStack != null) {
            !activityStack!!.isEmpty()
        } else false
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    fun currentActivity(): Activity? {
        return activityStack?.lastElement()
    }

    /**
     * 结束指定类名的Activity
     */
    fun finishActivity(cls: Class<*>) {
        activityStack?.let {
            it.filter { it.javaClass == cls }.forEach { finishActivity(it.javaClass) }
        }
    }

    /**
     * 结束指定类以外的Activity
     */
    fun finishActivityOutside(cls: Class<*>) {
        activityStack?.let {
            it.filter { it.javaClass != cls }.forEach { finishActivity(it.javaClass) }
        }
    }

    /**
     * 结束所有Activity
     */
    fun finishAllActivity() {
        activityStack?.let {
            it.forEach { finishActivity(it.javaClass) }
            it.clear()
        }
    }

    /**
     * 获取指定的Activity
     */
    fun getActivity(cls: Class<*>): Activity? {
        return activityStack?.first { it.javaClass == cls }
    }

}
