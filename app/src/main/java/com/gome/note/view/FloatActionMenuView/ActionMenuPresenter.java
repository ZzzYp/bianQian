package com.gome.note.view.FloatActionMenuView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.gome.note.R;

import java.util.ArrayList;


/**
 * ProjectName:Handler_DEMO_Go
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/4
 * DESCRIBE:
 */
@SuppressLint("RestrictedApi")
class ActionMenuPresenter extends BaseMenuPresenter {
    private final String TAG = "ActionMenuPresenter";
    private final LayoutInflater mInflater;
    private boolean mReserveOverflowSet;
    private boolean mReserveOverflow;
    private OverflowMenuButton mOverflowButton;
    private Drawable mPendingOverflowIcon;
    private int mMaxItems;
    private boolean mMaxItemsSet;
    private boolean mListMenuShowIcon;
    private boolean mOverFlowButtonResSet;
    private String mMoreButtonText;
    private Drawable mMoreButtonIconRes;

    private OverflowPopup mOverflowPopup;

    private OpenOverflowRunnable mPostedOpenRunnable;


    /**
     * Construct a new BaseMenuPresenter.
     *
     * @param context Context for generating system-supplied views
     */

    public ActionMenuPresenter(Context context) {
        super(context, R.layout.action_menu_layout, R.layout.action_menu_item_layout);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public void initForMenu(Context context, CustomMenuBuilder menu) {
        super.initForMenu(context, menu);


        Resources res = context.getResources();
        final ActionMenuPolicy abp = ActionMenuPolicy.get(context);
        if (!mReserveOverflowSet) {
            mReserveOverflow = abp.showsOverflowMenuButton();
        }
        if (!mMaxItemsSet) {
            mMaxItems = abp.getMaxActionButtons();
        }
        if (mReserveOverflow) {
            if (mOverflowButton == null) {
                mOverflowButton = createOverflowMenuButton(mSystemContext);
                if (mOverFlowButtonResSet) {
                    if (!TextUtils.isEmpty(mMoreButtonText)) {
                        mOverflowButton.setTitle(mMoreButtonText);
                    }
                    if (mMoreButtonIconRes != null) {
                        mOverflowButton.setIcon(mMoreButtonIconRes);
                    }
                    mOverFlowButtonResSet = false;
                }
            }
        } else {
            mOverflowButton = null;
        }

    }

    public void setMaxItems(int maxItem) {
        mMaxItemsSet = true;
        mMaxItems = maxItem;
        if (mMenu != null) {
            mMenu.onItemsChanged(true);
        }
    }

    public void setOverflowButtonText(String text) {
        Log.d(TAG, "setOverflowButtonText : " + text);
        mOverFlowButtonResSet = true;
        mMoreButtonText = text;
    }

    public void setOverflowButtonIcon(Drawable drawable) {
        Log.d(TAG, "setOverflowButtonIcon : " + drawable);
        mOverFlowButtonResSet = true;
        mMoreButtonIconRes = drawable;
    }

    public void resetOverflowButton() {
        mOverFlowButtonResSet = false;
        mOverflowButton = null;
        mMoreButtonIconRes = null;
        mMoreButtonText = null;
    }

    /**
     * set Overflow button enable
     *
     * @param enable
     */
    public void setOverflowButtonEnable(boolean enable) {
        if (mOverflowButton != null) {
            mOverflowButton.setActionItemEnable(enable);
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (mMenu != null) {
            mMenu.onItemsChanged(true);
        }
    }

    public void setReserveOverflow(boolean reserveOverflow) {
        mReserveOverflow = reserveOverflow;
        mReserveOverflowSet = true;
    }


    @Override
    public View getItemView(CustomMenuItemImpl item, View convertView, ViewGroup parent) {
        View actionView = item.getActionView();
        if (actionView == null || item.hasCollapsibleActionView()) {
            actionView = super.getItemView(item, convertView, parent);
        }
        actionView.setVisibility(item.isActionViewExpanded() ? View.GONE : View.VISIBLE);

        final ActionMenuView menuParent = (ActionMenuView) parent;
        final ViewGroup.LayoutParams lp = actionView.getLayoutParams();
        if (!menuParent.checkLayoutParams(lp)) {
            actionView.setLayoutParams(menuParent.generateLayoutParams(lp));
        }

        return actionView;
    }


    @Override
    public void bindItemView(CustomMenuItemImpl item, CustomMenuView.ItemView itemView) {
        itemView.initialize(item, 0);

        final ActionMenuView menuView = (ActionMenuView) mMenuView;
        final FloatActionItemView actionItemView = (FloatActionItemView) itemView;
        actionItemView.setItemInvoker(menuView);
    }

    @Override
    public boolean shouldIncludeItem(int childIndex, CustomMenuItemImpl item) {

        return item.isActionButton();
    }


    @Override
    public void updateMenuView(boolean cleared) {
        super.updateMenuView(cleared);

        ((View) mMenuView).requestLayout();
        final ArrayList<CustomMenuItemImpl> nonActionItems = mMenu != null ?
                mMenu.getNonActionItems() : null;
        boolean hasOverflow = false;
        if (mReserveOverflow && nonActionItems != null) {
            final int count = nonActionItems.size();
            if (count == 1) {
                hasOverflow = !nonActionItems.get(0).isActionViewExpanded();
            } else {
                hasOverflow = count > 0;
            }
        }

        if (hasOverflow) {
            if (mOverflowButton == null) {
                mOverflowButton = createOverflowMenuButton(mSystemContext);
            }
            if (mOverFlowButtonResSet) {
                mOverFlowButtonResSet = false;
                if (!TextUtils.isEmpty(mMoreButtonText)) {
                    mOverflowButton.setTitle(mMoreButtonText);
                }
                if (mMoreButtonIconRes != null) {
                    mOverflowButton.setIcon(mMoreButtonIconRes);
                }
            }
            ViewGroup parent = (ViewGroup) mOverflowButton.getParent();
            if (parent != mMenuView) {
                if (parent != null) {
                    parent.removeView(mOverflowButton);
                }
                ActionMenuView menuView = (ActionMenuView) mMenuView;
                menuView.addView(mOverflowButton, menuView.generateOverflowButtonLayoutParams());
            }
        } else if (mOverflowButton != null && mOverflowButton.getParent() == mMenuView) {
            mOverflowButton.clearAnimation();
            ((ViewGroup) mMenuView).removeView(mOverflowButton);
        }

        ((ActionMenuView) mMenuView).setOverflowReserved(mReserveOverflow);


    }


    @Override
    protected boolean filterLeftoverView(ViewGroup parent, int childIndex) {
        if (parent.getChildAt(childIndex) == mOverflowButton) return false;

        return super.filterLeftoverView(parent, childIndex);
    }


    /**
     * Display the overflow menu if one is present.
     *
     * @return true if the overflow menu was shown, false otherwise.
     */
    public boolean showOverflowMenu() {
        Log.d(TAG, "showOverflowMenu mReserveOverflow :" + mReserveOverflow + ", isShowing :" + isOverflowMenuShowing() + ", mPostRunalbe :" + mPostedOpenRunnable);
        if (mReserveOverflow && mMenu != null && mMenuView != null &&
                mPostedOpenRunnable == null && !mMenu.getNonActionItems().isEmpty()) {
            OverflowPopup popup = new OverflowPopup(mContext, mMenu, mOverflowButton, true);
            mPostedOpenRunnable = new OpenOverflowRunnable(popup);
            Log.d(TAG, "showOverflowMenu post");
            // Post this for later; we might still need a layout for the anchor to be right.
            ((View) mMenuView).post(mPostedOpenRunnable);

            // ActionMenuPresenter uses null as a callback argument here
            // to indicate overflow is opening.
            super.onSubMenuSelected(null);

            return true;
        }
        return false;
    }

    /**
     * Hide the overflow menu if it is currently showing.
     *
     * @return true if the overflow menu was hidden, false otherwise.
     */
    public boolean hideOverflowMenu() {
        if (mPostedOpenRunnable != null && mMenuView != null) {
            ((View) mMenuView).removeCallbacks(mPostedOpenRunnable);
            mPostedOpenRunnable = null;
            return true;
        }

        CustomMenuPopupHelper popup = mOverflowPopup;
        if (popup != null) {
            popup.dismiss();
            return true;
        }
        return false;
    }

    public void setListItemShowIcon(boolean show){
        Log.d(TAG,"ActionMenuPresenter forceShow :" + show);
        mListMenuShowIcon = show;
    }


    @Override
    public boolean onSubMenuSelected(CustomSubMenuBuilder menu) {
        Log.d(TAG,"onSubMenuSelected");
        return super.onSubMenuSelected(menu);
    }


    /**
     * Dismiss all popup menus - overflow and submenus.
     *
     * @return true if popups were dismissed, false otherwise. (This can be because none were open.)
     */
    public boolean dismissPopupMenus() {
        boolean result = hideOverflowMenu();
        return result;
    }

    /**
     * @return true if the overflow menu is currently showing
     */
    public boolean isOverflowMenuShowing() {
        return mOverflowPopup != null && mOverflowPopup.isShowing();
    }

    /**
     * @return true if space has been reserved in the action menu for an overflow item.
     */
    public boolean isOverflowReserved() {
        return mReserveOverflow;
    }

    /**
     *设置哪些Menu需要显示在MenuBar上，哪些需要显示在更多中
     *如果菜单数量小于最大maxItems，全部显示在MenuBar上
     *如果菜单数量大于最大maxItems：
     *    ----> 如果菜单item中设置有showAsActions="always"，则优先显示在MenuBar上，如果showAsAction数量大于MaxItems,则从其中选取前面的maxItems - 1个放在MenuBar上
     *    ----> 如果菜单item中设置有showAsActions="always"，其数量少于maxItems个，其全部显示在MenuBar上，不够的由没有设置该属性的补齐
     *    ----> 如果菜单中没有设置showAsActions="always"属性，则从前面取maxItems-1个放到MenuBar上
     */
    public boolean flagActionItems() {
        final ArrayList<CustomMenuItemImpl> visibleItems = mMenu.getVisibleItems();
        final int itemsSize = visibleItems.size();
        int maxActionItems = mMaxItems;
        if(itemsSize > mMaxItems){
            maxActionItems = mMaxItems - 1;
        }else{
            markAllItemsActionButton(visibleItems);
            return true;
        }

        int requireActionItems = 0;
        for(int i = 0; i < itemsSize; i++){
            CustomMenuItemImpl item = visibleItems.get(i);
            if(item.requiresActionButton()){
                requireActionItems++;
            }
        }

        if(requireActionItems <= maxActionItems){
            int needMoreActionNum = 0;
            needMoreActionNum = maxActionItems - requireActionItems;
            for (int i = 0; i < itemsSize; i++) {
                CustomMenuItemImpl item = visibleItems.get(i);
                if (item.requiresActionButton()) {
                    item.setIsActionButton(true);
                } else {
                    if(needMoreActionNum <= 0){
                        item.setIsActionButton(false);
                    }else{
                        item.setIsActionButton(true);
                        needMoreActionNum--;
                    }
                }
            }
        }else{
            for (int i = 0; i < itemsSize; i++) {
                CustomMenuItemImpl item = visibleItems.get(i);
                if (item.requiresActionButton()) {
                    if(i < maxActionItems){
                        item.setIsActionButton(true);
                    }else{
                        item.setIsActionButton(false);
                    }
                } else {
                    item.setIsActionButton(false);
                }
            }
        }

        return true;
    }

    private void markAllItemsActionButton(ArrayList<CustomMenuItemImpl> items){
        for(int i = 0; i< items.size(); i++){
            CustomMenuItemImpl item = items.get(i);
            item.setIsActionButton(true);
        }
    }

    @Override
    public void onCloseMenu(CustomMenuBuilder menu, boolean allMenusAreClosing) {
        dismissPopupMenus();
        super.onCloseMenu(menu, allMenusAreClosing);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Log.d(TAG,"ActionMenuPresenter onSaveInstanceState");
        SavedState state = new SavedState();
        state.showOverflowMenu = isOverflowMenuShowing() ? 1 : 0;
        return state;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Log.d(TAG,"ActionMenuPresenter onRestoreInstanceState");
        SavedState saved = (SavedState) state;
        if (saved.showOverflowMenu == 1) {
            Log.d(TAG,"should show overflow menu");
        }
    }

    public void setMenuView(ActionMenuView menuView) {
        mMenuView = menuView;
        menuView.initialize(mMenu);
    }

    private static class SavedState implements Parcelable {
        public int showOverflowMenu;

        SavedState() {
        }

        SavedState(Parcel in) {
            showOverflowMenu = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(showOverflowMenu);
        }

        public static final Creator<SavedState> CREATOR
                = new Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    private class OverflowPopup extends CustomMenuPopupHelper {

        public OverflowPopup(Context context, CustomMenuBuilder menu, View anchorView,
                             boolean overflowOnly) {
            super(context, menu, anchorView, overflowOnly, R.attr.gomeFloatMenuOverflowStyle);
            setGravity(Gravity.END);
            setForceShowIcon(mListMenuShowIcon);
        }

        @Override
        public void onDismiss() {
            super.onDismiss();
            mMenu.close();
            mOverflowPopup = null;
        }
    }

    private class OpenOverflowRunnable implements Runnable {
        private OverflowPopup mPopup;

        public OpenOverflowRunnable(OverflowPopup popup) {
            mPopup = popup;
        }

        public void run() {
            mMenu.changeMenuMode();
            final View menuView = (View) mMenuView;
            if (menuView != null && menuView.getWindowToken() != null && mPopup.tryShow()) {
                mOverflowPopup = mPopup;
            }
            mPostedOpenRunnable = null;
        }
    }

    private OverflowMenuButton createOverflowMenuButton(Context context) {
        final OverflowMenuButton overflowMenuButton = (OverflowMenuButton) mInflater.inflate(R.layout.action_menu_more_item_layout, null);
        overflowMenuButton.setTitle(context.getString(R.string.more_item_label));
        overflowMenuButton.setIcon(context.getDrawable(R.drawable.ic_gome_float_menu_more));
        overflowMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overflowMenuButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.gome_anim_btn_pressed));
                overflowMenuButton.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.gome_anim_btn_unpressed));
                showOverflowMenu();
            }
        });
        return overflowMenuButton;
    }

}
