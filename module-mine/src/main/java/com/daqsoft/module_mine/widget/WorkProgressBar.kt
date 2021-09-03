package com.daqsoft.module_mine.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.sp
import com.daqsoft.module_mine.R
import kotlin.math.abs
import kotlin.math.max


/**
 * @package name：com.daqsoft.module_mine.widget
 * @date 13/11/2020 下午 4:19
 * @author zp
 * @describe
 */
class WorkProgressBar : View {


    /**
     * 进度背景宽
     */
    var progressBackgroundWidth = 4.dp

    /**
     * 进度背景色
     */
    var progressBackgroundColor = resources.getColor(R.color.blue_438eff)

    /**
     * 进度条宽
     */
    var progressWidth = 4.dp

    /**
     * 进度颜色
     */
    var progressColor = resources.getColor(R.color.white_ffffff)

    /**
     * 当前进度
     */
    private var currentProgress  = 0

    /**
     * 文字颜色
     */
    var textColor = resources.getColor(R.color.white_ffffff)

    /**
     * 文字内容
     */
    var textContent = "0"

    /**
     * 固定文字
     */
    var fixedContent = resources.getString(R.string.module_mine_total_time)

    /**
     * 固定文字颜色
     */
    var fixedColor = resources.getColor(R.color.white_ffcec9)


    private lateinit var timePaint : Paint
    private lateinit var unitPaint : Paint
    private lateinit var fixedPaint : Paint
    private lateinit var progressBackgroundPaint : Paint
    private lateinit var progressPaint : Paint


    constructor(context: Context?) : super(context){
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context,attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView(context,attrs)
    }


    private fun initView(context: Context?, attrs: AttributeSet?){
        val array = getContext().obtainStyledAttributes(attrs, R.styleable.WorkProgressBar)
        progressBackgroundWidth = array.getDimensionPixelSize(R.styleable.WorkProgressBar_progress_background_width, 4.dp)
        progressBackgroundColor = array.getColor(R.styleable.WorkProgressBar_progress_background_color, resources.getColor(R.color.blue_438eff))
        progressWidth = array.getDimensionPixelSize(R.styleable.WorkProgressBar_progress_width, 4.dp)
        progressColor = array.getColor(R.styleable.WorkProgressBar_progress_color, resources.getColor(R.color.white_ffffff))
        textColor = array.getColor(R.styleable.WorkProgressBar_progress_color, resources.getColor(R.color.white_ffffff))
        textContent = array.getString(R.styleable.WorkProgressBar_text_content)?:""
        fixedContent = array.getString(R.styleable.WorkProgressBar_fixed_content)?:resources.getString(R.string.module_mine_total_time)
        fixedColor = array.getColor(R.styleable.WorkProgressBar_fixed_color, resources.getColor(R.color.white_ffcec9))
        array.recycle()


        initPaint()
    }

    private fun initPaint() {

        // 绘制时间
        timePaint = Paint()
        timePaint.color = textColor
        timePaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        timePaint.textAlign = Paint.Align.CENTER
        timePaint.style = Paint.Style.FILL
        timePaint.textSize = 18.sp.toFloat()

        // 绘制单位
        unitPaint = Paint()
        unitPaint.color = textColor
        unitPaint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
        unitPaint.textAlign = Paint.Align.CENTER
        unitPaint.style = Paint.Style.FILL
        unitPaint.textSize = 12.sp.toFloat()

        // 绘制固定文字
        fixedPaint =  Paint()
        fixedPaint.color = fixedColor
        fixedPaint.typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
        fixedPaint.textAlign = Paint.Align.CENTER
        fixedPaint.style = Paint.Style.FILL
        fixedPaint.textSize = 11.sp.toFloat()


        //绘制背景圆
        progressBackgroundPaint = Paint()
        progressBackgroundPaint.isAntiAlias = true
        progressBackgroundPaint.strokeWidth = progressBackgroundWidth.toFloat()
        progressBackgroundPaint.style = Paint.Style.STROKE
        progressBackgroundPaint.color = progressBackgroundColor
        progressBackgroundPaint.strokeCap = Paint.Cap.ROUND

        //绘制当前进度
        progressPaint = Paint()
        progressPaint.isAntiAlias = true
        progressPaint.style = Paint.Style.STROKE
        progressPaint.strokeWidth = progressWidth.toFloat()
        progressPaint.color = progressColor
        progressPaint.strokeCap = Paint.Cap.ROUND
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        onDrawProgress(canvas)
        onDrawText(canvas)
    }


    /**
     * 绘制文字
     * @param canvas Canvas?
     */
    private fun onDrawText(canvas: Canvas?){

        val unitWidth = unitPaint.measureText("h")
        // 绘制时间
        val timeWidth = timePaint.measureText(textContent)
        val timeX =  ( width - unitWidth) / 2
        val timeFontMetrics: Paint.FontMetrics = timePaint.fontMetrics
        val timeY: Float = (height - (max(progressBackgroundWidth, progressWidth) * 2)) * 0.35f + (abs(timeFontMetrics.ascent) - timeFontMetrics.descent) / 2
        canvas?.drawText(textContent, timeX, timeY, timePaint)

        // 绘制单位

        val unitX = timeX + timeWidth / 2 + unitWidth / 2
        val unitY: Float = timeY
        canvas?.drawText("h", unitX, unitY, unitPaint)

        // 绘制固定文字
        val fixedWidth = fixedPaint.measureText(fixedContent)
        val fixedX =  ( width  ) / 2 .toFloat()
        val fixedFontMetrics: Paint.FontMetrics = fixedPaint.fontMetrics
        val fixedY: Float = (height - (max(progressBackgroundWidth, progressWidth) * 2)) * 0.75f  + (abs(fixedFontMetrics.ascent) - fixedFontMetrics.descent) / 2
        canvas?.drawText(fixedContent, fixedX, fixedY, fixedPaint)

    }


    /**
     * 绘制进度
     * @param canvas Canvas?
     */
    private fun onDrawProgress(canvas: Canvas?){
        //绘制背景圆
        val rectF = RectF(
            (progressBackgroundWidth / 2).toFloat(),
            (progressBackgroundWidth / 2).toFloat(),
            (width - progressBackgroundWidth / 2).toFloat(),
            (height - progressBackgroundWidth / 2).toFloat()
        )
        canvas?.drawArc(rectF, 0f, 360f, false, progressBackgroundPaint)

        currentProgress = 50

        //绘制当前进度
        val sweepAngle: Int = 360 * currentProgress / 100
        canvas?.drawArc(rectF, -90F, sweepAngle.toFloat(), false, progressPaint)
    }

    /**
     * 设置进度
     * @param progress Int
     */
    fun setProgress(progress: Int){
        this.currentProgress = progress
        invalidate()
    }

    /**
     * 设置内容
     * @param content String
     */
    fun setText(content: String){
        this.textContent = content
        invalidate()
    }
}