package com.liz.wangying.svgpathview.clippingtransforms;

/**
 * desc: 波形图案
 * Created by Liz on 2017/2/22.
 * github: https://github.com/lizwangying
 */

public interface TransformFactory {
    ClippingTransform getClippingTransform(int waveType);
}
