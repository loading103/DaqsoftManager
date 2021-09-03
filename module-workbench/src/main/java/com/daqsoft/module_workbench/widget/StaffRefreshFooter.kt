package com.daqsoft.module_workbench.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.daqsoft.module_workbench.R
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

/**
 * @package name：com.daqsoft.module_workbench.widget
 * @date 14/1/2021 下午 1:10
 * @author zp
 * @describe
 */
class StaffRefreshFooter  : LinearLayout, RefreshFooter {

    constructor(context: Context?) : super(context){
        initView(context!!)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context!!)
    }


    private fun initView(context: Context){
        val view = LayoutInflater.from(context).inflate(
            R.layout.layout_refresh_footer_staff,
            this,
            false
        )
        addView(view)
    }


    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None -> {
            }
            RefreshState.PullUpToLoad -> {
            }
            RefreshState.PullUpCanceled -> {
            }
            RefreshState.ReleaseToLoad -> {
            }
            RefreshState.TwoLevelReleased -> {
            }
            RefreshState.LoadReleased -> {
            }
            RefreshState.Loading -> {
            }
            RefreshState.LoadFinish -> {
            }
            else->{
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun setPrimaryColors(vararg colors: Int) {
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {
    }


    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        return 0
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        return true
    }
}