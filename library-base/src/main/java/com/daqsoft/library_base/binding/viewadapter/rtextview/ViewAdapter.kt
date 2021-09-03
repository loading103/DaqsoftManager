package com.daqsoft.library_base.binding.viewadapter.rtextview

import android.graphics.Color
import android.util.TypedValue
import android.view.View
import androidx.databinding.BindingAdapter
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.ruffian.library.widget.RTextView
import java.util.*

/**
 * @package name：com.daqsoft.library_base.binding.viewadapter
 * @date 11/12/2020 上午 10:28
 * @author zp
 * @describe
 */


/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.view
 * @date 26/10/2020 上午 11:32
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {

        @JvmStatic
        @BindingAdapter(value = ["messageNumber"], requireAll = false)
        fun setRoundMessageViewNumber(view: RTextView, number: Int) {

            if (number == 0){
                view.visibility = View.INVISIBLE
                return
            }

            view.visibility = View.VISIBLE
            if (number > 0) {
                if (number < 10) {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                } else {
                    view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7f)
                }
                if (number <= 99) {
                    view.text = String.format(Locale.ENGLISH, "%d", number)
                } else {
                    view.text = String.format(Locale.ENGLISH, "%d+", 99)
                }
                return
            }

            if (number < 0){
                view.text = ""
            }
        }

        @JvmStatic
        @BindingAdapter(value = ["backgroundNormalArray"], requireAll = false)
        fun setRTextViewBackgroundNormalArray(view: RTextView, res : Int) {
            view.helper.backgroundColorNormalArray = view.context.resources.getIntArray(res)
        }


        @JvmStatic
        @BindingAdapter(value = ["backgroundNormalArrayRGB"], requireAll = false)
        fun setRTextViewBackgroundNormalArrayRGB(view: RTextView, rgbArray : IntArray) {
            view.helper.backgroundColorNormalArray = rgbArray
        }


        @JvmStatic
        @BindingAdapter(value = ["backgroundNormal"], requireAll = false)
        fun setRTextViewBackgroundNormal(view: RTextView, color : Int) {
            view.helper.backgroundColorNormal = color
        }
    }
}
