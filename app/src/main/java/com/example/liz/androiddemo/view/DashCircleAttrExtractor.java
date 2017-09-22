package com.example.liz.androiddemo.view;

/**
 * desc: 获取 xml 中的 attribute 属性
 * Created by Liz on 2017/1/22.
 * github: https://github.com/lizwangying
 */

public interface DashCircleAttrExtractor {
    int getDashStrokeColor();

    int getDashStrokeWidth();

    int getStrokeLineWidth();

    int getStrokeBlankWidth();

    int getCircleRadius();

    int getDrawingSpeed();

    int getStartAngle();

    boolean getIfRepeatDrawing();

    //循环绘制
    void recycleAttributes();

    void release();

}
