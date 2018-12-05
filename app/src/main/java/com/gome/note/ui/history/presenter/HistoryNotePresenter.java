package com.gome.note.ui.history.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gome.note.base.BasePresenter;
import com.gome.note.base.Config;
import com.gome.note.ui.history.HistoryNoteActivity;
import com.gome.note.ui.history.model.HistoryNoteModel;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/22
 * DESCRIBE:
 */

public class HistoryNotePresenter extends BasePresenter {
    private HistoryNoteModel mModel;

    public HistoryNotePresenter(Context context) {
        mContext = context;
    }

    @Override
    public void initModel() {
        mModel = new HistoryNoteModel(mContext, mHandler);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.QUERY_SUCCESS:
                    break;
                case Config.ADD_SUCCESS:
                    break;
                case Config.UPDATE_SUCCESS:
                    break;
                case Config.DELETE_SUCCESS:
                    break;
                case Config.QAUD_ERROR:
                    break;
                default:
                    break;


            }
        }
    };

    public void queryHistoryPocketsList() {
        mModel.query("");

    }
}
