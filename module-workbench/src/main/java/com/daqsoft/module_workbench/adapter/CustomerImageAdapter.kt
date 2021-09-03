package com.daqsoft.module_workbench.adapter

import android.net.Uri
import android.view.View
import androidx.databinding.ViewDataBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewAnnexImageItemCustomerBinding
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter


/**
 * @package name：com.daqsoft.module_project.adapter
 * @date 8/4/2021 上午 11:55
 * @author zp
 * @describe
 */
class CustomerImageAdapter : BindingRecyclerViewAdapter<LocalMedia>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: LocalMedia
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewAnnexImageItemCustomerBinding

        itemBinding.ivDel.setOnClickListener {
            onClickListener?.deleteOnClick(position, item)
        }

        if (item.mimeType == PictureMimeType.MIME_TYPE_VIDEO) {
            binding.video.visibility = View.VISIBLE
        } else {
            binding.video.visibility = View.GONE
        }

        if (item.path.isNullOrBlank()) {
            return
        }
        val chooseModel = item.chooseModel
        val path: String = if (item.isCut && !item.isCompressed) {
            // 裁剪过
            item.cutPath
        } else if (item.isCompressed || item.isCut && item.isCompressed) {
            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            item.compressPath
        } else {
            // 原图
            item.path
        }

        if (chooseModel == PictureMimeType.ofAudio()) {
            binding.fiv.setImageResource(R.drawable.picture_audio_placeholder)
        } else {
            Glide.with(binding.root.context)
                .load(
                    if (path.startsWith("content://") && !item.isCut && !item.isCompressed) Uri.parse(
                        path
                    ) else path
                )
                .apply(
                    RequestOptions()
                        .placeholder(R.color.white_ffffff)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                )
                .into(binding.fiv)
        }

    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun deleteOnClick(position: Int, item: LocalMedia)
    }

}