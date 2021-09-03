package com.daqsoft.module_project.widget

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.global.LEBKeyGlobal
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.AccountBackBean
import com.daqsoft.module_project.repository.pojo.vo.MoneyTypeBean
import com.google.gson.Gson
import com.jeremyliao.liveeventbus.LiveEventBus
import com.luck.picture.lib.tools.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import kotlinx.android.synthetic.main.item_account_add.view.*
import kotlinx.android.synthetic.main.layout_popup_account.view.*
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

/**
 * 记账本
 */
class ProjectAccountPopup  : BottomPopupView {

    lateinit var mcontext:Context
    lateinit var timePicker : TimePickerView
    lateinit var datas :MutableList<MoneyTypeBean>
    /**
     * 选择费用类型
     */
    lateinit var accountTypePopup : AccountTypePopup

    constructor(context: Context) : super(context)

    constructor(context: Context, title: String) : super(context) {
        this.mcontext=context
        this.titleContent = title
    }


    private var titleContent: String = ""

    private var title: TextView? = null




    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_account
    }

    override fun initPopupContent() {
        super.initPopupContent()
        initView()
        initData()
        initClick()
    }


    private fun initView() {
        accountTypePopup=AccountTypePopup(mcontext, "选择费用类型")


    }

    private fun initData() {

    }


    private fun initClick() {
        AccountTypeChooseOnclick()

        tv_add.setOnClickListener {
           ll_contain.addOneView()
            // 新建的View需要添加点击事件
            AccountTypeChooseOnclick()
        }

       ll_time.setOnClickListener {
            showDatePicker()
        }

        // 总金额变化
        ll_contain.lisitener=object : AccountAddView.MoneyChangedListener{
            override fun MoneyChangeder(number: String) {
               tv_money.setText("¥ $number")
            }
        }


        ll_time.setOnClickListener {
            showDatePicker()
        }
       tv_ensure.setOnClickListener {
            handleDataBack()
        }
    }



    fun setData(data:MutableList<MoneyTypeBean>) {
        this.datas = data
    }
    /**
     * 选择费用类型点击事件
     */
    private fun AccountTypeChooseOnclick() {
        for (index in 0 until ll_contain.childCount) {
            ll_contain.getChildAt(index).findViewById<TextView>(R.id.et_type).setOnClickListener {
                showNoticeTypePopup(it as  TextView,index)
            }
        }
    }
    /**
     * 时间选择器
     */
    fun showDatePicker() {
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        //正确设置方式 原因：注意事项有说明
        startDate[1970, 1] = 1
        endDate[2030, 12] = 31
        timePicker = TimePickerBuilder(mcontext,
            OnTimeSelectListener { date, v ->
                val stampToTime = stampToTime(date.time.toString())
                tv_time.text=stampToTime
                tv_time.setTextColor(resources.getColor(R.color.color_333333))

            }).setTimeSelectChangeListener { }
            .addOnCancelClickListener { }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
            .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
            .setLineSpacingMultiplier(2.0f)
            .isAlphaGradient(true)
            .setCancelText("取消") //取消按钮文字
            .setSubmitText("确认") //确认按钮文字
            .setTitleSize(20) //标题文字大小
            .setOutSideCancelable(true) //点击屏幕，点在控件外部范围时，是否取消显示
            .isCyclic(true) //是否循环滚动
            .setTitleBgColor(-0xa0a0b) //标题背景颜色 Night mode
            .setSubmitColor(
                resources.getColor(R.color.red_fa4848)
            ) //确定按钮文字颜色
            .setCancelColor(
                resources.getColor(R.color.color_333333)
            ) //取消按钮文字颜色
            .setRangDate(startDate, endDate) //起始终止年月日设定
            .setLabel("年", "月", "日", null, null, null) //默认设置为年月日时分秒
            .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
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
                dialogWindow.setWindowAnimations(R.style.picker_view_slide_anim) //修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM) //改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f) //值设为0则为完全透明
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
            .Builder(mcontext)
            .asCustom(accountTypePopup.apply {
                setItemOnClickListener(object : AccountTypePopup.ItemOnClickListener {
                    override fun onClick(position: Int, content: String) {
                        Timber.e("position $position  content $content")
                        it.text=datas[position].itemName
                        setSelectedPosition(position)
                    }
                })
                setData(datas?.map { it.itemName }?.toMutableList() ?: arrayListOf())
            })
            .show()
    }

    /**
     * 确认传回数据
     */
    private fun handleDataBack() {
        if(tv_time.text=="请选择"){
            ToastUtils.s(mcontext,"请选择时间")
            return
        }
        if(TextUtils.isEmpty(tv_money.text)){
            ToastUtils.s(mcontext,"请输入金额")
            return
        }
        var datas= ArrayList<AccountBackBean>()
        for (index in 0 until ll_contain.childCount) {
            if(TextUtils.isEmpty(ll_contain.et_yt.text)){
                ToastUtils.s(mcontext,"请输入资金用途")
                return
            }
            if(ll_contain.et_type.text=="请选择"){
                ToastUtils.s(mcontext,"请选择费用类型")
                return
            }
            var bean= AccountBackBean()
            bean.time=tv_time.text.toString()
            bean.totleMoney=tv_money.text.toString().substring(0,tv_money.text.toString().length)
            bean.costType=ll_contain.getChildAt(index).findViewById<TextView>(R.id.et_type).text.toString()
            bean.costUse=ll_contain.getChildAt(index).findViewById<EditText>(R.id.et_yt).text.toString()
            bean.money=ll_contain.getChildAt(index).findViewById<EditText>(R.id.et_money).text.toString()
            datas.add(bean)
        }
        Log.e("----------", Gson().toJson(datas))
        LiveEventBus.get(LEBKeyGlobal.PROJICT_ACCOUNT_DATA).post(datas)
        dismiss()
    }
}