package com.daqsoft.module_workbench.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.Nullable;

import com.daqsoft.library_base.utils.IMDensityUtil;
import com.daqsoft.module_workbench.R;

public class SportProgressView1 extends View {
    private static final String TAG = "SportProgressView";

    private Paint mPaint;
    private Paint mTextPaint;

    private int colorStart = Color.parseColor("#fa4848");
    private int colorEnd = Color.parseColor("#fa4848");
    private int colorEmpty = Color.parseColor("#fa4848");
    private int textColor = Color.parseColor("#333333");
    private int textColor1 = Color.parseColor("#999999");
    private int mWidth; // 控件的宽高
    private int mHeight;

    private int marginBottom;
    private int mProgressWidth;
    private int mProgressR;
    private int mStepTextSize;

    private double mCurProgress;

    private String mStartValue = "0%";
    private String mTargetValue = "100%";

    private Context mContext;

    public SportProgressView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SportProgressView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initAttr(attrs, defStyleAttr);

        init();
    }

    private void initAttr(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SportProgressView, defStyleAttr, 0);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            if (attr == R.styleable.SportProgressView_startColor) {
                colorStart = a.getColor(attr, colorStart);
            } else if (attr == R.styleable.SportProgressView_endColor) {
                colorEnd = a.getColor(attr, colorEnd);
            } else if (attr == R.styleable.SportProgressView_emptyColor) {
                colorEmpty = a.getColor(attr, colorEmpty);
            } else if (attr == R.styleable.SportProgressView_progressWidth) {
                mProgressWidth = a.getDimensionPixelSize(attr, 14);
            } else if (attr == R.styleable.SportProgressView_stepTextSize) {
                mStepTextSize = a.getDimensionPixelSize(attr, 10);
            }
        }
        a.recycle();
    }

    /**
     * 初始化
     */
    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(mProgressWidth);
        mPaint.setStyle(Paint.Style.STROKE);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(colorEmpty);
        mTextPaint.setTextSize(mStepTextSize);

        marginBottom = getTextHeight(mTextPaint) + IMDensityUtil.dip2px(mContext,8);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthSpecMode == MeasureSpec.EXACTLY) {
            mWidth = widthSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            mWidth = 500;
        }

        if (heightSpecMode == MeasureSpec.EXACTLY) {
            mHeight = heightSpecSize;
        } else if (widthSpecMode == MeasureSpec.AT_MOST) {
            mHeight = 500;
        }

        /**
         * 以给定的高度为限制条件，计算半径
         */
        mProgressR = mHeight - marginBottom - mProgressWidth / 2;
        mWidth = mProgressR * 2 + mProgressWidth + getPaddingLeft() + getPaddingRight();

        setMeasuredDimension(mWidth, mHeight);
    }

    private float mProgress;

    @Override
    protected void onDraw(Canvas canvas) {

        int left = mProgressWidth / 2 + getPaddingLeft();
        int right = left + 2 * mProgressR;
        int top = mHeight - marginBottom - mProgressR;
        int bottom = mHeight - marginBottom + mProgressR;
        RectF rect = new RectF(left, top, right, bottom);

        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(colorEmpty);
        mPaint.setShader(null);
        canvas.drawArc(rect, 180, 180, false, mPaint);

        /**
         * 设置渐变颜色
         */
        int[] colors = new int[]{colorStart, colorEnd};
        LinearGradient linearGradient = new LinearGradient(0, 0, mWidth, 0, colors,
                null, LinearGradient.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
        canvas.drawArc(rect, 180, mProgress, false, mPaint);
        drawText(canvas);

        drawProgressText(canvas, mProgress);
    }

    /**
     * 画底部初始的文字和结束的文字
     *
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        mTextPaint.setColor(colorEmpty);
        mTextPaint.setTextSize(mStepTextSize);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float baseline = mHeight - fm.descent;
        float stringWidth = mTextPaint.measureText(mStartValue);
//        canvas.drawText(mStartValue, getPaddingLeft() - stringWidth / 2 + mProgressWidth / 2, baseline, mTextPaint);

        stringWidth = mTextPaint.measureText(mTargetValue);
//        canvas.drawText(mTargetValue, mWidth - getPaddingRight() - stringWidth + stringWidth / 2 - mProgressWidth / 2, baseline, mTextPaint);
    }

    /**
     * 绘制中间进度的文字
     *
     * @param canvas
     * @param progress
     */

    private  String textContent="本月提交率";

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    private void drawProgressText(Canvas canvas, float progress) {
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(IMDensityUtil.dip2px(mContext,20));
        mTextPaint.setFakeBoldText(true);

        String text = String.format("%.2f",(progress / 180.f * 100)) + "%";
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        float baseline = mHeight / 2 + marginBottom - fm.descent;
        float baseline1 = mHeight / 2 - marginBottom - fm.descent+IMDensityUtil.dip2px(mContext,30);
        float stringWidth = mTextPaint.measureText(text);
        canvas.drawText(text, mWidth / 2 - stringWidth / 2, baseline1, mTextPaint);

        mTextPaint.setFakeBoldText(false);
        mTextPaint.setColor(textColor1);
        mTextPaint.setTextSize(IMDensityUtil.dip2px(mContext,12));
        float stringWidth1 = mTextPaint.measureText(textContent);
//        canvas.drawText(textContent, mWidth / 2 - stringWidth1 / 2, baseline, mTextPaint);
    }


    private int getTextHeight(Paint mTextPaint) {
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        int textHeight = (int) (Math.ceil(fm.descent - fm.ascent) + 2);
        return textHeight;
    }

    public void setProgress(double progress) {
        mCurProgress = progress;
        startAnimation();
    }

    /**
     * 动画效果的取值
     */
    public void startAnimation() {
        mProgress = 0f;
        ValueAnimator animator = ValueAnimator.ofFloat(0, (float) mCurProgress/ 100f * 180);
        animator.setDuration(1000).start();
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mProgress = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }
}