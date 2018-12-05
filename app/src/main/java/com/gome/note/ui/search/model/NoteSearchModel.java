package com.gome.note.ui.search.model;

import android.content.Context;
import android.os.Handler;

import com.gome.note.base.Config;
import com.gome.note.ui.label.model.ILabelManagerModel;
import com.gome.note.utils.HandlerUtils;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class NoteSearchModel implements INoteSearchModel {
    private final String TAG = "NoteSearchModel";
    private Context mContext;
    private Handler mHandler;

    public NoteSearchModel(Context context, Handler handler) {
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
