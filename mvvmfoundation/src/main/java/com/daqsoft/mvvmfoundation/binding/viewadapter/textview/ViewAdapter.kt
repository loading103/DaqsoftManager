package com.daqsoft.mvvmfoundation.binding.viewadapter.textview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.edittext
 * @date 26/10/2020 下午 2:33
 * @author zp
 * @describe
 */
class ViewAdapter {


    companion object {

        /**
         * 设置 drawable
         * @param textView TextView
         * @param res Int 图片资源
         * @param position Int? 0左 1上 2右 3下
         */
        @SuppressLint("UseCompatLoadingForDrawables")
        @JvmStatic
        @BindingAdapter(value = ["drawableResources","drawablePosition"], requireAll = false)
        fun drawableResources(textView: TextView , res: Int? = null, position : Int ? = null) {
            if (res == null){
                return
            }
            val drawable = textView.context.resources.getDrawable(res)
            drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
            if (position == null){
                textView.setCompoundDrawables(drawable, null, null, null)
            }else{
                when(position){
                    0->{
                        textView.setCompoundDrawables(drawable, null, null, null)
                    }
                    1->{
                        textView.setCompoundDrawables(null, drawable, null, null)
                    }
                    2->{
                        textView.setCompoundDrawables(null, null, drawable, null)
                    }
                    3->{
                        textView.setCompoundDrawables(null, null, null, drawable)
                    }
                }
            }
        }


        /**
         * 文字样式
         * @param textView TextView
         * @param ssb SpannableStringBuilder
         */
        @JvmStatic
        @BindingAdapter(value = ["spannableString"], requireAll = false)
        fun spannableString(textView: TextView , ssb: SpannableStringBuilder?) {
            if (ssb != null){
                textView.text = ssb
            }
        }



        /**
         * 文字颜色
         * @param textView TextView
         * @param ssb SpannableStringBuilder
         */
        @JvmStatic
        @BindingAdapter(value = ["textColorColor"], requireAll = false)
        fun setTextViewTextColor(textView: TextView , color : Int) {
            textView.setTextColor(color)
        }


        /**
         * 文字样式
         * @param textView TextView
         * @param ssb SpannableStringBuilder
         */
        @JvmStatic
        @BindingAdapter(value = ["fromHtml"], requireAll = false)
        fun setTextViewFromHtml(textView: TextView , s: String) {
            textView.setText(Html.fromHtml(s))
        }
    }
}