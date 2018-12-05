package com.gome.note.manager;

import android.graphics.drawable.AnimationDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.widget.ImageView;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/3/12
 * DESCRIBE:
 */

public class AudioPlayManager {
    public static final String TAG = "AudioPlayManager";
    private AudioTrack audioTrack;
    private DataInputStream dis;
    private Thread recordThread;
    private static AudioPlayManager mInstance;
    private int bufferSize;
    private AnimationDrawable animationDrawable;
    private ImageView imageView;
    private AudioTrackStopPlayListener audioTrackStopPlayListener;
    private byte[] mMusic;
    private int mMusicLength;

    private AudioPlayManager() {
        //        bufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
        //                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize * 2, AudioTrack.MODE_STREAM);
    }

    public static AudioPlayManager getInstance() {
        if (mInstance == null) {
            synchronized (AudioPlayManager.class) {
                if (mInstance == null) {
                    mInstance = new AudioPlayManager();
                }
            }
        }
        return mInstance;
    }

    private void destroyThread() {
        try {
            if (null != recordThread && Thread.State.RUNNABLE == recordThread.getState()) {
                try {
                    Thread.sleep(10);
                    recordThread.interrupt();
                } catch (Exception e) {
                    recordThread = null;
                }
            }
            recordThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recordThread = null;
        }
    }

    private void startThread() {
        destroyThread();
        if (recordThread == null) {
            recordThread = new Thread(recordRunnable);
            recordThread.start();
        }
    }

    private Runnable recordRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                byte[] tempBuffer = new byte[bufferSize];
                int readCount;
                audioTrack.play();
                while (dis.available() > 0) {
                    readCount = dis.read(tempBuffer);

                    if (readCount != 0 && readCount != -1) {
                        audioTrack.write(tempBuffer, 0, readCount);
                    }
                }
                stopPlay();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private void setPath(String path) throws Exception {
        //File file = new File(path);
        dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
    }

    public void startPlay(String path) {

        try {
            bufferSize = AudioTrack.getMinBufferSize(16000,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    16000, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize * 2,
                    AudioTrack.MODE_STREAM);
            setPath(path);
            startThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        if (null != audioTrackStopPlayListener) {
            audioTrackStopPlayListener.audioTrackStopPlay(imageView, animationDrawable);
        }

        try {
            if (audioTrack != null && audioTrack.getState() == AudioRecord.STATE_INITIALIZED) {
                audioTrack.stop();
            }

            destroyThread();
            if (audioTrack != null) {
                //
                audioTrack.release();
            }
            audioTrack = null;
            if (dis != null) {
                dis.close();
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void setAudioTrackStopPlayListener(AudioTrackStopPlayListener audioTrackStopPlayListener) {
        this.audioTrackStopPlayListener = audioTrackStopPlayListener;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;

    }

    public void setAnimationDrawable(AnimationDrawable animationDrawable) {
        this.animationDrawable = animationDrawable;
    }

    public interface AudioTrackStopPlayListener {
        void audioTrackStopPlay(ImageView imageView, AnimationDrawable animationDrawable);
    }

    public AudioTrack getAudioTrack() {
        return audioTrack;
    }

    public void setAudioTrack(AudioTrack audioTrack) {
        this.audioTrack = audioTrack;
    }
}
