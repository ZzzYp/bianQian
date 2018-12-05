package com.gome.note.utils;


import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;

/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/26
 * DESCRIBE:
 */

public class OverallSituationStickLabel {
    private static OverallSituationStickLabel instance = new OverallSituationStickLabel();
    private static ArrayList<LabelInfo> mLabelInfoList = new ArrayList<>();
    private static LabelInfo mLabelInfo = new LabelInfo();

    private OverallSituationStickLabel() {
    }

    public static OverallSituationStickLabel getInstance() {
        return instance;
    }


    public ArrayList<LabelInfo> getLabelInfoList() {
        return mLabelInfoList;
    }

    public void setLabelInfoList(ArrayList<LabelInfo> mLabelInfoList) {
        OverallSituationStickLabel.mLabelInfoList = mLabelInfoList;
    }

    public LabelInfo getStickLabelInfo() {
        return mLabelInfo;
    }

    public void setStickLabelInfo(LabelInfo mLabelInfo) {
        OverallSituationStickLabel.mLabelInfo = mLabelInfo;
    }
}
