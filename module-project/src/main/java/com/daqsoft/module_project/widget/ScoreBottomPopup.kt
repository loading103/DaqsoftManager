package com.daqsoft.module_project.widget

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.module_project.R
import com.lxj.xpopup.core.BottomPopupView
import per.wsj.library.AndRatingBar
import java.math.BigDecimal

/**
 * @package name：com.daqsoft.module_project.widget
 * @date 7/4/2021 上午 11:44
 * @author zp
 * @describe 评分 弹窗
 */
class ScoreBottomPopup  : BottomPopupView {

    constructor(context: Context):super(context)

    var ratingBar : AndRatingBar ? = null

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_score
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate() {
        super.onCreate()

        val close  = findViewById<ImageView>(R.id.close)
        close.setOnClickListener {
            dismiss()
        }

        val fraction = findViewById<TextView>(R.id.fraction)

        ratingBar = findViewById<AndRatingBar>(R.id.rating_bar)
        ratingBar?.setOnRatingChangeListener { ratingBar, rating ->
            val score = BigDecimal((rating*2).toString()).stripTrailingZeros().toPlainString()
            if (rating >=0 && rating <=1){
                fraction.text = "${score}，极差"
            }else if(rating >1 && rating <=2){
                fraction.text = "${score}，差"
            }else if(rating >2 && rating <=3) {
                fraction.text = "${score}，一般"
            }else if(rating >3 && rating <=4) {
                fraction.text = "${score}，优秀"
            }else if(rating >4 && rating <=5) {
                fraction.text = "${score}，超级棒"
            }
        }

        val confirm = findViewById<TextView>(R.id.confirm)
        confirm.setOnClickListener {
            dismiss()
            onRatingListener?.score(ratingBar!!.rating * 2)
        }
    }


    private var onRatingListener : OnRatingListener? = null

    fun setOnRatingListener(listener: OnRatingListener){
        this.onRatingListener = listener
    }

    interface OnRatingListener{
        fun score(rating : Float)
    }
}