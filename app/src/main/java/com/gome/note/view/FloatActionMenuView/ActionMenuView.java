package com.gome.note.view.FloatActionMenuView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.gome.note.R;


/**
 * ProjectName:Handler_DEMO_Go
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/4
 * DESCRIBE:
 */
@SuppressLint("RestrictedApi")
public class ActionMenuView extends LinearLayout implements CustomMenuBuilder.ItemInvoker, CustomMenuView {
    private CustomMenuBuilder mMenu;
    private int mGeneratedItemMargin = 0;
    private int mOneItemMargin = 0;
    private int mTwoItemMargin = 0;
    private int mThreeItemMargin = 0;
    private int mFourItemMargin = 0;
    private int mFiveItemMargin = 0;

    private int mSideMarginOfTow = 0;
    private int mSideMarginOfThree = 0;
    private int mSideMarginOfFour = 0;
    private int mSideMarginOfFive = 0;

    private static final int ONE_ITEM_MARGIN = 15;

    private int mItemSizeDefault = 0;
    private int mItemWithOfFive = 0;

    private static final int ITEM_SIZE_DEFAULT = 72;
    private static final double MAX_DISPLAY_DENSITY = 3.375;
    private boolean mReserveOverflow;

    public ActionMenuView(Context context) {
        super(context);
        this.init(context);
    }

    public ActionMenuView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public ActionMenuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init(context);
    }

    public ActionMenuView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.init(context);

    }

    private void init(Context context) {
        setBaselineAligned(false);
        //为了只让ViewGrop的点击事件只被目标子视图获取，则设置为false
        setMotionEventSplittingEnabled(false);
        float density = context.getResources().getDisplayMetrics().density;
        mGeneratedItemMargin = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_default);
        mTwoItemMargin = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_two);
        mThreeItemMargin = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_three);
        mFourItemMargin = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_four);
        mFiveItemMargin = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_margin_five);
        mSideMarginOfTow = (int) context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_two);
        mSideMarginOfThree = (int) context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_three);
        mSideMarginOfFour = (int) context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_four);
        mSideMarginOfFive = (int) context.getResources().getDimension(R.dimen.gome_float_menu_side_margin_five);

        mItemWithOfFive = (int) context.getResources().getDimension(R.dimen.gome_float_menu_item_width_five);

        mOneItemMargin = (int) (ONE_ITEM_MARGIN * density);
        mItemSizeDefault = (int) (ITEM_SIZE_DEFAULT * density);
        if (getBackground() == null) {
            Drawable background = getResources().getDrawable(R.drawable.gome_floataction_bg, null);
            setBackground(background);
        }
    }

    /**
     * @hide
     */
    public void setOverflowReserved(boolean reserveOverflow) {
        mReserveOverflow = reserveOverflow;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_VERTICAL;
        return params;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        if (p != null) {
            final LayoutParams result = p instanceof LayoutParams
                    ? new LayoutParams((LayoutParams) p)
                    : new LayoutParams(p);
            if (result.gravity <= Gravity.NO_GRAVITY) {
                result.gravity = Gravity.CENTER_VERTICAL;
            }
            return result;
        }
        return generateDefaultLayoutParams();
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    public LayoutParams generateOverflowButtonLayoutParams() {
        LayoutParams result = generateDefaultLayoutParams();
        result.width = (int) getContext().getResources().getDimension(R.dimen.gome_float_menu_item_width);
        result.height = (int) getContext().getResources().getDimension(R.dimen.gome_float_menu_item_height);
        result.isOverflowButton = true;
        return result;
    }

    @Override
    public boolean invokeItem(CustomMenuItemImpl item) {
        return mMenu.performItemAction(item, 0);
    }

    @Override
    public void initialize(CustomMenuBuilder menu) {
        mMenu = menu;
    }

    @Override
    public int getWindowAnimations() {
        return 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int childCount = getChildCount();
        // Previous measurement at exact format may have set margins - reset them.
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();

            switch (childCount) {
                case 1:
                    lp.leftMargin = lp.rightMargin = mOneItemMargin;
                    lp.width = mItemSizeDefault;
                    break;
                case 2:
                    if (i == 0) {
                        lp.leftMargin = mSideMarginOfTow;
                        lp.rightMargin = mTwoItemMargin;
                    } else if (i == 1) {
                        lp.rightMargin = mSideMarginOfTow;
                        lp.leftMargin = mTwoItemMargin;
                    }
                    lp.width = mItemSizeDefault;
                    break;
                case 3:
                    if (i == 0) {
                        lp.leftMargin = mSideMarginOfThree;
                        lp.rightMargin = mThreeItemMargin;
                    } else if (i == 2) {
                        lp.rightMargin = mSideMarginOfThree;
                        lp.leftMargin = mThreeItemMargin;
                    } else {
                        lp.leftMargin = lp.rightMargin = mThreeItemMargin;
                    }
                    lp.width = mItemSizeDefault;
                    break;
                case 4:
                    if (i == 0) {
                        lp.leftMargin = mSideMarginOfFour;
                        lp.rightMargin = mFourItemMargin;
                    } else if (i == 3) {
                        lp.rightMargin = mSideMarginOfFour;
                        lp.leftMargin = mFourItemMargin;
                    } else {
                        lp.leftMargin = lp.rightMargin = mFourItemMargin;
                    }
                    lp.width = mItemSizeDefault;
                    break;
                case 5:
                    if (i == 0) {
                        lp.leftMargin = mSideMarginOfFive;
                        lp.rightMargin = mFiveItemMargin;
                    } else if (i == 4) {
                        lp.rightMargin = mSideMarginOfFive;
                        lp.leftMargin = mFiveItemMargin;
                    } else {
                        lp.leftMargin = lp.rightMargin = mFiveItemMargin;
                    }
                    lp.width = mItemWithOfFive;
                    break;
                default:
                    lp.leftMargin = lp.rightMargin = mGeneratedItemMargin;
                    lp.width = mItemSizeDefault;
                    break;
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public static class LayoutParams extends LinearLayout.LayoutParams {
        /**
         * @hide
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean isOverflowButton;

        /**
         * @hide
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public int cellsUsed;

        /**
         * @hide
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public int extraPixels;

        /**
         * @hide
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean expandable;

        /**
         * @hide
         */
        @ViewDebug.ExportedProperty(category = "layout")
        public boolean preventEdgeOffset;

        /**
         * @hide
         */
        public boolean expanded;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(ViewGroup.LayoutParams other) {
            super(other);
        }

        public LayoutParams(LayoutParams other) {
            super((LinearLayout.LayoutParams) other);
            isOverflowButton = other.isOverflowButton;
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            isOverflowButton = false;
        }

        /**
         * @hide
         */
        public LayoutParams(int width, int height, boolean isOverflowButton) {
            super(width, height);
            this.isOverflowButton = isOverflowButton;
        }
    }
}
