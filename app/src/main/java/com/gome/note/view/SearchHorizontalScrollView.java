package com.gome.note.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * ProjectName:MyPocket_NEW
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/11/11
 * DESCRIBE:
 */

public class SearchHorizontalScrollView extends HorizontalScrollView {
    private final int DEFAULT_EDITEXT_MIN_WIDTH = 300;
    private final int DEFAULT_HORSCROLLVIEW_MIN_WIDTH = 693;
    private final int DEFAULT_HORSCROLLVIEW_MIN_WIDTH_First = 780;

    public SearchHorizontalScrollView(Context context) {
        super(context);
    }

    public SearchHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchHorizontalScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setEditextMinWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    @Override
    public void requestLayout() {
        super.requestLayout();


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    private void setEditextMinWidth() {
        int childCount = this.getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View view = this.getChildAt(i);
                if (view instanceof LinearLayout) {
                    int liChildCount = ((LinearLayout) view).getChildCount();
                    int add = 0;

                    for (int j = 0; j < liChildCount; j++) {
                        View viewChild = ((LinearLayout) view).getChildAt(j);

                        if (j == (liChildCount - 1)) {
                            add = add;
                        } else {
                            add = viewChild.getMeasuredWidth() + add;
                        }

                        if (viewChild instanceof NotesEditText) {

                            if (add >= this.getMeasuredWidth()) {
                                int addTemp = add - this.getMeasuredWidth();
                                if (addTemp < DEFAULT_EDITEXT_MIN_WIDTH) {
                                    addTemp = DEFAULT_EDITEXT_MIN_WIDTH;
                                }
                                ((NotesEditText) viewChild).setMinWidth(this.getMeasuredWidth() == 0 ? DEFAULT_HORSCROLLVIEW_MIN_WIDTH_First : addTemp);
                            } else {
                                ((NotesEditText) viewChild).setMinWidth(this.getMeasuredWidth() == 0 ? DEFAULT_HORSCROLLVIEW_MIN_WIDTH_First : (this.getMeasuredWidth() - add));
                            }
                        }
                    }
                }
            }
        }
    }
}
