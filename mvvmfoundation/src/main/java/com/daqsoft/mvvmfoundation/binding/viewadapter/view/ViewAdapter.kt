package com.daqsoft.mvvmfoundation.binding.viewadapter.view

import android.annotation.SuppressLint
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.databinding.BindingAdapter
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.longClicks
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.view
 * @date 26/10/2020 上午 11:32
 * @author zp
 * @describe
 */
class ViewAdapter {
    //防重复点击间隔(秒)
    companion object {
        const val CLICK_INTERVAL = 1L


        /**
         * 点击事件绑定
         * @param view View
         * @param bindingCommand BindingCommand<*>? 绑定命令
         * @param isThrottleFirst Boolean 是否防抖
         */
        @JvmStatic
        @SuppressLint("CheckResult")
        @BindingAdapter(value = ["onClickCommand", "isThrottleFirst"], requireAll = false)
        fun onClickCommand(
            view: View,
            bindingCommand: BindingCommand<*>?,
            isThrottleFirst: Boolean
        ) {
            if (isThrottleFirst) {
                view
                    .clicks()
                    .subscribe {
                        bindingCommand?.execute()
                    }
            } else {
                view
                    .clicks()
                    .throttleFirst(CLICK_INTERVAL, TimeUnit.SECONDS)
                    .subscribe {
                        bindingCommand?.execute()
                    }
            }
        }


        /**
         * 长按事件
         * @param view View
         * @param bindingCommand BindingCommand<*>?
         */
        @JvmStatic
        @BindingAdapter(value = ["onLongClickCommand"], requireAll = false)
        fun onLongClickCommand(view: View, bindingCommand: BindingCommand<*>?) {
            view
                .longClicks()
                .subscribe {
                    bindingCommand?.execute()
                }
        }

        /**
         * 回调本身
         * @param view View
         * @param bindingCommand BindingCommand<View>?
         */
        @JvmStatic
        @BindingAdapter(value = ["currentView"], requireAll = false)
        fun replyCurrentView(view: View, bindingCommand: BindingCommand<View>?) {
            bindingCommand?.execute(view)
        }

        /**
         * view的显示隐藏
         * @param view View
         * @param visibility Boolean
         */
        @JvmStatic
        @BindingAdapter(value = ["isVisible"], requireAll = false)
        fun isVisible(view: View, visibility: Boolean) {
            if (visibility) {
                view.visibility = View.VISIBLE
            } else {
                view.visibility = View.GONE
            }
        }

        /**
         * view是否需要获取焦点
         * @param view View
         * @param needRequestFocus Boolean
         */
        @JvmStatic
        @BindingAdapter(value = ["requestFocus"], requireAll = false)
        fun requestFocusCommand(view: View, needRequestFocus: Boolean) {
            if (needRequestFocus) {
                view.isFocusableInTouchMode = true
                view.requestFocus()
            } else {
                view.clearFocus()
            }
        }

        /***
         * view的焦点发生变化的事件绑定
         * @param view View
         * @param onFocusChangeCommand BindingCommand<Boolean?>?
         */
        @JvmStatic
        @BindingAdapter(value = ["onFocusChangeCommand"], requireAll = false)
        fun onFocusChangeCommand(view: View, onFocusChangeCommand: BindingCommand<Boolean?>?) {
            view.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
                onFocusChangeCommand?.execute(hasFocus)
            }
        }



        @JvmStatic
        @BindingAdapter(value = ["selectedFlag"], requireAll = false)
        fun setViewSelectedFlag(view: View, boolean: Boolean) {
            view.isSelected = boolean
        }

        @JvmStatic
        @BindingAdapter(value = ["enabledFlag"], requireAll = false)
        fun setViewEnabledFlag(view: View, boolean: Boolean) {
            view.isEnabled = boolean
        }
    }
}