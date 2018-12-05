package com.gome.note.utils;


import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;

/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/26
 * DESCRIBE:
 */

public class OverallSituationPocketInfo {
    private static OverallSituationPocketInfo instance = new OverallSituationPocketInfo();
    private static ArrayList<PocketInfo> mPacketList = new ArrayList<>();

    private OverallSituationPocketInfo() {
    }

    public static OverallSituationPocketInfo getInstance() {
        return instance;
    }


    public ArrayList<PocketInfo> getPacketList() {
        return mPacketList;
    }

    public void setPacketList(ArrayList<PocketInfo> mPacketList) {
        OverallSituationPocketInfo.mPacketList = mPacketList;
    }
}
