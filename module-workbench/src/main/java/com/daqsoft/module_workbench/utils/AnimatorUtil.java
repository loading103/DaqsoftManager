package com.daqsoft.module_workbench.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import net.lucode.hackware.magicindicator.buildins.UIUtil;


public class AnimatorUtil {
    private Context context;
    private long lastTime=0;
    private long DuringTime=200;
    public AnimatorUtil(Context context) {
        this.context=context;
    }

    /**
     * 这个属性动画中的ValueAnimator动画   还有一个ObjectAnimator动画
     */
    public void performAnim(final boolean show, final View mTvStase){
        //属性动画对象
        ValueAnimator va ; //高度
        ValueAnimator va_alpha ; //透明度
        if(show){
            //显示view，高度从0变到height值
            if(mTvStase.getVisibility()== View.VISIBLE){
                return;
            }
            va = ValueAnimator.ofInt(0, UIUtil.dip2px(context,55));
            va_alpha = ValueAnimator.ofFloat(1, 1f);
        }else{
            //隐藏view，高度从height变为0
            if(System.currentTimeMillis()-lastTime<400){
                return;
            }
            lastTime=System.currentTimeMillis();
            va = ValueAnimator.ofInt(UIUtil.dip2px(context,55),0);
            va_alpha = ValueAnimator.ofFloat(1f, 1f);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mTvStase.getLayoutParams().height = h;
                mTvStase.requestLayout();
            }
        });
        va_alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float alpha =(float)valueAnimator.getAnimatedValue();
                mTvStase.setAlpha(alpha);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    mTvStase.setVisibility(View.VISIBLE);
                }
                super.onAnimationStart(animation);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!show) {
                    mTvStase.setVisibility(View.GONE);
                }
            }
        });
        va.setDuration(400);
        va_alpha.setDuration(400);
        va.start();
        va_alpha.start();
    }
    public void performAnim1(final boolean show, final View mTvStase){
        //属性动画对象
        ValueAnimator va ; //高度
        ValueAnimator va_alpha ; //透明度
        if(show){
            //显示view，高度从0变到height值
            if(mTvStase.getVisibility()== View.VISIBLE){
                return;
            }
            va = ValueAnimator.ofInt(0, UIUtil.dip2px(context,50));
            va_alpha = ValueAnimator.ofFloat(1, 1f);
        }else{
            //隐藏view，高度从height变为0
            if(System.currentTimeMillis()-lastTime<300){
                return;
            }
            lastTime=System.currentTimeMillis();
            va = ValueAnimator.ofInt(UIUtil.dip2px(context,50),0);
            va_alpha = ValueAnimator.ofFloat(1f, 1f);
        }
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //获取当前的height值
                int h =(Integer)valueAnimator.getAnimatedValue();
                //动态更新view的高度
                mTvStase.getLayoutParams().height = h;
                mTvStase.requestLayout();
            }
        });
        va_alpha.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float alpha =(float)valueAnimator.getAnimatedValue();
                mTvStase.setAlpha(alpha);
            }
        });
        va.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (show) {
                    mTvStase.setVisibility(View.VISIBLE);
                }
                super.onAnimationStart(animation);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!show) {
                    mTvStase.setVisibility(View.GONE);
                }
            }
        });
        va.setDuration(300);
        va_alpha.setDuration(300);
        va.start();
        va_alpha.start();
    }
    /**
     * ObjectAnimator动画
     */

    @SuppressLint("WrongConstant")
    public void  objectAnimation(boolean isdown,View view) {

        float height = (float)view.getLayoutParams().height;
        ObjectAnimator  objectAnimator1 =null;
        ObjectAnimator  objectAnimator2 =null;
        if(!isdown){
            objectAnimator1 = ObjectAnimator.ofFloat(view, "translationY", 0.0f, height);
            objectAnimator2 = ObjectAnimator.ofFloat(view, "alpha", 1.0f, 0f);
        }else {
            objectAnimator1 = ObjectAnimator.ofFloat(view, "translationY", height, 0.0f);
            objectAnimator2 = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        }
        objectAnimator1.setDuration(900);
        objectAnimator2.setDuration(900);
        objectAnimator1.setRepeatMode(Animation.RESTART);
        objectAnimator2.setRepeatMode(Animation.RESTART);
        objectAnimator1.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator2.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator1.start();
        objectAnimator2.start();
    }

    /**
     * 帧动画
     */
    @SuppressLint("WrongConstant")
    public void  FrameAnimation(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) ((ImageView)view).getDrawable();
        //判断是否在运行
        if(!animationDrawable.isRunning()){
            //开启帧动画
            animationDrawable.setOneShot(false); //为true时 转一次  停留在最后一帧
            animationDrawable.start();
        }else {
            animationDrawable.selectDrawable(0);//暂停时留在第一帧
            animationDrawable.stop();
//          animationDrawable=null;
        }
    }


}