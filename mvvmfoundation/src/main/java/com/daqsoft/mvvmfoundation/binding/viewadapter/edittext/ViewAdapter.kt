package com.daqsoft.mvvmfoundation.binding.viewadapter.edittext

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
         * EditText重新获取焦点的事件绑定
         * @param editText EditText
         * @param needRequestFocus Boolean
         */
        @JvmStatic
        @BindingAdapter(value = ["requestFocus"], requireAll = false)
        fun requestFocusCommand(editText: EditText, needRequestFocus: Boolean) {
            if (needRequestFocus) {
                editText.setSelection(editText.text.length)
                editText.requestFocus()
                val imm =
                    editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
            editText.isFocusableInTouchMode = needRequestFocus
        }

        /**
         * EditText输入文字改变的监听
         * @param editText EditText
         * @param textChanged BindingCommand<String?>?
         */
        @JvmStatic
        @BindingAdapter(value = ["textChanged"], requireAll = false)
        fun addTextChangedListener(editText: EditText, textChanged: BindingCommand<String?>?) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    charSequence: CharSequence,
                    i: Int,
                    i1: Int,
                    i2: Int
                ) {
                }

                override fun onTextChanged(text: CharSequence, i: Int, i1: Int, i2: Int) {
                    textChanged?.execute(text.toString())
                }

                override fun afterTextChanged(editable: Editable) {}
            })
        }
    }
}