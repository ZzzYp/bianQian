package com.gome.note.ui.home.adapter;

import android.view.View;


import com.gome.note.entity.PocketInfo;

import java.util.List;

/**
 * Author：viston on 2017/6/29 11:13
 */
public interface ItemClickListener {
    void onItemClick(View view, int position);

    void onDeleteBtnCilck();

    void onLongClick(View view, int position);


    void onNoItemList(List<PocketInfo> list);

}
