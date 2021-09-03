package com.daqsoft.library_common.javascript

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_common.R
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.GsonBuilder
import com.jeremyliao.liveeventbus.LiveEventBus
import timber.log.Timber
import java.io.File


/**
 * @package name：com.daqsoft.module_webview.util
 * @date 5/11/2020 下午 2:36
 * @author zp
 * @describe
 */
class JavaScriptInvokeAndroid(val activity: AppBaseActivity<*, *>, val webView: WebView) {

    /**
     * js 获取token
     */
    @JavascriptInterface
    fun getToken(): String {
        val token = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN))
        val map = mapOf("token" to token)
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(map)
        Timber.e("js 调用 native 方法 getAppToken() 返回值 ：${json}")
        return json
    }

    /**
     * js 获取token
     */
    @JavascriptInterface
    fun getAppToken(): String {
        val token = AesCryptUtils.decrypt(DataStoreUtils.getString(DSKeyGlobal.TOKEN))
        val map = mapOf("token" to token)
        val json = GsonBuilder().disableHtmlEscaping().create().toJson(map)
        Timber.e("js 调用 native 方法 getAppToken() 返回值 ：${json}")
        return json
    }

    /**
     * 返回
     */
    @JavascriptInterface
    fun onBackPressed(){
        Timber.e("onBackPressed")
        try{
            if (activity::class.java.simpleName == "WebViewActivity"){
                activity.finish()
                return
            }

            Handler(Looper.getMainLooper()).post {
                if (webView.canGoBack()){
                    webView.goBack()
                }
            }

        }catch (e:Exception){
            e.printStackTrace()

        }
    }

    /**
     * 获取状态栏高度
     * @return Int
     */
    @JavascriptInterface
    fun getStatusBarHeight(): Int {
        return AppUtils.getStatusBarHeight()
    }

    /**
     * 获取Toolbar 高度  系统默认 56dp
     * @return Int
     */
    @JavascriptInterface
    fun getToolbarHeight(): Int {
        return AppUtils.getToolbarHeight()
    }

    /**
     * js 打开图片
     * @param pos Int 点击的位置
     * @param list MutableList<String>  所有图片地址list
     */
    @JavascriptInterface
    fun  openImage(pos: Int,list : List<String>){
        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_IMAGE_CLICK).post(
            hashMapOf(
                "pos" to pos,
                "list" to list
            )
        )
    }

    /**
     * js 打开图片
     * @param url String  url
     */
    @JavascriptInterface
    fun  openImage(url : String){
        LiveEventBus.get(LEBKeyGlobal.WEB_VIEW_IMAGE_CLICK).post(
            hashMapOf(
                "pos" to 0,
                "list" to arrayListOf<String>(url)
            )
        )
    }



    /**
     * js 预览文件
     * @param fileName String 文件名
     * @param url String 下载地址
     */
    @JavascriptInterface
    fun previewFile(fileName: String, url: String){
//        if (fileName.lastIndexOf(".") == -1){
//            ToastUtils.showShortSafe("暂不支持该格式")
//            return
//        }
//
//        val suffix = fileName.substring(fileName.lastIndexOf("."))
//        val type = FileUtils.MIME_MAP_TABLE.asSequence().find { it.key.equals(suffix,ignoreCase = true) }?.value
//        if (type.isNullOrBlank()){
//            ToastUtils.showLongSafe("暂不支持该格式")
//            return
//        }
//
//        val appDirPath = FileUtils.getAppDirPath()
//        val file = File(appDirPath, fileName)
//        if (!file.exists()){
//            activity.runOnUiThread {
//                activity.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, callback = {
//                    showDownloadDialog(
//                        fileName,
//                        url
//                    )
//                })
//            }
//            return
//        }
//        FileUtils.previewFile(activity, file)

        FileUtils.previewOrDownloadFile(activity, fileName,url)
    }


    /**
     * 下载提示 dialog
     */
    private fun showDownloadDialog(fileName: String, url: String){
        val dialog = MaterialDialog
            .Builder(activity)
            .customView(R.layout.dialog_tips, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = dialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text =  "提示"
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = "是否下载文件？"
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener{
            dialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            dialog?.dismiss()
            FileUtils.downloadFile(activity, fileName, url)
        }
        dialog?.setCancelable(false)
        dialog?.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /**
     * 任务状态改变
     */
    @JavascriptInterface
    fun taskStatusTransition(status: Int){
        LiveEventBus.get(LEBKeyGlobal.TASK_STATUS_CHANGE).post(status.toString())
    }

    /**
     * 消息数变化
     */
    @JavascriptInterface
    fun pendingTotalChanged(){
        LiveEventBus.get(LEBKeyGlobal.NUMBER_OF_MESSAGES_HAS_CHANGED).post(true.toString())
    }


    /**
     * 是否在任务列表页面
     */
    @JavascriptInterface
    fun currentInTaskList(boolean: Boolean){
        if (activity::class.java.simpleName == "MainActivity"){
            LiveEventBus.get(LEBKeyGlobal.CURRENT_IN_TASK_LIST).post(boolean)
        }

    }

    /**
     * 单选部门
     */
    @JavascriptInterface
    fun singleSelectOrg(){
        LiveEventBus.get(LEBKeyGlobal.ORG_SINGLE_CHOICE_WEB).post("")
    }


    /**
     * 导出加班报表
     * @param param String
     */
    @JavascriptInterface
    fun exportOvertimeReport(param:String){
        LiveEventBus.get(LEBKeyGlobal.EXPORT_OVERTIME_REPORT).post(param)
    }

    /**
     * 导出考勤报表
     * @param param String
     */
    @JavascriptInterface
    fun exportAttendanceReport(param:String){
        LiveEventBus.get(LEBKeyGlobal.EXPORT_ATTENDANCE_REPORT).post(param)
    }



    /**
     * 修改 toolbar title
     * @param param String
     */
    @JavascriptInterface
    fun changeNavBarTitle(title:String,rightIconVisible : Boolean = true){
        val map = hashMapOf<String,Any>(
            "title" to title,
            "rightIconVisible" to rightIconVisible
        )
        LiveEventBus.get(LEBKeyGlobal.SET_TOOLBAR_TITLE).post(map)
    }
}