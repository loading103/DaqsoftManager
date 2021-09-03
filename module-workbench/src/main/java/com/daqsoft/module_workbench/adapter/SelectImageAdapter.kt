package com.daqsoft.module_workbench.adapter

import android.net.Uri
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewSelectImageItemBinding
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia

/**
 * @package name：com.daqsoft.module_workbench.adapter
 * @date 26/2/2021 下午 2:24
 * @author zp
 * @describe 通知公告 图片选择adapter
 */
class SelectImageAdapter : RecyclerView.Adapter<SelectImageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: RecyclerviewSelectImageItemBinding):RecyclerView.ViewHolder(
        binding.root
    )

    private var list: MutableList<LocalMedia> = arrayListOf()
    private var selectMax = 9

    fun delete(position: Int) {
        try {
            if (position != RecyclerView.NO_POSITION && list.size > position) {
                list.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, list.size)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun setSelectMax(selectMax: Int) {
        this.selectMax = selectMax
    }

    fun setData(list: MutableList<LocalMedia>) {
        this.list = list
    }

    fun getData(): List<LocalMedia> {
        return list
    }

    fun remove(position: Int) {
        if (position < list.size) {
            list.removeAt(position)
        }
    }

    private var onItemClickListener : OnItemClickListener? = null

    fun setItemOnClickListener(onItemClickListener: OnItemClickListener){
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener{
        fun onClick(position: Int, v: View)
    }


    private var onItemLongClickListener : OnItemLongClickListener? = null

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener){
        this.onItemLongClickListener = onItemLongClickListener
    }

    interface OnItemLongClickListener{
        fun onItemLongClick(holder: RecyclerView.ViewHolder, position: Int, v: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RecyclerviewSelectImageItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.recyclerview_select_image_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.let { itemBinding ->
            itemBinding.position.text = (position+1).toString()
            var media = list[position]
            if (media == null || TextUtils.isEmpty(media.path)) {
                return
            }
            val chooseModel: Int = media.chooseModel
            var path: String = if (media.isCut && !media.isCompressed) {
                // 裁剪过
                media.cutPath
            } else if (media.isCompressed || media.isCut && media.isCompressed) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                media.compressPath
            } else {
                // 原图
                media.path
            }

            Glide
                .with(itemBinding.root.context)
                .load(
                    if (PictureMimeType.isContent(path) && !media.isCut && !media.isCompressed) Uri.parse(
                        path
                    ) else path
                )
                .centerCrop()
                .placeholder(R.color.gray_e8e8e8)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(itemBinding.image)


            itemBinding.root.setOnClickListener {
                val adapterPosition: Int = holder.bindingAdapterPosition
                onItemClickListener?.onClick(adapterPosition, it)
            }

            itemBinding.root.setOnLongClickListener {
                val adapterPosition: Int = holder.bindingAdapterPosition
                onItemLongClickListener?.onItemLongClick(holder, adapterPosition, it)
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

}