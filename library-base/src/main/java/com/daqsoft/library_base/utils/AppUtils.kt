package com.daqsoft.library_base.utils

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.text.InputFilter
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.webkit.CookieManager
import android.webkit.WebView
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.daqsoft.library_base.R
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.File
import java.text.NumberFormat


object AppUtils {

    /**
     * 跳转系统 浏览器
     * @param url String
     */
    fun jumpBrowser(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            ContextCompat.startActivity(ContextUtils.getContext(), intent, null)
        }catch (e: Exception){
            e.printStackTrace()

        }
    }

    /**
     * 复制
     * @param str String
     * @param needToast Boolean
     */
    fun primaryClip(str: String, needToast: Boolean = true){
        val cm = ContextUtils.getContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val mClipData = ClipData.newPlainText(null, str)
        cm.setPrimaryClip(mClipData)
        if (needToast){
            ToastUtils.showShort(ContextUtils.getContext().resources.getString(R.string.copy_successful))
        }
    }

    /**
     * 显示软键盘
     * @param et EditText
     */
    fun showInput(et: EditText) {
        et.postDelayed({
            et.requestFocus()
            val manager =
                et.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager?.showSoftInput(et, 0)
        }, 100)
    }


    /**
     * 获取版本号
     * @return String
     */
    fun getVersionName():String{
        return ContextUtils.getContext().packageManager.getPackageInfo(
            ContextUtils.getContext().packageName,
            0
        ).versionName
    }

    /**
     * 获取渠道名
     * @return String
     */
    fun getChannelData(): String {
        var channel = ""
        try {
            ContextUtils.getContext().packageManager?.let {
                it.getApplicationInfo(
                    ContextUtils.getContext().packageName,
                    PackageManager.GET_META_DATA
                )?.let {
                    channel = it.metaData.getString("UMENG_CHANNEL").toString()
                }
            }
        }catch (e: java.lang.Exception){
            e.printStackTrace()
        }
        return channel
    }

    /**
     * 获取状态栏高度
     * @return Int
     */
    fun getStatusBarHeight(): Int {
        val resources: Resources = ContextUtils.getContext().resources
        val resourceId: Int = resources.getIdentifier("status_bar_height", "dimen", "android")
        val height: Int = resources.getDimensionPixelSize(resourceId)
        return height
    }

    /**
     * 获取Toolbar 高度  系统默认 56dp
     * @return Int
     */
    fun getToolbarHeight(): Int {
        val typedValue = TypedValue()
        if (ContextUtils.getContext().theme.resolveAttribute(
                android.R.attr.actionBarSize,
                typedValue,
                true
            )){
            return TypedValue.complexToDimensionPixelSize(
                typedValue.data,
                ContextUtils.getContext().resources.displayMetrics
            )
        }
        return 56.toFloat().dp
    }


    /**
     * 判断 app 是否在运行
     * @param context Context
     * @param pkgName String
     * @return Boolean
     */
    fun isAppRunning(context: Context, pkgName: String):Boolean {
        if (pkgName.isBlank()){
            return false
        }
        val ai = context.applicationInfo
        val uid = ai.uid
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (am != null) {
            val taskInfo = am.getRunningTasks(Int.MAX_VALUE)
            if (taskInfo != null && taskInfo.size > 0) {
                for (aInfo in taskInfo) {
                    if (aInfo.baseActivity != null) {
                        if (pkgName == aInfo.baseActivity!!.packageName) {
                            return true
                        }
                    }
                }
            }
            val serviceInfo = am.getRunningServices(Int.MAX_VALUE)
            if (serviceInfo != null && serviceInfo.size > 0) {
                for (aInfo in serviceInfo) {
                    if (uid == aInfo.uid) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 判断 app 是否在前台
     * @param context Context
     * @return Boolean
     */
    fun isAppForeground(context: Context):Boolean{
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val appProcessInfoList = activityManager.runningAppProcesses
        for (appProcessInfo in appProcessInfoList) {
            if (appProcessInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                && appProcessInfo.processName == context.applicationInfo.processName
            ) {
                return true
            }
        }
        return false
    }

    /**
     * 将app 置为前台
     * @param context Context
     */
    fun setTopApp(context: Context){
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val taskInfoList = activityManager.getRunningTasks(100)
        for (taskInfo in taskInfoList) {
            if (taskInfo.topActivity!!.packageName == context.packageName) {
                activityManager.moveTaskToFront(taskInfo.id, 0)
                break
            }
        }
    }


    /**
     * activity stack 中是是否有 activity
     */
    fun  isExistActivity(context: Context, cls: Class<*>) : Boolean{
        val intent = Intent(context, cls)
        val componentName =  intent.resolveActivity(context.packageManager)
        var flag = false
        if (componentName != null){
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val stackInfo = activityManager.getRunningTasks(10)
            stackInfo.forEach{
                if (it.baseActivity != null && it.baseActivity == componentName){
                    flag = true
                }
            }
        }
        return flag
    }

    /**
     * 同步 cookie
     * @param url String
     * @param cookieValue String
     */
    fun syncCookie(url: String, cookieValue: String) {
        try {
            val cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookies(null)
            cookieManager.setCookie(url, cookieValue)
            cookieManager.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 清除 cookie
     */
    fun clearCookie() {
        try {
            val cookieManager: CookieManager = CookieManager.getInstance()
            cookieManager.removeSessionCookies(null)
            cookieManager.removeAllCookies(null)
            cookieManager.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 安装  apk
     * @param context Context
     * @param apk File
     */
    fun installApk(context: Context, apk: File){
        val intent = Intent(Intent.ACTION_VIEW)
        intent.action = Intent.ACTION_VIEW
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        val uri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(
                context,
                context.packageName + ConstantGlobal.FILE_PROVIDER,
                apk
            )
        } else {
            uri = Uri.fromFile(apk)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        context.startActivity(intent)
    }


    /**
     * 数字千分号
     * @return String
     */
    fun numberFormatGroupingUsed(any: Any):String{
        val numberFormat = NumberFormat.getNumberInstance()
        numberFormat.isGroupingUsed = true
        return numberFormat.format(any)
    }


    /**
     * 设置img标签下的width为手机屏幕宽度，height自适应
     *
     * @param data html字符串
     * @return 更新宽高属性后的html字符串
     */
    fun getNewData(data: String, width: Int): String? {
        val document: Document = Jsoup.parse(data)
        val pElements: Elements = document.select("p:has(img)")
        for (pElement in pElements) {
            pElement.attr("style", "text-align:center")
            pElement
                .attr("max-width", (width.toString() + "px"))
                .attr("height", "auto")
        }
        val imgElements: Elements = document.select("img")
        for (imgElement in imgElements) {
            //重新设置宽高
            imgElement.attr("max-width", "100%")
                .attr("height", "auto")
            imgElement.attr("style", "max-width:100%;height:auto")
        }
        return document.toString()
    }

    /**
     * js 设置img标签下的width为手机屏幕宽度，height自适应
     */
    fun jsResizeImages(webView: WebView){
        val javascript = "javascript:function ResizeImages() {" +
                "var myimg,oldwidth;" +
                "var maxwidth = document.body.clientWidth;" +
                "for(i=0;i <document.images.length;i++){" +
                "myimg = document.images[i];" +
                "if(myimg.width > maxwidth){" +
                "oldwidth = myimg.width;" +
                "myimg.width = maxwidth;" +
                "}" +
                "}" +
                "}"
        val width = webView.width
        webView.loadUrl(javascript)
        webView.loadUrl("javascript:ResizeImages();")
    }


    /**
     * 让一个输入框只能输入指定位数小数 和整数位
     *
     * @param editText   EditText
     * @param maxInteger 最大整数位数
     * @param maxPoint   最大小数位数
     * create by ggband
     */
    fun setPricePointWithInteger(
        editText: EditText,
        maxPoint: Int,
        maxInteger: Int,
        vararg inputFilters: InputFilter?
    ) {
        if (inputFilters == null || inputFilters.size == 0) {
            editText.filters = arrayOf<InputFilter>(InputNumLengthFilter(maxPoint, maxInteger))
        } else {
            val newInputFilters: Array<InputFilter?> =
                arrayOfNulls<InputFilter>(inputFilters.size + 1)
            System.arraycopy(inputFilters, 0, newInputFilters, 0, inputFilters.size)
            newInputFilters[inputFilters.size] = InputNumLengthFilter(maxPoint, maxInteger)
            editText.filters = newInputFilters
        }
    }


    /**
     * 是否安装 程序
     * @param context Context
     * @param packagename String
     */
    fun isAppInstalled(context: Context,packagename:String):Boolean{
        var packageInfo : PackageInfo ?
        try {
            packageInfo = context.packageManager.getPackageInfo(packagename, 0)
        }catch ( e : PackageManager.NameNotFoundException) {
            packageInfo = null
            e.printStackTrace()
        }
        return packageInfo != null
    }
}