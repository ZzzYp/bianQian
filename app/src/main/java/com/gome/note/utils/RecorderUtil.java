package com.gome.note.utils;

import android.content.Context;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Handler;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * ProjectName:audioDemo
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/11/10
 * DESCRIBE:
 */

public class RecorderUtil extends AsyncTask<Void, Integer, Void> {
    private String TAG = "RecorderUtil";
    public static boolean isRecording = true, isPlaying = false; // 标记
    private File audioFile;
    private AudioRecord record;
    private Context mContext;
    private MediaRecorder mMediaRecorder;
    private Handler mHandler;

    public RecorderUtil(Context context, Handler handler) {
        mContext = context;
        mHandler = handler;
    }

    public void init() {

    }

    @Override
    protected Void doInBackground(Void... arg0) {

        try {
            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mMediaRecorder.setAudioChannels(2);
            mMediaRecorder.setAudioEncodingBitRate(128000);
            mMediaRecorder.setAudioSamplingRate(48000);
            audioFile = FileUtils.createFile("wav");
            mMediaRecorder.setOutputFile(audioFile.getAbsolutePath());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void stop() {
        String content = "";
        if (mMediaRecorder != null) {
            try {
                //add below three param (setOnErrorListener,setOnInfoListener,setPreviewDisplay) is for
                //resolve (RuntimeException:stop failed) when mediarecorder.stop();
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } finally {
                mMediaRecorder.release();
                mMediaRecorder = null;
            }
        }
    }

    @Override
    protected void onCancelled() {
        isRecording = false;
        if (record != null) {
            record.stop();
        }
        record = null;

        super.onCancelled();
    }


    public static void write(byte[] bs, String destPath) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(destPath));
        out.write(bs);
        out.flush();
        out.close();
    }

    private byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
