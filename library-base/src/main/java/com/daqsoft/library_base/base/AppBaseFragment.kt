package com.daqsoft.library_base.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog

import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.R
import com.daqsoft.library_base.widget.QMUILoadingView
import com.daqsoft.mvvmfoundation.base.BaseFragment
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.MultipleLayoutManager
import com.daqsoft.mvvmfoundation.http.NetworkUtil
import com.daqsoft.mvvmfoundation.utils.JumpPermissionManagement
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.permissionx.guolindev.PermissionX
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.library_base.base
 * @date 2/11/2020 下午 3:19
 * @author zp
 * @describe
 */
abstract class AppBaseFragment<V : ViewDataBinding, VM : BaseViewModel<*>> : BaseFragment<V, VM>() {

    override fun initParam() {
        ARouter.getInstance().inject(this)
    }

    override fun initViewObservable() {
        viewModel.showLoadingDialogLiveData.observe(this, Observer {
            showLoadingDialog(it)
        })

        viewModel.dismissLoadingDialogLiveData.observe(this, Observer {
            dismissLoadingDialog()
        })
    }


    private var loadingDialog: MaterialDialog? = null
    fun showLoadingDialog(title:String?=null) {
        loadingDialog = MaterialDialog
            .Builder(activity!!)
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
        loadingDialog?.dismiss()
    }

    /**
     * 权限请求
     */
    fun requestPermission(vararg permissions: String, callback: ()->Unit){
        PermissionX
            .init(this)
            .permissions(permissions.toList())
            .onExplainRequestReason { scope, deniedList ->
                // 解释原因 重新申请
            }
            .onForwardToSettings { scope, deniedList ->
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
            .Builder(activity!!)
            .customView(R.layout.dialog_tips,false)
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
            JumpPermissionManagement.GoToSetting(activity)
            permissionDialog?.cancel()
        }
        permissionDialog?.setCancelable(false)
        permissionDialog?.show()
        permissionDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }



    private var multipleLayoutManager : MultipleLayoutManager? = null
    private var errorView : View ? = null
    private var errorContent : TextView ? = null
    private var retry : TextView ? = null
    private var chrysanthemum : QMUILoadingView? = null
    private var retryFlag : Boolean  = false
    private var disposable : Disposable? = null

    open fun retry(){}

    fun errorLayout(outermostId :Int,callback: () -> Unit){
        retry?.visibility = View.VISIBLE
        chrysanthemum?.visibility = View.INVISIBLE
        errorContent?.text = resources.getString(com.daqsoft.library_base.R.string.network_connection_failed)
        if (errorView == null) {
            errorView = LayoutInflater.from(this.context).inflate(com.daqsoft.library_base.R.layout.layout_error, null, false)
            errorContent = errorView!!.findViewById<TextView>(com.daqsoft.library_base.R.id.content)
            errorContent?.text = this.context!!.resources.getString(com.daqsoft.library_base.R.string.network_connection_failed)
            chrysanthemum = errorView!!.findViewById<QMUILoadingView>(com.daqsoft.library_base.R.id.chrysanthemum)
            retry = errorView!!.findViewById<TextView>(com.daqsoft.library_base.R.id.retry)
            retry?.visibility = View.VISIBLE
            retry?.setOnClickListener {
                retryFlag = true
                retry?.visibility = View.INVISIBLE
                chrysanthemum?.visibility = View.VISIBLE
                errorContent?.text = "正在重试中..."
                Observable
                    .timer(1, TimeUnit.SECONDS)
                    .compose(RxUtils.schedulersTransformer())
                    .subscribe(object : io.reactivex.rxjava3.core.Observer<Long>{
                        override fun onSubscribe(d: Disposable?) {
                            disposable = d
                        }

                        override fun onNext(t: Long?) {
                            callback()
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
                .Builder(activity!!.findViewById(outermostId))
                .setErrorLayout(errorView!!)
                .build()
        }
        multipleLayoutManager?.showErrorLayout()
    }

    fun normalLayout(){
        retry()
        retry?.visibility = View.VISIBLE
        chrysanthemum?.visibility = View.INVISIBLE
        errorContent?.text = resources.getString(R.string.network_connection_failed)
        multipleLayoutManager?.showSuccessLayout()

    }

}