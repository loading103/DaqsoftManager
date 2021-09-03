package com.daqsoft.library_base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.daqsoft.library_base.R
import com.scwang.smart.refresh.layout.api.RefreshFooter
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import kotlinx.android.synthetic.main.layout_refresh_head_chrysanthemum.view.*

/**
 * @package name：com.daqsoft.library_base.widget
 * @date 23/11/2020 下午 3:07
 * @author zp
 * @describe
 */
class ChrysanthemumFooter : LinearLayout, RefreshFooter {

    companion object{
        const val REFRESH_FOOTER_FINISH: String = "加载完成"

        const val REFRESH_FOOTER_FAILED: String = "加载失败"

        const val REFRESH_FOOTER_NOTHING: String = "没有更多数据了"

        const val REFRESH_FOOTER_LOADING: String = "正在加载..."

    }

    private var loadingContent  =  REFRESH_FOOTER_LOADING
    private var mNoMoreData = false
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
     * 设置提示文字
     * @param content String
     */
    fun setContent(content: String){
        this.loadingContent = content
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
                contentView?.text = loadingContent
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
        loadingView?.start()
        contentView?.text = loadingContent
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        loadingView?.stop()
        if (!mNoMoreData) {
            contentView?.text = if (success) REFRESH_FOOTER_FINISH else REFRESH_FOOTER_FAILED
        }
        return 200
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {
    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun setNoMoreData(noMoreData: Boolean): Boolean {
        if (noMoreData){
            contentView?.text = REFRESH_FOOTER_NOTHING
        }else{
            contentView?.text = loadingContent
        }
        return true
    }
}