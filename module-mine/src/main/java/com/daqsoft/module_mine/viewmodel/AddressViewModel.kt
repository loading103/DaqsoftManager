package com.daqsoft.module_mine.viewmodel

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.ObservableField
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.repository.MineRepository
import com.daqsoft.module_mine.repository.pojo.vo.EmployeeInfoUpdate
import com.daqsoft.module_mine.repository.pojo.vo.Province
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.binding.command.BindingConsumer
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jeremyliao.liveeventbus.LiveEventBus
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import java.io.BufferedReader
import java.io.InputStreamReader


/**
 * @package name：com.daqsoft.module_mine.viewmodel
 * @date 19/11/2020 下午 3:28
 * @author zp
 * @describe 修改住址 viewModel
 */
class AddressViewModel : ToolbarViewModel<MineRepository> {

    var cityLiveData = MutableLiveData<HashMap<String, String>>()
    /**
     * 城市点击事件
     */
    var cityOnClick = BindingCommand<Unit>(BindingAction {
        if (dataPreparation) {
            cityLiveData.value = map
        }
    })

    /**
     * 省市区
     */
    var provinceCityDistrict = ObservableField<String>("")

    /**
     * 详细地址
     */
    var detailedAddress = ObservableField<String>("")

    /**
     * 详细地址输入框 焦点变化
     */
    val detailedFocusChange = BindingCommand<Boolean>(BindingConsumer {
        if (!it) {
            val imm: InputMethodManager? = getApplication<Application>().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    })

    @ViewModelInject
    constructor(application: Application, mineRepository: MineRepository) : super(
        application,
        mineRepository
    )

    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setTitleText(getApplication<Application>().resources.getString(R.string.module_mine_address))
        setRightTextVisible(View.VISIBLE)
        setRightTextColor(R.color.red_fa4848)
        setRightTextTxt(getApplication<Application>().resources.getString(R.string.module_mine_save))

    }

    override fun rightTextOnClick() {
        if (provinceCityDistrict.get().isNullOrBlank()) {
            ToastUtils.showLong(getApplication<Application>().resources.getString(R.string.module_mine_please_choose_city))
            return
        }
        if (detailedAddress.get().isNullOrBlank()) {
            ToastUtils.showLong(getApplication<Application>().resources.getString(R.string.module_mine_please_enter_detailed_address))
            return
        }
        updateEmployeeInfo()
    }


    var dataPreparation = false
    var list :List<Province> = ArrayList()
    var provinceList:MutableList<String> = ArrayList()
    var cityList:MutableList<MutableList<String>> = ArrayList()
    var districtList :MutableList<MutableList<MutableList<String>>>  = ArrayList()
    /**
     * 获取城市树
     */
    fun getCityTree(){
        addSubscribe(
            Observable.create(ObservableOnSubscribe<Any> {
                if (list.isEmpty() && provinceList.isEmpty() && cityList.isEmpty() && districtList.isEmpty()) {
                    val stringBuilder = StringBuilder()
                    val bf = BufferedReader(
                        InputStreamReader(
                            getApplication<Application>().resources.assets.open(
                                "city.json"
                            )
                        )
                    )
                    val read: List<String> = bf.readLines()
                    for (line in read) {
                        stringBuilder.append(line)
                    }
                    stringBuilder.toString()
                    val type = object : TypeToken<MutableList<Province>>() {}.type
                    list = Gson().fromJson<MutableList<Province>>(stringBuilder.toString(), type)
                    list.forEach {
                        provinceList.add(it.name)
                        // 该省的城市列表（第二级）
                        val currentCityList: MutableList<String> = ArrayList()
                        // 该省的所有地区列表（第三级）
                        val currentDistrictList: MutableList<MutableList<String>> = ArrayList()
                        it.children?.forEach {
                            currentCityList.add(it.name)
                            // 该城市的所有地区列表
                            val currentAreaList: MutableList<String> = ArrayList()
                            it.children?.forEach {
                                currentAreaList.add(it.name)
                            }
                            currentDistrictList.add(currentAreaList)
                        }
                        cityList.add(currentCityList)
                        districtList.add(currentDistrictList)
                    }
                }
                it.onNext(1)
            })
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .subscribe { t ->
                    dataPreparation = true
                    getEmployeeToUpdate()
                }
        )
    }

    private val map = hashMapOf<String, String>()
    val update = ObservableField<EmployeeInfoUpdate>()
    fun getEmployeeToUpdate(){
        addSubscribe(
            model
                .getEmployeeToUpdate()
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<EmployeeInfoUpdate>>() {
                    override fun onSuccess(t: AppResponse<EmployeeInfoUpdate>) {
                        t.data?.let { eu ->
                            update.set(eu)
                            detailedAddress.set(eu.employeeAddressDetail)
                            val sb = StringBuilder()
                            list.find { it.id == eu.employeeAddressProvince.toString() }?.let {
                                sb.append(it.name)
                                map["province"] = it.name
                                val citys = it.children
                                if (!citys.isNullOrEmpty()) {
                                    citys.find { it.id == eu.employeeAddressCity.toString() }?.let {
                                        sb.append(it.name)
                                        map["city"] = it.name
                                        val districts = it.children
                                        if (!districts.isNullOrEmpty()) {
                                            districts.find { it.id == eu.employeeAddressDistrict.toString() }
                                                ?.let {
                                                    sb.append(it.name)
                                                    map["district"] = it.name
                                                }
                                        }
                                    }
                                }
                            }
                            provinceCityDistrict.set(sb.toString())
                        }
                    }
                })
        )
    }

    fun updateEmployeeInfo(){

        update.get()!!.employeeBirthdayType = update.get()!!.coverBirthdayType()
        update.get()!!.employeeAddressDetail = detailedAddress.get()!!

        val json = Gson().toJson(update.get()!!)
        val type = object : TypeToken<Map<String, String>>() {}.type
        val params = Gson().fromJson<Map<String, String>>(json, type)
        addSubscribe(
            model
                .updateEmployeeInfo(params)
                .doOnSubscribe {
                    Handler(Looper.getMainLooper()).post {
                        showLoadingDialog()
                    }
                }
                .compose(RxUtils.exceptionTransformer())
                .compose(RxUtils.schedulersTransformer())
                .doFinally {
                    dismissLoadingDialog()
                }
                .subscribeWith(object : AppDisposableObserver<AppResponse<Any>>() {
                    override fun onSuccess(t: AppResponse<Any>) {
                        LiveEventBus.get(LEBKeyGlobal.MODIFY_ADDRESS_KEY)
                            .post("${provinceCityDistrict.get()}${detailedAddress.get()}")
                        finish()
                    }
                })
        )

    }
}

