package com.daqsoft.module_project.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zachary on 2020/04/23.
 * 页面添加水印
 */
public class WaterMarkBgView extends Drawable {

    private Paint paint = new Paint();
    private List<String> labels=new ArrayList<>();
    private Context context;
    private int degress;    // 角度
    private int fontSize;   // 字体大小 单位sp

    /**
     * 初始化构造
     *
     * @param context  上下文
     * @param degress  水印角度
     * @param fontSize 水印文字大小
     */
    public WaterMarkBgView(Context context,String name, int degress, int fontSize) {
        this.labels.add(name);
        this.context = context;
        this.degress = degress;
        this.fontSize = fontSize;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        int width = getBounds().right;
        int height = getBounds().bottom;

        // 画布背景色
        canvas.drawColor(Color.parseColor("#ffffff"));
        // 水印文字的颜色
        paint.setColor(Color.parseColor("#50AEAEAE"));
        paint.setAntiAlias(true);
        paint.setTextSize(sp2px(context, fontSize));
        canvas.save();
        canvas.rotate(degress);
        float textWidth = paint.measureText(labels.get(0));
        int index = 0;
        for (int positionY = height / 10; positionY <= height; positionY += height / 10 + 80) {
            float fromX = -width + (index++ % 2) * textWidth;
            for (float positionX = fromX; positionX < width; positionX += textWidth * 2) {
                int spacing = 0;//间距
                for (String label : labels) {
                    canvas.drawText(label, positionX, positionY + spacing, paint);
                    spacing = spacing + 50;
                }
            }
        }
        canvas.restore();
    }

    @Override
    public void setAlpha(@IntRange(from = 0, to = 255) int alpha) {
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }


    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
}