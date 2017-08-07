本来有大神实现，可以代码运行有问题，还有很多同学迷惑不知道怎么使用，自己解决问题之后，分享给大家。

原文：[android textview 自动换行 整齐排版](http://www.cnblogs.com/goagent/p/5159125.html?utm_source=tuicool&utm_medium=referral)

上效果图：

![image](https://github.com/lizwangying/AndroidDemo/raw/AutoAplitTextView/screenshots/demo_auto_split_tv.png)  

通过自定义一个TextView实现，使用起来也很简单。

//使用了databinding
```
binding.autoSplitTv.post(new Runnable() {
    @Override
    public void run() {
        binding.autoSplitTv.setText(binding.autoSplitTv.autoSplitText(binding.autoSplitTv, "第一步、"));
    }
});
//没使用 databinding ,你需要先 findViewById ,假设叫 autoTv
autoTv.post(new Runnable() {
    @Override
    public void run() {
        autoTv.setText(autoTv.autoSplitText(autoTv, "第一步、"));
    }
});
```
很多同学在原博主地下留言，大神啊 ，为啥我运行黑屏啊？ 大家互相拥抱道：“你黑我也黑啊？”

我就问了我老大，非UI线程更新UI，必须要 post 呀，问完觉得自己好蠢，老大一眼识破，好棒。

上关键代码：

/**
 * 自动缩进方法，外部调用
 * @param tv TextView对象，在xml中必须得使用自定义的这个，至于参数为啥是 TextView ，其实你换成自己也没问题。
 * @param indent 在文本之后缩进，比如你需要缩进 1. 就传入 "1." 字符串就好, 会测量indent 的宽度，以他的宽度缩进
 * @return 返回缩进完了之后的 字符串，所以你的 setText 哦，傻傻的盯着屏幕，还问为啥不好使。返回的字符你没有 set 呀，傻子
 */
public String autoSplitText(final TextView tv, final String indent) {
    final String rawText = tv.getText().toString();//原始文本
    final Paint paint = tv.getPaint();//画笔，还包含字体信息
    int a = tv.getPaddingLeft();
    int b = tv.getPaddingRight();
    int c = tv.getWidth();
    if (c != 0) {
        final float tvWidth = c - a - b;//空间可用宽度

        //将缩进处理成空格
        String indentSpace = "";
        float indentWidth = 0;
        if (!TextUtils.isEmpty(indent)) {
            float rawIndentWidth = paint.measureText(indent);
            if (rawIndentWidth < tvWidth) {
                while ((indentWidth = paint.measureText(indentSpace)) < rawIndentWidth) {
                    indentSpace += " ";
                }
            }
        }

        //将原始文本按行拆分
        String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (paint.measureText(rawTextLine) <= tvWidth) {
                //如果行宽度在空间范围之内，就不处理了
                sbNewText.append(rawTextLine+"\n");
            } else {
                //否则按字符测量，在超过可用宽度的前一个字符处，手动替换，加上换行，缩进
                float lineWidth = 0;
                for (int i = 0; i != rawTextLine.length(); ++i) {
                    char ch = rawTextLine.charAt(i);
                    //从手动换行的第二行开始加上缩进
                    if (lineWidth < 0.1f && i != 0) {
                        sbNewText.append(indentSpace);
                        lineWidth += indentWidth;
                    }
                    float textWidth = paint.measureText(String.valueOf(ch));
                    lineWidth += textWidth;
                    if (lineWidth < tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --i;
                    }
                }
                sbNewText.append("\n");
            }
        }
        //结尾多余的换行去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }
        Log.e("haha", sbNewText.toString());
        return sbNewText.toString();
    }else {
        return "";
    }
}

另外，textview 的 text放到 string 里面，换行用 \n



<string name="process">把一只长颈鹿放进冰箱要几步？ \n把长颈鹿放到冰箱里\n
第一步：把冰箱门打开,瞅瞅有啥好吃的没，有，先吃光再说，昨晚的剩面条先吃了，前天的炸鸡排也赶紧吃了\n
第二步：把长颈鹿放进去，如果反抗，可以考虑里面放点好吃的引诱进去\n
第三步：把门关上\n</string>
    <string name="process2">把大象放到冰箱里\n
1：把冰箱门打开,瞅瞅有啥好吃的没，有，先吃光再说，昨晚的剩面条先吃了，前天的炸鸡排也赶紧吃了\n
2：把长颈鹿拿出来\n
3：把大象放进去，如果反抗，可以考虑里面放点好吃的引诱进去\n
4：把门关上\n
森林开会，大象没去,因为它在冰箱里\n
去到一条平时很多鳄鱼的河，但是没桥，你要怎么过河！\n
答：游过去，怎么过都可以，因为鳄鱼去开会去了。</string>    

github自定义view链接：
[TextView实现动态缩进](https://github.com/lizwangying/SomeCodeDemo/blob/AutoSplitTextView/AutoSplitTextView.java)
 谢谢你的star~