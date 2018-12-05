package com.gome.note.utils;

import android.content.Context;
import android.support.annotation.WorkerThread;
import android.widget.Toast;


/**
 * Created on 2016/12/14.
 *
 * @author pythoncat.cheng
 * @apiNote toast
 */

public class ToastHelper {

    private static Toast t;

    public static void show(Context c, Object text) {
        cancel();
        t = Toast.makeText(c, text.toString(), Toast.LENGTH_SHORT);
        t.show();
    }

    public static void cancel() {
        if (t != null)
            t.cancel();

    }

    @WorkerThread
    public static void showUI(Context c, Object text) {
        cancel();
        if (Thread.currentThread().getName().equals("main")) {
            t = Toast.makeText(c, text.toString(), Toast.LENGTH_SHORT);
            t.show();
        } else {
//            Observable.empty()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(obj -> {
//                        t = Toast.makeText(c, text.toString(), Toast.LENGTH_SHORT);
//                        t.show();
//                    }, Throwable::printStackTrace);
        }

    }
}
