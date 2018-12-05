package com.gome.note.view.FloatActionMenuView;

/**
 * Created by zhenjie on 2017/6/13.
 */

import android.content.Context;
import android.content.res.Resources;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;

import com.gome.note.R;

import java.util.ArrayList;


/**
 * Presents a menu as a small, simple popup anchored to another view.
 * @hide
 */
public class CustomMenuPopupHelper implements AdapterView.OnItemClickListener, View.OnKeyListener,
        ViewTreeObserver.OnGlobalLayoutListener, PopupWindow.OnDismissListener,
        View.OnAttachStateChangeListener, CustomMenuPresenter {
    private static final String TAG = "FloatActionMenu";

    static final int ITEM_LAYOUT = R.layout.action_menu_list_item_layout;

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final CustomMenuBuilder mMenu;
    private final MenuAdapter mAdapter;
    private final boolean mOverflowOnly;
    private final int mPopupMaxWidth;
    private final int mPopupStyleAttr;
    private final int mPopupStyleRes;

    private View mAnchorView;
    private ListPopupWindow mPopup;
    private ViewTreeObserver mTreeObserver;
    private Callback mPresenterCallback;

    boolean mForceShowIcon;

    private ViewGroup mMeasureParent;

    /** Whether the cached content width value is valid. */
    private boolean mHasContentWidth;

    /** Cached content width from {@link #measureContentWidth}. */
    private int mContentWidth;

    private int mDropDownGravity = Gravity.NO_GRAVITY;

    public CustomMenuPopupHelper(Context context, CustomMenuBuilder menu) {
        this(context, menu, null, false, android.R.attr.popupMenuStyle, 0);
    }

    public CustomMenuPopupHelper(Context context, CustomMenuBuilder menu, View anchorView) {
        this(context, menu, anchorView, false, android.R.attr.popupMenuStyle, 0);
    }

    public CustomMenuPopupHelper(Context context, CustomMenuBuilder menu, View anchorView,
                           boolean overflowOnly, int popupStyleAttr) {
        this(context, menu, anchorView, overflowOnly, popupStyleAttr, 0);
    }

    public CustomMenuPopupHelper(Context context, CustomMenuBuilder menu, View anchorView,
                           boolean overflowOnly, int popupStyleAttr, int popupStyleRes) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mMenu = menu;
        mAdapter = new MenuAdapter(mMenu);
        mOverflowOnly = overflowOnly;
        mPopupStyleAttr = popupStyleAttr;
        mPopupStyleRes = popupStyleRes;

        final Resources res = context.getResources();
        mPopupMaxWidth = (res.getDisplayMetrics().widthPixels / 3) * 2;

        mAnchorView = anchorView;

        // Present the menu using our context, not the menu builder's context.
        menu.addMenuPresenter(this, context);
    }

    public void setAnchorView(View anchor) {
        mAnchorView = anchor;
    }

    public void setForceShowIcon(boolean forceShow) {
        Log.d(TAG,"MenuPopupHelper forceShow :" + forceShow);
        mForceShowIcon = forceShow;
    }

    public void setGravity(int gravity) {
        mDropDownGravity = gravity;
    }

    public int getGravity() {
        return mDropDownGravity;
    }

    public void show() {
        if (!tryShow()) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
    }

    public ListPopupWindow getPopup() {
        return mPopup;
    }

    /**
     * Attempts to show the popup anchored to the view specified by
     * {@link #setAnchorView(View)}.
     *
     * @return {@code true} if the popup was shown or was already showing prior
     *         to calling this method, {@code false} otherwise
     */
    public boolean tryShow() {
        if (isShowing()) {
            return true;
        }

        mPopup = new ListPopupWindow(mContext, null, mPopupStyleAttr, mPopupStyleRes);
//        mPopup.setRoundCornerSelectorRes(R.drawable.gome_list_selector_first, com.gome.internal.R.drawable.gome_list_selector_last,
//                com.gome.internal.R.drawable.gome_list_selector_onlyone, com.gome.internal.R.drawable.gome_list_selector_normal);
        /// M: manipulate multiple instances of popup window
        final ListPopupWindow savedPopup = mPopup;
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                /// M: avoid memory leaks of observer
                if (savedPopup != null) {
                    savedPopup.setAdapter(null);
                }
                CustomMenuPopupHelper.this.onDismiss();
            }
        });
        Log.d(TAG,"set OnItem Click Listener");
        mPopup.setOnItemClickListener(this);
        mPopup.setAdapter(mAdapter);
        mPopup.setModal(true);

        final View anchor = mAnchorView;
        if (anchor != null) {
            final boolean addGlobalListener = mTreeObserver == null;
            mTreeObserver = anchor.getViewTreeObserver(); // Refresh to latest
            if (addGlobalListener) mTreeObserver.addOnGlobalLayoutListener(this);
            anchor.addOnAttachStateChangeListener(this);
            mPopup.setAnchorView(anchor);
            mPopup.setDropDownGravity(mDropDownGravity);
        } else {
            return false;
        }

        if (!mHasContentWidth) {
            mContentWidth = measureContentWidth();
            mHasContentWidth = true;
        }

        mPopup.setContentWidth(mContentWidth);
        mPopup.setInputMethodMode(PopupWindow.INPUT_METHOD_NOT_NEEDED);
        mPopup.show();
        mPopup.getListView().setOnKeyListener(this);
        return true;
    }

    public void dismiss() {
        if (isShowing()) {
            mPopup.dismiss();
        }
    }

    @Override
    public void onDismiss() {
        mPopup = null;
        mMenu.close();
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive()) mTreeObserver = mAnchorView.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(this);
            mTreeObserver = null;
        }
        mAnchorView.removeOnAttachStateChangeListener(this);
    }

    public boolean isShowing() {
        return mPopup != null && mPopup.isShowing();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuAdapter adapter = mAdapter;
        Log.d(TAG,"MenuPopupHelper onItemClick " + position);
        adapter.mAdapterMenu.performItemAction(adapter.getItem(position), 0);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_MENU) {
            dismiss();
            return true;
        }
        return false;
    }

    private int measureContentWidth() {
        // Menus don't tend to be long, so this is more sane than it looks.
        int maxWidth = 0;
        View itemView = null;
        int itemType = 0;

        final ListAdapter adapter = mAdapter;
        final int widthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int heightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        final int count = adapter.getCount();
        for (int i = 0; i < count; i++) {
            final int positionType = adapter.getItemViewType(i);
            if (positionType != itemType) {
                itemType = positionType;
                itemView = null;
            }

            if (mMeasureParent == null) {
                mMeasureParent = new FrameLayout(mContext);
            }

            itemView = adapter.getView(i, itemView, mMeasureParent);
            itemView.measure(widthMeasureSpec, heightMeasureSpec);

            final int itemWidth = itemView.getMeasuredWidth();
            if (itemWidth >= mPopupMaxWidth) {
                return mPopupMaxWidth;
            } else if (itemWidth > maxWidth) {
                maxWidth = itemWidth;
            }
        }

        return maxWidth;
    }

    @Override
    public void onGlobalLayout() {
        if (isShowing()) {
            final View anchor = mAnchorView;
            if (anchor == null || !anchor.isShown()) {
                dismiss();
            } else if (isShowing()) {
                // Recompute window size and position
                mPopup.show();
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(View v) {
    }

    @Override
    public void onViewDetachedFromWindow(View v) {
        if (mTreeObserver != null) {
            if (!mTreeObserver.isAlive()) mTreeObserver = v.getViewTreeObserver();
            mTreeObserver.removeGlobalOnLayoutListener(this);
        }
        v.removeOnAttachStateChangeListener(this);
    }

    @Override
    public void initForMenu(Context context, CustomMenuBuilder menu) {
        // Don't need to do anything; we added as a presenter in the constructor.
    }

    @Override
    public CustomMenuView getMenuView(ViewGroup root) {
        throw new UnsupportedOperationException("MenuPopupHelpers manage their own views");
    }

    @Override
    public void updateMenuView(boolean cleared) {
        mHasContentWidth = false;

        if (mAdapter != null && mMenu != null) {
            if(mMenu.getNonActionItems().size() == 0){
                Log.d(TAG,"no NoActionItems, don't no anything");
            }else{
                Log.d(TAG,"refresh the PopupMenu");
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void setCallback(Callback cb) {
        mPresenterCallback = cb;
    }

    @Override
    public boolean onSubMenuSelected(CustomSubMenuBuilder subMenu) {
        return false;
    }

    @Override
    public void onCloseMenu(CustomMenuBuilder menu, boolean allMenusAreClosing) {
        // Only care about the (sub)menu we're presenting.
        if (menu != mMenu) return;

        dismiss();
        if (mPresenterCallback != null) {
            mPresenterCallback.onCloseMenu(menu, allMenusAreClosing);
        }
    }

    @Override
    public boolean flagActionItems() {
        return false;
    }

    public boolean expandItemActionView(CustomMenuBuilder menu,CustomMenuItemImpl item) {
        return false;
    }

    public boolean collapseItemActionView(CustomMenuBuilder menu, CustomMenuItemImpl item) {
        return false;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        return null;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
    }

    private class MenuAdapter extends BaseAdapter {
        private CustomMenuBuilder mAdapterMenu;
        private int mExpandedIndex = -1;

        public MenuAdapter(CustomMenuBuilder menu) {
            mAdapterMenu = menu;
            findExpandedIndex();
        }

        public int getCount() {
            ArrayList<CustomMenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex < 0) {
                return items.size();
            }
            return items.size() - 1;
        }

        public CustomMenuItemImpl getItem(int position) {
            ArrayList<CustomMenuItemImpl> items = mOverflowOnly ?
                    mAdapterMenu.getNonActionItems() : mAdapterMenu.getVisibleItems();
            if (mExpandedIndex >= 0 && position >= mExpandedIndex) {
                position++;
            }
            return items.get(position);
        }

        public long getItemId(int position) {
            // Since a menu item's ID is optional, we'll use the position as an
            // ID for the item in the AdapterView
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(ITEM_LAYOUT, parent, false);
            }

            CustomMenuView.ItemView itemView = (CustomMenuView.ItemView) convertView;
            if (mForceShowIcon) {
                ((FloatActionListItemView) convertView).setShowIconWithText(true);
            }
            itemView.initialize(getItem(position), 0);
            return convertView;
        }

        void findExpandedIndex() {
            final CustomMenuItemImpl expandedItem = mMenu.getExpandedItem();
            if (expandedItem != null) {
                final ArrayList<CustomMenuItemImpl> items = mMenu.getNonActionItems();
                final int count = items.size();
                for (int i = 0; i < count; i++) {
                    final CustomMenuItemImpl item = items.get(i);
                    if (item == expandedItem) {
                        mExpandedIndex = i;
                        return;
                    }
                }
            }
            mExpandedIndex = -1;
        }

        @Override
        public void notifyDataSetChanged() {
            findExpandedIndex();
            super.notifyDataSetChanged();
        }
    }
}