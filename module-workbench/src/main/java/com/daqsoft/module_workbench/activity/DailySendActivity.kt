package com.daqsoft.module_workbench.activity

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.base.AppBaseActivity
import com.daqsoft.library_base.router.ARouterPath
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_workbench.BR
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.databinding.ActivitySendDailyBinding
import com.daqsoft.module_workbench.repository.pojo.vo.DialyProjec
import com.daqsoft.module_workbench.viewmodel.DailySendViewModel
import com.daqsoft.module_workbench.widget.ProjectTypesPopup
import com.lxj.xpopup.XPopup
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

/**
 * @describe  日报发送
 */
@AndroidEntryPoint
@Route(path = ARouterPath.Workbench.PAGER_DAILY_SEND)
class DailySendActivity : AppBaseActivity<ActivitySendDailyBinding, DailySendViewModel>() {

    @JvmField
    @Autowired
    var id : String = ""


    lateinit var timePicker : TimePickerView

    private var firstindex =0

    val projectTypePopup : ProjectTypesPopup by lazy { ProjectTypesPopup(this, "所属项目") }

    // 默认选中类型
    lateinit var chooseType : DialyProjec

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initContentView(savedInstanceState: Bundle?): Int {
        return R.layout.activity_send_daily
    }

    override fun initViewModel(): DailySendViewModel? {
        return viewModels<DailySendViewModel>().value
    }

    override fun initView() {
        super.initView()
        AppUtils.setPricePointWithInteger(binding.etGs,1,1)
    }

    /**
     * 获取费用类型点击事件
     */
    override fun initData() {
        super.initData()
        viewModel.id=id
        viewModel.getDailyProject()
    }
    /**
     * 点击监听事件
     */
    override fun initViewObservable() {
        super.initViewObservable()
        viewModel.timeLiveData.observe(this, androidx.lifecycle.Observer {
            showDatePicker()
        })

        viewModel.nameLiveData.observe(this, androidx.lifecycle.Observer {
            showTypePopup()
        })

        viewModel.typeLiveData.observe(this, androidx.lifecycle.Observer {
            it?.forEachIndexed { index, bean ->
                if(bean.id==id){
                    firstindex=index
                    projectTypePopup?.setSelectedPosition(bean)
                    viewModel.nameObservable.set(bean.projectName)
                    chooseType=bean
                }
            }

        })

}

/**
 * 所属项目选择
 */
private fun showTypePopup() {

    XPopup
        .Builder(this)
        .moveUpToKeyboard(false)
        .asCustom(projectTypePopup.apply {
            setItemOnClickListener(object : ProjectTypesPopup.ItemOnClickListener {
                override fun onClick(position: Int, content: DialyProjec) {
                    Timber.e("position $position  content $content")
                    viewModel.id=content.id
                    viewModel.nameObservable.set(content.projectName)
                }
            })
            viewModel.typeLiveData.value?.toMutableList()?.let { setData(it,chooseType) }
        })
        .show()
}

/**
 * 时间选择器
 */
fun showDatePicker() {
    val startDate = Calendar.getInstance()
    val endDate = Calendar.getInstance()
    val selectedDate = Calendar.getInstance() //系统当前时间
    //正确设置方式 原因：注意事项有说明
    startDate[1970, 1] = 1
    endDate[2030, 12] = 31
    timePicker = TimePickerBuilder(this,
        OnTimeSelectListener { date, v ->
            val stampToTime = viewModel.stampToTime(date.time.toString())
            viewModel.timeObservable.set(stampToTime)

        }).setTimeSelectChangeListener { }
        .addOnCancelClickListener { }
        .setType(booleanArrayOf(true, true, true, false, false, false))
        .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
        .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
        .setLineSpacingMultiplier(2.0f)
        .isAlphaGradient(true)
        .setCancelText("取消") //取消按钮文字
        .setSubmitText("确认") //确认按钮文字
        .setDate(selectedDate)
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




}