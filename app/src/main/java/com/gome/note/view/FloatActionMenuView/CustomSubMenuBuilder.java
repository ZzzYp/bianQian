package com.gome.note.view.FloatActionMenuView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

/**
 * Created by zhenjie on 2017/6/12.
 */

public class CustomSubMenuBuilder extends CustomMenuBuilder implements SubMenu {
    private CustomMenuBuilder mParentMenu;
    private CustomMenuItemImpl mItem;

    public CustomSubMenuBuilder(Context context, CustomMenuBuilder parentMenu, CustomMenuItemImpl item) {
        super(context);

        mParentMenu = parentMenu;
        mItem = item;
    }

    @Override
    public void setQwertyMode(boolean isQwerty) {
        mParentMenu.setQwertyMode(isQwerty);
    }

    @Override
    public boolean isQwertyMode() {
        return mParentMenu.isQwertyMode();
    }

    @Override
    public void setShortcutsVisible(boolean shortcutsVisible) {
        mParentMenu.setShortcutsVisible(shortcutsVisible);
    }

    @Override
    public boolean isShortcutsVisible() {
        return mParentMenu.isShortcutsVisible();
    }

    public Menu getParentMenu() {
        return mParentMenu;
    }

    public MenuItem getItem() {
        return mItem;
    }

    @Override
    public void setCallback(Callback callback) {
        mParentMenu.setCallback(callback);
    }

    @Override
    public CustomMenuBuilder getRootMenu() {
        return mParentMenu;
    }

    @Override
    boolean dispatchMenuItemSelected(CustomMenuBuilder menu, MenuItem item) {
        return super.dispatchMenuItemSelected(menu, item) ||
                mParentMenu.dispatchMenuItemSelected(menu, item);
    }

    public SubMenu setIcon(Drawable icon) {
        mItem.setIcon(icon);
        return this;
    }

    public SubMenu setIcon(int iconRes) {
        mItem.setIcon(iconRes);
        return this;
    }

    public SubMenu setHeaderIcon(Drawable icon) {
        return (SubMenu) super.setHeaderIconInt(icon);
    }

    public SubMenu setHeaderIcon(int iconRes) {
        return (SubMenu) super.setHeaderIconInt(iconRes);
    }

    public SubMenu setHeaderTitle(CharSequence title) {
        return (SubMenu) super.setHeaderTitleInt(title);
    }

    public SubMenu setHeaderTitle(int titleRes) {
        return (SubMenu) super.setHeaderTitleInt(titleRes);
    }

    public SubMenu setHeaderView(View view) {
        return (SubMenu) super.setHeaderViewInt(view);
    }

    @Override
    public boolean expandItemActionView(CustomMenuItemImpl item) {
        return mParentMenu.expandItemActionView(item);
    }

    @Override
    public boolean collapseItemActionView(CustomMenuItemImpl item) {
        return mParentMenu.collapseItemActionView(item);
    }

    @Override
    public String getActionViewStatesKey() {
        final int itemId = mItem != null ? mItem.getItemId() : 0;
        if (itemId == 0) {
            return null;
        }
        return super.getActionViewStatesKey() + ":" + itemId;
    }
}
