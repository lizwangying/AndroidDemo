package com.liz.wangying.svgpathview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.liz.wangying.svgpathview.clippingtransforms.ClippingTransform;
import com.liz.wangying.svgpathview.clippingtransforms.TransformFactory;
import com.liz.wangying.svgpathview.clippingtransforms.TransformFactoryImpl;

import java.lang.ref.WeakReference;

/**
 * desc:
 * Created by Liz on 2017/1/18.
 * github: https://github.com/lizwangying
 */

public class SVGAttributeExtractorImpl implements SVGAttributeExtractor {
    private WeakReference<Context> weakContext;
    private WeakReference<AttributeSet> weakAttrs;
    private WeakReference<TypedArray> weakAttributeArray;
    private TransformFactory transformFactory;

    public SVGAttributeExtractorImpl(WeakReference<Context> weakContext, WeakReference<AttributeSet> weakAttrs) {
        this.weakContext = weakContext;
        this.weakAttrs = weakAttrs;
        this.transformFactory = new TransformFactoryImpl();
    }

    private Context context() {
        return weakContext.get();
    }

    private TypedArray attributeArray() {
        if (weakAttributeArray == null)
            weakAttributeArray = new WeakReference<>(context().getTheme().obtainStyledAttributes(
                    weakAttrs.get(), R.styleable.SVGPathView, 0, 0
            ));
        return weakAttributeArray.get();
    }

    @Override
    public int getStrokeColor() {
        return attributeArray().getColor(R.styleable.SVGPathView_strokeColor
                , Color.BLACK);
    }

    @Override
    public int getFillColor() {
        return attributeArray().getColor(R.styleable.SVGPathView_fillColor
                , Color.BLUE);
    }

    @Override
    public int getStrokeWidth() {
        return attributeArray().getDimensionPixelSize(R.styleable.SVGPathView_strokeWidth
                , 1);
    }

    @Override
    public int getOriginalWidth() {
        return attributeArray().getInteger(R.styleable.SVGPathView_originalWidth
                , -1);
    }

    @Override
    public int getOriginalHeight() {
        return attributeArray().getInteger(R.styleable.SVGPathView_originalHeight
                , -1);
    }

    @Override
    public int getStrokeDrawingDuration() {
        return attributeArray().getInteger(R.styleable.SVGPathView_strokeDrawingDuration
                , 2000);
    }

    @Override
    public int getFillDuration() {
        return attributeArray().getInteger(R.styleable.SVGPathView_fillDuration
                , 0);
    }

    @Override
    public int getTraceLineColor() {
        return attributeArray().getColor(R.styleable.SVGPathView_traceLineColor, Color.BLACK);
    }

    @Override
    public int getTraceLineWidth() {
        return attributeArray().getDimensionPixelSize(R.styleable.SVGPathView_traceLineWidth, 0);
    }

    @Override
    public ClippingTransform getClippingTransform() {
        int value = attributeArray().getInteger(R.styleable.SVGPathView_clippingTransform, 0);
        return transformFactory.getClippingTransform(value);
    }

    //默认百分百
    @Override
    public int getFillPercentage() {
        return attributeArray().getInteger(R.styleable.SVGPathView_fillPercentage, 100);
    }

    @Override
    public boolean getNeedFillProgress() {
        return attributeArray().getBoolean(R.styleable.SVGPathView_needFillProgress, false);
    }

    @Override
    public void recycleAttributes() {
        if (weakAttributeArray != null) {
            weakAttributeArray.get().recycle();
        }
    }

    @Override
    public void release() {
        weakAttributeArray = null;
        weakContext = null;
        weakAttrs = null;
    }

    public static class Builder {
        private WeakReference<Context> weakContext;
        private WeakReference<AttributeSet> weakAttrs;

        public Builder with(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("context 不能为空");
            }
            weakContext = new WeakReference<Context>(context);
            return this;
        }

        public Builder with(AttributeSet attributeSet) {
            if (attributeSet == null) {
                throw new IllegalArgumentException("attributeSet 不能为空");
            }
            weakAttrs = new WeakReference<AttributeSet>(attributeSet);
            return this;
        }

        public SVGAttributeExtractorImpl build() {
            return new SVGAttributeExtractorImpl(weakContext, weakAttrs);
        }
    }
}
