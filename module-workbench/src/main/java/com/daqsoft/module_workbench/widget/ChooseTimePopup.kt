package com.daqsoft.module_workbench.widget

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.daqsoft.library_base.utils.ScaleTransitionPagerTitleView
import com.daqsoft.module_workbench.R
import com.daqsoft.module_workbench.repository.pojo.vo.DialyProjec
import com.daqsoft.mvvmfoundation.utils.ToastUtils
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.layout_popup_choose_time.view.*
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView
import java.text.SimpleDateFormat
import java.util.*

/**
 * @describe 日报选择时间
 */
class ChooseTimePopup  : BottomPopupView {

    var datas = arrayListOf("按月", "按日")
    var fragments : MutableList<Fragment> = mutableListOf()

    var year: String=""
    var month: String=""
    var day: String=""
    private var pvDayTime: TimePickerView? = null
    private var pvMonthTime: TimePickerView? = null
    constructor(
        context: Context
    ) : super(context)



    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_choose_time
    }


    private var finished=false
    override fun initPopupContent() {
        super.initPopupContent()
        magic_indicator.navigator=setNaviga(context, datas)
        frame_month.visibility=View.VISIBLE
        tv_cancle.setOnClickListener { dismiss() }
        tv_ensure.setOnClickListener {
            finished=true
            if(frame_month?.visibility==View.VISIBLE){
                pvMonthTime?.returnData()
            }else{
                if(tv_start.text.isNullOrBlank()){
                    ToastUtils.showLong("请选择开始时间")
                    return@setOnClickListener
                }
                if(tv_end.text.isNullOrBlank()){
                    ToastUtils.showLong("请选择结束时间")
                    return@setOnClickListener
                }

                itemClickListener?.chooseTime(tv_start.text.toString(),tv_end.text.toString());
            }
            Handler().postDelayed({
                if(finished){
                    dismiss()
                }
            },200)
        }
        tv_start.setOnClickListener {
            tv_start.isSelected=true
            tv_end.isSelected=false
        }

        tv_end.setOnClickListener {
            tv_end.isSelected=true
            tv_start.isSelected=false
        }
        tv_start.isSelected=true
        initMonthTimePicker()
        initDayTimePicker()
    }



    fun setData(){

    }


    override fun onDismiss() {
        super.onDismiss()
        dissmissListerer?.dissmissListerer()
    }

    override fun getMaxHeight(): Int {
        return (XPopupUtils.getScreenHeight(context)*.65f).toInt()
    }

    /**
     * 设置选中位置
     */
    fun setSelectedPosition(bean: DialyProjec?){

    }



    interface ItemOnClickListener{
        fun onClick(position: Int,content : DialyProjec)
    }

    fun setNaviga(context: Context, datas: List<String>) :CommonNavigator{
        val commonNavigator = CommonNavigator(context)
        commonNavigator.isAdjustMode=true
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return datas?.size ?: 0
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView: SimplePagerTitleView = ScaleTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.textSize = 16f
                colorTransitionPagerTitleView.normalColor =  context.resources.getColor(R.color.gray_666666)
                colorTransitionPagerTitleView.selectedColor = context.resources.getColor(R.color.color_333333)
                colorTransitionPagerTitleView.text = datas[index]
                colorTransitionPagerTitleView.setOnClickListener{
                    if(index==0){
                        frame_month.visibility=View.VISIBLE
                        frame_day.visibility=View.GONE
                        ll_root.visibility=View.GONE
                    }else{
                        frame_day.visibility=View.VISIBLE
                        frame_month.visibility=View.GONE
                        ll_root.visibility=View.VISIBLE
                    }
                    magic_indicator.onPageSelected(index);
                    magic_indicator.onPageScrolled(index, 0.0F, 0);
                }
                return colorTransitionPagerTitleView
            }
            override fun getIndicator(context: Context): IPagerIndicator {
                return LinePagerIndicator(context).apply {
                    mode = LinePagerIndicator.MODE_EXACTLY
                    lineHeight = UIUtil.dip2px(context, 3.0).toFloat()
                    lineWidth = UIUtil.dip2px(context, 20.0).toFloat()
                    roundRadius = UIUtil.dip2px(context, 6.0).toFloat()
                    startInterpolator = AccelerateInterpolator()
                    endInterpolator = DecelerateInterpolator(2.0f)
                    setColors(context.resources.getColor(R.color.red_fa4848))
                }
            }
        }
        return commonNavigator;
    }

    private fun initMonthTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        val selectedDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        startDate[2013, 0] = 23
        val endDate = Calendar.getInstance()
        endDate[2029, 11] = 28
        //时间选择器
        pvMonthTime =TimePickerBuilder(context,
            OnTimeSelectListener { date, v -> //选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/

                val current: Long = System.currentTimeMillis()
                val zero: Long = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().rawOffset +(1000 * 3600 * 24)
                if(date.time>zero){
                    ToastUtils.showLong("时间不能大于今天")
                    finished=false
                    return@OnTimeSelectListener
                }
                val stampToTime = stampToTime(date.time.toString(),"yyyy.MM")
                itemClickListener?.chooseTime(stampToTime,"");

            })
            .setLayoutRes(R.layout.pickerview_custom_time) { v ->
            }
            .setType(booleanArrayOf(true, true, false, false, false, false))
            .setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
            .setDividerColor(Color.DKGRAY)
            .setContentTextSize(20)
            .setDate(selectedDate)
            .setRangDate(startDate, selectedDate)
            .setDecorView(frame_month) //非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
            .setOutSideColor(0x00000000)
            .setOutSideCancelable(false)
            .build()
        pvMonthTime?.show()
    }


    private fun initDayTimePicker() {
        //控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        val selectedDate = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        startDate[2013, 0] = 23
        val endDate = Calendar.getInstance()
        endDate[2029, 11] = 28
        //时间选择器
        pvDayTime = TimePickerBuilder(context,
            OnTimeSelectListener { date, v -> //选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                val stampToTime = stampToTime(date.time.toString(),"yyyy.MM.dd")

                if(tv_start.isSelected){
                    if(!tv_end.text.isNullOrBlank()){
                        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
                        val time = simpleDateFormat.parse(tv_start.text.toString()).time
                        if(time<date.time){
                            ToastUtils.showLong("开始时间不能大于结束时间")
                            return@OnTimeSelectListener
                        }
                    }
                    tv_start.text=stampToTime
                }

                if(tv_end.isSelected){
                    if(!tv_start.text.isNullOrBlank()){
                        val simpleDateFormat = SimpleDateFormat("yyyy.MM.dd")
                        val time = simpleDateFormat.parse(tv_start.text.toString()).time
                        if(time>date.time){
                            ToastUtils.showLong("开始时间不能大于结束时间")
                            return@OnTimeSelectListener
                        }
                    }
                    tv_end.text=stampToTime
                }


            })
            .setTimeSelectChangeListener {
                val current: Long = System.currentTimeMillis()
                val zero: Long = current / (1000 * 3600 * 24) * (1000 * 3600 * 24) - TimeZone.getDefault().rawOffset +(1000 * 3600 * 24)
                if(it.time>zero){
                    ToastUtils.showLong("时间不能大于今天")
                    finished=false
                    return@setTimeSelectChangeListener
                }
                pvDayTime?.returnData()
            }
            .setLayoutRes(R.layout.pickerview_custom_time) { v ->
            }
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .setLabel("年", "月", "日", "", "", "") //设置空字符串以隐藏单位提示   hide label
            .setDividerColor(Color.DKGRAY)
            .setContentTextSize(20)
            .setDate(selectedDate)
            .setRangDate(startDate, selectedDate)
            .setDecorView(frame_day) //非dialog模式下,设置ViewGroup, pickerView将会添加到这个ViewGroup中
            .setOutSideColor(0x00000000)
            .setOutSideCancelable(false)
            .build()
        pvDayTime?.show()
    }




    fun stampToTime(stamp: String,type: String): String {
        val simpleDateFormat = SimpleDateFormat(type)
        val lt: Long = stamp.toLong()
        val date = Date(lt)
        return simpleDateFormat.format(date)
    }


    interface  ChooseListerer{
        fun  chooseTime(startTime: String,endTime :String)

    }
    private var itemClickListener : ChooseListerer? = null

    fun setOnChooseListerer(listener: ChooseListerer){
        this.itemClickListener = listener
    }

    interface  DissmissListerer{
        fun  dissmissListerer()

    }
    private var dissmissListerer : DissmissListerer? = null

    fun setOnDissmissListerer(dissmissListerer: DissmissListerer){
        this.dissmissListerer = dissmissListerer
    }
}