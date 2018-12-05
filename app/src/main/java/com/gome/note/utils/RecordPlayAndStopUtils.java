package com.gome.note.utils;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.manager.AudioPlayManager;
import com.gome.note.view.ColoredLinearyLayout;

import java.io.File;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/8/9
 * DESCRIBE:
 */

public class RecordPlayAndStopUtils {
    private Toast mToast;
    private TelephonyManager mTelephonyManager;
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;


    public void playOrStop(ColoredLinearyLayout coloredLinearyLayout, Context context, ImageView imageView, String audioPath,
                           AnimationDrawable animationDrawable, AudioPlayManager audioPlayManager) {

        getAudioFocus(context, animationDrawable, audioPlayManager);
        audioPlayManager.setImageView(imageView);
        audioPlayManager.setAnimationDrawable(animationDrawable);

        if (null != audioPath) {
            File file = new File(audioPath);
            if (null != file && file.length() > 0) {
                startOrStopAudio(coloredLinearyLayout, context, audioPath, imageView, animationDrawable, audioPlayManager);
            } else {
                showToast(context);
            }
        } else {
            showToast(context);
        }
    }


    private void startOrStopAudio(ColoredLinearyLayout coloredLinearyLayout, Context context, String audioPath,
                                  ImageView imageView, AnimationDrawable animationDrawable, AudioPlayManager audioPlayManager) {
        if (NoteConfig.AUDIOPLAYID == 0) {
            //start play
            NoteConfig.AUDIOPLAYID = imageView.getId();
            requestAudioFocus();
            playAudio(context, audioPlayManager, audioPath, imageView, animationDrawable);

        } else if (NoteConfig.AUDIOPLAYID == imageView.getId()) {
            AudioTrack audioTrack = audioPlayManager.getAudioTrack();
            if (null == audioTrack || audioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED
                    || audioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                requestAudioFocus();
                playAudio(context, audioPlayManager, audioPath, imageView, animationDrawable);
            } else {
                stopAudio(audioPlayManager, animationDrawable);
                abandonAudioFocus();
            }
        } else {
            int count = coloredLinearyLayout.getChildCount();
            for (int i = 0; i < count; i++) {
                View view = coloredLinearyLayout.getChildAt(i);
                if (view instanceof LinearLayout) {
                    View childView = ((LinearLayout) view).getChildAt(0);
                    if (childView instanceof RelativeLayout) {
                        ImageView imageViewChild = (ImageView) ((RelativeLayout) childView).getChildAt(0);
                        AnimationDrawable drawable = (AnimationDrawable) imageViewChild.getBackground();
                        if (drawable.isRunning()) {
                            stopAudio(audioPlayManager, drawable);
                            abandonAudioFocus();
                        }
                    }
                }
            }
            NoteConfig.AUDIOPLAYID = imageView.getId();
            requestAudioFocus();
            playAudio(context, audioPlayManager, audioPath, imageView, animationDrawable);
        }


    }

    private void playAudio(Context context, AudioPlayManager audioPlayManager, String audioPath, ImageView imageView, AnimationDrawable animationDrawable) {
        if (null == audioPlayManager || null == audioPath || audioPath.length() == 0 || null == imageView || null == animationDrawable) {
            return;
        }
        if (phoneIsInUse(context)) {
            return;
        }
        audioPlayManager.startPlay(audioPath);
        imageView.setBackground(animationDrawable);
        animationDrawable.start();
    }

    private void stopAudio(AudioPlayManager audioPlayManager, AnimationDrawable animationDrawable) {
        if (null == audioPlayManager || null == animationDrawable) {
            return;
        }
        audioPlayManager.stopPlay();
        animationDrawable.stop();
        animationDrawable.selectDrawable(0);
    }


    private void getAudioFocus(Context context, AnimationDrawable animationDrawable, AudioPlayManager audioPlayManager) {

        if (null == mAudioManager) {
            mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }
        if (null == mFocusChangeListener) {
            mFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                            || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        //stop play
                        stopAudio(audioPlayManager, animationDrawable);
                        abandonAudioFocus();
                    }
                }
            };
        }
    }

    private void requestAudioFocus() {
        if (null != mAudioManager && null != mFocusChangeListener) {
            int result = mAudioManager.requestAudioFocus(mFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void abandonAudioFocus() {
        if (null != mAudioManager && null != mFocusChangeListener) {
            mAudioManager.abandonAudioFocus(mFocusChangeListener);
        }
    }

    private void showToast(Context context) {
        if (null != mToast) {
            mToast.setText(R.string.file_is_error);
        } else {
            mToast = Toast.makeText(context, R.string.file_is_error, Toast.LENGTH_SHORT);
        }
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    public boolean phoneIsInUse(Context context) {
        boolean isInCall = false;
        if (null == mTelephonyManager) {
            mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        int type = mTelephonyManager.getCallState();
        if (type == TelephonyManager.CALL_STATE_IDLE) {
            //phone is not have call
            isInCall = false;
        } else if (type == TelephonyManager.CALL_STATE_OFFHOOK) {
            //phone is in call
            isInCall = true;
        } else if (type == TelephonyManager.CALL_STATE_RINGING) {
            //phone is call ringing
            isInCall = true;
        } else {
            //phone is not have call
            isInCall = false;
        }
        return isInCall;
    }
}
