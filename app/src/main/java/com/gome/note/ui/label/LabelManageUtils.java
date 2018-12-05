package com.gome.note.ui.label;

import android.content.Context;

import com.gome.note.R;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/8/13
 * DESCRIBE:
 */

public class LabelManageUtils {

    public static boolean isVoiceMemosLable(Context context, String title) {
        if (null == title) {
            return false;
        }
        if ((context.getString(R.string.lable_type_record_en).equals(title))
                || (context.getString(R.string.lable_type_record_cn).equals(title))) {
            return true;
        }
        return false;
    }




}
