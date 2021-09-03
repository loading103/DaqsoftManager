package com.daqsoft.module_project.activity

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.module_project.BR
import com.daqsoft.module_project.R
import com.daqsoft.module_project.databinding.ActivityProjectAccountBinding
import com.daqsoft.module_project.repository.pojo.vo.AccountBackBean
import com.daqsoft.module_project.viewmodel.ProjectAccountViewModel
import com.daqsoft.module_project.widget.AccountAddView
import com.daqsoft.module_project.widget.AccountTypePopup
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.tools.ToastUtils
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.item_account_add.view.*
import kotlinx.android.synthetic.main.layout_popup_account.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


/**
 * @describe 记账本
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Project.PAGER_PROJECT_ACCOUNT)
class ProjectAccountActivity : AppBaseActivity<ActivityProjectAccountBinding, ProjectAccountViewModel>() {

    @JvmField
    @Autowired
    var  arraydata : ArrayList<AccountBackBean>? = null

    lateinit var timePicker : TimePickerView
    /**
     * 选择费用类型
     */
    val accountTypePopup : AccountTypePopup by lazy { AccountTypePopup(this, "选择费用类型") }


    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_project_account
    }

    override fun initViewModel(): ProjectAccountViewModel? {
        return viewModels<ProjectAccountViewModel>().value
    }

    override fun initView() {
        super.initView()
        initClickEvent()
    }

    /**
     * 获取费用类型点击事件
     */
    override fun initData() {
        super.initData()
        viewModel.getMoneyType()
        if(!arraydata.isNullOrEmpty()){
            binding.tvMoney.text = "¥ ${arraydata!![0].totleMoney}"
            binding.tvTime.text = arraydata!![0].time
            arraydata?.forEachIndexed { index, bean ->
                if(index>0){
                    binding.llContain.addOneView()
                }
                binding.llContain.getChildAt(index).findViewById<EditText>(R.id.et_money).setText(bean.money)
                binding.llContain.getChildAt(index).findViewById<TextView>(R.id.et_yt).text=bean.costUse
                binding.llContain.getChildAt(index).findViewById<TextView>(R.id.et_type).text=bean.costType
                binding.llContain.getChildAt(index).tag = bean.itemValue
            }
        }else{
            binding.tvTime.text = stampToTime(System.currentTimeMillis().toString())
        }
    }
    /**
     * 点击监听事件
     */
    private fun initClickEvent() {
        AccountTypeChooseOnclick()

        binding.tvAdd.setOnClickListener {
            binding.llContain.addOneView()
            // 新建的View需要添加点击事件
            AccountTypeChooseOnclick()
        }

        binding.llTime.setOnClickListener {
            showDatePicker()
        }

        // 总金额变化
        binding.llContain.lisitener=object : AccountAddView.MoneyChangedListener{
            override fun MoneyChangeder(number: String) {
                binding.tvMoney.setText("¥ $number")
            }
        }


        binding.llTime.setOnClickListener {
            showDatePicker()
        }
        binding.tvEnsure.setOnClickListener {
            handleDataBack()
        }
    }


    /**
     * 选择费用类型点击事件
     */
    private fun AccountTypeChooseOnclick() {
        for (index in 0 until binding.llContain.childCount) {
            binding.llContain.getChildAt(index).findViewById<TextView>(R.id.et_type).setOnClickListener {
                showNoticeTypePopup(it as  TextView,index)

                var position = -1
                val costType = binding.llContain.getChildAt(index).findViewById<TextView>(R.id.et_type).text
                viewModel.typeLiveData.value?.forEachIndexed { typeIndex, moneyTypeBean ->
                    if (moneyTypeBean.itemName == costType){
                        position = typeIndex
                        return@forEachIndexed
                    }
                }
                if(position >=0){
                    accountTypePopup.setSelectedPosition(position)
                }

            }
        }
    }
    /**
     * 时间选择器
     */
    fun showDatePicker() {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        val selectedDate = Calendar.getInstance() //系统当前时间
        // 正确设置方式 原因：注意事项有说明
        startDate[1970, 1] = 1
        endDate[2030, 12] = 31
        timePicker = TimePickerBuilder(this,
            OnTimeSelectListener { date, v ->
                val stampToTime = stampToTime(date.time.toString())
                binding.tvTime.text=stampToTime
                binding.tvTime.setTextColor(resources.getColor(R.color.color_333333))

            }).setTimeSelectChangeListener { }
            .addOnCancelClickListener { }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isDialog(true)
            // 默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5)
            // 若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setCancelText("取消")
            // 取消按钮文字
            .setSubmitText("确认")
            .setDate(selectedDate)
            // 确认按钮文字
            .setTitleSize(20)
            // 标题文字大小
            .setOutSideCancelable(true)
            // 点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true)
            // 是否循环滚动
            .setTitleBgColor(-0xa0a0b)
            // 标题背景颜色 Night mode
            .setSubmitColor(
               resources.getColor(R.color.red_fa4848)
            ) //确定按钮文字颜色
            .setCancelColor(
                resources.getColor(R.color.color_333333)
            )
            // 取消按钮文字颜色
            .setRangDate(startDate, endDate)
            // 起始终止年月日设定
            .setLabel("年", "月", "日", null, null, null)
            // 是否只显示中间选中项的label文字，false则每项item全部都带有label。
            .isCenterLabel(false)
            .build()
        val mDialog = timePicker.dialog
        if (mDialog != null) {
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            )
            params.leftMargin = 0
            params.rightMargin = 0
            timePicker.dialogContainerLayout.layoutParams = params
            val dialogWindow = mDialog.window
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim)
                dialogWindow.setGravity(Gravity.BOTTOM)
                dialogWindow.setDimAmount(0.3f)
            }
        }
        timePicker?.show()
    }

    fun stampToTime(stamp: String): String {
         val   type = "yyyy-MM-dd"
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }


    /**
     * 费用类型
     */
    private fun showNoticeTypePopup(it: TextView, index: Int) {
        XPopup
            .Builder(this)
            .asCustom(accountTypePopup.apply {
                setItemOnClickListener(object : AccountTypePopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        it.text=viewModel.typeLiveData.value!![position].itemName
                        binding.llContain.getChildAt(index).tag = viewModel.typeLiveData.value!![position].itemValue
                        setSelectedPosition(position)
                    }
                })
                setData(viewModel.typeLiveData.value?.map { it.itemName }?.toMutableList() ?: arrayListOf())
            })
            .show()
    }

    /**
     * 确认传回数据
     */
    private fun handleDataBack() {
        if(binding.tvTime.text=="请选择"){
            ToastUtils.s(this,"请选择时间")
            return
        }
        var datas= arrayListOf<AccountBackBean>()
        for (index in 0 until binding.llContain.childCount) {
            if(TextUtils.isEmpty( binding.llContain.getChildAt(index).et_money.text.toString()) ){
                ToastUtils.s(this,"请输入金额")
                return
            }
            if(TextUtils.isEmpty(binding.llContain.getChildAt(index).et_yt.text)){
                ToastUtils.s(this,"请输入资金用途")
                return
            }
            if(binding.llContain.getChildAt(index).et_type.text=="请选择"){
                ToastUtils.s(this,"请选择费用类型")
                return
            }
            var bean=AccountBackBean()
            bean.itemValue = binding.llContain.getChildAt(index).tag.toString()
            bean.time=binding.tvTime.text.toString()
            bean.totleMoney=binding.tvMoney.text.toString().substring(0,binding.tvMoney.text.toString().length)
            bean.costType=binding.llContain.getChildAt(index).et_type.text.toString()
            bean.costUse=binding.llContain.getChildAt(index).et_yt.text.toString()
            bean.money=binding.llContain.getChildAt(index).et_money.text.toString()
            datas.add(bean)
        }

        Log.e("----------",Gson().toJson(datas))
        LiveEventBus.get(LEBKeyGlobal.PROJICT_ACCOUNT_DATA).post(datas)
        finish()
    }

}