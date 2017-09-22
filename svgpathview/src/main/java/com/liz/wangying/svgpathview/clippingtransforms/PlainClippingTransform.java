package com.liz.wangying.svgpathview.clippingtransforms;

import android.graphics.Canvas;
import android.view.View;

/**
 * desc: 平静的波
 * Created by Liz on 2017/2/22.
 * github: https://github.com/lizwangying
 */

public class PlainClippingTransform implements ClippingTransform {
    @Override
    public void transform(Canvas canvas, float currentFillPhase, View view) {
        //默认为 Region.Op.INTERSECT,就是显示交集
        canvas.clipRect(0, (view.getBottom() - view.getTop()) * (1f - currentFillPhase),
                view.getRight(), view.getBottom());
    }
}
