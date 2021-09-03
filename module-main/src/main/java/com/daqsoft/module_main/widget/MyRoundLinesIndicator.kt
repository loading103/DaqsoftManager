package com.daqsoft.module_main.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.youth.banner.indicator.BaseIndicator

/**
 * @package name：com.daqsoft.module_main.widget
 * @date 21/12/2020 下午 4:53
 * @author zp
 * @describe
 */
class MyRoundLinesIndicator : BaseIndicator {

    private var radius = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val count = config.indicatorSize
        if (count <= 1) {
            return
        }

        radius = Math.max(config.height, config.normalWidth)
        //间距*（总数-1）+选中宽度+默认宽度*（总数-1）
        val width = (count - 1) * config.indicatorSpace + config.selectedWidth + config.normalWidth * (count - 1)
        setMeasuredDimension(width, radius)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val count = config.indicatorSize
        if (count <= 1) {
            return
        }
        var left = 0f
        for (i in 0 until count) {
            mPaint.color = if (config.currentPosition == i) config.selectedColor else config.normalColor
            val indicatorWidth = if (config.currentPosition == i) config.selectedWidth else radius

            if (config.currentPosition == i){
                canvas.drawRoundRect(
                    left,
                    0f,
                    left + indicatorWidth,
                    radius.toFloat(),
                    config.radius.toFloat(),
                    config.radius.toFloat(),mPaint)
            }else{
                canvas.drawCircle(left + (radius / 2), (radius / 2 ).toFloat(), (radius / 2 ).toFloat(), mPaint)
            }

            left += indicatorWidth + config.indicatorSpace.toFloat()
        }

    }

}