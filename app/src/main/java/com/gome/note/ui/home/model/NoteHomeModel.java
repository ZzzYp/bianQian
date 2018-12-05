package com.gome.note.ui.home.model;

import android.content.Context;
import android.os.Handler;

import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

import static com.gome.note.db.PocketDbHandle.URI_POCKET;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class NoteHomeModel implements INoteHomeModel {
    private final String TAG = "NoteHomeModel";
    private Context mContext;
    private Handler mHandler;
    private List<LabelInfo> mLabelInfos = new ArrayList<>();
    private List<PocketInfo> mPocketInfos = new ArrayList<>();

    public NoteHomeModel(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }


    @Override
    public void query(String keywords) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mPocketInfos = PocketDbHandle
                        .queryPocketsList(mContext.getApplicationContext(), URI_POCKET);
                HandlerUtils.sendMessage(mHandler, Config.QUERY_SUCCESS);
            }
        }).start();

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

    @Override
    public void queryLabelInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLabelInfos = PocketDbHandle.queryLablesList(mContext.getApplicationContext());
                HandlerUtils.sendMessage(mHandler, Config.QUERY_HOME_LABEL_SUCCESS);
            }
        }).start();

    }

    public void deleteNoteInfo(ArrayList<PocketInfo> pocketInfos) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = 0; i < pocketInfos.size(); i++) {
//                    PocketInfo pocketInfo = pocketInfos.get(i);
//                    if (pocketInfo.isChecked()) {
//                        pocketInfo.setDateModified(System.currentTimeMillis());
//                        PocketDbHandle.insert(mContext, PocketDbHandle.URI_HISTORY, pocketInfo);
//                        PocketDbHandle.delete(mContext, URI_POCKET, pocketInfo.getId());
//                    }
//                }
//                HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);
//            }
//        }).start();
        for (int i = 0; i < pocketInfos.size(); i++) {
            PocketInfo pocketInfo = pocketInfos.get(i);
            if (pocketInfo.isChecked() && pocketInfo.getId() > 0) {
                pocketInfo.setDateModified(System.currentTimeMillis());
                PocketDbHandle.insert(mContext.getApplicationContext(), PocketDbHandle.URI_HISTORY, pocketInfo);
                PocketDbHandle.delete(mContext.getApplicationContext(), URI_POCKET, pocketInfo.getId());
            }
        }
        HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);
    }

    public List<LabelInfo> getLabelInfos() {
        return mLabelInfos;
    }

    public void setLabelInfos(List<LabelInfo> mLabelInfos) {
        this.mLabelInfos = mLabelInfos;
    }

    public List<PocketInfo> getPocketInfos() {
        return mPocketInfos;
    }

    public void setPocketInfos(List<PocketInfo> mPocketInfos) {
        this.mPocketInfos = mPocketInfos;
    }
}
