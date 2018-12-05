package com.gome.note.ui.label.model;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.PocketStore;
import com.gome.note.entity.LabelInfo;
import com.gome.note.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class LabelManagerModel implements ILabelManagerModel {
    private final String TAG = "LabelManagerModel";
    private Context mContext;
    private Handler mHandler;
    private List<LabelInfo> mLabelInfos = new ArrayList<>();
    private long status;

    public LabelManagerModel(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;

    }


    @Override
    public void query(String keywords) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mLabelInfos = PocketDbHandle.queryLablesList(mContext.getApplicationContext());
                HandlerUtils.sendMessage(mHandler, Config.QUERY_SUCCESS);
            }
        }).start();


    }

    @Override
    public void add(final Object obj) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                LabelInfo labelInfo = new LabelInfo();
                labelInfo.setTitle((String) obj);
                labelInfo.setDateAdded(System.currentTimeMillis());
                status = PocketDbHandle.insert(mContext.getApplicationContext(), labelInfo);
                if (status != -1) {
                    query("");
                } else {
                    HandlerUtils.sendMessage(mHandler, Config.QAUD_ERROR);
                }

            }
        }).start();
    }

    @Override
    public void update(final Object obj, final String id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                LabelInfo labelInfo = new LabelInfo();
                labelInfo.setTitle((String) obj);
                labelInfo.setId(Integer.parseInt(id));
                labelInfo.setDateModified(System.currentTimeMillis());
                status = PocketDbHandle.update(mContext.getApplicationContext(), labelInfo);

                if (status != -1) {
                    query("");
                } else {
                    HandlerUtils.sendMessage(mHandler, Config.QAUD_ERROR);
                }

            }
        }).start();
    }

    @Override
    public void delete(String id) {
        HandlerUtils.sendMessage(mHandler, Config.DELETE_SUCCESS);

    }


    @Override
    public void updateContentLabels(final long mId, final ArrayList<LabelInfo> labelInfos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                status = PocketDbHandle.updateContentLabels(mContext.getApplicationContext(), mId, labelInfos);

                if (status != -1) {
                    HandlerUtils.sendMessage(mHandler, Config.COTENT_LABEL_UPDATE_SUCCESS);
                } else {
                    HandlerUtils.sendMessage(mHandler, Config.QAUD_ERROR);
                }


            }
        }).start();
    }

    @Override
    public void deleteBatchLabel(String s, final ArrayList<LabelInfo> deleteLabelInfos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String table = PocketStore.Lable.TABLE_NAME;
                //delete lables
                List<Long> ids = new ArrayList<>();
                for (int i = 0; i < deleteLabelInfos.size(); i++) {
                    if (deleteLabelInfos.get(i).getIsChecked()) {
                        long id = deleteLabelInfos.get(i).getId();
                        ids.add(id);
                       // updatePocketInfo(deleteLabelInfos.get(i));
                    }
                }

                boolean isSucess = PocketDbHandle.delete(mContext.getApplicationContext(), table, ids);

                if (isSucess) {
                    query("");
                } else {
                    HandlerUtils.sendMessage(mHandler, Config.QAUD_ERROR);
                }
            }
        }).start();

    }





    @Override
    public void deleteLabel(String labelName, long labelId) {

    }


    public List<LabelInfo> getLabelInfos() {
        return mLabelInfos;
    }

}
