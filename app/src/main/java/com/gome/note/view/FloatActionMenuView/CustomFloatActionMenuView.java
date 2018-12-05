package com.gome.note.view.FloatActionMenuView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.gome.note.R;


/**
 * ProjectName:Handler_DEMO_Go
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/4
 * DESCRIBE:
 */
@SuppressLint("RestrictedApi")
public class CustomFloatActionMenuView extends FrameLayout {
    private ActionMenuPresenter mMenuPresenter;
    private CustomMenuBuilder mMenu;
    private int mMaxItems;
    private ActionMenuView mMenuView;
    private OnFloatActionMenuSelectedListener mMenuItemSelectedListener;
    private MenuInflater mMenuInflater;


    private static final int PRESENTER_FLOATMENU_VIEW_ID = 1;
    private static final int DEFAULT_ELEVATION = 4;
    private static final int DEFAULT_TRANSLATION_Z = 4;
    private static final int DEFAULT_MAX_ITEM = 5;
    private static final int MIN_ITEM_SIZE = 1;
    private static final int MAX_ITEM_SIZE = 5;

    IntentFilter homeKeyFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    /**
     * 用于监听Home键，在点击Home键后，悬浮菜单的PopupMenu需要隐藏。
     * Reason: 由于更多按钮随时可以被移除掉，会导致PopupMenu发生异常，所以要及时隐藏PopupMenu。
     */
    private final BroadcastReceiver homeKeyReceiver = new BroadcastReceiver() {

        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reson = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.equals(reson, SYSTEM_DIALOG_REASON_HOME_KEY)) {
                    if (mMenuPresenter != null) {
                        mMenuPresenter.hideOverflowMenu();
                    }
                }
            }
        }
    };


    public CustomFloatActionMenuView(@NonNull Context context) {
        this(context, null);
    }

    public CustomFloatActionMenuView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomFloatActionMenuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CustomFloatActionMenuView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mMenu = new CustomMenuBuilder(context);
        mMenuPresenter = new ActionMenuPresenter(context);

        TypedArray ta = this.getContext().obtainStyledAttributes(attrs, R.styleable.FloatActionMenuView);
        int maxItem = ta.getInteger(R.styleable.FloatActionMenuView_maxItems, 0);
        String moreItemText = ta.getString(R.styleable.FloatActionMenuView_overflowButtonText);
        Drawable moreItemIconRes = ta.getDrawable(R.styleable.FloatActionMenuView_overflowButtonIcon);

        if (moreItemIconRes != null) {
            mMenuPresenter.setOverflowButtonIcon(moreItemIconRes);
        }
        if (!TextUtils.isEmpty(moreItemText)) {
            mMenuPresenter.setOverflowButtonText(moreItemText);
        }
        mMaxItems = ta.getInteger(R.styleable.FloatActionMenuView_maxItems, 0);
        if (mMaxItems >= 1) {
            mMenuPresenter.setMaxItems(mMaxItems > DEFAULT_MAX_ITEM ? DEFAULT_MAX_ITEM : mMaxItems);
        }
        //set the overflow menu show icon or not
        boolean listItemShowIcon = ta.getBoolean(R.styleable.FloatActionMenuView_listItemShowIcon, false);
        mMenuPresenter.setListItemShowIcon(listItemShowIcon);
        mMenu.setCallback(new CustomMenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(CustomMenuBuilder menu, MenuItem item) {
                return mMenuItemSelectedListener != null && mMenuItemSelectedListener.onFloatActionItemSelected(item);
            }

            @Override
            public void onMenuModeChange(CustomMenuBuilder menu) {

            }

            @Override
            public void onMenuItemNumberChanged(int number) {
                if (number == 0) {
                    mMenuView.setVisibility(GONE);
                } else {
                    mMenuView.setVisibility(VISIBLE);
                }
            }
        });


        //set the float action menu default elevation and translationZ
        if (getElevation() == 0 && getTranslationZ() == 0) {
            setElevation(DEFAULT_ELEVATION);
            setTranslationZ(DEFAULT_TRANSLATION_Z);
        }

        mMenuPresenter.setId(PRESENTER_FLOATMENU_VIEW_ID);
        mMenuPresenter.initForMenu(context, mMenu);
        mMenu.addMenuPresenter(mMenuPresenter);
        mMenuView = (ActionMenuView) mMenuPresenter.getMenuView(this);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mMenuView, layoutParams);
        if (ta.hasValue(R.styleable.FloatActionMenuView_menu)) {
            inflateMenu(ta.getResourceId(R.styleable.FloatActionMenuView_menu, 0));
        }
        ta.recycle();

    }


    /**
     * Inflate a menu resource into this navigation view.
     * <p>
     * <p>Existing items in the menu will not be modified or removed.</p>
     *
     * @param resId ID of a menu resource to inflate
     */
    public void inflateMenu(int resId) {
        mMenu.clearAll();
        getMenuInflater().inflate(resId, mMenu);
        mMenuPresenter.updateMenuView(false);
    }

    private MenuInflater getMenuInflater() {
        if (mMenuInflater == null) {
            mMenuInflater = new MenuInflater(getContext());
        }
        return mMenuInflater;
    }


    public void invalidateFloatMenu() {
        if (mMenuPresenter != null) {
            mMenuPresenter.updateMenuView(false);
        }
    }

    /**
     * set menu item visible by the menu ids
     *
     * @param menuIds the menu item id
     */
    public void setMenuItemsVisible(int... menuIds) {
        for (int id : menuIds) {
            MenuItem item = mMenu.findItem(id);
            if (item != null) {
                item.setVisible(true);
            }
        }
        invalidateFloatMenu();
    }

    /**
     * set menu item invisible by the menu ids
     *
     * @param menuIds the menu item id
     */
    public void setMenuItemsInVisible(int... menuIds) {
        for (int id : menuIds) {
            MenuItem item = mMenu.findItem(id);
            if (item != null) {
                item.setVisible(false);
            }
        }
        invalidateFloatMenu();
    }

    //MODIFIED BEGIN BY ZHENJIE.CHANG FOR ENABLE/DISABLE FLOAT_MENU_VIEW AT 2017/07/5

    /**
     * set menu item Enabled by the menu ids
     *
     * @param menuIds the menu item id
     */
    public void setMenuItemsEnable(int... menuIds) {
        for (int id : menuIds) {
            MenuItem item = mMenu.findItem(id);
            if (item != null) {
                item.setEnabled(true);
            }
        }
        invalidateFloatMenu();
    }

    /**
     * set menu item Disabled by the menu ids
     *
     * @param menuIds the menu item id
     */
    public void setMenuItemsDisable(int... menuIds) {
        for (int id : menuIds) {
            MenuItem item = mMenu.findItem(id);
            if (item != null) {
                item.setEnabled(false);
            }
        }
        invalidateFloatMenu();
    }
    //MODIFIED END BY ZHENJIE.CHANG FOR ENABLE/DISABLE FLOAT_MENU_VIEW AT 2017/07/5

    /**
     * set the maxItems of FloatActionMenu
     *
     * @param maxItems must > 1 && < 6
     */
    public void setMaxItems(int maxItems) {
        if (maxItems >= MIN_ITEM_SIZE && maxItems <= MAX_ITEM_SIZE) {
            mMenuPresenter.setMaxItems(maxItems);
            invalidateFloatMenu();
        }
    }

    /**
     * 修改更多按钮的图标
     *
     * @param iconRes 图标资源ID
     */
    public void setOverflowButtonIcon(int iconRes) {
        if (mMenuPresenter != null) {
            Drawable icon = getResources().getDrawable(iconRes);
            mMenuPresenter.setOverflowButtonIcon(icon);
            invalidateFloatMenu();
        }
    }

    /**
     * 修改更多按钮的文字
     *
     * @param text 要修改的文字
     */
    public void setOverflowButtonText(String text) {
        if (mMenuPresenter != null) {
            mMenuPresenter.setOverflowButtonText(text);
            invalidateFloatMenu();
        }
    }

    /**
     * 修改更多按钮的文字和图标
     *
     * @param text    文字信息
     * @param iconRes 图标资源ID
     */
    public void setOverflowButtonRes(String text, int iconRes) {
        if (mMenuPresenter != null) {
            Drawable icon = getResources().getDrawable(iconRes);
            mMenuPresenter.setOverflowButtonIcon(icon);
            mMenuPresenter.setOverflowButtonText(text);
            invalidateFloatMenu();
        }
    }

    /**
     * 重置更多按钮的文字和图标
     */
    public void resetOverflowButton() {
        if (mMenuPresenter != null) {
            mMenuPresenter.resetOverflowButton();
            invalidateFloatMenu();
        }
    }

    /**
     * 设置更多按钮的Enable属性
     * 更多按钮和其他的菜单按钮不同，所以需要单独的方法设置
     *
     * @param enable
     */
    public void setOverflowButtonEnable(boolean enable) {
        if (mMenuPresenter != null) {
            mMenuPresenter.setOverflowButtonEnable(enable);
            invalidateFloatMenu();
        }
    }

    /**
     * 设置MenuItem的title
     *
     * @param menuId
     * @param resId
     */
    public void setMenuItemTitle(int menuId, int resId) {
        MenuItem item = mMenu.findItem(menuId);
        if (item != null) {
            item.setTitle(resId);
        }
        invalidateFloatMenu();
    }

    /**
     * 设置MenuItem的Icon
     *
     * @param menuId
     * @param resId
     */
    public void setMenuItemIcon(int menuId, int resId) {
        MenuItem item = mMenu.findItem(menuId);
        if (item != null) {
            item.setIcon(resId);
        }
        invalidateFloatMenu();
    }

    /**
     * 隐藏更多弹窗
     */
    public void hideOverflowMenu() {
        if (mMenuPresenter != null) {
            mMenuPresenter.hideOverflowMenu();
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getContext().registerReceiver(homeKeyReceiver, homeKeyFilter);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mMenuPresenter != null) {
            mMenuPresenter.hideOverflowMenu();
        }
        getContext().unregisterReceiver(homeKeyReceiver);
    }


    public void setOnFloatActionMenuSelectedListener(OnFloatActionMenuSelectedListener listener) {
        mMenuItemSelectedListener = listener;
    }

    public interface OnFloatActionMenuSelectedListener {
        boolean onFloatActionItemSelected(@NonNull MenuItem item);
    }
}
