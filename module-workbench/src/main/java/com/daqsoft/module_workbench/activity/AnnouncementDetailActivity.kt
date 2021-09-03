package com.daqsoft.module_workbench.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.executePaging
import com.daqsoft.library_common.javascript.JavaScriptInvokeAndroid
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivityAnnouncementDetailBinding
import com.daqsoft.module_workbench.viewmodel.AnnouncementDetailViewModel
import com.daqsoft.module_workbench.widget.AppBarStateChangeListener
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import dagger.hilt.android.AndroidEntryPoint
import org.jsoup.Jsoup
import timber.log.Timber

/**
 * @package name：com.daqsoft.module_workbench.activity
 * @date 7/12/2020 上午 9:59
 * @author zp
 * @describe 通知公告详情
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_ANNOUNCEMENT_DETAIL)
class AnnouncementDetailActivity : AppBaseActivity<ActivityAnnouncementDetailBinding, AnnouncementDetailViewModel>() {

    @JvmField
    @Autowired
    var id:String = ""

    @JvmField
    @Autowired
    var pos:Int = -1

    @JvmField
    @Autowired
    var messageId:String? = null

    @Autowired
    @JvmField
    var nextId: String? = null

    lateinit var jsInvokAndroid : JavaScriptInvokeAndroid

    var multipleLayoutManager : MultipleLayoutManager ? = null
    var errorView:View? = null


    var recyclerViewLayoutManager : MultipleLayoutManager ? = null
    
    var firstLoad = true
    var coverUrl = ""

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_announcement_detail
    }

    override fun initViewModel(): AnnouncementDetailViewModel? {
        return viewModels<AnnouncementDetailViewModel>().value
    }

    override fun initView() {
        super.initView()

        if(nextId.isNullOrBlank()){
            viewModel.nextVisible.set(View.GONE)
        }else{
            viewModel.nextVisible.set(View.VISIBLE)
            viewModel.getNextDetailData(nextId!!)
        }
        initWebView()
        initRecycleView()
    }

    private fun initRecycleView() {
        binding.recyclerView.apply {
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    val position = parent.getChildAdapterPosition(view)
                    if (position > 0) {
                        outRect.top = 30.dp
                    }
                    val count = state.itemCount - 1
                    if (position == count){
                        outRect.bottom = 30.dp
                    }
                }
            })

            recyclerViewLayoutManager = MultipleLayoutManager
                .Builder(this)
                .build()
        }
    }


    override fun initData() {
        viewModel.id = id
        viewModel.pos = pos
        viewModel.getAnnouncementDetail(id, pos)
        viewModel.getAnnouncementComment(id)
        viewModel.pageList.observe(this, Observer {
            binding.recyclerView.executePaging(it, viewModel.diff)
    })


        messageId?.let{
            if(it.isNotBlank()){
                viewModel.readSingle(it)
            }
        }

    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.detailLiveData.observe(this, Observer {

            multipleLayoutManager?.showSuccessLayout()

            if (viewModel.needScroll) {
                val hashMap = hashMapOf<String, String>()
                hashMap["readAmount"] = it.readNumbers.toString()
                hashMap["commentAmount"] = it.commentNumbers.toString()
                hashMap["likeAmount"] = it.likeNumbers.toString()
                LiveEventBus.get(LEBKeyGlobal.ANNOUNCEMENT_ACTIVITY_REFRESH).post(hashMap)
                binding.appbar.setExpanded(false)
                binding.recyclerView.smoothScrollToPosition(0)
                viewModel.needScroll = false
            }


//            coverUrl = it.noticeContent.replace("\\","").toHtml()
//            coverUrl = it.noticeContent.replace("\\","")
//            binding.webView.loadDataWithBaseURL(null, coverUrl , "text/html", "utf-8", null)

            if (firstLoad){
                coverUrl = HttpGlobal.NOTICE_ANNOUNCEMENT_DETAILS + id
                binding.webView.loadUrl(coverUrl)
                firstLoad = false
            }
//            val rgbArray =
//                try {
//                    it.color.split(" ").subList(0,2).map {
//                        Color.parseColor("#$it")
//                    }.toIntArray()
//                }catch (e:Exception){
//                    ContextUtils.getContext().resources.getIntArray(R.array.red)
//                }
//            binding.icon.helper.backgroundColorNormalArray = rgbArray
        })

        viewModel.backLiveData.observe(this, Observer {
            if (binding.webView.canGoBack()) {
                binding.webView.goBack()
            } else {
                finish()
            }
        })


        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_IMAGE_CLICK, HashMap::class.java).observe(
            this,
            Observer {
                if (it != null) {
                    val pos = it["pos"] as Int
                    val list = it["list"] as List<String>
                    previewImage(pos, list)
                }
            })


        viewModel.detailsFailed.observe(this, Observer {
            if (errorView == null) {
                errorView = LayoutInflater.from(this).inflate(R.layout.layout_error, null, false)
                val errorContent = errorView!!.findViewById<TextView>(R.id.content)
                errorContent.text = it
            }
            if (multipleLayoutManager == null) {
                multipleLayoutManager = MultipleLayoutManager
                    .Builder(binding.parentContainer)
                    .setErrorLayout(errorView!!)
                    .build()
            }

            multipleLayoutManager?.showErrorLayout()
        })

        LiveEventBus.get(LEBKeyGlobal.ANNOUNCEMENT_COMMENT_SUCCESS, Boolean::class.java).observe(
            this,
            Observer {
                viewModel.getAnnouncementDetail(id, pos)
                viewModel.getAnnouncementComment(id)
                viewModel.dataSource?.invalidate()
                viewModel.needScroll = true
            })


        viewModel.commentLiveData.observe(this, Observer {

//            if (it){
//                recyclerViewLayoutManager?.showSuccessLayout()
//            }else{
//                recyclerViewLayoutManager?.showEmptyLayout()
//            }
        })

    }

    /**
     * 初始化 webView
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView(){
        binding.webView.requestFocus()
        binding.webView.clearCache(true)
        binding.webView.onResume()
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        WebView.setWebContentsDebuggingEnabled(true)
        val webSettings =  binding.webView.settings
        // 不阻止加载网络图片
        webSettings.blockNetworkImage = false
        // 支持混合模式
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        // 将图片调整到适合webView的大小
        webSettings.useWideViewPort = true
        // 缩放至屏幕的大小
        webSettings.loadWithOverviewMode = true
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
        jsInvokAndroid = JavaScriptInvokeAndroid(this, binding.webView)
        binding.webView.addJavascriptInterface(jsInvokAndroid, "android")
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                addWebViewImage(url!!)
                setWebViewImageClick()
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient(){
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }
        }
        binding.webView.setDownloadListener { p0, p1, p2, p3, p4 ->
        }
    }


    var webViewImageList : MutableList<String> ? = null

    /**
     * 添加 webView 中的图片
     */
    fun addWebViewImage(url: String){
        val doc = Jsoup.parse(url)
        val elements = doc.getElementsByTag("img")
        if (elements.isEmpty()){
            return
        }
        webViewImageList = mutableListOf()
        elements.forEach {
            webViewImageList!!.add(it.attr("src"))
        }
    }

    /**
     * 给 img 标签添加 点击事件
     */
    fun setWebViewImageClick(){
        val jsCode = "javascript:(function(){" +
                "var imgs=document.getElementsByTagName(\"img\");" +
                "var list=[];"+
                "for(var i=0;i<imgs.length;i++){"+
                "list.push(imgs[i].src)"+
                "}"
                "for(var i=0;i<imgs.length;i++){" +
                "imgs[i].pos = i;"+
                "imgs[i].onclick=function(){" +
                "window.android.openImage(this.src,list);" +
                "}}})()"
        binding.webView.loadUrl(jsCode)
    }


    /**
     * 预览图片
     */
    private fun previewImage(pos: Int, list: List<String>){

        list.let {
            val selectList = it.map {
                val localMedia = LocalMedia()
                localMedia.path = it
                localMedia.mimeType = PictureMimeType.MIME_TYPE_IMAGE
                return@map localMedia
            }
            if (selectList.isNotEmpty()) {
                val media = selectList[pos]
                PictureSelector
                    .create(this)
                    .openGallery(PictureMimeType.ofAll())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .isNotPreviewDownload(true)
                    .openExternalPreview(selectList.indexOf(media), selectList)
            }
        }
    }
}