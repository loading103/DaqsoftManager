package com.daqsoft.module_mine.widget

import android.R.attr
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager


/**
 * @package name：com.daqsoft.module_mine.widget
 * @date 23/11/2020 下午 5:44
 * @author zp
 * @describe
 */
class MagicIndicatorPinBehavior  : CoordinatorLayout.Behavior<View> {

   constructor():super()
   constructor(context: Context?, attrs: AttributeSet?):  super(context, attrs)

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return dependency is ViewPager
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        val dy: Float = dependency.y - child.height
        if (dy < 0){
            child.visibility = View.VISIBLE
        }else{
            child.visibility = View.GONE
        }
        return true
    }
}