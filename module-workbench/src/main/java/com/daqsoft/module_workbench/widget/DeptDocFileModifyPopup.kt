package com.daqsoft.module_workbench.widget

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.daqsoft.module_workbench.R
import com.lxj.xpopup.core.BottomPopupView

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 22/2/2021 下午 3:02
 * @author zp
 * @describe
 */
class DeptDocFileModifyPopup(context: Context,val needModify : Boolean)  : BottomPopupView(context) {


    override fun getImplLayoutId(): Int {
        return R.layout.layout_popup_bottom_modify
    }


    override fun onCreate() {
        super.onCreate()
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


        val group =  findViewById<Group>(R.id.group)
        val deleteFull = findViewById<TextView>(R.id.delete_full)
        deleteFull.setOnClickListener {
            itemOnClickListener?.onDeleteClick()
            cancel()
        }

        if (needModify){
            group.visibility = View.VISIBLE
            deleteFull.visibility = View.GONE
        }else{
            group.visibility = View.GONE
            deleteFull.visibility = View.VISIBLE
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
        fun onModifyClick()
        fun onDeleteClick()
    }

}