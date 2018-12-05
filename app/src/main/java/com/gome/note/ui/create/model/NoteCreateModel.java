package com.gome.note.ui.create.model;

import android.content.Context;
import android.os.Handler;

import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.HandlerUtils;

import static com.gome.note.db.PocketDbHandle.URI_POCKET;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class NoteCreateModel implements INoteCreateModel {
    private final String TAG = "NoteCreateModel";
    private Context mContext;
    private Handler mHandler;


    public NoteCreateModel(Context context, Handler handler) {
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

    public void deleteNoteInfo(PocketInfo pocketInfo) {
        if (null != pocketInfo) {
            PocketInfo pocketInfoDele = PocketDbHandle.queryPocketInfoById(mContext, URI_POCKET, pocketInfo.getId());
            if (null != pocketInfoDele) {
                pocketInfoDele.setDateModified(System.currentTimeMillis());
                PocketDbHandle.insert(mContext, PocketDbHandle.URI_HISTORY, pocketInfoDele);
                PocketDbHandle.delete(mContext, URI_POCKET, pocketInfoDele.getId());
                HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);
            }
        }
    }

    public void deleteNoteInfoById(long updateId) {
        PocketInfo pocketInfoDele = PocketDbHandle.queryPocketInfoById(mContext, URI_POCKET, updateId);
        if (null != pocketInfoDele) {
            //PocketDbHandle.insert(mContext, PocketDbHandle.URI_HISTORY, pocketInfoDele);
            PocketDbHandle.delete(mContext, URI_POCKET, pocketInfoDele.getId());
            HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);
        }
    }
}
