package com.daqsoft.module_workbench.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.hilt.lifecycle.ViewModelInject
import com.daqsoft.library_base.global.ConstantGlobal
import com.daqsoft.library_base.net.AppResponse
import com.daqsoft.library_base.toolbar.ToolbarViewModel
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.WorkBenchRepository
import com.daqsoft.module_workbench.repository.pojo.vo.PaySlipDetail
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.PaySlipDetailItemViewModel
import com.daqsoft.module_workbench.viewmodel.itemviewmodel.PaySlipDetailTitleViewModel
import com.daqsoft.mvvmfoundation.base.MultiItemViewModel
import com.daqsoft.library_base.net.AppDisposableObserver
import com.daqsoft.mvvmfoundation.utils.RxUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.tatarka.bindingcollectionadapter2.ItemBinding
import java.lang.StringBuilder

/**
 * @package name：com.daqsoft.module_workbench.viewmodel
 * @date 10/12/2020 上午 10:02
 * @author zp
 * @describe
 */
class PaySlipDetailViewModel : ToolbarViewModel<WorkBenchRepository> {

    @ViewModelInject
    constructor(application: Application,workBenchRepository: WorkBenchRepository):super(application,workBenchRepository)

    val detail = ObservableField<PaySlipDetail>()
    val department = ObservableField<String>()
    val jobNumber = ObservableField<String>()


    override fun onCreate() {
        initToolbar()
    }

    private fun initToolbar() {
        setBackground(Color.TRANSPARENT)
    }

    /**
     * 给RecyclerView添加ObservableList
     */
    var titleObservableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    var currentObservableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    var beforeObservableList: ObservableList<MultiItemViewModel<*>> = ObservableArrayList()
    /**
     * 给RecyclerView添加ItemBinding
     */
    var itemBinding: ItemBinding<MultiItemViewModel<*>> =  ItemBinding.of { itemBinding, position, item ->
        when (item.itemType as String) {
            ConstantGlobal.TITLE -> itemBinding.set(BR.viewModel, R.layout.recyclerview_pay_slip_detail_title)
            ConstantGlobal.ITEM -> itemBinding.set(BR.viewModel, R.layout.recyclerview_pay_slip_detail_item)
            else -> itemBinding.set(BR.viewModel, R.layout.recyclerview_pay_slip_detail_item)
        }
    }


    /**
     * 获取详情
     * @param id String
     * @param secret String
     */
    fun getPaySlipDetail( id: String,secret: String){
        addSubscribe(
            model
                .getPaySlipDetail(id,secret)
                .compose(RxUtils.schedulersTransformer())
                .compose(RxUtils.exceptionTransformer())
                .subscribeWith(object : AppDisposableObserver<AppResponse<PaySlipDetail>>(){
                    override fun onSuccess(t: AppResponse<PaySlipDetail>) {
                        t.data?.let {

                            detail.set(it)
                            setTitleText(it.wageMonth)

                            // 如果模板信息为空直接 return
                            if (it.templateContent.isNullOrBlank()){
                                return@let
                            }

                            // 转换数据
                            val type = object : TypeToken<List<Any?>>() {}.type
                            val coverTemplateContent = Gson().fromJson<List<Any?>>(it.templateContent, type)

                            var coverEmployeeWageContent : List<Any?> ? = null
                            if (!it.employeeWageContent.isNullOrBlank()){
                                coverEmployeeWageContent = Gson().fromJson<List<Any?>>(it.employeeWageContent, type)
                            }

                            var coverWageDifferenceContent : List<Any?>? = null
                            if (!it.wageDifferenceContent.isNullOrBlank()){
                                coverWageDifferenceContent = Gson().fromJson<List<Any?>>(it.wageDifferenceContent, type)
                            }

                            // 提取基本信息
                            val info = coverTemplateContent[0] as List<*>
                            val keys = info[1] as List<String>
                            var values: List<Any?> ? = null
                            if(!coverEmployeeWageContent.isNullOrEmpty()){
                                values = coverEmployeeWageContent.subList(0,keys.size)
                            }
                            val map = if (values.isNullOrEmpty()){
                                keys.associateBy({it},{""})
                            }else{
                                keys.mapIndexed { index, s -> s to values[index] }.toMap()
                            }
                            jobNumber.set((map["员工工号"]?:"").toString())
                            val sb = StringBuilder()
                            val center = map["所属中心"]?:""
                            val branch = map["所属部门"]?:""
                            sb.append(center)
                            if (sb.isNotBlank()){
                                sb.append("-")
                            }
                            sb.append(branch)
                            department.set(sb.toString())
                            // 添加title数据
                            titleObservableList.add(PaySlipDetailTitleViewModel(this@PaySlipDetailViewModel,"").apply { multiItemType(ConstantGlobal.TITLE) })
                            coverTemplateContent.subList(1,coverTemplateContent.size).forEach{
                                titleObservableList.add(PaySlipDetailTitleViewModel(this@PaySlipDetailViewModel, if (it == null) "" else (it as List<*>)[0].toString()).apply { multiItemType(ConstantGlobal.TITLE) })
                            }
                            // 添加本月数据
                            currentObservableList.add(PaySlipDetailTitleViewModel(this@PaySlipDetailViewModel,"本月").apply { multiItemType(ConstantGlobal.TITLE) })
                            if (coverEmployeeWageContent.isNullOrEmpty()){
                                coverTemplateContent.subList(1,coverTemplateContent.size).forEach{
                                    currentObservableList.add(PaySlipDetailItemViewModel(this@PaySlipDetailViewModel,  "").apply { multiItemType(ConstantGlobal.ITEM) })
                                }
                            }else{
                                coverEmployeeWageContent.subList(keys.size,coverEmployeeWageContent.size).forEach{
                                    currentObservableList.add(PaySlipDetailItemViewModel(this@PaySlipDetailViewModel, it?.toString() ?: "").apply { multiItemType(ConstantGlobal.ITEM) })
                                }
                            }
                            // 添加上月数据
                            beforeObservableList.add(PaySlipDetailTitleViewModel(this@PaySlipDetailViewModel,"对比上月").apply { multiItemType(ConstantGlobal.TITLE) })
                            if (coverWageDifferenceContent.isNullOrEmpty()){
                                coverTemplateContent.subList(1,coverTemplateContent.size).forEach{
                                    beforeObservableList.add(PaySlipDetailItemViewModel(this@PaySlipDetailViewModel,  "",true).apply { multiItemType(ConstantGlobal.ITEM) })
                                }
                            }else{
                                coverWageDifferenceContent.subList(keys.size,coverWageDifferenceContent.size).forEach{
                                    beforeObservableList.add(PaySlipDetailItemViewModel(this@PaySlipDetailViewModel,it?.toString() ?: "",true).apply { multiItemType(ConstantGlobal.ITEM) })
                                }
                            }
                        }
                    }
                })
        )
    }


}