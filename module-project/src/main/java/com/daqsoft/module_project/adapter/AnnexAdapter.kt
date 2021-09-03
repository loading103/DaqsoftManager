package com.daqsoft.module_project.adapter

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
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.RecyclerviewProjectDynamicAnnexFileBinding
import com.daqsoft.module_project.databinding.RecyclerviewProjectDynamicAnnexImageBinding
import com.daqsoft.module_project.databinding.RecyclerviewProjectDynamicAnnexLedgerBinding
import com.daqsoft.module_project.databinding.RecyclerviewProjectDynamicAnnexLedgerBindingImpl
import com.daqsoft.module_project.repository.pojo.vo.AccountBackBean
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
class AnnexAdapter  : BindingRecyclerViewAdapter<String>() {

    /**
     * 文档 adapter
     */
    private val fileAdapter : FileAdapter by lazy { FileAdapter() }
    /**
     * 文档 数据
     */
    private val fileData : MutableList<String> by lazy { mutableListOf<String>() }


    /**
     * 图片 adapter
     */
    private val imageAdapter : ImageAdapter by lazy { ImageAdapter() }
    /**
     * 图片 数据
     */
    private val imageData : MutableList<LocalMedia> by lazy { mutableListOf<LocalMedia>() }

    /**
     * 记账 数据
     */
    private val ledgerData : MutableList<AccountBackBean> by lazy { mutableListOf<AccountBackBean>() }


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
                val itemBinding = binding as RecyclerviewProjectDynamicAnnexFileBinding
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
                        this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recyclerview_project_dynamic_annex_file_item)
                        setItems(fileData)
                        setItemOnClickListener(object : FileAdapter.ItemOnClickListener{
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
                val itemBinding = binding as RecyclerviewProjectDynamicAnnexImageBinding
                itemBinding.imageRecyclerView.run {
                    layoutManager = FullyGridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                    if(itemDecorationCount == 0) {
                        addItemDecoration(GridSpacingItemDecoration(4, 3.dp, false))
                    }
                    adapter = imageAdapter.apply {
                        this.itemBinding = ItemBinding.of(ItemBinding.VAR_NONE,R.layout.recyclerview_project_dynamic_annex_image_item)
                        setItems(imageData)
                        setOnClickListener(object : ImageAdapter.OnClickListener{
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
            ConstantGlobal.LEDGER ->{
                val itemBinding = binding as RecyclerviewProjectDynamicAnnexLedgerBinding
                itemBinding.run {
                    ledgerModify.setOnClickListener {
                        onClickListener?.ledgerModify()
                    }

                    ledgerDelete.setOnClickListener {
                        onClickListener?.remove(position,ConstantGlobal.LEDGER)
                    }


                    lumpSum.text = "总金额：${ledgerData[0].totleMoney}"
                    ledgerTime.text = "记账日期：${ledgerData[0].time}"

                    val sb = StringBuilder()
                    ledgerData.forEachIndexed { index, accountBackBean ->
                        sb.append("${index+1}、${accountBackBean.costType}：¥${String.format("%.2f", accountBackBean.money?.toFloat()?:0)}")
                        if (index != ledgerData.size-1){
                            sb.append("\n")
                        }
                    }
                    ledgerContent.text = sb.toString()
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

    /**
     * 设置记账数据
     */
    fun setLedgerData(data :  MutableList<AccountBackBean>){
        ledgerData.clear()
        ledgerData.addAll(data)
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