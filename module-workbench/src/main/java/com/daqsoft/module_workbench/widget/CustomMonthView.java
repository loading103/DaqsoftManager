package com.daqsoft.module_workbench.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.text.TextUtils;

import com.daqsoft.module_workbench.R;
import com.daqsoft.mvvmfoundation.base.BaseApplication;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.MonthView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 演示一个变态需求的月视图
 * Created by huanghaibin on 2018/2/9.
 */

public class CustomMonthView extends MonthView {

    private int mRadius;

    /**
     * 自定义魅族标记的文本画笔
     */
    private Paint mTextPaint = new Paint();

//
//    /**
//     * 24节气画笔
//     */
//    private Paint mSolarTermTextPaint = new Paint();

    /**
     * 背景圆点
     */
    private Paint mPointPaint = new Paint();

    /**
     * 今天的背景色
     */
    private Paint mCurrentDayPaint = new Paint();

    /**
     * 圆点半径
     */
    private float mPointRadius;

    private int mPadding;

    /**
     * 自定义魅族标记的圆形背景
     */
    private Paint mSchemeBasicPaint = new Paint();

    private float mSchemeBaseLine;
    public CustomMonthView(Context context) {
        super(context);

        mTextPaint.setTextSize(dipToPx(context, 8));
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setFakeBoldText(true);


        mSchemeBasicPaint.setAntiAlias(true);
        mSchemeBasicPaint.setStyle(Paint.Style.FILL);
        mSchemeBasicPaint.setTextAlign(Paint.Align.CENTER);
        mSchemeBasicPaint.setFakeBoldText(true);
        mSchemeBasicPaint.setColor(Color.WHITE);


        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setColor(0xFFeaeaea);

        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.RED);


        // 设置标记圆点半径
        mPointRadius = dipToPx(context, 2.5f);

        // 设置标记圆点半径到文字的距离
        mPadding = dipToPx(getContext(), 3f);



    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 12 * 5;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;

        int color_ff5252 = BaseApplication.Companion.getInstance().getResources().getColor(R.color.color_ff5252);
        int color_ff7920 = BaseApplication.Companion.getInstance().getResources().getColor(R.color.color_ff7920);
        LinearGradient linearGradient=new LinearGradient(0,0,x,y,new int[]{color_ff5252,color_ff7920},null, Shader.TileMode.MIRROR);
        mSelectedPaint.setShader(linearGradient);
        if(hasScheme){
            canvas.drawCircle(cx, cy+mItemHeight / 10, mRadius, mSelectedPaint);
        }else {
            canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        }

        return true;
    }





    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {

        boolean isSelected = isSelected(calendar);
        if (isSelected) {
            mPointPaint.setColor(Color.WHITE);
        } else {
            mPointPaint.setColor(Color.RED);
        }
        canvas.drawCircle(x + mItemWidth / 2, y + mItemHeight - 3 * mPadding, mPointRadius, mPointPaint);
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        int top = y - mItemHeight / 6;

        if (hasScheme) {
            canvas.drawCircle(x + mItemWidth - mPadding , y + mPadding , 0, mSchemeBasicPaint);
            mTextPaint.setColor(calendar.getSchemeColor());
            canvas.drawText(calendar.getScheme(), x + mItemWidth - mPadding , y + mPadding + mSchemeBaseLine, mTextPaint);
        }
        //日期是否在范围内，超出范围的可以置灰
        boolean isInRange = isInRange(calendar);
        //日期是否可用，没有被拦截，被拦截的可以置灰
        boolean isEnable = !onCalendarIntercept(calendar);

        //当然可以换成其它对应的画笔就不麻烦，
        if (calendar.isWeekend() && calendar.isCurrentMonth() && isInRange) {
            mCurMonthTextPaint.setColor(0xFF999999);
            mCurMonthLunarTextPaint.setColor(0xFF999999);
            mSchemeTextPaint.setColor(0xFF999999);
            mSchemeLunarTextPaint.setColor(0xFF999999);
            mOtherMonthLunarTextPaint.setColor(0xFF999999);
            mOtherMonthTextPaint.setColor(0xFF999999);
        } else {
            mCurMonthTextPaint.setColor(0xff333333);
            mCurMonthLunarTextPaint.setColor(0xffCFCFCF);
            mSchemeTextPaint.setColor(0xff333333);
            mSchemeLunarTextPaint.setColor(0xffCFCFCF);

            mOtherMonthTextPaint.setColor(0xFFe1e1e1);
            mOtherMonthLunarTextPaint.setColor(0xFFe1e1e1);
        }

        String content="";
        if(calendar.isCurrentDay()){
            content="今天";
        }else {
            content=String.valueOf(calendar.getDay());
        }
        if (isSelected) {
            canvas.drawText(content, cx, mTextBaseLine + top + mItemHeight / 7, mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(content, cx, mTextBaseLine + top+ mItemHeight / 7, calendar.isCurrentMonth() & isEnable &isInRange ? mSchemeTextPaint : mOtherMonthTextPaint);
        } else {
            canvas.drawText(content, cx, mTextBaseLine + top+ mItemHeight / 7, calendar.isCurrentMonth() & isEnable & isInRange ? mCurMonthTextPaint : mOtherMonthTextPaint);
        }
    }

    /**
     * dp转px
     *
     * @param context context
     * @param dpValue dp
     * @return px
     */
    private static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
