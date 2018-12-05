package com.gome.note.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Author：viston on 2017/6/23 09:23
 */
public class TimeUtil {
    /**
     * long to MM/ dd/yyyy
     *
     * @param dateFormat(MM/ dd/yyyy HH:mm:ss)
     * @param millSec
     * @return
     */
    public static String transferLongToDate(String dateFormat, Long millSec) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date date = new Date(millSec);
        return sdf.format(date);
    }
}
