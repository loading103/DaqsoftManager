package com.daqsoft.module_mine.binding

import android.util.TypedValue
import android.view.View
import androidx.databinding.BindingAdapter
import com.daqsoft.module_mine.widget.WorkProgressBar
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.ruffian.library.widget.RTextView
import java.util.*

/**
 * @package name：com.daqsoft.module_mine.binding
 * @date 31/12/2020 上午 11:34
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["workDuration"], requireAll = false)
        fun setWorkProgressBarDuration(view: WorkProgressBar, str : String?) {
            view.setText(str?:"")
        }
    }
}
