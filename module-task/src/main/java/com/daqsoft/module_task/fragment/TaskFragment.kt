package com.daqsoft.module_task.fragment

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.base.AppBaseFragment
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.HttpGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.library_common.javascript.JavaScriptInvokeAndroid
import com.daqsoft.module_task.BR
import com.daqsoft.module_task.R
import com.daqsoft.module_task.databinding.FragmentTaskBinding
import com.daqsoft.module_task.viewmodel.TaskViewModel
import com.daqsoft.mvvmfoundation.base.BaseFragment
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import com.trello.rxlifecycle4.components.support.RxAppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_task.fragment
 * @date 10/11/2020 上午 11:32
 * @author zp
 * @describe
 */

@AndroidEntryPoint
@Route(path = ARouterPath.Task.PAGER_TASK)
class TaskFragment  : AppBaseFragment<FragmentTaskBinding, TaskViewModel>() {


    private var mUploadMessage: ValueCallback<Uri>? = null
    var uploadMessage: ValueCallback<Array<Uri>>? = null

    companion object{
        const val REQUEST_SELECT_FILE = 100
        private const val FILECHOOSER_RESULTCODE = 2
    }


    override fun initContentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): Int {
        return  R.layout.fragment_task
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initViewModel(): TaskViewModel? {
        return activity?.viewModels<TaskViewModel>()?.value
    }

    override fun initView() {
        super.initView()
        if (!NetworkUtil.isNetworkAvailable(this.context)) {
            errorLayout(R.id.web_view,callback = {initView()})
        } else {
            normalLayout()
            initWebView()
        }
    }

    override fun initViewObservable() {
        super.initViewObservable()

        LiveEventBus.get(LEBKeyGlobal.TASK_FRAGMENT_ON_KEY_DOWN,Pair::class.java).observe(this, Observer {
            onKeyDown(it.first as Int,it.second as KeyEvent?)
        })
    }


    /**
     * 初始化 webView
     */
    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface","ClickableViewAccessibility")
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
        val jsInvokAndroid = JavaScriptInvokeAndroid(
            activity as AppBaseActivity<*, *>,
            binding.webView
        )
        binding.webView.addJavascriptInterface(jsInvokAndroid, "android")
        webSettings.domStorageEnabled = true// 打开本地缓存提供JS调用,至关重要
        binding.webView.webViewClient = object : WebViewClient(){
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showLoadingDialog()
                super.onPageStarted(view, url, favicon)
                Timber.e(url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                dismissLoadingDialog()
                super.onPageFinished(view, url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                Timber.e("url $url")
//                AppUtils.syncCookie(url?:"", "token="+AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN)))
                return super.shouldOverrideUrlLoading(view, url)
            }
        }

        binding.webView.webChromeClient = object : WebChromeClient(){

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                Timber.e("title ${title}")
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
                    Toast.makeText(context, "Cannot Open File Chooser", Toast.LENGTH_LONG).show()
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
        binding.webView.setDownloadListener { p0, p1, p2, p3, p4 ->
        }
        val token = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN))
        AppUtils.syncCookie(HttpGlobal.TASK_MAIN, "token=$token")
        binding.webView.loadUrl(HttpGlobal.TASK_MAIN, mapOf("xq_token" to token))

//        binding.webView.loadUrl("file:///android_asset/dist/index.html", mapOf("xq_token" to token))
    }


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
//        binding.webView.onPause()
//        binding.webView.pauseTimers()
//    }
//
//    override fun onDestroy() {
//        binding.webView.clearHistory()
//        (binding.webView.parent as ViewGroup).removeView(binding.webView)
//        binding.webView.destroy()
//        super.onDestroy()
//    }


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
            val result = if (data == null || resultCode != Activity.RESULT_OK) null else data.data
            mUploadMessage!!.onReceiveValue(result)
            mUploadMessage = null
        } else {
            Toast.makeText(context, "Failed to Upload Image", Toast.LENGTH_LONG).show()
        }
    }


    private fun onKeyDown(keyCode: Int, event: KeyEvent?) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.webView.canGoBack()){
                binding.webView.goBack()
            }else{
                val intent = Intent(Intent.ACTION_MAIN)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
        }
    }

}