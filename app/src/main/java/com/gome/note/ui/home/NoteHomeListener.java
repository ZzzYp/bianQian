package com.gome.note.ui.home;

import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/5/8
 * DESCRIBE:
 */

public interface NoteHomeListener {
    void setQueryPocketInfos(ArrayList<PocketInfo> mPocketInfos);
    void deleteSuccess();
}
