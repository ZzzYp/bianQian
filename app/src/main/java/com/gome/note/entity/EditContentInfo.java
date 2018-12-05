package com.gome.note.entity;

/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/7/4
 * DESCRIBE:
 */

public class EditContentInfo {
    private String text;
    private boolean isHasCheckBox;
    private boolean isChecked;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getIsHasCheckBox() {
        return isHasCheckBox;
    }

    public void setHasCheckBox(boolean hasCheckBox) {
        isHasCheckBox = hasCheckBox;
    }

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
