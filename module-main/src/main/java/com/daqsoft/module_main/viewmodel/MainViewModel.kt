package com.daqsoft.module_main.viewmodel

import android.app.Application
import android.content.Intent
import android.text.TextUtils
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import cn.jpush.android.api.JPushInterface
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.MyUtils
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.library_common.pojo.vo.UpdateInfo
import com.daqsoft.module_main.repository.MainRepository
import com.daqsoft.module_main.repository.pojo.vo.Pending
import com.daqsoft.mvvmfoundation.base.BaseApplication
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.http.BaseResponse
import com.daqsoft.mvvmfoundation.http.download.DownLoadManager
import com.daqsoft.mvvmfoundation.http.download.ProgressCallBack
import com.daqsoft.mvvmfoundation.utils.AesCryptUtils
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import io.reactivex.rxjava3.observers.DisposableObserver
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import timber.log.Timber
import java.io.File

/**
 * @package name：com.daqsoft.module_main.viewmodel
 * @date 28/10/2020 下午 3:24
 * @author zp
 * @describe
 */
class MainViewModel : BaseViewModel<MainRepository> {

    @ViewModelInject
    constructor(application: Application,mainRepository:MainRepository):super(application,mainRepository)


    val pendingLiveData = MutableLiveData<Pending>()


    /**
     * 获取我的信息
     */
    fun getEmployeeInfo(){
        addSubscribe(
            model
                .getEmployeeInfo()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeInfo>>() {
                    override fun onSuccess(t: AppResponse<EmployeeInfo>) {
                        t.data?.let {
//                            if (DataStoreUtils.getInt(DSKeyGlobal.USER_ID) != 0){
//                                return
//                            }
                            DataStoreUtils.put(DSKeyGlobal.USER_ID,it.employeeId)
                            DataStoreUtils.put(DSKeyGlobal.USER_INFO, AesCryptUtils.encrypt(Gson().toJson(it)))
                            JPushInterface.setAlias(ContextUtils.getContext(),it.employeeId,it.employeeId.toString())
                        }
                    }
                })
        )
    }


    /**
     * 获取未处理
     */
    fun getPendingTotal(){
        addSubscribe(
            model
                .getPendingTotal()
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<Pending>>(){
                    override fun onSuccess(t: AppResponse<Pending>) {
                        t.data?.let {
                            pendingLiveData.value = it
                        }
                    }
                })
        )
    }


    val updateLiveData = MutableLiveData<UpdateInfo>()
    /**
     * 检查更新
     */
    fun checkUpdate(){
        addSubscribe(
            model
                .checkUpdate()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : DisposableObserver<Response<ResponseBody>>() {
                    override fun onNext(t: Response<ResponseBody>?) {
                        t?.body()?.let {
                            val result = it.string()
                            try {
                                val updateInfo = Gson().fromJson<UpdateInfo>(result,UpdateInfo::class.java)
//                                if (updateInfo.VersionCode != AppUtils.getVersionName()){
//                                    updateLiveData.value = updateInfo
////                                    ARouter.getInstance().build(ARouterPath.Base.UPDATE).withParcelable("updateInfo",updateInfo).navigation()
//                                }
                                // 高版本才提示更新
                                val update: Boolean = MyUtils.isNeedUpdate(BaseApplication.getInstance()?.applicationContext, AppUtils.getVersionName(),updateInfo.VersionCode )
                                if(update){
                                    updateLiveData.value = updateInfo
                                }
                            }catch (e:Exception){
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        e?.printStackTrace()
                    }

                    override fun onComplete() {
                    }
                })
        )
    }

}