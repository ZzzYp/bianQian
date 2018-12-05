package com.gome.note.ui.create;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/4/25
 * DESCRIBE:
 */

public interface SetValueToActivityListener {
    void setNoteId(long mId);

    void setDoEdit(boolean doEdit);

    void onClickColoredLayoutChildrenView();

    void setSkinBgName(String name);

    void setSkinBgId(int id);

    void onClickColoredLayoutEditText();

    void withoutData();
}
