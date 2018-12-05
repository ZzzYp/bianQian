package com.gome.note.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * Author：viston on 2017/6/26 20:02
 */
public class SystemUtils {
    public static String LAUNCHER_PACKET = "com.gome.launcher";
    public static String DEMO_PACK = "com.example.dell_.myapplication2";

    public static Context getContext(Context context) {
        if (ShowStyle.IS_FROM_ACTIVITY) {
            return context;
        } else {
            try {
                context = context.createPackageContext(LAUNCHER_PACKET, Context.
                        CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return context;
        }
    }

//    public static String isShowNavigationBar(Context context) {
//        String show = Settings.System.getString(context.getContentResolver(), Settings.System.SHOW_NAVIGATIONBAR_SWITCH);
//        if (TextUtils.isEmpty(show)) {
//            return "";
//        } else {
//            return show;
//        }
//    }

    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean phoneIsInUse(Context context) {
        boolean isInCall = false;
        if (context != null) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

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
        }
        return isInCall;
    }
}
