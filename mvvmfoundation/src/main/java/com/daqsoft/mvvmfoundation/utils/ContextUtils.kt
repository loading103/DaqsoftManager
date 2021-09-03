package com.daqsoft.mvvmfoundation.utils

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.NonNull

/**
 * @package name：com.daqsoft.mvvmfoundation.utils
 * @date 26/10/2020 下午 4:10
 * @author zp
 * @describe
 */
object ContextUtils {

    @SuppressLint("StaticFieldLeak")
    private var context: Context? = null


    /**
     * 初始化工具类
     *
     * @param context 上下文
     */
    fun init(@NonNull context: Context) {
        this.context = context.applicationContext
    }

    /**
     * 获取ApplicationContext
     *
     * @return ApplicationContext
     */
    fun getContext(): Context {
        if (context != null) {
            return context!!
        }
        throw NullPointerException("should be initialized in application")
    }

}