package com.gome.note.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.ui.create.DialogActionToActivityListener;
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

public class ImageAndCameraDialog extends Dialog implements View.OnClickListener, View.OnTouchListener {

    private String TAG = "NoteCreateActivity";
    private Context mContext;
    private TextView mTvCamera;
    private TextView mTvImage;
    private DialogActionToActivityListener mDialogActionToActivityListener;
    private View mBlankview;

    public ImageAndCameraDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_and_camera_item_view);

        initView();
        initListener();

    }


    private void initView() {
        mTvCamera = (TextView) findViewById(R.id.tv_camera);
        mTvImage = (TextView) findViewById(R.id.tv_image);
        mBlankview = findViewById(R.id.rl_blankview);

    }


    private void initListener() {

        mTvCamera.setOnClickListener(this);
        mTvImage.setOnClickListener(this);
        mBlankview.setOnTouchListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                if (null != mDialogActionToActivityListener) {
                    mDialogActionToActivityListener.clickCamera();
                }

                break;

            case R.id.tv_image:
                if (null != mDialogActionToActivityListener) {
                    mDialogActionToActivityListener.clickImage();
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.rl_blankview:
                cancel();
                break;
        }
        return false;
    }


    public void setDialogActionToActivityListener(DialogActionToActivityListener dialogActionToActivityListener) {
        mDialogActionToActivityListener = dialogActionToActivityListener;
    }
}
