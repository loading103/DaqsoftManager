package com.daqsoft.library_base.toolbar

import android.app.Application
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.daqsoft.library_base.R
import com.daqsoft.library_base.utils.AppUtils
import com.daqsoft.mvvmfoundation.base.BaseModel
import com.daqsoft.mvvmfoundation.base.BaseViewModel
import com.daqsoft.mvvmfoundation.binding.command.BindingAction
import com.daqsoft.mvvmfoundation.binding.command.BindingCommand
import com.daqsoft.mvvmfoundation.utils.ContextUtils
import com.daqsoft.library_base.utils.dp


abstract class ToolbarViewModel<M : BaseModel> : BaseViewModel<M> {

    constructor(application: Application):super(application)
    constructor(application: Application, model: M): super(application, model)

    //标题文字
    var titleTextObservable = ObservableField("")
    //标题文字隐藏
    var titleTextVisibleObservable = ObservableInt(View.VISIBLE)
    //标题文字颜色
    var titleTextColorObservable = ObservableInt(R.color.black_333333)

    //左边返回按钮图片
    var  backIconSrcObservable = ObservableInt(R.mipmap.back_black)
    //左边返回按钮隐藏
    var  backIconVisibleObservable =  ObservableInt(View.VISIBLE)

    //右边按钮图片
    var rightIconSrcObservable = ObservableInt(R.mipmap.ellipsis_black)
    //右边按钮隐藏
    var rightIconVisibleObservable = ObservableInt(View.GONE)

    //右边按钮图片2
    var rightIcon2SrcObservable = ObservableInt(R.mipmap.ellipsis_black)
    //右边按钮隐藏2
    var rightIcon2VisibleObservable = ObservableInt(View.GONE)


    //右边文字隐藏
    var rightTextVisibleObservable = ObservableInt(View.GONE)
    //右边文字
    var rightTextObservable = ObservableField("")
    //右边文字颜色
    var rightTextColorObservable = ObservableInt(R.color.black_000000)

    //背景颜色
    var backgroundObservable = ObservableInt(R.color.white_ffffff)

    //兼容databinding，去泛型化
    var toolbarViewModel  = this

    // 状态栏高度
    var statusBarHeight = ObservableField<Int>()
    // toolbar 高度
    var toolbarHeight = ObservableField<Int>()


    // 标题右图标
    var titleRightIconObservable = ObservableInt(R.mipmap.ellipsis_black)
    // 标题右图标隐藏
    var titleRightIconVisibleObservable = ObservableInt(View.GONE)

    init {
        statusBarHeight.set(AppUtils.getStatusBarHeight())
        toolbarHeight.set(AppUtils.getStatusBarHeight() + AppUtils.getToolbarHeight())
    }

    /**
     * 设置标题
     *
     * @param text 标题文字
     */
    fun setTitleText(text: String) {
        titleTextObservable.set(text)
    }

    /**
     * 设置标题是否隐藏
     */
    fun setTitleTextVisible(visibility: Int){
        titleTextVisibleObservable.set(visibility)
    }

    /**
     * 设置标题文字颜色
     */
    fun setTitleTextColor(res: Int){
        titleTextColorObservable.set(res)
    }

    /**
     * 设置返回按钮图片资源
     */
    fun setBackIconSrc(src: Int){
        backIconSrcObservable.set(src)
    }

    /**
     * 设置返回按钮是否隐藏
     */
    fun setBackIconVisible(visibility: Int){
        backIconVisibleObservable.set(visibility)
    }

    /**
     * 设置右边按钮图片资源
     */
    fun setRightIconSrc(src: Int){
        rightIconSrcObservable.set(src)
    }

    /**
     * 设置右边图标的显示和隐藏
     *
     * @param visibility
     */
    fun setRightIconVisible(visibility: Int) {
        rightIconVisibleObservable.set(visibility)
    }


    /**
     * 设置右边按钮图片资源2
     */
    fun setRightIcon2Src(src: Int){
        rightIcon2SrcObservable.set(src)
    }

    /**
     * 设置右边图标的显示和隐藏2
     *
     * @param visibility
     */
    fun setRightIcon2Visible(visibility: Int) {
        rightIcon2VisibleObservable.set(visibility)
    }


    /**
     * 设置右边文字颜色
     */
    fun setRightTextColor(res: Int){
        rightTextColorObservable.set(res)
    }

    /**
     * 设置背景资源
     */
    fun setBackground(src: Int){
        backgroundObservable.set(src)
    }

    /**
     * 设置右边文字显示和隐藏
     */
    fun setRightTextVisible(visibility: Int) {
        rightTextVisibleObservable.set(visibility)
    }

    /**
     * 设置右边文字
     */
    fun setRightTextTxt(txt: String){
        rightTextObservable.set(txt)
    }


    /**
     * 返回按钮的点击事件
     */
    val backOnClick: BindingCommand<Any> = BindingCommand(BindingAction { backOnClick() })


    open fun backOnClick() {
        finish()
    }
    /**
     * 右边按钮的点击事件
     */
    var rightIconOnClick = BindingCommand<Any>(BindingAction {
        rightIconOnClick()
    })

    /**
     * 右边按钮的点击事件2
     */
    var rightIcon2OnClick = BindingCommand<Any>(BindingAction {
        rightIcon2OnClick()
    })

    /**
     * 右边图标的点击事件
     */
    open fun rightIconOnClick() {}

    /**
     * 右边图标的点击事件2
     */
    open fun rightIcon2OnClick() {}

    /**
     * 右边文字点击事件
     */
    var rightTextOnClick = BindingCommand<Any>(BindingAction {
        rightTextOnClick()
    })
    /**
     * 右边文字点击事件
     */
    open fun rightTextOnClick() {}

    /**
     * 状态栏的高度
     * @param height Int
     */
    fun setStatusBarHeight(height:Int){
        statusBarHeight.set(height)
    }

    /**
     * toolbar 的高度
     * @param height Int
     */
    fun setToolbarHeight(height:Int){
        toolbarHeight.set(height)
    }



    /**
     * 设置title右图片资源
     */
    fun setTitleRightIconObservable(src: Int){
        titleRightIconObservable.set(src)
    }

    /**
     * 设置title右图片资源隐藏
     *
     * @param visibility
     */
    fun setTitleRightIconVisibleObservable(visibility: Int) {
        titleRightIconVisibleObservable.set(visibility)
    }


    val titleTextOnClick = BindingCommand<Unit>(BindingAction {
        titleTextOnClick()
    })

    open fun titleTextOnClick(){}
}