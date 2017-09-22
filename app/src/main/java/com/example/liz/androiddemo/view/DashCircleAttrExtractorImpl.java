package com.example.liz.androiddemo.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;

import com.example.liz.androiddemo.R;

import java.lang.ref.WeakReference;

/**
 * desc: 接口实现类
 * Created by Liz on 2017/1/22.
 * github: https://github.com/lizwangying
 */

public class DashCircleAttrExtractorImpl implements DashCircleAttrExtractor {
    private WeakReference<Context> weakContext;
    private WeakReference<AttributeSet> weakAttrs;
    private WeakReference<TypedArray> weakAttributeArray;

    public DashCircleAttrExtractorImpl(WeakReference<Context> weakContext, WeakReference<AttributeSet> weakAttrs) {
        this.weakAttrs = weakAttrs;
        this.weakContext = weakContext;
    }

    private Context context() {
        return weakContext.get();
    }

    private TypedArray attributeArray() {
        if (weakAttributeArray == null) {
            weakAttributeArray = new WeakReference<TypedArray>(context().getTheme().obtainStyledAttributes(weakAttrs.get()
                    , R.styleable.DashCircleView, 0, 0));
        }
        return weakAttributeArray.get();
    }

    @Override
    public int getDashStrokeColor() {
        return attributeArray().getColor(R.styleable.DashCircleView_dashStrokeColor,
                Color.BLACK);
    }

    @Override
    public int getDashStrokeWidth() {
        return attributeArray().getDimensionPixelSize(R.styleable.DashCircleView_dashStrokeWidth,
                10);
    }

    @Override
    public int getStrokeLineWidth() {
        return attributeArray().getDimensionPixelSize(R.styleable.DashCircleView_strokeLineWidth,
                15);
    }

    @Override
    public int getStrokeBlankWidth() {
        return attributeArray().getDimensionPixelSize(R.styleable.DashCircleView_strokeBlankWidth,
                10);
    }

    @Override
    public int getCircleRadius() {
        return attributeArray().getDimensionPixelSize(R.styleable.DashCircleView_circleRadius
                , 100);
    }


    @Override
    public int getDrawingSpeed() {
        return attributeArray().getInteger(R.styleable.DashCircleView_drawingSpeed,
                20);
    }


    @Override
    public int getStartAngle() {
        //默认从坐标系-90度开始画
        return attributeArray().getInteger(R.styleable.DashCircleView_startAngle,-90);
    }

    @Override
    public boolean getIfRepeatDrawing() {
        return attributeArray().getBoolean(R.styleable.DashCircleView_ifRepeatDrawing, false);
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

        public Builder withContext(Context context) {
            if (context == null) {
                throw new IllegalArgumentException("context不能为空");
            }
            weakContext = new WeakReference<Context>(context);
            return this;
        }

        public Builder withAttributeSet(AttributeSet attributeSet) {
            if (attributeSet == null) {
                throw new IllegalArgumentException("attributeSet 不能为空");
            }
            weakAttrs = new WeakReference<AttributeSet>(attributeSet);
            return this;
        }

        public DashCircleAttrExtractorImpl build() {
            return new DashCircleAttrExtractorImpl(weakContext, weakAttrs);
        }
    }
}
