package com.gome.note.utils;

import java.util.ArrayList;
import java.util.List;

public class AntiShake {
    private List<OneClickUtils> utils = new ArrayList<>();

    private boolean check(Object o) {
        String flag;
        if (o == null)
            flag = Thread.currentThread().getStackTrace()[2].getMethodName();
        else
            flag = o.toString();
        for (OneClickUtils util : utils) {
            if (util.getFlag().equals(flag)) {
                return util.check();
            }
        }
        OneClickUtils clickUtils = new OneClickUtils(flag);
        utils.add(clickUtils);
        return clickUtils.check();
    }

    public boolean check() {
        return check(null);
    }
}
