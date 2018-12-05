package com.gome.note.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

/**
 * Created by Administrator on 2018-10-26.
 */

public class SetResizeHeigth {

    public static void assistActivity(View content) {
        new SetResizeHeigth(content);
    }

    private View mChildOfContent;
    private int usableHeightPrevious;
    private ViewGroup.LayoutParams frameLayoutParams;

    private SetResizeHeigth(View content) {
        if (content != null) {
            mChildOfContent = content;
            mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    possiblyResizeChildOfContent();
                }
            });
            frameLayoutParams = mChildOfContent.getLayoutParams();
        }
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            //if twice height is diff
            // calculate`s visual height = view`s height
            frameLayoutParams.height = usableHeightNow;
            mChildOfContent.requestLayout();
            usableHeightPrevious = usableHeightNow;
        }
    }

    private int computeUsableHeight() {
        //calculate visual height
        Rect r = new Rect();
        mChildOfContent.getWindowVisibleDisplayFrame(r);
        return (r.bottom);
    }

}
