package com.daqsoft.module_workbench.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.daqsoft.library_base.utils.dp
import com.daqsoft.library_base.utils.sp
import com.daqsoft.module_workbench.R
import java.util.*

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 27/11/2020 上午 10:20
 * @author zp
 * @describe 拼音索引侧边栏
 */
class SideBar : View {

    companion object{

        /**默认字体大小*/
        private const val DEFAULT_TEXT_SIZE = 12
        /**默认偏移*/
        private const val DEFAULT_MAX_OFFSET = 80
        /**默认索引*/
        private val DEFAULT_INDEX_ITEMS = arrayOf(
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z"
        )

        /**对齐方式*/
        private const val TEXT_ALIGN_CENTER = 0
        private const val TEXT_ALIGN_LEFT = 1
        private const val TEXT_ALIGN_RIGHT = 2

        /**位置*/
        private const val POSITION_RIGHT = 0
        private const val POSITION_LEFT = 1
    }

    private var mIndexItems: Array<String> = arrayOf()

    /**当前位置*/
    private var mCurrentIndex = -1

    /**当前坐标*/
    private var mCurrentY = -1f

    private var mPaint: Paint? = null
    private var mNormalTextColor = 0
    private var mSelectedTextColor = 0
    private var mTextSize = 0f

    /**item 高*/
    private var mIndexItemHeight = 0f

    /**当前 item 偏移*/
    private var mMaxOffset = 0f

    /**按下时区域*/
    private val mStartTouchingArea = RectF()
    private var mBarHeight = 0f
    private var mBarWidth = 0f

    /**按下标识域*/
    private var mStartTouching = false

    /**位置*/
    private var mSideBarPosition = 0

    /**对齐方式*/
    private var mTextAlignment = 0

    private var onSelectIndexItemListener: OnSelectIndexItemListener? = null

    /**第一个文字基线*/
    private var mFirstItemBaseLineY = 0f


    constructor(context: Context?) : super(context){
        initView(context, null)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context, attrs)
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        initView(context, attrs)
    }


    private fun initView(context: Context?, attrs: AttributeSet?){
        val typedArray = context!!.obtainStyledAttributes(attrs, R.styleable.SideBar)
        mNormalTextColor = typedArray.getColor(R.styleable.SideBar_sidebar_normal_text_color, resources.getColor(R.color.black_333333))
        mSelectedTextColor = typedArray.getColor(R.styleable.SideBar_sidebar_selected_text_color, resources.getColor(R.color.red_fa4848))
        mTextSize = typedArray.getDimension(
            R.styleable.SideBar_sidebar_text_size,
            DEFAULT_TEXT_SIZE.sp.toFloat()
        )
        mMaxOffset = typedArray.getDimension(
            R.styleable.SideBar_sidebar_max_offset,
            DEFAULT_MAX_OFFSET.dp.toFloat()
        )
        mSideBarPosition = typedArray.getInt(R.styleable.SideBar_sidebar_position, POSITION_RIGHT)
        mTextAlignment = typedArray.getInt(
            R.styleable.SideBar_sidebar_text_alignment,
            TEXT_ALIGN_CENTER
        )
        typedArray.recycle()
        mIndexItems = DEFAULT_INDEX_ITEMS
        initPaint()
    }

    private fun initPaint() {
        mPaint = Paint()
        mPaint!!.isAntiAlias = true
        mPaint!!.color = mNormalTextColor
        mPaint!!.textSize = mTextSize
        when (mTextAlignment) {
            TEXT_ALIGN_CENTER -> mPaint!!.textAlign = Paint.Align.CENTER
            TEXT_ALIGN_LEFT -> mPaint!!.textAlign = Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> mPaint!!.textAlign = Paint.Align.RIGHT
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)


        val height = MeasureSpec.getSize(heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)

        val fontMetrics = mPaint!!.fontMetrics
        mIndexItemHeight = fontMetrics.bottom - fontMetrics.top
        mBarHeight = mIndexItems.size * mIndexItemHeight

        // 计算最大宽度 作为控件宽度
        for (indexItem in mIndexItems) {
            mBarWidth = Math.max(mBarWidth, mPaint!!.measureText(indexItem))
        }

        val areaLeft = if (mSideBarPosition == POSITION_LEFT) 0f else width - mBarWidth - paddingRight
        val areaRight = if (mSideBarPosition == POSITION_LEFT) paddingLeft + areaLeft + mBarWidth else width.toFloat()
        val areaTop = height / 2 - mBarHeight / 2
        val areaBottom = areaTop + mBarHeight
        mStartTouchingArea[areaLeft, areaTop, areaRight] = areaBottom

        mFirstItemBaseLineY = (height / 2 - mIndexItems.size * mIndexItemHeight / 2 + (mIndexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2) - fontMetrics.ascent)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mIndexItems.forEachIndexed { i, s ->
            val baseLineY = mFirstItemBaseLineY + mIndexItemHeight * i

            var baseLineX = 0f
            if (mSideBarPosition == POSITION_LEFT) {
                when (mTextAlignment) {
                    TEXT_ALIGN_CENTER -> baseLineX = paddingLeft + mBarWidth / 2
                    TEXT_ALIGN_LEFT -> baseLineX = paddingLeft.toFloat()
                    TEXT_ALIGN_RIGHT -> baseLineX = paddingLeft + mBarWidth
                }
            } else {
                when (mTextAlignment) {
                    TEXT_ALIGN_CENTER -> baseLineX = width - paddingRight - mBarWidth / 2
                    TEXT_ALIGN_RIGHT -> baseLineX = (width - paddingRight).toFloat()
                    TEXT_ALIGN_LEFT -> baseLineX = width - paddingRight - mBarWidth
                }
            }

            if(mStartTouching && i == mCurrentIndex){
              mPaint!!.color = mSelectedTextColor
            }else{
                mPaint!!.color = mNormalTextColor
            }
            canvas!!.drawText(mIndexItems[i], baseLineX, baseLineY, mPaint!!)
        }

        mPaint!!.alpha = 255
        mPaint!!.textSize = mTextSize
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(mIndexItems.isEmpty()){
            return super.onTouchEvent(event)
        }

        val y = event.y
        val x = event.x

        mCurrentIndex = getSelectedIndex(y)

        when(event.action){
            MotionEvent.ACTION_DOWN -> {
                if (mStartTouchingArea.contains(x, y)) {
                    mStartTouching = true
                    if (onSelectIndexItemListener != null) {
                        onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems[mCurrentIndex])
                    }
                    invalidate()
                    return true
                } else {
                    mCurrentIndex = -1
                    return false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mStartTouching && onSelectIndexItemListener != null) {
                    onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems[mCurrentIndex])
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (onSelectIndexItemListener != null) {
                    onSelectIndexItemListener!!.onSelectIndexItem(mIndexItems[mCurrentIndex])
                }
                mCurrentIndex = -1
                mStartTouching = false
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getSelectedIndex(eventY: Float): Int {
        mCurrentY = eventY - (height / 2 - mBarHeight / 2)
        if (mCurrentY <= 0) {
            return 0
        }
        var index = (mCurrentY / mIndexItemHeight).toInt()
        if (index >= mIndexItems.size) {
            index = mIndexItems.size - 1
        }
        return index
    }

    fun setIndexItems(vararg indexItems: String) {
        mIndexItems = Arrays.copyOf(indexItems, indexItems.size)
        requestLayout()
    }

    fun setIndexItemsArray(indexItems: Array<String>) {
        mIndexItems = Arrays.copyOf(indexItems, indexItems.size)
        requestLayout()
    }

    fun setNormalTextColor(color: Int) {
        mNormalTextColor = color
        mPaint!!.color = color
        invalidate()
    }

    fun setSelectedTextColor(color: Int) {
        mSelectedTextColor = color
        mPaint!!.color = color
        invalidate()
    }

    fun setPosition(position: Int) {
        require(!(position != POSITION_RIGHT && position != POSITION_LEFT)) {
            "the position must be POSITION_RIGHT or POSITION_LEFT"
        }
        mSideBarPosition = position
        requestLayout()
    }

    fun setMaxOffset(offset: Int) {
        mMaxOffset = offset.toFloat()
        invalidate()
    }

    fun setTextAlign(align: Int) {
        if (mTextAlignment == align) {
            return
        }
        when (align) {
            TEXT_ALIGN_CENTER -> mPaint!!.textAlign =
                Paint.Align.CENTER
            TEXT_ALIGN_LEFT -> mPaint!!.textAlign =
                Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> mPaint!!.textAlign =
                Paint.Align.RIGHT
            else -> throw IllegalArgumentException(
                "the alignment must be TEXT_ALIGN_CENTER, TEXT_ALIGN_LEFT or TEXT_ALIGN_RIGHT"
            )
        }
        mTextAlignment = align
        invalidate()
    }

    fun setTextSize(size: Float) {
        if (mTextSize == size) {
            return
        }
        mTextSize = size
        mPaint!!.textSize = size
        invalidate()
    }


    fun setOnSelectIndexItemListener(onSelectIndexItemListener: OnSelectIndexItemListener?) {
        this.onSelectIndexItemListener = onSelectIndexItemListener
    }

    interface OnSelectIndexItemListener {
        fun onSelectIndexItem(index: String?)
    }
}