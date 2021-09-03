package com.daqsoft.module_mine.activity

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.OptionsPickerBuilder
import com.bigkoo.pickerview.listener.CustomListener
import com.bigkoo.pickerview.listener.OnOptionsSelectListener
import com.bigkoo.pickerview.view.OptionsPickerView
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_mine.BR
import com.daqsoft.module_mine.R
import com.daqsoft.module_mine.databinding.ActivityAddressBinding
import com.daqsoft.module_mine.repository.pojo.vo.City
import com.daqsoft.module_mine.repository.pojo.vo.District
import com.daqsoft.module_mine.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.view_calendar_title.*

/**
 * @package name：com.daqsoft.module_mine.activity
 * @date 19/11/2020 下午 3:27
 * @author zp
 * @describe 修改住址
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Mine.PAGER_ADDRESS)
class AddressActivity : AppBaseActivity<ActivityAddressBinding, AddressViewModel>() {

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_address
    }

    override fun initViewModel(): AddressViewModel? {
        return viewModels<AddressViewModel>().value
    }

    override fun initViewObservable() {
        super.initViewObservable()

        viewModel.cityLiveData.observe(this, Observer {
            if (optionsPickerView == null) {
                showCityPicker(it)
            } else {
                if (!optionsPickerView!!.isShowing) {
                    optionsPickerView!!.show()
                }
            }
        })
    }


    override fun initData() {
        viewModel.getCityTree()
    }

    private var optionsPickerView: OptionsPickerView<String>? = null

    /**
     * 城市选择器
     */
    @SuppressLint("CheckResult")
    fun showCityPicker(city: HashMap<String, String>?){
        optionsPickerView = OptionsPickerBuilder(this,
            OnOptionsSelectListener { options1, option2, options3, v ->
                // 返回的分别是三个级别的选中位置
                val province = viewModel.list[options1]
                var city: City? = null
                var district: District? = null
                val citys = viewModel.list[options1].children
                if (!citys.isNullOrEmpty()) {
                    city = citys[option2]
                    val districts = citys[option2].children
                    if (!districts.isNullOrEmpty()) {
                        district = districts[options3]
                    }
                }
                viewModel.provinceCityDistrict.set("${province.name}${city?.name ?: ""}${district?.name ?: ""}")
                viewModel.update.get()!!.employeeAddressProvince = province.id.toInt()
                viewModel.update.get()!!.employeeAddressCity = city?.id?.toInt() ?: 0
                viewModel.update.get()!!.employeeAddressDistrict = district?.id?.toInt() ?: 0
                viewModel.update.notifyChange()
            })
            .setLayoutRes(R.layout.pickerview_custom_options, object : CustomListener {
                override fun customLayout(v: View) {
                    val determine = v.findViewById<TextView>(R.id.determine)
                    determine.setOnClickListener {
                        optionsPickerView?.returnData()
                        optionsPickerView?.dismiss()
                    }
                    val cancel = v.findViewById<TextView>(R.id.cancel)
                    cancel.setOnClickListener {
                        optionsPickerView?.dismiss()
                    }
                }
            })
            .apply {
                city?.takeIf {
                    it.isNotEmpty()
                }?.let {
                    val provinceIndex = viewModel.provinceList.indexOf(it["province"])
                    val citys = viewModel.cityList[provinceIndex]
                    var cityIndex = 0
                    var districtIndex = 0
                    if (!citys.isNullOrEmpty()){
                        cityIndex = viewModel.cityList[provinceIndex].indexOf(it["city"])
                        val districts = viewModel.districtList[provinceIndex][cityIndex]
                        if (!districts.isNullOrEmpty()){
                            districtIndex = viewModel.districtList[provinceIndex][cityIndex].indexOf(
                                it["district"]
                            )
                        }
                    }
                    this.setSelectOptions(provinceIndex, cityIndex, districtIndex)
                }
            }
            .setDividerColor(resources.getColor(R.color.gray_e2e2e2))
            .setTextColorCenter(resources.getColor(R.color.black_333333))
            .setTextColorOut(resources.getColor(R.color.gray_999999))
            .setLineSpacingMultiplier(2.5f)
            .setItemVisibleCount(5)
            .build<String>()
        optionsPickerView?.setPicker(
            viewModel.provinceList,
            viewModel.cityList,
            viewModel.districtList
        )
        optionsPickerView?.show()
    }

}