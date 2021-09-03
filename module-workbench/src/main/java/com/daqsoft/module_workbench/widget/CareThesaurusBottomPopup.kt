package com.daqsoft.module_workbench.widget

import android.content.Context
import android.widget.TextView
import com.daqsoft.module_workbench.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 22/2/2021 下午 3:02
 * @author zp
 * @describe
 */
class CareThesaurusBottomPopup(context: Context)  : BottomPopupView(context) {


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_bottom_care
    }


    override fun onCreate() {
        super.onCreate()

        val enable = findViewById<TextView>(R.id.enable)
        enable.setOnClickListener {
            itemOnClickListener?.onEnableClick()
            cancel()
        }


        val disable = findViewById<TextView>(R.id.disable)
        disable.setOnClickListener {
            itemOnClickListener?.onDisableClick()
            cancel()
        }


        val modify = findViewById<TextView>(R.id.modify)
        modify.setOnClickListener {
            itemOnClickListener?.onModifyClick()
            cancel()
        }


        val delete = findViewById<TextView>(R.id.delete)
        delete.setOnClickListener {
            itemOnClickListener?.onDeleteClick()
            cancel()
        }

    }

    private fun cancel(){
        postDelayed(Runnable {
            if (popupInfo.autoDismiss){
                dismiss()
            }
        },100)
    }


    private var itemOnClickListener : ItemOnClickListener? = null

    fun setItemOnClickListener(listener: ItemOnClickListener){
        this.itemOnClickListener = listener
    }

    interface ItemOnClickListener{
        fun onEnableClick()
        fun onDisableClick()
        fun onModifyClick()
        fun onDeleteClick()
    }

}