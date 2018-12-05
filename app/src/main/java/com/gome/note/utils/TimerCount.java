package com.gome.note.utils;

/**
 * Created by shuaishuai.shi on 2016/3/25.
 */

import android.widget.Button;

import com.gome.note.view.RecordDialog;

public class TimerCount extends CountDownTimers {
    private Button bnt;
    private RecordDialog mRecordDialog;


    public TimerCount(long millisInFuture, long countDownInterval, RecordDialog recordDialog) {
        super(millisInFuture, countDownInterval);
        mRecordDialog = recordDialog;
    }


    @Override
    public void onFinish() {


    }

    @Override
    public void onTick(long arg0) {
        // TODO Auto-generated method stub
        if (null != mRecordDialog) {
            mRecordDialog.setCountTime(arg0);
        }

    }

}