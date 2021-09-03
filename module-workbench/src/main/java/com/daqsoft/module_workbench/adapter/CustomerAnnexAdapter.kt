package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.graphics.Rect
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.widget.FullyGridLayoutManager
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.RecyclerviewAnnexFileCustomerBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewAnnexImageCustomerBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewAnnexLedgerCustomerBinding
import com.daqsoft.module_workbench.repository.pojo.vo.AccountBackBean
import com.luck.picture.lib.decoration.GridSpacingItemDecoration
import com.luck.picture.lib.entity.LocalMedia
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.lang.StringBuilder

/**
 * @package name：com.daqsoft.module_project.adapter
 * @date 9/4/2021 下午 5:59
 * @author zp
 * @describe 附件 adapter
 */
class CustomerAnnexAdapter  : BindingRecyclerViewAdapter<String>() {

    /**
     * 文档 adapter
     */
    private val fileAdapter : CustomerFileAdapter by lazy { CustomerFileAdapter() }
    /**
     * 文档 数据
     */
    private val fileData : MutableList<String> by lazy { mutableListOf<String>() }


    /**
     * 图片 adapter
     */
    private val imageAdapter : CustomerImageAdapter by lazy { CustomerImageAdapter() }
    /**
     * 图片 数据
     */
    private val imageData : MutableList<LocalMedia> by lazy { mutableListOf<LocalMedia>() }



    @SuppressLint("SetTextI18n")
    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: String
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        when (item) {
            ConstantGlobal.FILE -> {
                val itemBinding = binding as RecyclerviewAnnexFileCustomerBinding
                itemBinding.fileRecyclerView.run {
                    layoutManager = LinearLayoutManager(context)
                    if(itemDecorationCount == 0) {
                        addItemDecoration(object : RecyclerView.ItemDecoration() {
                            override fun getItemOffsets(
                                outRect: Rect,
                                view: View,
                                parent: RecyclerView,
                                state: RecyclerView.State
                            ) {
                                val position = parent.getChildAdapterPosition(view)
                                if (position > 0) {
                                    outRect.top = 20.dp
                                }
                            }
                        })
                    }
                    adapter = fileAdapter.apply {
                        this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE, R.layout.recyclerview_annex_file_item_customer)
                        setItems(fileData)
                        setItemOnClickListener(object : CustomerFileAdapter.ItemOnClickListener{
                            override fun deleteOnClick(position: Int, item: String) {
                                fileData.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, fileData.size)
                                onClickListener?.remove(position, ConstantGlobal.FILE)
                            }
                        })
                    }
                }
            }
            ConstantGlobal.IMAGE -> {
                val itemBinding = binding as RecyclerviewAnnexImageCustomerBinding
                itemBinding.imageRecyclerView.run {
                    layoutManager = FullyGridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                    if(itemDecorationCount == 0) {
                        addItemDecoration(GridSpacingItemDecoration(4, 3.dp, false))
                    }
                    adapter = imageAdapter.apply {
                        this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recyclerview_annex_image_item_customer)
                        setItems(imageData)
                        setOnClickListener(object : CustomerImageAdapter.OnClickListener{
                            override fun deleteOnClick(position: Int, item: LocalMedia) {
                                imageData.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, imageData.size)
                                onClickListener?.remove(position, ConstantGlobal.IMAGE)

                            }
                        })
                    }
                }
            }
        }
    }



    /**
     * 设置文件数据
     */
    fun setFileData(data :  MutableList<String>){
        fileData.clear()
        fileData.addAll(data)
        fileAdapter.setItems(fileData)
        fileAdapter.notifyDataSetChanged()
    }


    /**
     * 设置图片数据
     */
    fun setImageData(data :  MutableList<LocalMedia>){
        imageData.clear()
        imageData.addAll(data)
        imageAdapter.setItems(imageData)
        imageAdapter.notifyDataSetChanged()
    }

    private var onClickListener : OnClickListener? = null

    fun setItemRemoveListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun remove(position: Int, item: String)


        fun ledgerModify()
    }
}