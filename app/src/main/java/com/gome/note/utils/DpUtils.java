package com.gome.note.utils;

import android.content.Context;

/**
 * Created on 2016/12/20.
 *
 * @author pythoncat.cheng from http://outofmemory
 * .cn/code-snippet/2910/Android--xiangsu-px-dp-zhuanhua
 * @apiNote dputils
 */

public class DpUtils {
    public static int dp2Px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2Dp(Context context, float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }
}
