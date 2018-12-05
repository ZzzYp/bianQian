package com.gome.note.manager;

import android.graphics.Bitmap;

import com.blankj.utilcode.util.SizeUtils;


public class EditNoteConstans {

    // 图片标记的前便签和后标签
    public static String imageTabBefore="<image>";
    public static String imageTabAfter="</image>";

    // 图片距离左右的总距离
    public static float imageMargin= SizeUtils.dp2px(32);

    // 分享时的水印文字
    public static String watermarkText="";

    public static Bitmap shareBitmap;
    public static Bitmap shareTempBitmap;
}
