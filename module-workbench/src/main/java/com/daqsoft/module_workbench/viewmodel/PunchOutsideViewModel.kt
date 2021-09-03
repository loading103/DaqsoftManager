package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.library_common.pojo.vo.WelcomeMessage
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 8/1/2021 下午 4:17
 * @author zp
 * @describe
 */
class PunchOutsideViewModel : BaseViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application, workBenchRepository: WorkBenchRepository):super(
        application,
        workBenchRepository
    )

    /**
     * 欢迎语
     */
    var welcomeObservable = ObservableField<WelcomeMessage>()

    val takePhotosLiveData = MutableLiveData<Unit>()

    /**
     * 打卡
     */
    val itemOnClick = BindingCommand<Unit>(BindingAction {
        takePhotosLiveData.value = null
    })

    /**
     * 时间
     */
    val timeObservable = ObservableField<String>()


    /**
     * 地址
     */
    var address = ObservableField<String>()


    /**
     * 显示时间
     */
    fun displayTime(){
        addSubscribe(
            Observable
                .interval(1, TimeUnit.SECONDS)
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : DisposableObserver<Long>() {
                    override fun onNext(t: Long?) {
                        val calendar = Calendar.getInstance()
                        calendar.timeZone = TimeZone.getTimeZone("GMT+8:00")
                        val hour = calendar.get(Calendar.HOUR_OF_DAY)
                        val minute = calendar.get(Calendar.MINUTE)
                        timeObservable.set(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onComplete() {
                    }
                })
        )

    }

    /**
     * 打卡
     * @param uri Uri
     */
    fun clockIn(uri: Uri){
        addSubscribe(
            Observable.create(ObservableOnSubscribe<MultipartBody.Part> {
                val bitmap = BitmapFactory.decodeStream(
                    getApplication<Application>().contentResolver.openInputStream(
                        uri
                    )
                )
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                var options = 90
                while (stream.toByteArray().size / 1024 > 1024 * 2) {
                    stream.reset()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)
                    options -= 10
                }
                val body = RequestBody.create(MediaType.get("image/png"), stream.toByteArray())
                val file = MultipartBody.Part.createFormData(
                    "file",
                    "${System.currentTimeMillis()}.jpg",
                    body
                )
                it.onNext(file)
                bitmap.recycle()
            })
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<MultipartBody.Part, Observable<AppResponse<List<UploadResult>>>> {
                    return@Function model.uploadOSS(file = it)
                })
                .concatMap(Function<AppResponse<List<UploadResult>>, Observable<AppResponse<Any>>> {
                    var url = ""
                    it.data?.let {
                        if (it.isNotEmpty()) {
                            url = it[0].url
                        }
                    }
                    return@Function model.clockIn()
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    // 未执行 ？
//                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }


    /**
     * 获取欢迎语
     */
    fun getWelcomeMessage(){
        addSubscribe(
            model
                .getWelcomeMessage()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<WelcomeMessage>>() {
                    override fun onSuccess(t: AppResponse<WelcomeMessage>) {
                        t.data?.let {
                            welcomeObservable.set(it)
                        }
                    }
                })
        )
    }
}