package com.daqsoft.module_webview.activity

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.module_webview.BR
import com.daqsoft.module_webview.R
import com.daqsoft.module_webview.databinding.ActivityWebviewBinding
import com.daqsoft.library_common.javascript.JavaScriptInvokeAndroid
import com.daqsoft.library_common.pojo.vo.Employee
import com.daqsoft.module_webview.viewmodel.WebViewViewModel
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.Exception


@AndroidEntryPoint
@Route(path = ARouterPath.Web.PAGER_WEB)
class WebViewActivity : AppBaseActivity<ActivityWebviewBinding, WebViewViewModel>(){

    @Autowired
    @JvmField
    var url: String? = "https://www.baidu.com/"

    @Autowired
    @JvmField
    var nextId: String? = null

    private var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    companion object{
        const val REQUEST_SELECT_FILE = 100
        private const val FILECHOOSER_RESULTCODE = 2

        const val ORGANIZATION = 300
    }




    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_webview
    }

    override fun initViewModel(): WebViewViewModel? {
        return viewModels<WebViewViewModel>().value
    }

    override fun initView() {
        super.initView()
        initWebView()

        if(nextId.isNullOrBlank()){
            viewModel.nextVisible.set(View.GONE)
        }else{
            viewModel.nextVisible.set(View.VISIBLE)
            viewModel.getNextDetailData(nextId!!)
        }

        when(url){
            HttpGlobal.ATTENDANCE_REPORT->{
                binding.includeCl.visibility = View.VISIBLE
                viewModel.setBackground(Color.WHITE)
                viewModel.setTitleText("考勤报表")
                viewModel.setRightIconSrc(R.mipmap.kqbb_xz)
                viewModel.setRightIconVisible(View.VISIBLE)
                viewModel.setStatusBarHeight(0)
                viewModel.setToolbarHeight(AppUtils.getToolbarHeight())
            }
            HttpGlobal.OVERTIME_REPORT->{
                binding.includeCl.visibility = View.VISIBLE
                viewModel.setBackground(Color.WHITE)
                viewModel.setTitleText("加班报表")
                viewModel.setRightIconSrc(R.mipmap.kqbb_xz)
                viewModel.setRightIconVisible(View.VISIBLE)
                viewModel.setStatusBarHeight(0)
                viewModel.setToolbarHeight(AppUtils.getToolbarHeight())
            }
            HttpGlobal.PERSONNEL_REPORT->{
                binding.includeCl.visibility = View.VISIBLE
                viewModel.setBackground(Color.parseColor("#ec4c3e"))
                viewModel.setTitleText("人事分析总览")
                viewModel.setStatusBarHeight(0)
                viewModel.setToolbarHeight(AppUtils.getToolbarHeight())
                viewModel.setTitleTextColor(R.color.white_ffffff)
                viewModel.setBackIconSrc(R.mipmap.list_top_return_white)
            }
        }
    }


    override fun initViewObservable() {
        super.initViewObservable()

        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_ON_BACK_PRESSED, String::class.java)
            .observe(this, Observer {
                if (it == this::class.java.simpleName) {
                    onBackPressed()
                }
            })


        LiveEventBus.get(LEBKeyGlobal.ORG_SINGLE_CHOICE_WEB, String::class.java)
            .observe(this, Observer {
                singleSelectOrg()
            })


        LiveEventBus.get(LEBKeyGlobal.EXPORT_OVERTIME_REPORT, String::class.java)
            .observe(this, Observer {
                if (it.isNullOrBlank()){
                    return@Observer
                }
                viewModel.exportOvertimeReport(it)
            })

        LiveEventBus.get(LEBKeyGlobal.EXPORT_ATTENDANCE_REPORT, String::class.java)
            .observe(this, Observer {
                if (it.isNullOrBlank()){
                    return@Observer
                }
                viewModel.exportAttendanceReport(it)
            })


        viewModel.rightIconLiveData.observe(this, Observer {
            when(url){
                HttpGlobal.ATTENDANCE_REPORT->{
                    binding.webView.evaluateJavascript("getDownLoadParams('examination')",object : ValueCallback<String>{
                        override fun onReceiveValue(value: String?) {
                            Timber.e("value  ${value}")
                            if (value.isNullOrBlank()){
                                return
                            }
                            viewModel.exportAttendanceReport(value)
                        }
                    })
                }
                HttpGlobal.OVERTIME_REPORT->{
                    binding.webView.evaluateJavascript("getDownLoadParams('workOverTime')",object : ValueCallback<String>{
                        override fun onReceiveValue(value: String?) {
                            Timber.e("value  ${value}")
                            if (value.isNullOrBlank()){
                                return
                            }
                            viewModel.exportOvertimeReport(value)
                        }
                    })
                }

            }
        })


        LiveEventBus.get(LEBKeyGlobal.SET_TOOLBAR_TITLE,HashMap::class.java).observe(this, Observer {
            try {
                val title = it["title"] as String
                val rightIconVisible = it["rightIconVisible"] as Boolean
                viewModel.setTitleText(title)
                viewModel.setRightIconVisible(if (rightIconVisible) View.VISIBLE else View.GONE)
            }catch (e:Exception){
                e.printStackTrace()
            }
        })


        viewModel.backLiveData.observe(this, Observer {
            when(url){
                HttpGlobal.ATTENDANCE_REPORT->{
                    binding.webView.evaluateJavascript("handleBackClick()",null)
                }
                HttpGlobal.OVERTIME_REPORT->{
                    binding.webView.evaluateJavascript("handleBackClick()",null)
                }
                else ->{
                    onBackPressed()
                }
            }

        })
    }

    override fun onBackPressed() {
        if(binding.webView.canGoBack()){
            binding.webView.goBack()
            return
        }


        finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        if(url?.contains(HttpGlobal.DAILY_ADD_EDIT)!! || url?.contains(HttpGlobal.DAILY_TEAM_ADD)!!){
            // true表示刷新列表  false代表刷新item
            LiveEventBus.get(LEBKeyGlobal.RESH_UI).post(false)
        }
        if( url?.contains(HttpGlobal.DAILY_DETAIL)!! || url?.contains(HttpGlobal.DAILY_TEAM_DETAIL)!!) {
            LiveEventBus.get(LEBKeyGlobal.RESH_UI).post(false)
        }
        // 刷新项目详情页流程
        if( url?.contains(HttpGlobal.DAILY_TASK_MANAGE)!!) {
            LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).post(false)
        }
        if(  url?.contains(HttpGlobal.DAILY_ADD_TASK)!! ) {
            LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).post(true)
        }
        if(  url?.contains(HttpGlobal.DAILY_ADD_CUSTOMER)!! ) {
            LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).post(true)
        }
        if(  url?.contains(HttpGlobal.DAILY_ADD_PATERNER)!! ) {
            LiveEventBus.get(LEBKeyGlobal.RESH_DETAIL_UI).post(true)
        }
    }
    /**
     * 初始化 webView
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView(){
        binding.webView.requestFocus()
        binding.webView.clearCache(true)
        binding.webView.onResume()
        if (Build.VERSION.SDK_INT >= 19) {
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        } else {
            binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }

        WebView.setWebContentsDebuggingEnabled(true)
        val webSettings =  binding.webView.settings
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        webSettings.useWideViewPort = true//将图片调整到适合webView的大小
        webSettings.loadWithOverviewMode = true//缩放至屏幕的大小
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE//关闭webView缓存
        webSettings.allowFileAccess = true//设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true//支持通过js打开新窗口
        webSettings.javaScriptEnabled = true
        webSettings.loadsImagesAutomatically = true//设置支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8"//设置编码格式
        webSettings.savePassword = false
        webSettings.saveFormData = false
        val jsInvokAndroid = JavaScriptInvokeAndroid(this, binding.webView)
        binding.webView.addJavascriptInterface(jsInvokAndroid, "android")
        webSettings.domStorageEnabled = true// 打开本地缓存提供JS调用,至关重要
        binding.webView.webViewClient = object : WebViewClient(){

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Timber.e("shouldOverrideUrlLoading ${url}")
//                AppUtils.syncCookie(url?:"","token="+ AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN)))
                url?.let {
                    if(it.contains("/file/oss/") || it.contains(FileUtils.getSDCardPath())){
                        return true
                    }
                }
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                view?.let{
                    if (!it.canGoBack()){
                        showLoadingDialog()
                    }
                }
                super.onPageStarted(view, url, favicon)
                Timber.e("onPageStarted${url}")
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                dismissLoadingDialog()
                super.onPageFinished(view, url)
            }



        }
        binding.webView.webChromeClient = object : WebChromeClient(){
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
             fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(i, "File Browser"),
                    FILECHOOSER_RESULTCODE
                )
            }

            // For Lollipop 5.0+ Devices
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun onShowFileChooser(
                mWebView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (uploadMessage != null) {
                    uploadMessage!!.onReceiveValue(null)
                    uploadMessage = null
                }
                uploadMessage = filePathCallback
                val intent = fileChooserParams.createIntent()
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE)
                } catch (e: ActivityNotFoundException) {
                    uploadMessage = null
                    Toast.makeText(baseContext, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
                    return false
                }
                return true
            }

            //For Android 4.1 only
            protected fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String?,
                capture: String?
            ) {
                mUploadMessage = uploadMsg
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(intent, "File Browser"),
                    FILECHOOSER_RESULTCODE
                )
            }

            protected fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUploadMessage = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                    FILECHOOSER_RESULTCODE
                )
            }

        }

        binding.webView.setDownloadListener { _, p1, p2, p3, p4 ->
        }

        val token = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN))
        AppUtils.syncCookie(url?:"", "token=$token")
        binding.webView.loadUrl(url, mapOf("xq_token" to token))


        Timber.e("loadUrl==$url")
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK &&  binding.webView.canGoBack()) {
            if(binding.webView.canGoBack()){
                binding.webView.goBack()
                return true
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

//
//    override fun onStart() {
//        super.onStart()
//        binding.webView.isFocusable = true
//        binding.webView.requestFocus()
//        binding.webView.onResume()
//        binding.webView.resumeTimers()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (binding.webView != null) {
//            binding.webView.onPause()
//            binding.webView.pauseTimers()
//        }
//    }
//
//    override fun onDestroy() {
//        binding.webView.clearHistory()
//        (binding.webView.parent as ViewGroup).removeView(binding.webView)
//        binding.webView.destroy()
//        super.onDestroy()
//    }


    /**
     * 单选组织
     */
    fun singleSelectOrg(){
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_ORG_OR_STAFF_SELECT)
            .withInt("type",1)
            .withBoolean("orgSingleChoice",true)
            .navigation(this, ORGANIZATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null) return
                uploadMessage!!.onReceiveValue(
                    WebChromeClient.FileChooserParams.parseResult(
                        resultCode,
                        data
                    )
                )
                uploadMessage = null
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage) return
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            val result = if (data == null || resultCode != RESULT_OK) null else data.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else {
            Toast.makeText(baseContext, "Failed to Upload Image", Toast.LENGTH_LONG).show()
        }



        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ORGANIZATION ->{
                    val data = data?.getStringExtra("result")
                    data?.let {
                        val type = object : TypeToken<List<Employee>>() {}.type
                        val list = Gson().fromJson<List<Employee>>(it, type)

                        if (list.isNotEmpty()){
                            binding.webView.evaluateJavascript("closeDepartComponent(${Gson().toJson(list[0])});", null)
                        }
                    }
                }
            }
        }
    }
}
