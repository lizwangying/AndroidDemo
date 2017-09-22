package com.example.liz.androiddemo;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.liz.androiddemo.databinding.ActivitySvgPathViewBinding;
import com.example.liz.androiddemo.view.LogoPaths;
import com.liz.wangying.svgpathview.SVGPathView;

public class SvgPathViewActivity extends AppCompatActivity {
    private ActivitySvgPathViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_svg_path_view);
        binding.svgPathView1.setSvgPath(LogoPaths.MoneyLogo);
        binding.svgPathView2.setSvgPath(LogoPaths.PIGGY);
        binding.svgPathView3.setSvgPath(LogoPaths.CLOCK);
        binding.svgPathView4.setSvgPath(LogoPaths.ONE_HUNDRED);
        binding.animatedSvgView.setSvgPath(LogoPaths.COMPLICATED_LOGO);
        binding.animatedSvgView.setOnStateChangeListener(new SVGPathView.SVGStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == SVGPathView.STROKE_STARTED) {
                    binding.svgPathView1.start();
                    binding.svgPathView2.start();
                    binding.svgPathView3.start();
                    binding.svgPathView4.start();
                } else if (state == SVGPathView.FINISHED) {
                    bootImageAnimation();
                }
            }
        });
        binding.animatedSvgView.start();
    }

    /**
     * 启动页动画
     */
    private void bootImageAnimation() {
        //文字先隐藏 稍后渐变出现
        binding.tvWonenglc.setVisibility(View.INVISIBLE);
        binding.svgLogoRed.setSvgPath(getResources().getString(R.string.svg_logo_triangle));
        binding.svgLogoRed.setOnStateChangeListener(new SVGPathView.SVGStateChangedListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == SVGPathView.FILL_STARTED) {
                    // 位移
                    TranslateAnimation translateAnimation = new TranslateAnimation(
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, -0.5f
                    );
                    translateAnimation.setDuration(400);
                    translateAnimation.setFillAfter(true);
                    // 渐变
                    final AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
                    alphaAnimation.setDuration(400);
                    alphaAnimation.setFillAfter(true);
                    translateAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            //位移开始的同时 渐变一起执行
                            binding.tvWonenglc.startAnimation(alphaAnimation);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
//                    // 执行位移动画
                    binding.fillContainer.startAnimation(translateAnimation);
                }
            }
        });
        binding.svgLogoRed.start();
    }
}
