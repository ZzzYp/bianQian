package com.gome.note.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.gome.note.R;


/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/21
 * DESCRIBE:
 */

public class EditTextClearUtils {


    public static void drawRigthClick(final EditText editText) {
        editText.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Drawable drawable = editText.getCompoundDrawables()[2];
                if (drawable == null) {
                    return false;
                } else if (event.getAction() != 1) {
                    return false;
                } else {
                    if (event.getX() > (float) (editText.getWidth() - editText.getPaddingRight()
                            - drawable.getIntrinsicWidth())) {
                        editText.setText("");
                    }

                    return false;
                }
            }
        });
    }

    public static void setClearIconVisible(Context context, boolean visible, EditText editText) {

        Drawable mClearDrawable = editText.getCompoundDrawables()[2];
        if (mClearDrawable == null) {
            mClearDrawable = context.getResources().getDrawable(R.drawable
                    .launch_folder_edit_delete);
        }

        mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable
                .getIntrinsicHeight());

        Drawable right = visible ? mClearDrawable : null;
        editText.setCompoundDrawables(editText.getCompoundDrawables()[0], editText.
                getCompoundDrawables()[1], right, editText.getCompoundDrawables()[3]);
    }

}
