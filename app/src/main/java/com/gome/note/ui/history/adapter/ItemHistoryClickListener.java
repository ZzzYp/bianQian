package com.gome.note.ui.history.adapter;

import android.view.View;


/**
 * Author：viston on 2017/6/29 11:13
 */
public interface ItemHistoryClickListener {
    void onItemClick(View view, int position);

    void onItemCheckShowClick(View view, int position);

    void onLongClick(View view, int position);
}
