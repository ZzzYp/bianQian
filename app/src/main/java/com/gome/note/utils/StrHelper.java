package com.gome.note.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gome.note.R;
import com.gome.note.entity.Forever;


/**
 * Created on 2016/12/17.
 *
 * @author pythoncat.cheng
 * @apiNote str utils
 */

public class StrHelper {


    /**
     * check is string contains head and  end
     *
     * @param origin origin string
     * @param head   head str
     * @param foot   end str
     * @return is contains head and foot
     */
    public static boolean isContainsKeyStr(@NonNull String origin,
                                           @NonNull String head,
                                           @NonNull String foot) {
        return origin.contains(head) && origin.contains(foot);
    }

    public static boolean isContainsImageKey(@NonNull String origin) {
        return origin.contains(Forever.IMG_HEAD) && origin.contains(Forever.IMG_FOOT);
    }

    public static boolean isContainsAudioKey(@NonNull String origin) {
        return origin.contains(Forever.AUDIO_HEAD) && origin.contains(Forever.AUDIO_FOOT);
    }

    /**
     * fully remove all str start with head and end with end str
     *
     * @param origin origin string
     * @param head   head str
     * @param foot   end str
     * @return the origin str without removed
     */
    private static String removeAllKeyStr(@NonNull String origin, @NonNull String head, @NonNull
            String foot) {
        while (isContainsKeyStr(origin, head, foot)) {
            // have head + foot
            String drop = origin.substring(origin.indexOf(head), origin.indexOf(foot) + foot
                    .length());
            origin = origin.replace(drop, "");
        }
        return origin;
    }

    /**
     * remove all subStr start with {@link }
     * and end with {@link }
     *
     * @param origin origin str
     * @return the origin str without removed
     */
    public static String removeAllKeyStr(@NonNull String origin) {
        return removeAllKeyStr(origin, Forever.IMG_HEAD, Forever.IMG_FOOT);
    }

    /**
     * pick up the key str start with head and foot
     *
     * @param origin           origin string
     * @param head             head str
     * @param foot             end str
     * @param containsHeadFoot need containsHeadFoot？
     * @return the key str or ""
     */
    private static String pickupKey(@NonNull String origin,
                                    @NonNull String head,
                                    @NonNull String foot,
                                    boolean containsHeadFoot) {
        if (isContainsKeyStr(origin, head, foot)) {
            String keyStr;
            if (containsHeadFoot) {
                keyStr = origin.substring(origin.indexOf(head), origin.indexOf(foot) + foot
                        .length());
            } else {
                // no head + foot
                keyStr = origin.substring(origin.indexOf(head) + head.length(), origin.indexOf
                        (foot));
            }
            return keyStr;
        } else {
            return "";
        }
    }

    /**
     * pick up the key str start with head and foot
     *
     * @param origin origin string
     * @param head   head str
     * @param foot   end str
     * @return the key str or ""
     */
    public static String pickupKey(@NonNull String origin,
                                   @NonNull String head,
                                   @NonNull String foot) {
        return pickupKey(origin, head, foot, false);
    }

    /**
     * pick up the key str start with {@link }
     * and end with {@link }
     *
     * @param origin origin string
     * @return the key str or ""
     */
    public static String pickupImageKey(@NonNull String origin) {
        return pickupKey(origin, Forever.IMG_HEAD, Forever.IMG_FOOT, false);
    }

    public static String pickupAudioKey(@NonNull String origin) {
        return pickupKey(origin, Forever.AUDIO_HEAD, Forever.AUDIO_FOOT, false);
    }

    /**
     * get the count of how many the key subStr in the origin str
     *
     * @param origin origin string
     * @param head   head str
     * @param foot   end str
     * @return count
     */
    private static int getKeyStrCounts(@NonNull String origin,
                                       @NonNull String head,
                                       @NonNull String foot) {
        int count = 0;
        while (isContainsKeyStr(origin, head, foot)) {
            // contains head + foot
            count += 1;
            String drop = origin.substring(origin.indexOf(head), origin.indexOf(foot) + foot
                    .length());
            // you can not use String.replace method here. because may have the same subStr in
            // origin string
            //            origin = origin.replace(drop, "");
            origin = origin.substring(0, origin.indexOf(drop)) +
                    origin.substring(origin.indexOf(drop) + drop.length(),
                            origin.length());
        }
        return count;
    }

    /**
     * * get the count of how many the key subStr in the origin str
     *
     * @param origin origin string
     * @return count
     */
    public static int getKeyStrCounts(@NonNull String origin) {
        return getKeyStrCounts(origin, Forever.IMG_HEAD, Forever.IMG_FOOT);
    }

    public static String concatTitleContent(@NonNull Context c, @Nullable String title,
                                            @Nullable String content) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(title)) {
            sb.append(c.getString(R.string.title_1))
                    .append(":")
                    .append(title.trim());
        }
        if (!TextUtils.isEmpty(content) && !TextUtils.isEmpty(content.trim())) {
            sb.append("\n")
                    .append(c.getString(R.string.content_tip))
                    .append(":")
                    .append(content);
        }
        return sb.toString();
    }




}
