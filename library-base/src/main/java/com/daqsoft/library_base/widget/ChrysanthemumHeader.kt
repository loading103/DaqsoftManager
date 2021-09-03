package com.daqsoft.library_base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.daqsoft.library_base.R
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle

/**
 * @package name：com.daqsoft.library_base.widget
 * @date 23/11/2020 下午 3:07
 * @author zp
 * @describe
 */
class ChrysanthemumHeader : LinearLayout, RefreshHeader {

    companion object{
        var REFRESH_HEADER_REFRESHING: String = "正在刷新..."

        var REFRESH_HEADER_FINISH: String = "刷新完成"

        var REFRESH_HEADER_FAILED: String = "刷新失败"

    }

    private var refreshContent  =  REFRESH_HEADER_REFRESHING
    private var loadingView: QMUILoadingView? = null
    private var contentView: TextView? = null

    constructor(context: Context?) : super(context){
        initView(context!!)
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs){
        initView(context!!)
    }


    private fun initView(context: Context){
        val view = LayoutInflater.from(context).inflate(
            R.layout.layout_refresh_head_chrysanthemum,
            this,
            false
        )
        loadingView  = view.findViewById(R.id.loading)
        contentView = view.findViewById(R.id.content)
        addView(view)
    }


    /**
     * 色设置提示文字
     * @param content String
     */
    fun setContent(content: String){
        this.refreshContent  = content
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None -> {
            }
            RefreshState.PullDownToRefresh -> {
                contentView?.text = refreshContent
            }
            RefreshState.PullDownCanceled -> {
            }
            RefreshState.ReleaseToRefresh -> {
            }
            RefreshState.ReleaseToTwoLevel -> {
            }
            RefreshState.RefreshReleased -> {
            }
            RefreshState.Refreshing -> {
            }
            RefreshState.RefreshFinish -> {
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
        loadingView?.start()
        contentView?.text = refreshContent
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        loadingView?.stop()
        contentView?.text = if (success) REFRESH_HEADER_FINISH else REFRESH_HEADER_FAILED
        return 200
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}