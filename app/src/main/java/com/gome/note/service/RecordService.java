package com.gome.note.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.gome.note.R;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RecordService extends Service {
    private AudioRecord mRecorder;
    private TelephonyManager manager;
    private Context mContext;
    private String TAG = "RecordService";
    private boolean isRecording;
    private String mAudioStorePath;
    private final Messenger serverMsger = new Messenger(new MessageHandler());

    public RecordService() {

    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // mAudioStorePath = intent.getStringExtra(NoteConfig.AUDIO_STORE_PATH);

        // startRecording();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        return serverMsger.getBinder();

    }

    private class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Message replyMsg = new Message();
            Bundle data = new Bundle();
            String stop = msg.getData().getString("send");
            if (null != stop && stop.equals("stop")) {
                isRecording = false;
            } else if (null != stop && stop.equals("start")) {
                mAudioStorePath = msg.getData().getString("path");
                startRecording();
            }
        }
    }


    private void startRecording() {
        if (validateMicAvailability() && !phoneIsInUse(mContext)) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    startRecord();
                }
            });
            thread.start();
        } else {
            Toast.makeText(mContext, mContext.getString(R.string.mic_or_audio_is_in_use), Toast.LENGTH_SHORT).show();
        }
    }

    public void startRecord() {
        int frequency = 16000;
        int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        File file = new File(mAudioStorePath);

        if (file.exists())
            file.delete();

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException(file.toString());
        }
        try {
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);

            byte[] buffer = new byte[bufferSize];
            audioRecord.startRecording();
            isRecording = true;
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);

                if (bufferReadResult != 0 && bufferReadResult != -1) {
                    dos.write(buffer, 0, bufferReadResult);
                } else {
                    break;
                }
            }

            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {

        }
    }

    private boolean validateMicAvailability() {
        ((AudioManager) getSystemService(AUDIO_SERVICE))
                .requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        Boolean available = true;
        mRecorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);

        try {
            if (mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                available = false;
            }
            mRecorder.startRecording();
            if (mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                mRecorder.stop();
                available = false;
            }
            mRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
        return available;
    }


    public boolean phoneIsInUse(Context context) {
        boolean isInCall = false;
        if (null == manager) {
            manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        int type = manager.getCallState();
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
