package com.gome.note.utils;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/3/8
 * DESCRIBE:
 */

public class AudioPlayUtils {

    private AudioTrack player;
    private boolean running;

    public void playAudio(String audioPath) {
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (running)
                    play(audioPath);
            }
        }).start();


    }

    //播放音频（PCM）
    public void play(String audioPath) {


        DataInputStream dis = null;
        try {
            //从音频文件中读取声音
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(audioPath)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //最小缓存区
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioTrack对象   依次传入 :流类型、采样率（与采集的要一致）、音频通道（采集是IN 播放时OUT）、量化位数、最小缓冲区、模式

        if (null == player) {
            player = new AudioTrack(AudioManager.STREAM_MUSIC, 8000,
                    AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);
        }


        byte[] data = new byte[bufferSizeInBytes];
        player.play();//开始播放
        while (true) {
            int i = 0;
            try {
                while (dis.available() > 0 && i < data.length) {
                    data[i] = dis.readByte();//录音时write Byte 那么读取时就该为readByte要相互对应
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            player.write(data, 0, data.length);

            if (i != bufferSizeInBytes) //表示读取完了
            {

                if (null != player && player.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {

                    player.stop();//停止播放
                    player.release();//释放资源

                    break;
                }
            }
        }

    }


    public void stop() {
        if (null != player) {
            player.stop();
            player.release();
            running = false;
        }

    }
}
