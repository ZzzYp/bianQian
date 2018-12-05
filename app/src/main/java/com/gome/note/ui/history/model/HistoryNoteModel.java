package com.gome.note.ui.history.model;

import android.content.Context;
import android.os.Handler;

import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.HandlerUtils;

import java.util.ArrayList;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class HistoryNoteModel implements IHistoryNoteModel {
    private final String TAG = "HistoryNoteModel";
    private Context mContext;
    private Handler mHandler;

    public HistoryNoteModel(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    @Override
    public void query(String keywords) {
        HandlerUtils.sendMessage(mHandler, Config.QUERY_SUCCESS);

    }

    @Override
    public void add(Object obj) {
        HandlerUtils.sendMessage(mHandler, Config.ADD_SUCCESS);

    }

    @Override
    public void update(Object obj, String id) {
        HandlerUtils.sendMessage(mHandler, Config.UPDATE_SUCCESS);

    }

    @Override
    public void delete(String id) {
        HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);

    }
}
