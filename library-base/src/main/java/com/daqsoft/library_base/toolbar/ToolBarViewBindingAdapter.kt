package com.daqsoft.library_base.toolbar

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * @package name：com.daqsoft.library_base.bindingadapter
 * @date 2/11/2020 下午 1:33
 * @author zp
 * @describe
 */
class ToolBarViewBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["backgroundRes"], requireAll = false)
        fun setBackgroundRes(view: View, res: Int) {
            try {
                view.setBackgroundResource(res)
            } catch (e: Exception) {
                view.setBackgroundColor(res)
            }

        }


        @JvmStatic
        @BindingAdapter(value = ["imageSrc"], requireAll = false)
        fun setImageSrc(view: ImageView, res: Int) {
            view.setImageResource(res)
        }

        @JvmStatic
        @BindingAdapter(value = ["loadResImage", "loadUrlImage"], requireAll = false)
        fun setLoadLocalImage(imageView: ImageView, res: Int, url: String?) {
            Glide.with(imageView.context)
                .load(
                    if (url.isNullOrEmpty()) {
                        res
                    } else {
                        url
                    }
                )
                .into(imageView)
        }

        @JvmStatic
        @BindingAdapter(value = ["textColorRes"], requireAll = false)
        fun setTextColorRes(textView: TextView, res: Int) {
            textView.setTextColor(textView.context.resources.getColor(res))
        }

        @JvmStatic
        @BindingAdapter(value = ["viewHeight"], requireAll = false)
        fun setViewHeight(view: View, height: Int) {
            val params = view.layoutParams
            params.height = height
            view.layoutParams = params

        }


        @JvmStatic
        @BindingAdapter(value = ["viewPaddingTop"], requireAll = false)
        fun setViewPaddingTop(view: View, paddingTop: Int) {
            view.setPadding(0,paddingTop,0,0)
        }

    }
}