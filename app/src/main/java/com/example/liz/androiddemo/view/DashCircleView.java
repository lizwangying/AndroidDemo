package com.example.liz.androiddemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * desc:
 * Created by Liz on 2017/1/19.
 * github: https://github.com/lizwangying
 */

public class DashCircleView extends View {

    private int strokeColor, strokeWidth, strokeLineWidth, strokeBlankWidth, circleRadius, drawingSpeed, startAngle;
    private boolean ifRepeatDrawing;
    private Paint mPaint;
    private RectF oval1;
    private int mWidth, mHeight, topMargin, bottomMargin, leftMargin, rightMargin;
    RectF rectF;

    //是不是开始绘制下一个圆弧
    private boolean isNext = false;
    //圆弧绘制的速度
    private int mSpeed = 20;

    public DashCircleView(Context context) {
        super(context);
        init();

    }

    public DashCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    public DashCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    private void initAttrs(AttributeSet attributeSet) {
        DashCircleAttrExtractorImpl.Builder builder = new DashCircleAttrExtractorImpl.Builder();
        DashCircleAttrExtractor extractor = builder.withContext(getContext()).withAttributeSet(attributeSet).build();
        strokeColor = extractor.getDashStrokeColor();
        strokeWidth = extractor.getDashStrokeWidth();
        strokeLineWidth = extractor.getStrokeLineWidth();
        strokeBlankWidth = extractor.getStrokeBlankWidth();
        circleRadius = extractor.getCircleRadius();
        drawingSpeed = extractor.getDrawingSpeed();
        ifRepeatDrawing = extractor.getIfRepeatDrawing();
        startAngle = extractor.getStartAngle();

        extractor.recycleAttributes();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) getLayoutParams();
        topMargin = params.topMargin;
        bottomMargin = params.bottomMargin;
        leftMargin = params.leftMargin;
        rightMargin = params.rightMargin;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(strokeColor);
        PathEffect effects = new DashPathEffect(new float[]{strokeLineWidth, strokeBlankWidth}, 1);
        mPaint.setPathEffect(effects);
        //绘图线程
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    drawingSpeed++;
                    if (drawingSpeed == 360 && ifRepeatDrawing) {
                        drawingSpeed = 0;
                        if (!isNext) {
                            isNext = true;
                        } else {
                            isNext = false;
                        }
                        return;
                    }
                    postInvalidate();
                    try {
                        Thread.sleep(mSpeed); //通过传递过来的速度参数来决定线程休眠的时间从而达到绘制速度的快慢
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        rectF = new RectF(-circleRadius, -circleRadius, circleRadius, circleRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.translate(mWidth / 2 + leftMargin - rightMargin, mHeight / 2 + topMargin - bottomMargin);                  //画布坐标原点移动到中心位置
        canvas.drawArc(rectF, startAngle, drawingSpeed, false, mPaint);
    }
}
