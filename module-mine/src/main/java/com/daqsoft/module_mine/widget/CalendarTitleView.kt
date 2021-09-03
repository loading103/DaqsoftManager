package com.daqsoft.module_mine.widget

import android.content.Context
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.view.Gravity
import androidx.appcompat.widget.AppCompatTextView
import cn.iwgang.simplifyspan.SimplifySpanBuild
import cn.iwgang.simplifyspan.unit.SpecialTextUnit
import com.daqsoft.module_mine.R
import net.lucode.hackware.magicindicator.buildins.UIUtil
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

/**
 * @package name：com.daqsoft.module_mine.widget
 * @date 13/11/2020 上午 10:59
 * @author zp
 * @describe 我的页面 日期指示器
 */
class CalendarTitleView(context: Context) : AppCompatTextView(context) , IMeasurablePagerTitleView {

    //  默认颜色
    var normalColor : Int = context.resources.getColor(R.color.black_666666)

    // 选中颜色
    var selectedColor : Int = context.resources.getColor(R.color.red_fa4848)

    // title
    var titleText : String = ""

    // content
    var contentText : String = ""


    init {
        gravity = Gravity.CENTER
        ellipsize = TextUtils.TruncateAt.END
    }

    override fun onSelected(index: Int, totalCount: Int) {
        this.text = SimplifySpanBuild()
            .append(
                SpecialTextUnit(titleText).setTextSize(14f).setTextColor(selectedColor)
                    .setTextStyle(
                        Typeface.BOLD
                    )
            )
            .append("\n")
            .append(
                SpecialTextUnit(contentText).setTextSize(12f).setTextColor(selectedColor)
                    .setTextStyle(
                        Typeface.NORMAL
                    )
            )
            .build()
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        this.text = SimplifySpanBuild()
            .append(
                SpecialTextUnit(titleText).setTextSize(14f).setTextColor(normalColor).setTextStyle(
                    Typeface.NORMAL
                )
            )
            .append("\n")
            .append(
                SpecialTextUnit(contentText).setTextSize(12f).setTextColor(normalColor)
                    .setTextStyle(
                        Typeface.NORMAL
                    )
            )
            .build()
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {

    }

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {

    }

    override fun getContentLeft(): Int {
        val bound = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 - contentWidth / 2
    }

    override fun getContentTop(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val bound = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 + contentHeight / 2).toInt()
    }


}