package com.daqsoft.module_workbench.adapter

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.IMDensityUtil
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_common.javascript.JavaScriptInvokeAndroid
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.databinding.RecyclerviewCustomerRecordReplyItemBinding
import com.daqsoft.module_workbench.databinding.RecyclerviewPatherRecordItemBinding
import com.daqsoft.module_workbench.repository.pojo.vo.CustomerRecordBean
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.PatherRecordItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.zzhoujay.richtext.ImageHolder
import com.zzhoujay.richtext.RichText
import com.zzhoujay.richtext.callback.ImageFixCallback
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding
import timber.log.Timber
import java.math.BigDecimal


class PatherRecordAdapter constructor(val activity: AppBaseActivity<*, *>) : BindingRecyclerViewAdapter<ItemViewModel<*>>() {


    /**
     * 点击item
     */
    val clickItem = hashMapOf<String, Any>()

    override fun onBindBinding(
        binding: ViewDataBinding,
        variableId: Int,
        layoutRes: Int,
        position: Int,
        item: ItemViewModel<*>?
    ) {
        super.onBindBinding(binding, variableId, layoutRes, position, item)
        val itemBinding = binding as RecyclerviewPatherRecordItemBinding
        val itemViewModel = item as PatherRecordItemViewModel


        // itr状态（0 = 无，1=有，2=已关闭）
        when(itemViewModel.projectDynamic.itrState){
            0 -> {
                binding.labelItr.visibility = View.GONE
                binding.closeItr.visibility = View.GONE
                binding.closeItrTime.visibility = View.GONE
                binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.white_ffffff))
            }
            1 -> {
                binding.labelItr.visibility = View.VISIBLE
                binding.labelItr.helper.backgroundColorNormal =
                    binding.root.context.resources.getColor(
                        R.color.red_fa4848
                    )

//                binding.closeItr.visibility = View.VISIBLE
                binding.closeItr.visibility = View.GONE
                binding.closeItrTime.visibility = View.GONE
                binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.white_fff3f3))
            }
            2 -> {
                binding.labelItr.visibility = View.VISIBLE
                binding.labelItr.helper.backgroundColorNormal =
                    binding.root.context.resources.getColor(
                        R.color.green_23c070
                    )
                binding.closeItr.visibility = View.GONE
                binding.closeItrTime.visibility = View.VISIBLE
                binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.white_ffffff))
            }
            else ->{
                binding.labelItr.visibility = View.GONE
                binding.closeItr.visibility = View.GONE
                binding.closeItrTime.visibility = View.GONE
                binding.root.setBackgroundColor(binding.root.context.resources.getColor(R.color.white_ffffff))
            }
        }

//        // 日报
//        if(itemViewModel.projectDynamic.dailyReport){
//            binding.daily.visibility = View.VISIBLE
//        }else{
//            binding.daily.visibility = View.GONE
//        }

        // 标签
        if(!itemViewModel.projectDynamic.tags.isNullOrEmpty()){
            binding.labelTxt.visibility = View.VISIBLE
            binding.labelTxt.text = itemViewModel.projectDynamic.tags.joinToString(separator = " ") { it.tagName }
        }else{
            binding.labelTxt.visibility = View.GONE
        }


        // 内容
        if(itemViewModel.projectDynamic.noteInfo.isNullOrBlank()){
            binding.content.visibility = View.GONE
        }else{
//            binding.content.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            RichText
                .from(itemViewModel.projectDynamic.noteInfo)
                .autoFix(false)
                .fix(object : ImageFixCallback {
                    override fun onInit(holder: ImageHolder?) {
                    }

                    override fun onLoading(holder: ImageHolder?) {
                    }

                    override fun onSizeReady(
                        holder: ImageHolder?,
                        imageWidth: Int,
                        imageHeight: Int,
                        sizeHolder: ImageHolder.SizeHolder?
                    ) {
                        Log.e("------onSizeReady",sizeHolder?.width.toString())
                        if(sizeHolder?.width!! >700){
                            sizeHolder?.setSize(IMDensityUtil.dip2px(BaseApplication.getInstance()?.baseContext,300f),IMDensityUtil.dip2px(BaseApplication.getInstance()?.baseContext,600f))
                        }
                    }

                    override fun onImageReady(holder: ImageHolder?, width: Int, height: Int) {
                    }

                    override fun onFailure(holder: ImageHolder?, e: java.lang.Exception?) {
                    }

                })
                .autoPlay(true)
                .size(ImageHolder.WRAP_CONTENT, ImageHolder.WRAP_CONTENT)
                .scaleType(ImageHolder.ScaleType.fit_center)
                .showBorder(false)
                .imageClick{imageUrls, position ->
                    Timber.e("imageUrls ${imageUrls} , position ${position}")
                    onClickListener?.previewPicture(imageUrls,position)
                }
                .urlClick{
                    Timber.e("urlClick ${it}")
                    return@urlClick false
                }
                .linkFix {
                    Timber.e("linkFix ${it.url}")
                }
                .into(binding.content)
        }

        // 附件
        itemBinding.annexCl.visibility = View.GONE
        itemBinding.replyCardView.visibility = View.GONE
//        itemBinding.ledgerCl.visibility = View.GONE

        // 回复
        if (!itemViewModel.projectDynamic.replys.isNullOrEmpty()){
            itemBinding.annexCl.visibility = View.VISIBLE
            itemBinding.replyCardView.visibility = View.VISIBLE
            itemBinding.replyRecyclerView.apply {
                val replyAdapter = ReplyAdapter().apply {
                    items=itemViewModel.projectDynamic
                }
                replyAdapter.itemBinding = ItemBinding.of(
                    BR.daily,
                    R.layout.recyclerview_customer_record_reply_item
                )
                replyAdapter.setItems(itemViewModel.projectDynamic.replys)
                replyAdapter.setViewModel(position,itemBinding,itemViewModel)
                adapter = replyAdapter
                addItemDecoration(object : RecyclerView.ItemDecoration() {
                    override fun getItemOffsets(
                        outRect: Rect,
                        view: View,
                        parent: RecyclerView,
                        state: RecyclerView.State
                    ) {
                        outRect.top = 16.dp
                    }
                })
            }
        }

//        // 记账
//        if(itemViewModel.projectDynamic.bookingState == 1){
//            binding.annexCl.visibility = View.VISIBLE
//            binding.ledgerCl.visibility = View.VISIBLE
//
//            binding.ledgerAmount.text = "¥${itemViewModel.projectDynamic.booking.total}"
//            binding.ledgerTime.text = itemViewModel.projectDynamic.booking.bookingDate
//            val sb = StringBuilder()
//            itemViewModel.projectDynamic.booking.detail.forEachIndexed { index, bookingDetail ->
//                sb.append("${index + 1}、${bookingDetail.itemName}：¥${bookingDetail.money}")
//                if (index != itemViewModel.projectDynamic.booking.detail.size-1){
//                    sb.append("\n")
//                }
//            }
//            binding.ledgerContent.text = sb.toString()
//        }else{
//            binding.ledgerCl.visibility = View.GONE
//        }

        binding.closeItr.setOnClickListener {
            clickItem["position"] = position
            clickItem["itemBinding"] = itemBinding
            clickItem["itemViewModel"] = itemViewModel
            onClickListener?.closeItr(itemViewModel.projectDynamic)
        }

        binding.reply.setOnClickListener {
            clickItem["position"] = position
            clickItem["itemBinding"] = itemBinding
            clickItem["itemViewModel"] = itemViewModel
            onClickListener?.reply(itemViewModel.projectDynamic,itemViewModel.projectDynamic)
        }


    }


    /**
     * 初始化 webView
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView(webView: WebView){
        webView.requestFocus()
        webView.clearCache(true)
        webView.onResume()
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)
        val webSettings =  webView.settings
        // 不阻止加载网络图片
        webSettings.blockNetworkImage = false
        // 支持混合模式
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
//        // 将图片调整到适合webView的大小
//        webSettings.useWideViewPort = true
//        // 缩放至屏幕的大小
//        webSettings.loadWithOverviewMode = true
        // 关闭webView缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        // 设置可以访问文件
        webSettings.allowFileAccess = true
        // 支持通过js打开新窗口
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.javaScriptEnabled = true
        // 设置支持自动加载图片
        webSettings.loadsImagesAutomatically = true
        // 设置编码格式
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.savePassword = false
        webSettings.saveFormData = false
        // 打开本地缓存提供JS调用,至关重要
        webSettings.domStorageEnabled = true

        val jsInvokAndroid = JavaScriptInvokeAndroid(activity, webView)
        webView.addJavascriptInterface(jsInvokAndroid, "android")

        webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                AppUtils.jsResizeImages(webView)
            }
        }

        webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
        webView.setDownloadListener { p0, p1, p2, p3, p4 ->
        }
    }



    inner class ReplyAdapter : BindingRecyclerViewAdapter<CustomerRecordBean>() {

        var clickPosition: Int=0
        var itemClickBinding: RecyclerviewPatherRecordItemBinding?=null
        var itemClickViewModel: PatherRecordItemViewModel?=null
        var items: CustomerRecordBean?=null

        override fun onBindBinding(
            binding: ViewDataBinding,
            variableId: Int,
            layoutRes: Int,
            position: Int,
            item: CustomerRecordBean
        ) {
            super.onBindBinding(binding, variableId, layoutRes, position, item)

            val itemBinding = binding as RecyclerviewCustomerRecordReplyItemBinding

            // 标签
            if(!item.tags.isNullOrEmpty()){
                binding.labelTxt.visibility = View.VISIBLE
                binding.labelTxt.text = item.tags.joinToString(separator = " ") { it.tagName }
            }else{
                binding.labelTxt.visibility = View.GONE
            }

            // 内容
            if(item.noteInfo.isNullOrBlank()){
                binding.content.visibility = View.GONE
            }else {
//                binding.content.visibility = View.VISIBLE
//                initWebView(binding.content)
//                val content = AppUtils.getNewData(item.noteInfo.toHtml(), binding.content.width)
//                val bgColor = if (item.itrState == 1) "#fff3f3" else "#f5f5f5"
//                val html = "<html><body style=\"background-color:${bgColor}\">$content</body></html>"
//                binding.content.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

//                binding.content.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                RichText
                    .from(item.noteInfo)
                    .autoFix(false)
                    .fix(object : ImageFixCallback {
                        override fun onInit(holder: ImageHolder?) {
                        }

                        override fun onLoading(holder: ImageHolder?) {
                        }

                        override fun onSizeReady(
                            holder: ImageHolder?,
                            imageWidth: Int,
                            imageHeight: Int,
                            sizeHolder: ImageHolder.SizeHolder?
                        ) {
                            Log.e("------onSizeReady",sizeHolder?.width.toString())
                            if(sizeHolder?.width!! > 700){
                                sizeHolder?.setSize(IMDensityUtil.dip2px(BaseApplication.getInstance()?.baseContext,300f),IMDensityUtil.dip2px(BaseApplication.getInstance()?.baseContext,600f))
                            }
                        }

                        override fun onImageReady(holder: ImageHolder?, width: Int, height: Int) {
                        }

                        override fun onFailure(holder: ImageHolder?, e: java.lang.Exception?) {
                        }

                    })
                    .size(ImageHolder.WRAP_CONTENT, ImageHolder.WRAP_CONTENT)
                    .scaleType(ImageHolder.ScaleType.fit_center)
                    .showBorder(false)
                    .imageClick{imageUrls, position ->
                        Timber.e("imageUrls ${imageUrls} , position ${position}")
                        onClickListener?.previewPicture(imageUrls,position)
                    }
                    .urlClick{
                        Timber.e("urlClick ${it}")
                        return@urlClick false
                    }
                    .linkFix {
                        Timber.e("linkFix ${it.url}")
                    }
                    .into(binding.content)
            }

//            // 记账
//            if(item.bookingState == 1){
//                binding.ledgerCl.visibility = View.VISIBLE
//
//                binding.ledgerAmount.text = "¥${item.booking.total}"
//                binding.ledgerTime.text = item.booking.bookingDate
//                val sb = StringBuilder()
//                item.booking.detail.forEachIndexed { index, bookingDetail ->
//                    sb.append("${index + 1}、${bookingDetail.itemName}：¥${bookingDetail.money}")
//                    if (index != item.booking.detail.size-1){
//                        sb.append("\n")
//                    }
//                }
//                binding.ledgerContent.text = sb.toString()
//            }else{
//                binding.ledgerCl.visibility = View.GONE
//            }

            if (position == itemCount -1){
                binding.line.visibility =  View.INVISIBLE
            }

            itemBinding.reply.setOnClickListener {
                clickItem["position"] = clickPosition
                clickItem["itemBinding"] = itemClickBinding!!
                clickItem["itemViewModel"] = itemClickViewModel!!
                onClickListener?.reply(items!!,item)
            }

        }

        fun setViewModel(position: Int, itemBinding: RecyclerviewPatherRecordItemBinding, itemViewModel: PatherRecordItemViewModel) {
            this.clickPosition=position
            this.itemClickBinding=itemBinding
            this.itemClickViewModel=itemViewModel
        }
    }

    /**
     * 刷新单个
     * @param item ProjectDynamic
     */
    fun refreshItem(item: CustomerRecordBean){

        if (clickItem.isEmpty()){
            return
        }
        try {
            clickItem.run {
                val position = this["position"] as Int
                val itemBinding = this["itemBinding"] as RecyclerviewPatherRecordItemBinding
                val itemViewModel = this["itemViewModel"] as PatherRecordItemViewModel
                itemViewModel.projectDynamic = item
                itemViewModel.projectDynamicObservable.set(item)
                notifyItemChanged(position)
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
    }


    private var onClickListener : OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener){
        this.onClickListener = listener
    }

    interface OnClickListener{
        fun closeItr(projectDynamic: CustomerRecordBean)
        fun reply(bean1: CustomerRecordBean,bean2: CustomerRecordBean)
        fun previewPicture(list:List<String>,pos: Int)
    }
}