package notesearch.utils;

import android.content.Context;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DataUtils {
    private static String TAG = "DataUtils";

    /**
     * by judge the time of character string to confirm whether is today
     * this method is inefficient
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * turn the type of character string to date
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new
            ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                }
            };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new
            ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return new SimpleDateFormat("yyyy-MM-dd");
                }
            };
    private static ThreadLocal<SimpleDateFormat> DateLocal = new ThreadLocal<SimpleDateFormat>();

    /**
     * judge whether is today , this method is efficient enough
     *
     * @param day the time   "2016-06-28 10:10:30" "2016-06-28" all be ok
     * @return true means today
     * @throws ParseException
     */
    public static boolean IsToday(String day) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = null;
        try {
            date = getDateFormat().parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }


    /**
     * judge whether is today , this method is efficient enough
     *
     * @param millis the time   "1526030343290" all be ok
     * @return true means today
     * @throws ParseException
     */
    public static boolean IsToday(long millis) {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = new Date(millis);
//        try {
//            date = getDateFormat().parse(day);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * judge whether is yesterday , this method is efficient enough
     *
     * @param day the time   "2016-06-28 10:10:30" "2016-06-28" all be ok
     * @return true means today
     * @throws ParseException
     */
    public static boolean IsYesterday(String day) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        Date date = getDateFormat().parse(day);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * judge whether is yesterday , this method is efficient enough
     *
     * @param millis the time   "1526030343290" all be ok
     * @return true means today
     * @throws ParseException
     */
    public static boolean IsYesterday(long millis) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);

        Calendar cal = Calendar.getInstance();
        //Date date = getDateFormat().parse(day);
        Date date = new Date(millis);
        cal.setTime(date);

        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR))) {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR)
                    - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == -1) {
                return true;
            }
        }
        return false;
    }

    public static SimpleDateFormat getDateFormat() {
        if (null == DateLocal.get()) {
            DateLocal.set(new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA));
        }
        return DateLocal.get();
    }


    public static boolean isCurrentYear(long modifiedTime) throws ParseException {

        Calendar pre = Calendar.getInstance();
        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        if (pre.get(Calendar.YEAR) == modify.get(Calendar.YEAR)) {
            return true;
        }


        return false;
    }


    public static String currentTime24(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        //24 hours
        int hour24 = modify.get(Calendar.HOUR_OF_DAY);
        int minute = modify.get(Calendar.MINUTE);
        if (minute < 10) {
            return (hour24 < 10 ? "0" + hour24 : hour24) + ":" + "0" + minute;
        } else {
            return (hour24 < 10 ? "0" + hour24 : hour24) + ":" + minute;
        }

    }

    public static String currentTime12(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        //12 hours
        int hour12 = modify.get(Calendar.HOUR);
        int minute = modify.get(Calendar.MINUTE);
        if (minute < 10) {
            return (hour12 < 10 ? "0" + hour12 : hour12) + ":" + "0" + minute;
        } else {
            return (hour12 < 10 ? "0" + hour12 : hour12) + ":" + minute;
        }
    }


    public static int currentNoon(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        //0 is shangwu , 1 is xiawu
        int noon = modify.get(Calendar.AM_PM);

        return noon;
    }

    public static int getModifyYear(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        int year = modify.get(Calendar.YEAR);


        return year;
    }

    public static int getModifyMouth(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        int mouth = modify.get(Calendar.MONTH);


        return mouth + 1;
    }


    public static int getModifyDay(long modifiedTime) throws ParseException {

        Calendar modify = Calendar.getInstance();
        Date modifiedDate = new Date(modifiedTime);
        modify.setTime(modifiedDate);
        int day = modify.get(Calendar.DAY_OF_MONTH);


        return day;
    }
}
