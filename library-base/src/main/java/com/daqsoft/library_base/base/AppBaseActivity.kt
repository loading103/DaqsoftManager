package com.daqsoft.library_base.base

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.R
import com.daqsoft.library_base.utils.AndroidBug5497Workaround
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.mvvmfoundation.base.BaseActivity
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import com.permissionx.guolindev.PermissionX
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import java.util.concurrent.TimeUnit


/**
 * @package name：com.daqsoft.library_base.base
 * @date 2/11/2020 上午 10:12
 * @author zp
 * @describe
 */
abstract class AppBaseActivity<V : ViewDataBinding, VM : BaseViewModel<*>> : BaseActivity<V, VM>(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 始终竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // 软键盘自适应
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        AndroidBug5497Workaround.assistActivity(this)
    }

    override fun initParam() {
        ARouter.getInstance().inject(this)
    }

    override fun initView() {
        super.initView()
        ImmersionBar
            .with(this)
            .statusBarDarkFont(true)
            .init()
    }

    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.showLoadingDialogLiveData.observe(this, Observer {
            showLoadingDialog(it)
        })

        viewModel.dismissLoadingDialogLiveData.observe(this, Observer {
            dismissLoadingDialog()
        })

    }

    private var loadingDialog: MaterialDialog? = null
    fun showLoadingDialog(title: String? = null) {
        loadingDialog = MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_loading, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = loadingDialog?.customView
        val content = view?.findViewById<TextView>(R.id.tv)
        if (!title.isNullOrBlank()){
            content?.visibility = View.VISIBLE
            content?.text = title
        }else{
            content?.visibility = View.GONE
        }
        loadingDialog!!.setCancelable(false)
        loadingDialog!!.show()
        //设置自适应的方法：
        val dialogParams = loadingDialog!!.window?.attributes
        dialogParams?.width = 300
        dialogParams?.height = 350
        loadingDialog?.window?.attributes = dialogParams
        loadingDialog!!.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        loadingDialog!!.window?.setDimAmount(0f)
    }

    fun dismissLoadingDialog() {
        try {
            loadingDialog?.dismiss()
        }catch (e: Exception){
        }
    }



    /**
     * 权限请求
     */
    fun requestPermission(vararg permissions: String, callback: () -> Unit){
        PermissionX
            .init(this)
            .permissions(permissions.toList())
            .onExplainRequestReason { scope, deniedList ->
                // 解释原因 重新申请
                Timber.e("解释原因 重新申请  ${Gson().toJson(deniedList)}")
            }
            .onForwardToSettings { scope, deniedList ->
                Timber.e("跳转设置开启权限 ${Gson().toJson(deniedList)}")
                // 跳转设置开启权限
                goToSetting()
            }
            .request { allGranted, grantedList, deniedList ->
                // 权限通过
                if (allGranted) {
                    callback()
                }
            }
    }

    fun goToSetting(){
        if (permissionDialog == null){
            showGoToSettingDialog()
        }else if(!permissionDialog!!.isShowing){
            permissionDialog!!.show()
        }
    }

    /**
     * 跳转设置开启权限
     */
    private var permissionDialog:MaterialDialog? = null
    private fun showGoToSettingDialog(){
        permissionDialog= MaterialDialog
            .Builder(this)
            .customView(R.layout.dialog_tips, false)
            .backgroundColor(Color.TRANSPARENT)
            .build()
        val view = permissionDialog?.customView
        val title = view?.findViewById<TextView>(R.id.title)
        title?.text = resources.getString(R.string.tips)
        val content = view?.findViewById<TextView>(R.id.content)
        content?.text = resources.getString(R.string.permission_tips)
        val cancel = view?.findViewById<TextView>(R.id.cancel)
        cancel?.setOnClickListener{
            permissionDialog?.cancel()
        }
        val determine = view?.findViewById<TextView>(R.id.determine)
        determine?.setOnClickListener {
            JumpPermissionManagement.GoToSetting(this)
            permissionDialog?.cancel()
        }
        permissionDialog?.setCancelable(false)
        permissionDialog?.show()
        permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.let {
            if (!it.isDisposed){
                it.dispose()
            }
        }
    }

    private var multipleLayoutManager : MultipleLayoutManager? = null
    private var errorView : View ? = null
    private var errorContent : TextView ? = null
    private var retry : TextView ? = null
    private var chrysanthemum : QMUILoadingView? = null
    private var retryFlag : Boolean  = false
    private var disposable : Disposable? = null


    /**
     * 最外层布局 id
     * 这里用于多状态布局  需要处理的页面  复写改方法
     *
     * @return Int
     */
    open fun outermostId():Int = 0

    /**
     * 重试
     */
    open fun retry(){}

    /**
     * 无网络处理
     */
    private fun isNetworkAvailable(){
        if (outermostId() != 0) {
            if (!NetworkUtil.isNetworkAvailable(this)) {
                retry?.visibility = View.VISIBLE
                chrysanthemum?.visibility = View.INVISIBLE
                errorContent?.text = resources.getString(R.string.network_connection_failed)
                if (errorView == null) {
                    errorView = LayoutInflater.from(this).inflate(
                        R.layout.layout_error,
                        null,
                        false
                    )
                    errorContent = errorView!!.findViewById<TextView>(R.id.content)
                    errorContent?.text = resources.getString(R.string.network_connection_failed)
                    chrysanthemum = errorView!!.findViewById<QMUILoadingView>(R.id.chrysanthemum)
                    retry = errorView!!.findViewById<TextView>(R.id.retry)
                    retry?.visibility = View.VISIBLE
                    retry?.setOnClickListener {
                        retryFlag = true
                        retry?.visibility = View.INVISIBLE
                        chrysanthemum?.visibility = View.VISIBLE
                        errorContent?.text = "正在重试中..."
                        Observable
                            .timer(1, TimeUnit.SECONDS)
                            .compose(RxUtils.schedulersTransformer())
                            .subscribe(object : io.reactivex.rxjava3.core.Observer<Long> {
                                override fun onSubscribe(d: Disposable?) {
                                    disposable = d
                                }

                                override fun onNext(t: Long?) {
                                    initView()
                                }

                                override fun onError(e: Throwable?) {
                                    e?.printStackTrace()
                                }

                                override fun onComplete() {
                                }

                            })
                    }
                }
                if (multipleLayoutManager == null) {
                    multipleLayoutManager = MultipleLayoutManager
                        .Builder(findViewById(outermostId()))
                        .setErrorLayout(errorView!!)
                        .build()
                }
                multipleLayoutManager?.showErrorLayout()
            } else {
                retry?.visibility = View.VISIBLE
                chrysanthemum?.visibility = View.INVISIBLE
                errorContent?.text = resources.getString(R.string.network_connection_failed)

                multipleLayoutManager?.showSuccessLayout()
                if (retryFlag) {
                    retry()
                }
            }
        }
    }
}