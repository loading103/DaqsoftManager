package com.daqsoft.module_project.widget

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.daqsoft.module_project.R
import java.lang.Exception

class AccountAddView :LinearLayout, TextWatcher {

    var mContext: Context? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        initView()
    }

    private fun initView() {
        var view = LayoutInflater.from(mContext).inflate(R.layout.item_account_add, this)
        view.findViewById<EditText>(R.id.et_money).addTextChangedListener(this)
        view.findViewById<ImageView>(R.id.iv_delete).visibility=View.GONE
    }


    /**
     * 添加一个item
     */
    fun addOneView(){
        var view = LayoutInflater.from(mContext).inflate(R.layout.item_account_add, null)
        view.findViewById<ImageView>(R.id.iv_delete).visibility=View.VISIBLE
        view.findViewById<EditText>(R.id.et_money).addTextChangedListener(this)
        view.findViewById<ImageView>(R.id.iv_delete).setOnClickListener {
            this.removeView(view)
            getTotleMoney()
        }
        this.addView(view)
    }

    override fun afterTextChanged(p0: Editable?) {
        getTotleMoney()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    interface MoneyChangedListener {
        fun MoneyChangeder(number: String)
    }
    var lisitener: MoneyChangedListener? = null


    /**
     * 填写金额时修改金额
     */
    fun getTotleMoney(){
        try {
            var  number:Double=0.00;
            for (index in 0 until childCount){
                if(!TextUtils.isEmpty(getChildAt(index).findViewById<EditText>(R.id.et_money).text)){
                    val text = getChildAt(index).findViewById<EditText>(R.id.et_money).text.toString().toDouble()
                    number+=text;
                }
            }
            if (lisitener != null) {
                lisitener?.MoneyChangeder(String.format("%.2f", number).toString())
            }
        }catch (e:Exception){
            //排除第一位输入小数点
        }
    }
}
