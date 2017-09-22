package com.liz.wangying.svgpathview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.liz.wangying.svgpathview.clippingtransforms.ClippingTransform;
import com.liz.wangying.svgpathview.parser.ConstrainedSvgPathParser;
import com.liz.wangying.svgpathview.parser.SvgPathParser;

import java.text.ParseException;

/**
 * desc: Path变成View
 * Created by Liz on 2017/1/17.
 * github: https://github.com/lizwangying
 */

public class SVGPathView extends View {
    public static final int NOT_STARTED = 0;
    public static final int STROKE_STARTED = 1;
    public static final int FILL_STARTED = 2;
    public static final int FINISHED = 3;
    private int strokeColor, fillColor, strokeWidth, traceLineColor;
    private int originalWidth, originalHeight, traceLineWidth;
    private int strokeDrawingDuration, fillDuration;
    private int drawingState;
    private int viewWidth;
    private int viewHeight;
    private String svgPath;// svg 的 path 哦
    private Paint dashPaint, fillPaint;
    private Interpolator animInterpolator;
    private SVGPathData pathData;
    private long initialTime;//初始化时间
    private SVGStateChangedListener stateChangeListener;

    private ClippingTransform clippingTransform;
    /**
     * Whether the percentage mode is enabled or not. When the percentage mode is enabled then the
     * filling animation will cover part of the loader, up to the {@link #percentage} value.
     */
    private boolean percentageEnabled;

    /**
     * The percentage that this view should load up to.
     */
    private float percentage;

    /**
     * The percentage that the previous {@link #onDraw(Canvas)} displayed on the screen.
     */
    private float previousFramePercentage;

    /**
     * The time in millis when the {@link #previousFramePercentage} was displayed on the screen.
     */
    private long previousFramePercentageTime;
    private boolean ifFill;//是否需要填充，默认不需要，需要必须要在xml中改成true，暂时添加这个属性，以后优化，没想到更好的idea

    public SVGPathView(Context context) {
        super(context);
        init();
    }

    public SVGPathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init();
    }

    public SVGPathView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init();
    }

    private void init() {
        drawingState = NOT_STARTED;

        initDashPaint();
        initFillPaint();
        animInterpolator = new AccelerateInterpolator();
        //这个据我所知是为了防止 XFermode 不好使
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void initAttrs(AttributeSet attrs) {
        SVGAttributeExtractorImpl.Builder extractorBuilder = new SVGAttributeExtractorImpl.Builder();
        SVGAttributeExtractorImpl extractor = extractorBuilder.with(getContext()).with(attrs).build();
        fillColor = extractor.getFillColor();
        strokeColor = extractor.getStrokeColor();
        strokeWidth = extractor.getStrokeWidth();
        originalHeight = extractor.getOriginalHeight();
        originalWidth = extractor.getOriginalWidth();
        strokeDrawingDuration = extractor.getStrokeDrawingDuration();
        fillDuration = extractor.getFillDuration();
        traceLineColor = extractor.getTraceLineColor();
        traceLineWidth = extractor.getTraceLineWidth();
        clippingTransform = extractor.getClippingTransform();
        percentage = extractor.getFillPercentage();
        if (percentage != 100) {
            percentageEnabled = true;
        }
        ifFill = extractor.getNeedFillProgress();
        extractor.recycleAttributes();

    }

    private void initDashPaint() {
        dashPaint = new Paint();
        dashPaint.setStyle(Paint.Style.STROKE);
        dashPaint.setStrokeCap(Paint.Cap.ROUND);
        dashPaint.setAntiAlias(true);
        dashPaint.setStrokeWidth(strokeWidth);
        dashPaint.setColor(strokeColor);

    }

    private void initFillPaint() {
        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(fillColor);
    }

    private void checkRequirements() {
        checkOriginalDimensions();
        checkPath();
    }

    private void checkOriginalDimensions() {
        if (originalWidth <= 0 || originalHeight <= 0) {
            throw new IllegalArgumentException(
                    "嘿，兄弟，你必须提供 svg 的原来的尺寸");
        }
    }

    private void checkPath() {
        if (pathData == null) {
            throw new IllegalArgumentException(
                    "嘿，兄弟，你必须提供一个 svg path 我才能画好么");
        }
    }

    /**
     * 开始绘制 stroke 啦
     */
    public void start() {
        checkRequirements();
        initialTime = System.currentTimeMillis();
        changeState(STROKE_STARTED);
        invalidate();
        //Cause an invalidate to happen on the next animation time step, typically the next display frame.
        //翻译一下，就是 当下一次动画发生的时候重绘界面，一般是下一帧的时候
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 重置 ， stroke 一样消失掉
     */
    public void reset() {
        initialTime = 0;
        changeState(NOT_STARTED);
        ViewCompat.postInvalidateOnAnimation(this);
    }

    /**
     * 改变生命周期并且修改生命周期的listener中的state
     *
     * @param SVGViewState
     */
    private void changeState(int SVGViewState) {
        if (drawingState == SVGViewState) return;
        drawingState = SVGViewState;
        if (stateChangeListener != null) {
            stateChangeListener.onStateChanged(SVGViewState);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
        buildPathData();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //判断能不能画，主要是不能再 SVGViewState 的没开始和 path 为空的时候画
        if (!hasToDraw()) return;
        long elapsedTime = System.currentTimeMillis() - initialTime;
        //开始画轮廓
        drawStroke(canvas, elapsedTime);
        //开始画填充内容
        if (ifFill) {
            drawFill(canvas, elapsedTime);
        }

        if (hasToKeepDrawing(elapsedTime)) {
            ViewCompat.postInvalidateOnAnimation(this);
        } else {
            changeState(FINISHED);
        }
    }

    private void drawFill(Canvas canvas, long elapsedTime) {
        // 开始画填充内容
        if (isStrokeTotallyDrawn(elapsedTime)) {
            if (drawingState < FILL_STARTED) {
                changeState(FILL_STARTED);
                previousFramePercentageTime = System.currentTimeMillis() - initialTime;
            }
            float fillPhase;
            if (percentageEnabled) {
                fillPhase = getFillPhaseForPercentage(elapsedTime);
            } else {
                fillPhase = getFillPhaseWithoutPercentage(elapsedTime);
            }
            clippingTransform.transform(canvas, fillPhase, this);//裁剪画布大小，为轮廓和你自定义的波形重合的部分
            canvas.drawPath(pathData.path, fillPaint);//画出来的直接就是svg的轮廓，实心的。。。
        }
    }

    private float getFillPhaseForPercentage(long elapsedTime) {
        float fillPhase = constrain(0, percentage / 100,
                previousFramePercentage / 100 + ((float) (elapsedTime - previousFramePercentageTime)
                        / fillDuration));
        previousFramePercentage = fillPhase * 100;
        previousFramePercentageTime = System.currentTimeMillis() - initialTime;
        return fillPhase;
    }

    private float getFillPhaseWithoutPercentage(long elapsedTime) {
        return constrain(0, 1, (float) (elapsedTime - strokeDrawingDuration) / fillDuration);
    }

    public boolean hasToDraw() {
        return !(drawingState == NOT_STARTED || pathData == null);
    }

    //这个patheffect 只会对STROKE或者FILL_AND_STROKE的paint style产生影响。如果style == FILL它会被忽略掉。
    private void drawStroke(Canvas canvas, long elapsedTime) {
        float phase = constrain(0, 1, elapsedTime * 1f / strokeDrawingDuration);
        float distance = animInterpolator.getInterpolation(phase) * pathData.length;

        if (traceLineWidth > 0) {
            dashPaint.setColor(traceLineColor);
            dashPaint.setPathEffect(new DashPathEffect(new float[]{0, distance, phase > 0 ? traceLineWidth : 0, pathData.length}, 0));
            canvas.drawPath(pathData.path, dashPaint);
        }
        dashPaint.setColor(strokeColor);
        dashPaint.setPathEffect(getDashPathForDistance(distance));
        canvas.drawPath(pathData.path, dashPaint);
    }

    public boolean isStrokeTotallyDrawn(long elapsedTime) {
        return elapsedTime > strokeDrawingDuration;
    }

    private PathEffect getDashPathForDistance(float distance) {
        return new DashPathEffect(new float[]{distance, pathData.length}, 0);
    }

    private void buildPathData() {
        SvgPathParser parser = getPathParser();
        pathData = new SVGPathData();
        if (!TextUtils.isEmpty(svgPath)) {
            try {
                pathData.path = parser.parsePath(svgPath);
            } catch (ParseException e) {
                pathData.path = new Path();
            }
        } else {
//            throw new IllegalArgumentException("嘿，兄弟，你必须提供一个 svg path 我才能画好么");


        }
        // true 不管 path 是否闭合，都自动闭合，如果可以的话...但是他不会影响原有 path 的状态
        // 当不是闭合的 path 强设置为 true 的时候可能会影响测量长度，可能会测量偏大，因为获取到的是闭合的状态的长度
        PathMeasure pm = new PathMeasure(pathData.path, true);
        while (true) {
            pathData.length = Math.max(pathData.length, pm.getLength());
            //nextContour() 跳转到下一个轮廓，跳转成功true否则。。。
            if (!pm.nextContour()) {
                break;
            }
        }

    }

    public void setSvgPath(String svgPath) {
        if (svgPath == null || svgPath.length() == 0) {
            throw new IllegalArgumentException("嘿，兄弟，你必须提供一个 svg path 我才能画好么");
        }
        this.svgPath = svgPath;
        buildPathData();
    }

    /**
     * 判断有没有画完
     *
     * @param elapsedTime
     * @return
     */
    private boolean hasToKeepDrawing(long elapsedTime) {
//        if (percentageEnabled) {
//            return previousFramePercentage < 100;
//        } else {
        return elapsedTime < strokeDrawingDuration + fillDuration;
//        }
    }

    private SvgPathParser getPathParser() {
        ConstrainedSvgPathParser.Builder builder = new ConstrainedSvgPathParser.Builder();
        return builder.originalWidth(originalWidth)
                .originalHeight(originalHeight)
                .viewWidth(viewWidth)
                .viewHeight(viewHeight)
                .build();
    }

    /**
     * 获得取值范围在 min 和 max 之间的值
     *
     * @param min 最小值
     * @param max 最大值
     * @param v   变量
     * @return 满足条件的值
     */
    public float constrain(float min, float max, float v) {
        return Math.max(min, Math.min(max, v));
    }

    public void setOnStateChangeListener(SVGStateChangedListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public interface SVGStateChangedListener {
        void onStateChanged(int state);
    }

    class SVGPathData {
        Path path;
        float length;
    }


}
