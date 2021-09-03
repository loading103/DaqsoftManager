package com.daqsoft.module_project.widget;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.MetricAffectingSpan;
import android.util.AttributeSet;

import com.daqsoft.module_project.R;
import com.ruffian.library.widget.REditText;

import org.jetbrains.annotations.NotNull;

/**
 *  整块删除 可编辑
 */

public class UseEditText extends REditText {

    private StringBuilder builder;

    public UseEditText(Context context) {
        super(context);
    }

    public UseEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * 添加一个块,在文字的后面添加
     */
    public void addAtSpan(String showText, @NotNull Context context) {
        SpannableString ss = new SpannableString(showText);
        ss.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red_fa4848)), 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        getText().insert(getSelectionStart(),ss);
        SpannableString sps = new SpannableString(getText());
        int start = getSelectionEnd() -ss.length() ;
        int end = getSelectionEnd();
        makeSpan(sps, new UnSpanText(start, end, ss.toString()), "userId");
        setText(sps);
        setSelection(end);
    }

    //生成一个需要整体删除的Span
    private void makeSpan(Spannable sps, UnSpanText unSpanText, String userId) {
        MyTextSpan what = new MyTextSpan(unSpanText.returnText, userId);
        int start = unSpanText.start;
        int end = unSpanText.end;
        sps.setSpan(what, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //向前删除一个字符，@后的内容必须大于一个字符，可以在后面加一个空格
        if (lengthBefore == 1 && lengthAfter == 0) {
            MyTextSpan[] spans = getText().getSpans(0, getText().length(), MyTextSpan.class);
            for (MyTextSpan myImageSpan : spans) {
                if (getText().getSpanEnd(myImageSpan) == start && !text.toString().endsWith(myImageSpan.getShowText())) {
                    getText().delete(getText().getSpanStart(myImageSpan), getText().getSpanEnd(myImageSpan));
                    break;
                }
            }
        }

    }

    private class MyTextSpan extends MetricAffectingSpan {
        private String showText;
        private String userId;

        public MyTextSpan(String showText, String userId) {
            this.showText = showText;
            this.userId = userId;
        }


        public String getShowText() {
            return showText;
        }

        public String getUserId() {
            return userId;
        }

        @Override
        public void updateMeasureState(TextPaint p) {

        }

        @Override
        public void updateDrawState(TextPaint tp) {

        }
    }

    private class UnSpanText {
        int start;
        int end;
        String returnText;

        UnSpanText(int start, int end, String returnText) {
            this.start = start;
            this.end = end;
            this.returnText = returnText;
        }
    }


}
