package com.daqsoft.module_main.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.daqsoft.library_base.utils.dp
import com.daqsoft.module_main.R
import com.ruffian.library.widget.RTextView
import me.majiajie.pagerbottomtabstrip.internal.RoundMessageView
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem
import java.util.*

/**
 * @package name：com.daqsoft.module_main.widget
 *
 * @date 6/11/2020 上午 9:50
 * @author zp
 * @describe
 */
class BottomTabItem : BaseTabItem {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    private val mIcon: ImageView
    private val mTitle: TextView
    private val mMessages: RTextView

    private lateinit var mDefaultDrawable: Drawable
    private lateinit var mCheckedDrawable: Drawable

    private var mDefaultTextColor = resources.getColor(R.color.gray_666666)
    private var mCheckedTextColor = resources.getColor(R.color.red_fa4848)

    private var mChecked: Boolean = false

    init {
        inflate(context, R.layout.main_bottom_tab_item, this)
        mIcon = findViewById(R.id.icon)
        mTitle = findViewById(R.id.title)
        mMessages = findViewById(R.id.messages)
        setRoundMessageViewNumber(0)
    }

    fun initialize(@DrawableRes drawableRes: Int, @DrawableRes checkedDrawableRes: Int) {
        mDefaultDrawable = ContextCompat.getDrawable(context, drawableRes)!!
        mCheckedDrawable = ContextCompat.getDrawable(context, checkedDrawableRes)!!
    }

    fun setTextDefaultColor(@ColorInt color: Int) {
        mDefaultTextColor = color
    }

    fun setTextCheckedColor(@ColorInt color: Int) {
        mCheckedTextColor = color
    }

    override fun setChecked(checked: Boolean) {
        if (checked) {
            if (selectedSingleDrawableFlag){
                setSelectedSingleDrawable()
            }
            mIcon.setImageDrawable(mCheckedDrawable)
            mTitle.setTextColor(mCheckedTextColor)

        } else {
            if (selectedSingleDrawableFlag){
                setNormalDrawable()
            }
            mIcon.setImageDrawable(mDefaultDrawable)
            mTitle.setTextColor(mDefaultTextColor)
        }
        mChecked = checked
    }

    override fun getTitle(): String {
        return mTitle.text.toString()
    }

    override fun setMessageNumber(number: Int) {
        setRoundMessageViewNumber(number)

    }

    override fun setHasMessage(hasMessage: Boolean) {
    }

    override fun setSelectedDrawable(drawable: Drawable) {
        mCheckedDrawable = drawable
        if (mChecked) {
            mIcon.setImageDrawable(drawable)
        }
    }

    override fun setDefaultDrawable(drawable: Drawable) {
        mDefaultDrawable = drawable
        if (!mChecked) {
            mIcon.setImageDrawable(drawable)
        }
    }

    override fun setTitle(title: String?) {
        mTitle.text = title
    }


    private var selectedSingleDrawableFlag = false
    private var selectedSingleDrawableSize = 0

    fun setSelectedSingleDrawableFlag(flag: Boolean,size: Int){
        this.selectedSingleDrawableFlag = flag
        this.selectedSingleDrawableSize = size
    }

    private fun setSelectedSingleDrawable(){
        val layoutParams =  mIcon.layoutParams
        layoutParams.width = selectedSingleDrawableSize
        layoutParams.height = selectedSingleDrawableSize
        mIcon.layoutParams = layoutParams
        mTitle.visibility = View.GONE
    }

    private fun setNormalDrawable(){
        val layoutParams =  mIcon.layoutParams
        layoutParams.width = 20.dp
        layoutParams.height = 20.dp
        mIcon.layoutParams = layoutParams
        mTitle.visibility = View.VISIBLE
    }

    private fun setRoundMessageViewNumber(number: Int) {

        if (number == 0){
            mMessages.visibility = View.INVISIBLE
            return
        }

        mMessages.visibility = View.VISIBLE
        if (number > 0) {
            if (number < 10) {
                mMessages.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
            } else {
                mMessages.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 7f)
            }
            if (number <= 99) {
                mMessages.text = String.format(Locale.ENGLISH, "%d", number)
            } else {
                mMessages.text = String.format(Locale.ENGLISH, "%d+", 99)
            }
            return
        }

        if (number < 0){
            mMessages.text = ""
        }
    }
}