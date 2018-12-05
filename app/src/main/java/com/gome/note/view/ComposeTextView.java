package com.gome.note.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import java.util.Arrays;


/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/5/15
 * DESCRIBE:
 */

public class ComposeTextView extends TextView {
    private int mLineY;
    private int mViewWidth;

    public ComposeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        TextPaint mPaint = getPaint();
        Paint.FontMetrics fm = mPaint.getFontMetrics();

        float baseline = fm.descent - fm.ascent;
        float x = 0;
        float y = baseline;  //由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。

        String txt = getText().toString();

        //文本自动换行
        String[] texts = autoSplit(txt, mPaint, getWidth());

        System.out.printf("line indexs: %s\n", Arrays.toString(texts));
        for (int i = 0; i < texts.length; i++) {
            String text = texts[i];
            if (texts.length > 2 && i == 1) {
                text = text.substring(0, (text.length() - 3));
                text = text + "...";
            }
            canvas.drawText(text == null ? "" : text, x, y, mPaint);  //坐标以控件左上角为原点
            y += baseline + fm.leading; //添加字体行间距
        }
//        for (String text : texts) {
//
//        }
    }

    /**
     * 自动分割文本
     *
     * @param content 需要分割的文本
     * @param p       画笔，用来根据字体测量文本的宽度
     * @param width   最大的可显示像素（一般为控件的宽度）
     * @return 一个字符串数组，保存每行的文本
     */
    private String[] autoSplit(String content, Paint p, float width) {
        int length = content.length();
        float textWidth = p.measureText(content);
        if (textWidth <= width) {
            return new String[]{content};
        }

        int start = 0, end = 1, i = 0;
        int lines = (int) Math.ceil(textWidth / width); //计算行数
        String[] lineTexts = new String[lines];
        while (start < length) {
            if ((p.measureText(content, start, end) + 40) > width) { //文本宽度超出控件宽度时

                lineTexts[i++] = (String) content.subSequence(start, end);
                start = end;
            }
            if (end == length) { //不足一行的文本
                if (i >= lineTexts.length) {
                    break;
                }
                lineTexts[i] = (String) content.subSequence(start, end);
                break;
            }
            end += 1;
        }
        return lineTexts;
    }
}
