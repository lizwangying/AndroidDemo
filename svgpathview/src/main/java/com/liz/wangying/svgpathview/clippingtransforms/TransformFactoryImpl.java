package com.liz.wangying.svgpathview.clippingtransforms;

/**
 * desc:
 * Created by Liz on 2017/2/22.
 * github: https://github.com/lizwangying
 */

public class TransformFactoryImpl implements TransformFactory {


    @Override
    public ClippingTransform getClippingTransform(int waveType) {
        switch (waveType) {
            case FillWaveType.PLAIN:
                return new PlainClippingTransform();
            case FillWaveType.WAVES:
                return new WavesClippingTransform();

            default:
                return new WavesClippingTransform();
        }
    }
}
