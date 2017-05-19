package com.qinjunyuan.zhihu.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.qinjunyuan.zhihu.R;


public class ZhiHuLogoAnimator extends View {
    private float rectSize;
    private RectF rect;
    private Paint paint, textPaint, subTextPaint;
    private int angle;

    public ZhiHuLogoAnimator(Context context, AttributeSet attrs) {
        super(context, attrs);
        rectSize = context.getResources().getDisplayMetrics().density * 45 + 0.5f;
        rect = new RectF(0, 0, rectSize, rectSize);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(20f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        float textSize = context.getResources().getDisplayMetrics().scaledDensity * 20 + 0.5f;
        float subTextSize = context.getResources().getDisplayMetrics().scaledDensity * 14 + 0.5f;
        textPaint = new Paint();
        textPaint.setColor(ContextCompat.getColor(context, R.color.white));
        textPaint.setTextSize(textSize);
        subTextPaint = new Paint();
        subTextPaint.setColor(ContextCompat.getColor(context, R.color.grey_6));
        subTextPaint.setTextSize(subTextSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureHeight(int heightMeasureSpec) {
        int result;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            int i1 = (int) (rectSize + getPaddingTop() + getPaddingBottom());
            int i2 = (int) (textPaint.descent() - textPaint.getFontMetrics().top
                    + subTextPaint.descent() - subTextPaint.getFontMetrics().top + getPaddingTop() + getPaddingBottom());
            result = Math.max(i1, i2);
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.scale(0.5f, 0.5f, rectSize / 2, rectSize / 2);
        canvas.drawArc(rect, 90, angle, false, paint);
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
        invalidate();
    }
}
