package com.gome.note.ui.search.view;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Eric on 2018/2/28 11:08
 */

public abstract class EditChangeListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        onEditTextChange(s,start,before,count);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    public abstract void onEditTextChange(CharSequence s, int start, int before, int count);
}
