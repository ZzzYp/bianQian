package com.gome.note.ui.create;

import com.gome.note.entity.PocketInfo;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/4/25
 * DESCRIBE:
 */

public interface PresentToActivityListener {
    void deleteSuccess();

    void toShareActivity(String path);

    void setCompressImagePath(String path);

    void setNoteInfoToActivity(PocketInfo pocketInfo);
}
