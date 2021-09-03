package com.daqsoft.module_workbench.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.alibaba.android.arouter.utils.TextUtils;
import com.daqsoft.module_workbench.R;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.WeekView;

public class CalendarWeekView extends WeekView {

    /**
     * 选中圆形半径
     */
    private int mRadius;

    /**
     * 周末画笔
     */
    private Paint weekendPaint = new Paint();

    /**
     * 今天画笔
     */
    private Paint mCurrentDayPaint = new Paint();


    /**
     * 标记圆点画笔
     */
    private Paint mPointPaint = new Paint();

    /**
     * 标记圆点半径
     */
    private float mPointRadius;

    /**
     * 标记圆点 距文字的距离
     */
    private int mPadding;



    public CalendarWeekView(Context context) {
        super(context);

        // 设置周末画笔
        weekendPaint.setAntiAlias(true);
        weekendPaint.setStyle(Paint.Style.FILL);
        weekendPaint.setTextAlign(Paint.Align.CENTER);
        weekendPaint.setColor(getContext().getResources().getColor(R.color.gray_ffcaca));
        weekendPaint.setTextSize(mCurDayTextPaint.getTextSize());

        // 设置今天画笔
        mCurrentDayPaint.setAntiAlias(true);
        mCurrentDayPaint.setStyle(Paint.Style.FILL);
        mCurrentDayPaint.setColor(getContext().getResources().getColor(R.color.white_ffffff_alpha70));

        // 设置标记圆点
        mPointPaint.setAntiAlias(true);
        mPointPaint.setStyle(Paint.Style.FILL);
        mPointPaint.setTextAlign(Paint.Align.CENTER);
        mPointPaint.setColor(Color.WHITE);

        // 设置标记圆点半径
        mPointRadius = dipToPx(context, 2.5f);

        // 设置标记圆点半径到文字的距离
        mPadding = dipToPx(getContext(), 3f);
    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 11 * 5;
        mSchemePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return true;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        String scheme = calendar.getScheme();
        if (!TextUtils.isEmpty(scheme)){
            switch (Integer.parseInt(scheme)){
                case 0 : {
                    mPointPaint.setColor(getContext().getResources().getColor(R.color.yellow_ffe641));
                    break;
                }
                case 1 :{
                    mPointPaint.setColor(getContext().getResources().getColor(R.color.gray_fed1d1));
                    break;
                }
                default:{
                    mPointPaint.setColor(getContext().getResources().getColor(R.color.gray_fed1d1));
                }
            }

        }
        canvas.drawCircle(x + mItemWidth / 2,  mItemHeight - 3 * mPadding, mPointRadius, mPointPaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine ;
        int cx = x + mItemWidth / 2;
        int cy =  mItemHeight / 2;
        int top =  - mItemHeight / 6;

        // 今天
        if (calendar.isCurrentDay() && !isSelected) {
            canvas.drawCircle(cx, cy, mRadius, mCurrentDayPaint);
        }


        // 是否选中
        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            // 是否有标记
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {
            canvas.drawText(
                    String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isWeekend() ? weekendPaint : calendar.isCurrentDay() ? mCurDayTextPaint : calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
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
