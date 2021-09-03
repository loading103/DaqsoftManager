package com.daqsoft.module_project.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.daqsoft.module_project.R
import com.daqsoft.module_project.repository.pojo.vo.ProjectDetailBean
import com.daqsoft.module_project.utils.StringUtils
import kotlinx.android.synthetic.main.project_layout_progress.view.*

class ProcessView :FrameLayout {

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
        var view = LayoutInflater.from(mContext).inflate(R.layout.project_layout_progress, this)
    }

    /**
     * 设置数据
     */
    fun setData(it: ProjectDetailBean) {
        if(it.projectType==1){
            ll_root.visibility=View.VISIBLE
        }
        tv_title.text=it.delayDays
        if (StringUtils.isEmptyString(it.createTime)) {
            iv_one.setBackgroundResource(R.mipmap.xmxq_sb)
            tv_one_time.visibility = View.GONE
        } else {
            iv_one.setBackgroundResource(R.mipmap.xmxq_cg)
            tv_one_time.setText(it.createTime)
            tv_one_time.visibility = View.VISIBLE
        }

        if (StringUtils.isEmptyString(it.contractSignTime)) {
            iv_two.setBackgroundResource(R.mipmap.xmxq_sb)
            tv_two_time.setText("未备案")
        } else {
            iv_two.setBackgroundResource(R.mipmap.xmxq_cg)
            tv_two_time.setText(it.contractSignTime)
        }

        if (StringUtils.isEmptyString(it.starterTime)) {
            iv_three.setBackgroundResource(R.mipmap.xmxq_sb)
            tv_three_time.setText("未交底")
        } else {
            iv_three.setBackgroundResource(R.mipmap.xmxq_cg)
            tv_three_time.setText(it.starterTime)
        }

        if (StringUtils.isEmptyString(it.firstCheckTime)) {
            ll_first.visibility=View.GONE
        }else{
            ll_first.visibility=View.VISIBLE
            if (it.firstState == 1) {
                iv_four.setBackgroundResource(R.mipmap.xmxq_cg)
            } else {
                iv_four.setBackgroundResource(R.drawable.shape_project_flow_circle)
            }
            tv_four_time.setText(it.firstCheckTime)
        }
        if (StringUtils.isEmptyString(it.finalCheckTime)) {
            ll_last.visibility=View.GONE
        }else {
            ll_last.visibility = View.VISIBLE
            if (it.finalState == 1) {
                iv_five.setBackgroundResource(R.mipmap.xmxq_cg)
            } else {
                iv_five.setBackgroundResource(R.drawable.shape_project_flow_circle)
            }
            tv_five_time.setText(it.finalCheckTime)
        }
        if(StringUtils.isEmptyString(it.firstCheckTime) && StringUtils.isEmptyString(it.finalCheckTime)){
            lin3.visibility=View.GONE
        }else{
            lin3.visibility=View.VISIBLE
        }

    }
}

