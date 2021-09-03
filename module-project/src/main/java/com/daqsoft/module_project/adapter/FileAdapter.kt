package com.daqsoft.module_project.adapter

import androidx.databinding.ViewDataBinding
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProjectDynamicAnnexFileItemBinding

import com.luck.picture.lib.entity.LocalMedia
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import java.io.File


/**
 * @package name：com.daqsoft.module_project.adapter
 * @date 8/4/2021 上午 11:55
 * @author zp
 * @describe
 */
class FileAdapter : BindingRecyclerViewAdapter<String>() {

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewProjectDynamicAnnexFileItemBinding
        val file = File(item)
        val fileName = file.name
        val suffix: String = fileName.substring(fileName.lastIndexOf("."))
        itemBinding.name.text = file.name
        itemBinding.icon.setImageResource(
            when {
                FileUtils.isPDF(suffix) -> {
                    R.mipmap.xmdt_file_icon_pdf
                }
                FileUtils.isPPT(suffix) -> {
                    R.mipmap.xmdt_file_icon_ppt
                }
                FileUtils.isCompressed(suffix) -> {
                    R.mipmap.xmdt_file_icon_zip
                }
                FileUtils.isExcel(suffix) -> {
                    R.mipmap.xmdt_file_icon_excel
                }
                FileUtils.isWord(suffix) -> {
                    R.mipmap.xmdt_file_icon_word
                }
                FileUtils.isTxt(suffix) -> {
                    R.mipmap.bmwj_list_txt
                }
                FileUtils.isVideo(suffix) -> {
                    R.mipmap.bmwj_list_video
                }
                FileUtils.isImage(suffix) -> {
                    R.mipmap.bmwj_list_jpg
                }
                else -> {
                    R.mipmap.bmwj_list_default
                }
            }
        )
        itemBinding.delete.setOnClickListener {
            itemOnClickListener?.deleteOnClick(position, item)
        }
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun deleteOnClick(position: Int, item: String)
    }
}