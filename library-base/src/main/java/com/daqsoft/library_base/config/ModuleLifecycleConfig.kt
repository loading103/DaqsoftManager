package com.daqsoft.library_base.config

import android.app.Application
import androidx.annotation.Nullable
import com.daqsoft.library_base.init.IModuleInit

/**
 * 模块初始化
 */
object ModuleLifecycleConfig {


    //初始化组件-靠前
    fun initModuleAhead(@Nullable application: Application) {
        for (moduleInitName in ModuleLifecycleReflex.initModuleNames) {
            try {
                val clazz = Class.forName(moduleInitName)
                val init = clazz.newInstance() as IModuleInit
                //调用初始化方法
                init.onInitAhead(application)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }

    //初始化组件-靠后
    fun initModuleLow(@Nullable application: Application) {
        for (moduleInitName in ModuleLifecycleReflex.initModuleNames) {
            try {
                val clazz = Class.forName(moduleInitName)
                val init = clazz.newInstance() as IModuleInit
                //调用初始化方法
                init.onInitLow(application)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        }
    }

}