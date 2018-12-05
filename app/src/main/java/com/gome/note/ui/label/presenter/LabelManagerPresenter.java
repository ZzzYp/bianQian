package com.gome.note.ui.label.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gome.note.base.BasePresenter;
import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.label.LabelManageUtils;
import com.gome.note.ui.label.LabelManagerActivity;
import com.gome.note.ui.label.model.LabelManagerModel;

import java.util.ArrayList;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/22
 * DESCRIBE:
 */

public class LabelManagerPresenter extends BasePresenter {
    private String TAG = "LabelManagerPresenter";
    private LabelManagerModel mModel;
    private ArrayList<LabelInfo> labelInfos = new ArrayList<>();
    private LabelManagerActivity mActivity;
    private ArrayList<LabelInfo> itemLabelInfosTemp = null;

    public LabelManagerPresenter(Context context, LabelManagerActivity activity) {
        mContext = context;
        mActivity = activity;
        initModel();
    }

    @Override
    public void initModel() {
        mModel = new LabelManagerModel(mContext, mHandler);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (mActivity == null) {
                return;
            }
            switch (msg.what) {
                case Config.QUERY_SUCCESS:

                    labelInfos = (ArrayList) mModel.getLabelInfos();
                    mActivity.setLabelInfos(labelInfos);

                    getCounts();


                    break;
                case Config.ADD_SUCCESS:

                    break;
                case Config.UPDATE_SUCCESS:

                    break;
                case Config.DELETE_SUCCESS:

                    break;
                case Config.QAUD_ERROR:

                    mActivity.showError();
                    break;
                case Config.COTENT_LABEL_UPDATE_SUCCESS:

                    mActivity.setContentLables();
                    break;

                case Config.LABEL_GET_COUNT:

                    mActivity.countResfesh(labelInfos);

                    break;
                default:

                    break;
            }
        }
    };

    public void queryLablesList() {

        mModel.query("");
    }


    public boolean isHasSameLabel(String labelName, ArrayList<LabelInfo> labelInfos) {

        if (null != labelInfos && null != labelName) {
            for (int i = 0; i < labelInfos.size(); i++) {
                String tempLabelName = labelInfos.get(i).getTitle() == null ? "" : labelInfos.get(i).getTitle();
                if (LabelManageUtils.isVoiceMemosLable(mContext.getApplicationContext(), labelName) &&
                        LabelManageUtils.isVoiceMemosLable(mContext.getApplicationContext(), tempLabelName)) {
                    return true;
                }
                if (labelName.equals(tempLabelName)) {
                    return true;
                }
            }
        }
        return false;
    }


    public void updataLabel(String input, long labelId) {
        mModel.update(input, String.valueOf(labelId));

    }

    public void createLabel(String input) {
        mModel.add(input);

    }

    public void updateContentLabels(long mId, ArrayList<LabelInfo> itemLabelInfos) {
        mModel.updateContentLabels(mId, itemLabelInfos);

    }

    public void deleteBatchLabel(String s, ArrayList<LabelInfo> itemLabelsDeleteChecked) {

        mModel.deleteBatchLabel("", itemLabelsDeleteChecked);

    }

    public void deleteLabel(String labelName, long labelId) {
        mModel.deleteLabel(labelName, labelId);
    }

    private void getCounts() {
//        ArrayList<PocketInfo> mPacketList
//                = OverallSituationPocketInfo.getInstance().getPacketList();

        if (null != itemLabelInfosTemp) {
            itemLabelInfosTemp.clear();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<PocketInfo> mPacketList = (ArrayList<PocketInfo>) PocketDbHandle
                        .queryPocketsList(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET);

                if (null != labelInfos) {
                    for (int i = 0; i < labelInfos.size(); i++) {
                        int count = 0;
                        long labelId = labelInfos.get(i).getId();
                        for (int m = 0; m < mPacketList.size(); m++) {
                            itemLabelInfosTemp = (ArrayList<LabelInfo>) mPacketList.get(m).getLabels();
                            if (null != itemLabelInfosTemp) {
                                for (int j = 0; j < itemLabelInfosTemp.size(); j++) {
                                    long itemLabelId = itemLabelInfosTemp.get(j).getId();
                                    if (itemLabelId == labelId) {
                                        count = count + 1;
                                    }
                                }
                            }
                        }
                        labelInfos.get(i).setCount(count);
                    }
                }

                Message message = new Message();
                message.what = 5;
                mHandler.sendMessage(message);
            }
        }).start();

    }

    public void setActivityNull() {

        mActivity = null;

    }
}
