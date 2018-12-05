package com.gome.note.entity;

import android.widget.CheckBox;

import com.gome.note.view.ZanyEditText;


/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/29
 * DESCRIBE:
 */

public class EditTextCheckBoxInfo {
    private int id;
    private ZanyEditText zanyEditText;
    private CheckBox checkBox;
    private boolean isShowedCheckBox;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ZanyEditText getZanyEditText() {
        return zanyEditText;
    }

    public void setZanyEditText(ZanyEditText zanyEditText) {
        this.zanyEditText = zanyEditText;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(CheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public boolean getShowedCheckBox() {
        return isShowedCheckBox;
    }

    public void setShowedCheckBox(boolean showedCheckBox) {
        isShowedCheckBox = showedCheckBox;
    }
}
