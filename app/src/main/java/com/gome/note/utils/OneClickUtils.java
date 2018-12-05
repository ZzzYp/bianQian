package com.gome.note.utils;

import java.util.Calendar;

class OneClickUtils {

    private String flag;
    private              long lastClickTime        = 0;
    private static final int  MIN_CLICK_DELAY_TIME = 200;

    OneClickUtils(String flag) {
        this.flag = flag;
    }

    String getFlag() {
        return flag;
    }

    boolean check() {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            return false;
        } else {
            return true;
        }
    }
}
