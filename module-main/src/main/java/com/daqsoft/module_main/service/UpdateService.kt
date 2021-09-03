package com.daqsoft.module_main.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_common.pojo.vo.UpdateInfo
import com.daqsoft.mvvmfoundation.http.download.DownLoadManager
import com.daqsoft.mvvmfoundation.http.download.ProgressCallBack
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.jeremyliao.liveeventbus.LiveEventBus
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.File

/**
 * @package name：com.daqsoft.module_main.service
 * @date 25/1/2021 下午 4:06
 * @author zp
 * @describe 下载更新服务
 */
class UpdateService : Service() {

    private var myBinder : MyUpdateBinder? = null
    private var updateInfo : UpdateInfo? = null
    private var progressCallBack : UpdateProgressListener ? = null

    override fun onBind(intent: Intent?): IBinder? {
        myBinder = MyUpdateBinder()
        updateInfo = intent?.getBundleExtra("bundle")?.getParcelable<UpdateInfo>("updateInfo")
        return myBinder
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        updateInfo = intent.getBundleExtra("bundle")?.getParcelable<UpdateInfo>("updateInfo")
        execute()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    private fun execute(){
        downloadApk()
    }

    /**
     * 下载apk
     */
    private fun downloadApk(){
        val destFileDir = applicationContext.externalCacheDir!!.path
        val destFileName = "${applicationContext.packageName}_v${updateInfo?.VersionCode}.apk"
        DownLoadManager.getInstance().load(updateInfo?.DownPath, object : ProgressCallBack<ResponseBody>(destFileDir, destFileName) {
            override fun onStart() {
                super.onStart()
                ToastUtils.showShortSafe("开始下载")
                progressCallBack?.onCommence()
            }



            override fun progress(progress: Long, total: Long) {
                Timber.e("progress: ${progress},total:${total}")
                progressCallBack?.progress(progress, total)
            }

            override fun onSuccess(t: ResponseBody?) {
                ToastUtils.showShortSafe("下载完成")
                val file = File(destFileDir, destFileName)
                progressCallBack?.onSuccess(file)
                LiveEventBus.get(LEBKeyGlobal.NEW_VERSION_DOWNLOADED).post(Pair(true,file))
            }

            override fun onError(e: Throwable?) {
                ToastUtils.showShortSafe("下载失败")
                LiveEventBus.get(LEBKeyGlobal.NEW_VERSION_DOWNLOADED).post(Pair(false,null))
                e?.printStackTrace()
                progressCallBack?.onError(e)
                stopSelf()
            }

            override fun onCompleted() {
                super.onCompleted()
                progressCallBack?.onCompleted()
            }
        })
    }


    inner class MyUpdateBinder : Binder(){

        fun setProgressCallBack(progressCallBack : UpdateProgressListener){
            this@UpdateService.progressCallBack = progressCallBack
        }

        fun execute(){
            this@UpdateService.execute()
        }
    }
}