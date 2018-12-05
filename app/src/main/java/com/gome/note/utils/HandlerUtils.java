package com.gome.note.utils;

import android.os.Handler;
import android.os.Message;


/**
 * Created by Administrator on 2017/8/29.
 */

public class HandlerUtils {

    public static boolean hasMessages(Handler handler, int what) {
        if (handler != null) {
            return handler.hasMessages(what);
        }
        return true;
    }

    public static void sendMessage(Handler handler, int what) {
        sendMessageDelayed(handler, what, 0);
    }

    public static void sendMessage(Handler handler, int what, Object content) {
        sendMessageDelayed(handler, what, content, 0, 0, 0);
    }

    public static void sendMessageDelayed(Handler handler, int what, Object content, long delay) {
        sendMessageDelayed(handler, what, content, 0, 0, delay);
    }

    public static void sendMessageDelayed(Handler handler, int what, long delay) {
        sendMessageDelayed(handler, what, null, 0, 0, delay);
    }

    public static void removeMessages(Handler handler, int what) {
        if (handler != null) {
            handler.removeMessages(what);
        }
    }

    public static void sendMessageDelayed(Handler handler, int what, Object content, int arg1, int arg2, long delay) {
        if (handler != null) {
            Message msg = Message.obtain();
            msg.what = what;
            msg.obj = content;
            msg.arg1 = arg1;
            msg.arg2 = arg2;
            handler.sendMessageDelayed(msg, delay);
        }
    }

}
