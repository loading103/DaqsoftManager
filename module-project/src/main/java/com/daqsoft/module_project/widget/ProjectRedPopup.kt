package com.daqsoft.module_project.widget

import android.content.Context
import android.graphics.Rect
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_project.R
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.adapter.ProjectRedAdapter
import com.daqsoft.module_project.adapter.ProjectRedTopAdapter
import com.daqsoft.module_project.repository.pojo.vo.Member
import com.daqsoft.module_project.repository.pojo.vo.ProductBean
import com.daqsoft.module_project.repository.pojo.vo.Standard
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import me.tatarka.bindingcollectionadapter2.ItemBinding

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @describe 建设内容
 */
class ProjectRedPopup  : BottomPopupView {

    private var bean: ProductBean? =null

    constructor(context: Context) : super(context)

    constructor(context: Context, title: ProductBean) : super(context) {
        this.bean = title
    }

    private val mAdapter : ProjectRedAdapter by lazy { ProjectRedAdapter() }
    private var title: TextView? = null
    private var title2: TextView? = null
    private var icon: ImageView? = null

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_project_red
    }

    override fun initPopupContent() {
        super.initPopupContent()
        icon = findViewById<ImageView>(R.id.icon)
        title = findViewById<TextView>(R.id.title)
        title2 = findViewById<TextView>(R.id.title2)
        val ll_infor_content = findViewById<TextView>(R.id.ll_infor_content)


        title?.text = bean?.product?.productName
        title2?.text = bean?.product?.productServiceMethod
        ll_infor_content?.text = Html.fromHtml(bean?.product?.productIntroduction)

        if(bean?.product?.productLogoUrl.isNullOrBlank()){
            icon?.visibility=View.GONE
        }else{
            icon?.visibility=View.VISIBLE
            Glide.with(context).load( bean?.product?.productLogoUrl).centerInside().into(icon!!)
        }

        val close = findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dismiss()
        }

        val recycleView = findViewById<RecyclerView>(R.id.file_recycler_view)
        recycleView.apply {
            adapter = mAdapter.apply {
                itemBinding = ItemBinding.of(BR.content, R.layout.recyclerview_project_red)
                setItems(bean?.member)
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                        outRect.right =20.dp
                    }
                })
            }
        }
    }


    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.7f).toInt()
    }

    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onClick(position: Int,content : String)
    }

}