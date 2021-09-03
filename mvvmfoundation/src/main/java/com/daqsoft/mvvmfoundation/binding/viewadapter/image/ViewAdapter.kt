package com.daqsoft.mvvmfoundation.binding.viewadapter.image

import android.text.TextUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

/**
 * @package name：com.daqsoft.mvvmfoundation.binding.viewadapter.image
 * @date 26/10/2020 下午 2:38
 * @author zp
 * @describe
 */
class ViewAdapter {

    companion object {
        /**
         * 图片加载
         * @param imageView ImageView
         * @param url String?
         * @param placeholderRes Int
         */
        @JvmStatic
        @BindingAdapter(value = ["url", "placeholderRes","circle"], requireAll = false)
        fun setImageUri(imageView: ImageView, url: String?, placeholderRes: Int,circle:Boolean = false) {
                Glide
                    .with(imageView.context)
                    .load(url?:"")
                    .placeholder(placeholderRes)
                    .centerInside()
                    .apply {
                        if (circle){
                            this.apply(RequestOptions.bitmapTransform(CircleCrop()))
                        }
                    }
                    .into(imageView)
        }
    }
}