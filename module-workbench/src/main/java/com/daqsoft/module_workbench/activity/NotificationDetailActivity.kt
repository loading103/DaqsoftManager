package com.daqsoft.module_workbench.activity

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.transition.TransitionManager
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.GlideEngine
import com.daqsoft.library_common.javascript.JavaScriptInvokeAndroid
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.adapter.NoticeDetailAuditAdapter
import com.daqsoft.module_workbench.databinding.ActivityNotificationDetailBinding
import com.daqsoft.module_workbench.repository.pojo.bo.MenuPermissionCover
import com.daqsoft.module_workbench.repository.pojo.vo.NoticeAudit
import com.daqsoft.module_workbench.viewmodel.NotificationDetailViewModel
import com.daqsoft.module_workbench.widget.NoticeApprovePopup
import com.daqsoft.module_workbench.widget.TopMenuPopup
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import me.tatarka.bindingcollectionadapter2.ItemBinding
import org.jsoup.Jsoup

/**
 * @ClassName    NotificationActivity
 * @Description
 * @Author       yuxc
 * @CreateDate   2021/2/19
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_NOTIFICATION_DETAIL)
class NotificationDetailActivity : AppBaseActivity<ActivityNotificationDetailBinding, NotificationDetailViewModel>() {

    val noticeApprovePopup : NoticeApprovePopup by lazy {
        NoticeApprovePopup(this).apply {
            setConsumer(object : NoticeApprovePopup.Consumer{
                override fun accept(result: Boolean, content: String) {
                    viewModel.approve(result,content)
                }
            })
        }
    }

    @JvmField
    @Autowired
    var  permission : MenuPermissionCover ? = null


    @JvmField
    @Autowired
    var id: String = ""

    lateinit var jsInvokAndroid : JavaScriptInvokeAndroid
    var coverUrl = ""

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_notification_detail
    }

    override fun initViewModel(): NotificationDetailViewModel? {
        return viewModels<NotificationDetailViewModel>().value
    }

    override fun initView() {
        super.initView()

        initWebView()
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.detail.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback(){
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                viewModel.detail.get()?.run {
                    val rgbArray =
                        try {
                            this.color.split(" ").subList(0,2).map {
                                Color.parseColor("#$it")
                            }.toIntArray()
                        }catch (e:Exception){
                            ContextUtils.getContext().resources.getIntArray(R.array.red)
                        }

                    binding.icon.helper.backgroundColorNormalArray = rgbArray
//                    coverUrl = noticeContent.replace("\\","")
//                    binding.webView.loadDataWithBaseURL(null, coverUrl , "text/html", "utf-8", null)

                    coverUrl = HttpGlobal.NOTICE_ANNOUNCEMENT_DETAILS + id
                    binding.webView.loadUrl(coverUrl)

                    if(!auditString.isNullOrEmpty()){
                        initAuditRecycleView(auditString)
                    }

                    val constraintSet = ConstraintSet()
                    constraintSet.clone(binding.bottomCl)
                    when(noticeStatus){
                        "待提交" ->{
                            constraintSet.setHorizontalWeight(binding.modify.id,1f)
                            constraintSet.setHorizontalWeight(binding.determine.id,2f)
                            binding.determine.text = "提交审核"
                        }
                        "待审核" ->{
                            constraintSet.setHorizontalWeight(binding.modify.id,1f)
                            constraintSet.setHorizontalWeight(binding.determine.id,2f)
                            binding.determine.text = "审核"
                        }
                        "已发布" ->{
                            constraintSet.setHorizontalWeight(binding.modify.id,0f)
                            constraintSet.setHorizontalWeight(binding.determine.id,1f)
                            binding.determine.text = "撤回"
                        }
                        "已撤回" ->{
                            constraintSet.setHorizontalWeight(binding.modify.id,0f)
                            constraintSet.setHorizontalWeight(binding.determine.id,1f)
                            binding.determine.text = "修改"
                        }
                        "已退回" ->{
//                            constraintSet.setHorizontalWeight(binding.modify.id,1f)
//                            constraintSet.setHorizontalWeight(binding.determine.id,2f)
//                            binding.determine.text = "提交审核"
                            constraintSet.setHorizontalWeight(binding.modify.id,0f)
                            constraintSet.setHorizontalWeight(binding.determine.id,1f)
                            binding.determine.text = "修改"
                        }
                        else -> {}
                    }
                    constraintSet.applyTo(binding.bottomCl)
                }
            }
        } )

        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_IMAGE_CLICK,HashMap::class.java).observe(this, Observer {
            if (it != null){
                val pos = it["pos"] as Int
                val list = it["list"] as List<String>
                previewImage(pos,list)
            }
        })

        LiveEventBus.get(LEBKeyGlobal.ADDED_NOTICE_SUCCESSFUL,String::class.java).observe(this, Observer {
            viewModel.getNoticeDetail(id)
        })

        viewModel.approveLiveData.observe(this, Observer {
            showApprovePopup()
        })
    }

    /**
     * 初始化审核recycleVi
     */
    private fun initAuditRecycleView(data:List<NoticeAudit>) {
        binding.recycleViewRecord.apply {
            layoutManager = LinearLayoutManager(this@NotificationDetailActivity)
            adapter = NoticeDetailAuditAdapter().apply{
                itemBinding = ItemBinding.of(BR.noticeAudit,R.layout.recyclerview_notice_detail_audit_item)
                setItems(data)
            }
        }
    }

    override fun initData() {
        super.initData()
        viewModel.id = id
        viewModel.permission = permission

        viewModel.getNoticeDetail(id)
        viewModel.getAllAuditStatus()
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
                addWebViewImage(coverUrl)
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
    fun addWebViewImage(url:String){
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
                "window.android.openImage(this.src,this.pos);" +
                "}}})()"
        binding.webView.loadUrl(jsCode)
    }


    /**
     * 预览图片
     */
    private fun previewImage(pos:Int,list: List<String>){

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
                    .imageEngine(GlideEngine.createGlideEngine())
                    .isNotPreviewDownload(true)
                    .openExternalPreview(selectList.indexOf(media),selectList)
            }
        }
    }

    /**
     * 展示审批菜单
     */
    fun showApprovePopup(){
        XPopup.Builder(this)
            .autoOpenSoftInput(true)
            .asCustom(noticeApprovePopup)
            .show()
    }
}