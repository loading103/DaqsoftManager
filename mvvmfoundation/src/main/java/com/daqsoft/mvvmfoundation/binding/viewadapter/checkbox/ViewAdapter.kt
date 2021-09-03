package com.daqsoft.mvvmfoundation.binding.viewadapter.checkbox

import android.widget.CheckBox
import androidx.databinding.BindingAdapter
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand

/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.checkbox
 * @date 26/10/2020 下午 2:35
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {


        /**
         * 单选框改变监听
         * @param checkBox CheckBox
         * @param bindingCommand BindingCommand<Boolean?>
         */
        @JvmStatic
        @BindingAdapter(value = ["onCheckedChangedCommand"], requireAll = false)
        fun setCheckedChanged(checkBox: CheckBox, bindingCommand: BindingCommand<Boolean?>) {
            checkBox.setOnCheckedChangeListener { compoundButton, b -> bindingCommand.execute(b) }
        }
    }
}