package com.daqsoft.module_main.activity

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.view.KeyEvent
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_common.pojo.vo.UpdateInfo
import com.daqsoft.module_main.BR
import com.daqsoft.module_main.R
import com.daqsoft.module_main.databinding.ActivityUpdateBinding
import com.daqsoft.module_main.service.UpdateProgressListener
import com.daqsoft.module_main.service.UpdateService
import com.daqsoft.module_main.viewmodel.UpdateViewModel
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.File


/**
 * @package name：com.daqsoft.module_main.activity
 * @date 25/1/2021 下午 3:57
 * @author zp
 * @describe 更新
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Base.UPDATE)
class UpdateActivity : AppBaseActivity<ActivityUpdateBinding, UpdateViewModel>(), UpdateProgressListener {


    companion object{
        const val INSTALL_PERMISSION_CODE = 1000
    }

    @JvmField
    @Autowired
    var updateInfo : UpdateInfo? = null

    private var serviceConnection: ServiceConnection? = null

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_update
    }

    override fun initViewModel(): UpdateViewModel? {
        return viewModels<UpdateViewModel>().value
    }


    override fun initData() {

        setFinishOnTouchOutside(false)

        updateInfo?.let {
            binding.content.text = it.AppUpdateInfo
        }

        binding.determine.setOnClickListener {
            bindService()
            finish()
        }

        binding.close.setOnClickListener {
            finish()
        }

    }

    override fun initViewObservable() {
        super.initViewObservable()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (event?.keyCode == KeyEvent.KEYCODE_BACK) {
            true
        } else {
            super.dispatchKeyEvent(event)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        serviceConnection?.let {
            unbindService(it)
        }

    }


    /**
     * 启动服务
     */
    private fun  bindService(){

        val intent = Intent(this,UpdateService::class.java)
        val bundle =  Bundle()
        bundle.putParcelable("updateInfo",updateInfo)
        intent.putExtra("bundle",bundle)
        startService(intent)
        return

//        serviceConnection = object : ServiceConnection {
//            override fun onServiceConnected(name: ComponentName, service: IBinder) {
//                val myBinder = service as UpdateService.MyUpdateBinder
//                myBinder.setProgressCallBack(this@UpdateActivity)
//                myBinder.execute()
//            }
//
//            override fun onServiceDisconnected(name: ComponentName) {
//                serviceConnection?.let {
//                    unbindService(it)
//                }
//            }
//        }
//        bindService(
//            intent,
//            serviceConnection!!,
//            Context.BIND_AUTO_CREATE
//        )

    }

    var apkFile : File? = null

    override fun onCommence() {

    }


    override fun progress(progress: Long, total: Long) {
        // todo 展示进度
    }

    override fun onSuccess(file: File) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            apkFile = file
            val haveInstallPermission = packageManager.canRequestPackageInstalls()
            if (haveInstallPermission){
                AppUtils.installApk(applicationContext, file)
                return
            }
            showInstallPermissionDialog()
            return
        }

        AppUtils.installApk(applicationContext, file)
    }

    override fun onError(e: Throwable?) {

    }

    override fun onCompleted() {
    }

    /**
     * 展示未知来源安装
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showInstallPermissionDialog(){
        val permissionDialog= MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_tips, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = permissionDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = "提示"
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = "请允许安装未知来源应用权限，以便更好地为您服务"
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener{
            permissionDialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            toInstallPermissionSettingIntent()
            permissionDialog?.cancel()
        }
        permissionDialog?.setCancelable(false)
        permissionDialog?.show()
        permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    /**
     * 开启安装未知来源权限
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    fun toInstallPermissionSettingIntent() {
        val packageURI = Uri.parse("package:$packageName")
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI)
        startActivityForResult(intent, INSTALL_PERMISSION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            when(requestCode){
                INSTALL_PERMISSION_CODE ->{
                    apkFile?.let {
                        AppUtils.installApk(applicationContext, it)
                    }
                }
            }
        }


    }

}



