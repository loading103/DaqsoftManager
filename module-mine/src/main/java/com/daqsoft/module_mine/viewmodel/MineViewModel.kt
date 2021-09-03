package com.daqsoft.module_mine.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import com.alibaba.android.arouter.launcher.ARouter
import com.daqsoft.library_base.global.DSKeyGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.library_common.pojo.vo.EmployeeInfo
import com.daqsoft.library_common.pojo.vo.UploadResult
import com.daqsoft.library_common.pojo.vo.WelcomeMessage
import com.daqsoft.module_mine.viewmodel.itemviewmodel.DepartmentColleaguesItemViewModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.base.ItemViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.library_base.utils.DataStoreUtils
import com.daqsoft.library_base.utils.WXEntryHelper
import com.daqsoft.module_mine.repository.pojo.vo.DateInfo
import com.daqsoft.mvvmfoundation.utils.RxUtils
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.functions.Function
import me.tatarka.bindingcollectionadapter2.ItemBinding
import me.tatarka.bindingcollectionadapter2.collections.AsyncDiffObservableList
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.ByteArrayOutputStream

/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 6/11/2020 上午 11:41
 * @author zp
 * @describe 我的viewModel
 */
class MineViewModel : BaseViewModel<MineRepository> {

    @ViewModelInject constructor(application: Application, mineRepository: MineRepository):super(
        application,
        mineRepository
    )

    /**
     * 欢迎语
     */
    var welcomeObservable = ObservableField<WelcomeMessage>()

    /**
     * 结束下来刷新
     */
    var refreshLiveData = MutableLiveData<Boolean>()

    /**
     * 状态栏高
     */
    var statusBarHeight =  ObservableField<Int>(AppUtils.getStatusBarHeight())

    /**
     * 控件高
     */
    var viewHeight = ObservableField<Int>(AppUtils.getStatusBarHeight() + 48.dp)

    /**
     * 员工信息
     */
    val employeeInfo = ObservableField<EmployeeInfo>()

    /**
     * 给RecyclerView添加ObservableList
     */
    /**
     * 给RecyclerView添加ObservableList
     */
    var observableList: AsyncDiffObservableList<ItemViewModel<*>> = AsyncDiffObservableList(object : DiffUtil.ItemCallback<ItemViewModel<*>>() {
        override fun areItemsTheSame(
            oldItem: ItemViewModel<*>,
            newItem: ItemViewModel<*>
        ): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(
            oldItem: ItemViewModel<*>,
            newItem: ItemViewModel<*>
        ): Boolean {
            return oldItem == newItem
        }
    })
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<ItemViewModel<*>> = ItemBinding.of(
        BR.viewModel,
        R.layout.recyclerview_department_colleagues_item
    )

    /**
     * 设置点击事件
     */
    var personalInfoOnClick = BindingCommand<Unit>(BindingAction {
        ARouter.getInstance().build(ARouterPath.Mine.PAGER_PERSONAL_INFO).navigation()
    })


    val backgroundIconLiveData = MutableLiveData<Unit>()
    /**
     * 背景图点击事件
     */
    var backgroundIconOnClick = BindingCommand<Unit>(BindingAction {
        backgroundIconLiveData.value = null
    })

    /**
     * 设置头像点击事件
     */
    var headViewOnClick = BindingCommand<Unit>(BindingAction {
        ARouter
            .getInstance()
            .build(ARouterPath.Workbench.PAGER_STAFF)
            .withString("id",employeeInfo.get()?.employeeId.toString())
            .withString("name",employeeInfo.get()?.employeeName)
            .navigation()
    })

    /**
     * 设置顶部Url点击事件
     */
    var TopUrlOnClick = BindingCommand<Unit>(BindingAction {
        welcomeObservable.get()?.let {
            if (!it.careUrl.isNullOrBlank()){
                ARouter
                    .getInstance()
                    .build(ARouterPath.Web.PAGER_WEB)
                    .withString("url", it.careUrl)
                    .navigation()
            }
        }
    })


    var  department = ObservableField<String>()
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
                        refreshLiveData.value = true
                        t.data?.let {
                            employeeInfo.set(it)
                            department.set(it.employeeOrganizationStr + getApplication<Application>().resources.getString(R.string.module_mine_dash) + it.employeePostStr)
                        }
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        refreshLiveData.value = false
                    }
                })
        )
    }


    /**
     * 获取员工组织员工
     */
    fun getOrganizationEmployee(){
        addSubscribe(
            model
                .getOrganizationEmployee()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<List<EmployeeInfo>>>() {
                    override fun onSuccess(t: AppResponse<List<EmployeeInfo>>) {
                        t.data?.let {
                            val tempList = arrayListOf<ItemViewModel<*>>()
                            val ownId = if (employeeInfo.get() == null){
                                DataStoreUtils.getInt(DSKeyGlobal.USER_ID)
                            }else{
                                employeeInfo.get()!!.employeeId
                            }
                            it.filter {
                                it.employeeId != ownId
                            }?.forEach {
                                val departmentColleaguesItemViewModel =
                                    DepartmentColleaguesItemViewModel(
                                        this@MineViewModel,
                                        it
                                    )
                                tempList.add(departmentColleaguesItemViewModel)
                            }

                            observableList.update(tempList)
                        }
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

    /**
     * 日期liveData
     */
    val dateInfoLiveData = MutableLiveData<DateInfo>()

    /**
     * 获取日期信息
     */
    fun getDateInfo(){
        addSubscribe(
            model
                .getDateInfo()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<DateInfo>>() {
                    override fun onSuccess(t: AppResponse<DateInfo>) {
                        t.data?.let {
                            dateInfoLiveData.value = it
                        }
                    }
                })
        )
    }

    /**
     * 上传背景图
     */
    fun uploadBackgroundImage(path:String){
        addSubscribe(
            Observable.create(ObservableOnSubscribe<MultipartBody.Part> {
                val bitmap = BitmapFactory.decodeFile(path)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                var options = 90
                while (stream.toByteArray().size / 1024 > 1024 * 2){
                    stream.reset()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, options, stream)
                    options -= 10
                }
                val body = RequestBody.create(MediaType.get("image/png"), stream.toByteArray())
                val file = MultipartBody.Part.createFormData("file","${System.currentTimeMillis()}.jpg", body)
                it.onNext(file)
                bitmap.recycle()
            })
                .doOnSubscribe{
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .concatMap(Function<MultipartBody.Part, Observable<AppResponse<List<UploadResult>>>> {
                    return@Function model.uploadOSS(file = it)
                })
                .concatMap(Function<AppResponse<List<UploadResult>>, Observable<AppResponse<Any>>>{
                    var url = ""
                    it.data?.let {
                        if (it.isNotEmpty()){
                            url = it[0].url
                        }
                    }
                    return@Function model.updateBackgroundImage(url)
                })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
//                    dismissLoadingDialog()
                }
                .subscribeWith(object: AppDisposableObserver<AppResponse<Any>>(){
                    override fun onSuccess(t: AppResponse<Any>) {
                        dismissLoadingDialog()
                        getEmployeeInfo()
                    }

                    override fun onFail(e: Throwable) {
                        super.onFail(e)
                        dismissLoadingDialog()
                    }
                })
        )
    }



}