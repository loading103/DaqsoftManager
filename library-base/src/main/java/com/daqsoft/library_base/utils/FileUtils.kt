package com.daqsoft.library_base.utils

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.TextView
import androidx.core.content.FileProvider
import com.afollestad.materialdialogs.MaterialDialog
import com.daqsoft.library_base.R
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.RetrofitClient
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.observers.DisposableObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * @package name：com.daqsoft.library_base.utils
 * @date 12/1/2021 下午 2:47
 * @author zp
 * @describe
 */
object FileUtils {

    private const val APP_DIR = "com.daqsoft.manager.daq"

    /**
     * 获取 SD 路径
     */
    fun getSDCardPath():String{
        var sdDir : File?  = null
        val sdCardExist = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
        if(sdCardExist){
            if (Build.VERSION.SDK_INT>=29){
                sdDir = ContextUtils.getContext().getExternalFilesDir(null)
            }else {
                sdDir = Environment.getExternalStorageDirectory()
            }
        } else {
            sdDir = Environment.getRootDirectory()
        }
        return sdDir!!.path
    }


    /**
     * 获取 app 文件目录
     */
    fun getAppDirPath():String{
        val appDir = File(getSDCardPath() + File.separator + APP_DIR)
        if (!appDir.exists()){
            appDir.mkdirs()
        }
        return appDir.path
    }


    /**
     * 保存文件
     * @param fileName String
     * @param byteArray ByteArray
     */
    fun saveFile(context: Context, fileName: String, byteArray: ByteArray){
        var outputStream : FileOutputStream? = null
        try {
            val file = File(getAppDirPath(), fileName)
            outputStream = FileOutputStream(file)
            outputStream.write(byteArray)
            outputStream.flush()
            outputStream.close()
            ToastUtils.showLongSafe("文件下载完成")

            LiveEventBus.get(LEBKeyGlobal.FILE_DOWNLOADED_SUCCESSFULLY).post(true)

            previewFile(context, file)
        }catch (e: Exception){
            e.printStackTrace()
            ToastUtils.showLongSafe("文件下载失败")
        }finally {
            Handler(Looper.getMainLooper()).post {
                dialog?.dismiss()
            }
            try {
                outputStream?.close()
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

    /**
     * 下载文件
     */
    fun downloadFile(context: Context, fileName: String, url: String){
        RetrofitClient
            .Builder()
            .build()
            .create(DownloadApiService::class.java)
            .download(url)
            .doOnSubscribe {
                Handler(Looper.getMainLooper()).post {
                    showLoadingDialog(context)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .doFinally {
                Handler(Looper.getMainLooper()).post {
                    dialog?.dismiss()
                }
            }
            .subscribeWith(object : DisposableObserver<ResponseBody>() {
                override fun onComplete() {
                    Handler(Looper.getMainLooper()).post {
                        dialog?.dismiss()
                    }
                }

                override fun onNext(t: ResponseBody) {
                    Handler(Looper.getMainLooper()).post {
                        dialog?.dismiss()
                    }
                    saveFile(context, fileName, t.bytes())
                }

                override fun onError(e: Throwable) {
                    Handler(Looper.getMainLooper()).post {
                        dialog?.dismiss()
                        ToastUtils.showShort("文件下载失败")
                    }
                }
            })
    }

    /**
     * 获取 URI
     */
    fun getAppDirUri(context: Context, file: File):Uri{
        val authority = context.packageName + ConstantGlobal.FILE_PROVIDER
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(ContextUtils.getContext(), authority, file)
        } else {
            Uri.fromFile(file)
        }
    }


    fun fileExist(fileName: String) : Boolean{
        if (fileName.lastIndexOf(".") == -1){
            ToastUtils.showShortSafe("暂不支持该格式")
            return false
        }
        val suffix = fileName.substring(fileName.lastIndexOf("."))
        val type = MIME_MAP_TABLE.asSequence().find { it.key.equals(suffix, ignoreCase = true) }?.value
        if (type.isNullOrBlank()){
            ToastUtils.showLongSafe("暂不支持该格式")
            return false
        }

        val appDirPath = getAppDirPath()
        val file = File(appDirPath, fileName)
        return file.exists()
    }

    /**
     * 文件预览/下载
     */
    fun  previewOrDownloadFile(activity: AppBaseActivity<*, *>, fileName: String, fileUrl: String){
        if (fileName.lastIndexOf(".") == -1){
            ToastUtils.showShortSafe("暂不支持该格式")
            return
        }
        val suffix = fileName.substring(fileName.lastIndexOf("."))
        val type = MIME_MAP_TABLE.asSequence().find { it.key.equals(suffix, ignoreCase = true) }?.value
        if (type.isNullOrBlank()){
            ToastUtils.showLongSafe("暂不支持该格式")
            return
        }

        val appDirPath = getAppDirPath()
        val file = File(appDirPath, fileName)
        if (!file.exists()){
            activity.runOnUiThread {
                activity.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, callback = {
                    showDownloadDialog(
                        activity,
                        fileName,
                        fileUrl
                    )
                })
            }
            return
        }
        previewFile(activity, file)
    }

    /**
     * 下载提示 dialog
     */
    private fun showDownloadDialog(context: Context, fileName: String, url: String){
        val dialog = MaterialDialog
            .Builder(context)
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
            downloadFile(context, fileName, url)
        }
        dialog?.setCancelable(false)
        dialog?.show()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    /**
     * 预览文件
     */
    fun previewFile(context: Context, file: File){
        try {
            val fileName = file.name
            val suffix = fileName.substring(fileName.lastIndexOf("."))
            val type = MIME_MAP_TABLE.asSequence().find { it.key.equals(suffix, ignoreCase = true) }?.value
            if (type.isNullOrBlank()){
                ToastUtils.showLongSafe("暂不支持该格式")
                return
            }
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.setDataAndType(getAppDirUri(context, file), type)
            context.startActivity(intent)
            LiveEventBus.get(LEBKeyGlobal.PREVIEW_FILE).post(true)
        }catch (e: Exception){
            e.printStackTrace()
            ToastUtils.showLong(e.message)
        }
    }

    private var dialog: MaterialDialog? = null
    private fun showLoadingDialog(context: Context) {
        dialog = MaterialDialog
            .Builder(context)
            .customView(R.layout.dialog_loading, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        dialog!!.setCancelable(false)
        dialog!!.show()
        val tv = dialog?.contentView?.findViewById<TextView>(R.id.tv)
        tv?.visibility = View.VISIBLE
        tv?.text = "下载中"
        //设置自适应的方法：
        val dialogParams = dialog!!.window?.attributes
        dialogParams?.width = 300
        dialogParams?.height = 350
        dialog?.window?.attributes = dialogParams
        dialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.window?.setDimAmount(0f)
    }


    val MIME_MAP_TABLE = mapOf<String, String>(
        ".3gp" to "video/3gpp",
        ".apk" to "application/vnd.android.package-archive",
        ".asf" to "video/x-ms-asf",
        ".avi" to "video/x-msvideo",
        ".bin" to "application/octet-stream",
        ".bmp" to "image/bmp",
        ".c" to "text/plain",
        ".class" to "application/octet-stream",
        ".conf" to "text/plain",
        ".cpp" to "text/plain",
        ".doc" to "application/msword",
        ".docx" to "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        ".xls" to "application/vnd.ms-excel",
        ".xlsx" to "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        ".exe" to "application/octet-stream",
        ".gif" to "image/gif",
        ".gtar" to "application/x-gtar",
        ".gz" to "application/x-gzip",
        ".h" to "text/plain",
        ".htm" to "text/html",
        ".html" to "text/html",
        ".jar" to "application/java-archive",
        ".java" to "text/plain",
        ".jpeg" to "image/jpeg",
        ".jpg" to "image/jpeg",
        ".js" to "application/x-javascript",
        ".log" to "text/plain",
        ".m3u" to "audio/x-mpegurl",
        ".m4a" to "audio/mp4a-latm",
        ".m4b" to "audio/mp4a-latm",
        ".m4p" to "audio/mp4a-latm",
        ".m4u" to "video/vnd.mpegurl",
        ".m4v" to "video/x-m4v",
        ".mov" to "video/quicktime",
        ".mp2" to "audio/x-mpeg",
        ".mp3" to "audio/x-mpeg",
        ".mp4" to "video/mp4",
        ".mpc" to "application/vnd.mpohun.certificate",
        ".mpe" to "video/mpeg",
        ".mpeg" to "video/mpeg",
        ".mpg" to "video/mpeg",
        ".mpg4" to "video/mp4",
        ".mpga" to "audio/mpeg",
        ".msg" to "application/vnd.ms-outlook",
        ".ogg" to "audio/ogg",
        ".pdf" to "application/pdf",
        ".png" to "image/png",
        ".pps" to "application/vnd.ms-powerpoint",
        ".ppt" to "application/vnd.ms-powerpoint",
        ".pptx" to "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        ".prop" to "text/plain",
        ".rc" to "text/plain",
        ".rmvb" to "audio/x-pn-realaudio",
        ".rtf" to "application/rtf",
        ".sh" to "text/plain",
        ".tar" to "application/x-tar",
        ".tgz" to "application/x-compressed",
        ".txt" to "text/plain",
        ".wav" to "audio/x-wav",
        ".wma" to "audio/x-ms-wma",
        ".wmv" to "audio/x-ms-wmv",
        ".wps" to "application/vnd.ms-works",
        ".xml" to "text/plain",
        ".z" to "application/x-compress",
        ".zip" to "application/x-zip-compressed",
        "" to "*/*"
    )


    interface DownloadApiService {

        /**
         * 下载文件
         */
        @GET
        @Streaming
        fun download(@Url url: String): Observable<ResponseBody>
    }


    /**
     * 根据Uri获取文件绝对路径，解决Android4.4以上版本Uri转换 兼容Android 10
     *
     * @param context
     * @param uri
     */
    fun getFileAbsolutePath(context: Context, uri: Uri): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, uri)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < 29 && DocumentsContract.isDocumentUri(
                context,
                uri
            )) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(
                        id
                    )
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":".toRegex()).toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            }
        } // MediaStore (and general)
        if (Build.VERSION.SDK_INT >= 29) {
            return uriToFileApiQ(context, uri)
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            return if (isGooglePhotosUri(uri)) {
                uri.lastPathSegment
            } else {
                val paths: String? = getDataColumn(context, uri, null, null)
                if (paths == null) {
                    uriToFileApiQ(context, uri)
                } else {
                    paths
                }
            }
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    //此方法 只能用于4.4以下的版本
    private fun getRealFilePath(context: Context, uri: Uri?): String? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)

//            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return data
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri!!,
                projection,
                selection,
                selectionArgs,
                null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(index)
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    private fun getFileFromContentUri(context: Context, uri: Uri?): String? {
        if (uri == null) {
            return null
        }
        val filePath: String
        val filePathColumn = arrayOf(
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME
        )
        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            uri, filePathColumn, null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                return filePath
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
        }
        return ""
    }

    /**
     * Android 10 以上适配,以及读一些第三方文件管理器
     * @param context
     * @param uri
     * @return
     */
    //@RequiresApi(api = [29, 28])
    private fun uriToFileApiQ(context: Context, uri: Uri): String? {
        var file: File? = null
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            if (cursor?.moveToFirst() == true) {
                val displayName = cursor?.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                try {
                    val inputStream = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir!!.absolutePath, /*Math.round((Math.random() + 1) * 1000).toString() + */
                        displayName
                    )
                    val fos = FileOutputStream(cache)
                    copy(inputStream!!, fos)
                    file = cache
                    fos.close()
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file?.absolutePath
    }


    private fun copy(inputStream: InputStream, fos: FileOutputStream) {
        try {
            val buffer = ByteArray(1024)
            var len: Int
            while (inputStream.read(buffer).also { len = it } > 0) {
                fos.write(buffer, 0, len)
            }
        } catch (e: java.lang.Exception) {

        } finally {
            fos.flush()
            inputStream.close()
            fos.close()
        }
    }



    val VIDEO = arrayListOf(
        ".avi",
        ".rmvb",
        ".rm",
        ".asf",
        ".divx",
        ".mpg",
        ".mpeg",
        ".mpe",
        ".wmv",
        ".mp4",
        ".mkv",
        ".vob"
    )

    /***
     * 是否 视频
     */
    fun isVideo(suffix: String):Boolean{
        return VIDEO.find { suffix.equals(it, ignoreCase = true) }!= null
    }

    val IMAGE = arrayListOf(".bmp", ".gif", ".jpeg", ".png", ".jpg")

    /**
     * 是否 图片
     */
    fun isImage(suffix: String):Boolean{
        return IMAGE.find { suffix.equals(it, ignoreCase = true) }!= null
    }

    /**
     * 是否 压缩
     */
    fun isCompressed(suffix: String):Boolean{
        return suffix.equals(".rar", ignoreCase = true) || suffix.equals(".zip", ignoreCase = true)
    }

    /**
     * 是否 文档
     */
    fun isWord(suffix: String):Boolean{
        return suffix.equals(".doc", ignoreCase = true) || suffix.equals(".docx", ignoreCase = true)
    }

    /**
     * 是否 表格
     */
    fun isExcel(suffix: String):Boolean{
        return suffix.equals(".xls", ignoreCase = true) || suffix.equals(".xlsx", ignoreCase = true)
    }

    /**
     * 是否 ppt
     */
    fun isPPT(suffix: String):Boolean{
        return suffix.equals(".ppt", ignoreCase = true) || suffix.equals(".pptx", ignoreCase = true)
    }

    /**
     * 是否 pdf
     */
    fun isPDF(suffix: String):Boolean{
        return suffix.equals(".pdf", ignoreCase = true)
    }

    /**
     * 是否 txt
     */
    fun isTxt(suffix: String):Boolean{
        return suffix.equals(".txt", ignoreCase = true)
    }


    /**
     * 按输入流保存文件
     */
    @SuppressLint("SimpleDateFormat")
    fun saveFileByResponseBody(context: Context, responseBody : ResponseBody, fileName: String){
        val `is`: InputStream = responseBody.byteStream()
        var fos: FileOutputStream? = null
        try {
            val simpleDateFormat = SimpleDateFormat("yyyyMMddHHmmss")
            val date = Date(System.currentTimeMillis())
            val file = File(getAppDirPath(), "${fileName}${simpleDateFormat.format(date)}.xlsx")
            fos = FileOutputStream(file)
            val buffer = ByteArray(1024)
            var len: Int
            while ( `is`.read(buffer).also { len = it } != -1) {
                fos.write(buffer, 0, len)
            }
            ToastUtils.showShortSafe("文件下载完成")
            previewFile(context, file)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            ToastUtils.showShortSafe("文件下载失败")
        } finally {
            try {
                fos?.flush()
                fos?.close()
                `is`.close()
            }catch (e:java.lang.Exception){
                e.printStackTrace()
            }
        }
    }
}