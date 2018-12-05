package com.gome.note.view;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.service.RecordService;
import com.gome.note.ui.create.NoteCreateActivity;
import com.gome.note.utils.TimerCount;

import java.util.Timer;
import java.util.TimerTask;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/3/6
 * DESCRIBE:
 */

public class RecordDialog extends Dialog implements View.OnClickListener {


    private Context mContext;
    private RelativeLayout mRlStartTime;
    private ImageView mIvRecordingMic;
    private TextView mTvRecordingTime;
    private RelativeLayout mRlEndCountDown;
    private TextView mTvEndCountDown;
    private TextView mTvMarkEndCountDown;
    private RelativeLayout mRlEndLastSecond;
    private ImageView mIvEndLastSecond;
    private TextView mTvMarkLastSecond;
    private Button mBtComplete;
    private ImageView mIvCancel;
    private TimerCount timerCount;
    private AnimationDrawable animationDrawable;
    private NoteCreateActivity mActivity;
    private long timeStatistics;
    private Animation animationRlEndLastSecondVisible;
    private Animation animationStartTimeGone;
    private Animation animationEndCountDownVisible;
    private Handler mHandler;
    //is send recording complete handler`s msg
    private boolean isSendCompleteMsg;


    public RecordDialog(@NonNull Context context, int themeResId, NoteCreateActivity activity, Handler handler) {
        super(context, themeResId);
        mContext = context;
        mActivity = activity;
        mHandler = handler;
        isSendCompleteMsg = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recording_view);

        setCanceledOnTouchOutside(false);
        initView();
        initListener();
        startTime();

    }

    private void initView() {

        mRlStartTime = (RelativeLayout) findViewById(R.id.rl_start_time);
        mIvRecordingMic = (ImageView) findViewById(R.id.iv_recording_mic);
        mTvRecordingTime = (TextView) findViewById(R.id.tv_recording_time);

        mRlEndCountDown = (RelativeLayout) findViewById(R.id.rl_end_count_down);
        mTvEndCountDown = (TextView) findViewById(R.id.tv_end_count_down);
        mTvMarkEndCountDown = (TextView) findViewById(R.id.tv_mark_end_count_down);

        mRlEndLastSecond = (RelativeLayout) findViewById(R.id.rl_end_last_second);
        mIvEndLastSecond = (ImageView) findViewById(R.id.iv_end_last_second);
        mTvMarkLastSecond = (TextView) findViewById(R.id.tv_mark_last_second);

        mBtComplete = (Button) findViewById(R.id.bt_complete);

        mIvCancel = (ImageView) findViewById(R.id.iv_cancel);

    }


    private void initListener() {

        mBtComplete.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_complete:
                // RecordService
                if (!isSendCompleteMsg) {
                    isSendCompleteMsg = true;
                    Message message = new Message();
                    message.what = NoteConfig.RECORD_COMPLETE;
                    mHandler.sendMessage(message);

                    timerCountCancel();
                }
                break;

            case R.id.iv_cancel:
                Message messageCancel = new Message();
                messageCancel.what = NoteConfig.RECORD_CANCEL;
                mHandler.sendMessage(messageCancel);

                timerCountCancel();
                break;
        }
    }


    private void startTime() {

        if (timerCount != null) {
            timerCount.cancel();
            //防止new出多个导致时间跳动加速
            timerCount = null;
        }

        animationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(
                R.drawable.record_mic_anim);
        animationRlEndLastSecondVisible = AnimationUtils.loadAnimation(mContext, R.anim.end_last_visible);
        animationStartTimeGone = AnimationUtils.loadAnimation(mContext, R.anim.mic_gone);
        animationEndCountDownVisible = AnimationUtils.loadAnimation(mContext, R.anim.count_down_visible);

        timerCount = new TimerCount(60000, 1000, this);
        timerCount.start();

    }

    public void setCountTime(long arg0) {
        long time = arg0 / 1000;
        timeStatistics = 60 - time;
        if (timeStatistics == 59) {
            timeStatistics = 60;
        }
        if (time <= 10) {
            if (time <= 1) {

                if (mRlStartTime.getVisibility() == View.VISIBLE) {
                    mRlStartTime.setVisibility(View.GONE);
                }

                if (mRlEndCountDown.getVisibility() == View.VISIBLE) {
                    mRlEndCountDown.setVisibility(View.GONE);
                }
                if (mRlEndLastSecond.getVisibility() == View.GONE) {
                    mRlEndLastSecond.setAnimation(animationRlEndLastSecondVisible);
                    mRlEndLastSecond.setVisibility(View.VISIBLE);
                }
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if (!isSendCompleteMsg) {
                            isSendCompleteMsg = true;
                            Message message = new Message();
                            message.what = NoteConfig.RECORD_COMPLETE;
                            mHandler.sendMessage(message);
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 900);

            } else {


                if (mRlStartTime.getVisibility() == View.VISIBLE) {
                    mRlStartTime.setAnimation(animationStartTimeGone);
                    mRlStartTime.setVisibility(View.GONE);
                }

                if (mRlEndCountDown.getVisibility() == View.GONE) {
                    mRlEndCountDown.setAnimation(animationEndCountDownVisible);
                    mRlEndCountDown.setVisibility(View.VISIBLE);
                }

                if (mRlEndLastSecond.getVisibility() == View.VISIBLE) {
                    mRlEndLastSecond.setVisibility(View.GONE);
                }
            }
            if (null != animationDrawable && animationDrawable.isRunning()) {
                animationDrawable.stop();
            }

        } else {
            if (mRlStartTime.getVisibility() == View.GONE) {
                mRlStartTime.setVisibility(View.VISIBLE);
            }
            if (mRlEndCountDown.getVisibility() == View.VISIBLE) {
                mRlEndCountDown.setVisibility(View.GONE);
            }

            if (mRlEndLastSecond.getVisibility() == View.VISIBLE) {
                mRlEndLastSecond.setVisibility(View.GONE);
            }

            mIvRecordingMic.setBackground(animationDrawable);
            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }


            if (60 - time < 10) {
                mTvRecordingTime.setText("00:0" + String.valueOf(60 - time));
            } else {
                mTvRecordingTime.setText("00:" + String.valueOf(60 - time));
            }

        }
        mTvEndCountDown.setText(String.valueOf(time));

    }

    public void timerCountCancel() {
        if (null != timerCount) {
            timerCount.cancel();
        }
    }

    public long getTime() {
        return timeStatistics;
    }

    @Override
    protected void onStop() {
        super.onStop();
        timerCountCancel();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mActivity.recordCancel();
    }

    public void setActivityNull() {
        mActivity = null;
    }
}
