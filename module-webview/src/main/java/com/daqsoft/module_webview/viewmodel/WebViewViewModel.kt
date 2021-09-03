package com.daqsoft.module_webview.viewmodel

import android.app.Application
import android.os.Handler
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.FileUtils
import com.daqsoft.library_common.pojo.vo.NextMessage
import com.daqsoft.library_common.utils.NextMessageOpenHelper
import com.daqsoft.module_webview.repository.WebViewRepository
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.ResponseBody
import retrofit2.Response
import timber.log.Timber
import java.io.File

/**
 * @package name：com.daqsoft.module_webview.viewmodel
 * @date 5/11/2020 下午 2:05
 * @author zp
 * @describe
 */
class WebViewViewModel : ToolbarViewModel<WebViewRepository>{

    @ViewModelInject
    constructor(application: Application, webViewRepository: WebViewRepository):super(
        application,
        webViewRepository
    )


    val webViewStatusBarHeight = AppUtils.getStatusBarHeight()


    val backLiveData = MutableLiveData<Unit?>()
    override fun backOnClick() {
        backLiveData.value = null
    }

    val rightIconLiveData = MutableLiveData<Unit?>()
    override fun rightIconOnClick() {
        super.rightIconOnClick()
        rightIconLiveData.value = null
    }


    /**
     * 导出加班报表
     */
    fun exportOvertimeReport(param: String){

        val type = object : TypeToken<Map<String, String?>>() {}.type
        val map = Gson().fromJson<Map<String, String?>>(param, type)

        if (map.isNullOrEmpty()){
            ToastUtils.showShortSafe("参数错误")
            return
        }


        val orgId: String = map["orgId"]?:""
        val endDate: String = map["endDate"]?:""
        val beginDate: String = map["beginDate"]?:""
        val employeeName: String = map["employeeName"]?:""

        addSubscribe(
            model
                .exportOvertimeReport(orgId, endDate, beginDate, employeeName)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {
                    override fun onNext(t: Response<ResponseBody>?) {
                        Timber.e("${t.toString()}")
                        val body = t?.body() ?: return
                        FileUtils.saveFileByResponseBody(getApplication(),body,"加班报表")
                    }

                    override fun onError(e: Throwable?) {
                        ToastUtils.showShortSafe("文件下载失败")
                    }

                    override fun onComplete() {
                    }

                    override fun onStart() {
                        super.onStart()
                        ToastUtils.showShortSafe("开始下载")
                    }
                })

        )
    }


    /**
     * 导出考勤报表
     */
    fun exportAttendanceReport(param: String){
        val type = object : TypeToken<Map<String, String?>>() {}.type
        val map = Gson().fromJson<Map<String, String?>>(param, type)

        if (map.isNullOrEmpty()){
            ToastUtils.showShortSafe("参数错误")
            return
        }

        val orgId: String = map["orgId"]?:""
        val state: String = map["state"]?:""
        val endDate: String = map["endDate"]?:""
        val beginDate: String = map["beginDate"]?:""
        val employeeName: String = map["employeeName"]?:""

        addSubscribe(
            model
                .exportAttendanceReport(orgId, state, endDate, beginDate, employeeName)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {
                    override fun onNext(t: Response<ResponseBody>?) {
                        val body = t?.body() ?: return
                        FileUtils.saveFileByResponseBody(getApplication(),body,"考勤报表")
                    }

                    override fun onError(e: Throwable?) {
                        ToastUtils.showShortSafe("文件下载失败")
                    }

                    override fun onComplete() {
                    }

                    override fun onStart() {
                        super.onStart()
                        ToastUtils.showShortSafe("开始下载")
                    }
                })

        )
    }

    var nextLiveData=MutableLiveData<NextMessage>()
    var nextId:String=""
    fun getNextDetailData(nextIds: String) {
        nextId=nextIds;
    }

    /**
     * 下一条是否可见
     */
    var nextVisible=ObservableField<Int>(View.GONE)
    val NextOnClick = BindingCommand<String>(BindingAction {
        addSubscribe(
            model
                .getNextDetail(nextId.toInt())
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<NextMessage>>() {
                    override fun onSuccess(it: AppResponse<NextMessage>) {
                        nextLiveData.value=it.data
                        if(nextLiveData?.value==null){
                            return
                        }
                        NextMessageOpenHelper.pageJump(nextLiveData?.value!!.next,nextLiveData?.value?.templateCode,nextLiveData?.value!!.messageExtId)
                    }
                })
        )

    })
}