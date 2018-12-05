package com.gome.note.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.gome.note.db.config.NoteConfig;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/3/8
 * DESCRIBE:
 */

public class RecordServiceBinder {

    private Context mContext;
    private Intent serviceIntent;

    private boolean serviceBound = false;
    private Messenger serverMsger;
    private String path;
    private boolean isBindServer;

    public RecordServiceBinder(Context context) {
        mContext = context;


    }

    public void bindservice(String audioPath) {
        path = audioPath;
        serviceIntent = new Intent(mContext, RecordService.class);
        serviceIntent.putExtra(NoteConfig.AUDIO_STORE_PATH, audioPath);
        isBindServer = mContext.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            serverMsger = new Messenger(binder);
            serviceBound = true;


            if (serverMsger != null) {
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("send", "start");
                data.putString("path", path);
                msg.setData(data);
                msg.replyTo = clientMsger;
                try {
                    serverMsger.send(msg);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serverMsger = null;
            serviceBound = false;
        }
    };
    private Handler h = new Handler() {
        @Override
        public void handleMessage(Message msg) {


        }
    };
    private Messenger clientMsger = new Messenger(h);


    public void stopService() {

        if (serverMsger != null) {
            Message msg = new Message();
            Bundle data = new Bundle();
            data.putString("send", "stop");
            msg.setData(data);
            msg.replyTo = clientMsger;
            try {
                serverMsger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        if (isBindServer) {
            mContext.unbindService(serviceConnection);
            isBindServer = false;
        }
        mContext.stopService(serviceIntent);
        serviceBound = false;
        serverMsger = null;
        h = null;

    }
}
