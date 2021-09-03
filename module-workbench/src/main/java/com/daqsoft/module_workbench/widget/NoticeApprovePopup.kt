package com.daqsoft.module_workbench.widget

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.daqsoft.module_workbench.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 3/3/2021 上午 10:35
 * @author zp
 * @describe
 */
class NoticeApprovePopup(context: Context) : BottomPopupView(context) {


    private var cancel : TextView ? = null
    private var determine : TextView ? = null

    private var agree : TextView ? = null
    private var disagree : TextView ? = null

    private var content : EditText ? = null

    private var result : Boolean = true

    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_notice_approve
    }


    override fun onCreate() {
        super.onCreate()
    }

    override fun initPopupContent() {
        super.initPopupContent()
        cancel = findViewById(R.id.cancel)
        cancel?.setOnClickListener {
            dismiss()
        }
        determine = findViewById(R.id.determine)
        determine?.setOnClickListener {
            consumer?.accept(result,content?.text.toString())
            dismiss()
        }
        agree = findViewById(R.id.approve)
        agree?.setOnClickListener {
            agree?.isSelected = true
            disagree?.isSelected = false
            result = true
        }
        disagree = findViewById(R.id.not_approved)
        disagree?.setOnClickListener {
            agree?.isSelected = false
            disagree?.isSelected = true
            result = false
        }
        content = findViewById(R.id.content)

        agree?.isSelected = true
    }




    private var consumer : Consumer? = null

    fun setConsumer(consumer: Consumer){
        this.consumer = consumer
    }

    interface Consumer{
        fun accept(result:Boolean,content: String)
    }
}