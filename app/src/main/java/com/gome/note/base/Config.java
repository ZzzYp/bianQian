package com.gome.note.base;

import android.text.TextUtils;

import com.blankj.utilcode.util.FileUtils;
import com.gome.note.utils.FileHelper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class Config {
    public final static int QUERY_SUCCESS = 101;
    public final static int QUERY_ERROR = 102;
    public final static int QUERY_HOME_LABEL_SUCCESS = 103;
    public final static int CLASSIFY_HOME_NOTEINFO = 104;
    public final static int ADD_SUCCESS = 201;
    public final static int ADD_ERROR = 202;
    public final static int UPDATE_SUCCESS = 301;
    public final static int UPDATE_ERROR = 302;
    public final static int COTENT_LABEL_UPDATE_SUCCESS = 303;
    public final static int DELETE_SUCCESS = 401;
    public final static int DELETE_ERROR = 402;
    public final static int QAUD_ERROR = 501;
    public final static int LABEL_GET_COUNT = 5;

    public static final boolean DEBUG = Boolean.parseBoolean("true");
    public static final String APPLICATION_ID = "com.gome.note";
    public static final String BUILD_TYPE = "debug";
    public static final String FLAVOR = "";
    public static final int VERSION_CODE = 1;
    public static final String VERSION_NAME = "1.0.0.180101_alpha";
    // Fields from default config.
    public static final String DBPROVIDER_AUTHORITIES = "com.gome.note.dbProvider";
    public static final String FILEPROVIDER_AUTHORITIES = "com.gome.note.fileProvider";

    private static String sPreDataString = "";
    private static int sPreDataInt = 0;
    public static final String _ROOT_PATH_CACHE = "/storage/emulated/0/noteRecord/cache/";


    //as cache
    public static final String ROOT_PATH_CACHE = "/storage/emulated/0/.my_pocket/";
    public static final String DIR_PATH_CACHE_IMAGES = "images/";
    public static final String DIR_PATH_CACHE_AUDIOS = "audios/";
    public static final String DIR_PATH_CACHE_VIDEOS = "videos/";
    public static final String DIR_PATH_CACHE_FILES = "files/";
    public static final String DIR_PATH_CACHE_WEBS = "webs/";
    public static final String PATH_CACHE_IMAGES = "images";
    public static final String PATH_CACHE_AUDIOS = "audios";
    public static final String PATH_CACHE_VIDEOS = "videos";
    public static final String PATH_CACHE_FILES = "files";
    public static final String PATH_CACHE_WEBS = "webs";


    public static final int TYPE_PATH_CACHE_IMAGES = 100;
    public static final int TYPE_PATH_CACHE_AUDIOS = 101;
    public static final int TYPE_PATH_CACHE_VIDEOS = 102;
    public static final int TYPE_PATH_CACHE_FILES = 103;
    public static final int TYPE_PATH_CACHE_WEBS = 104;

    public static String getRandomlyGeneratedFilePath(String suffix) {
        String preDataString = formatDate(System.currentTimeMillis());
        if (!isEmpty(sPreDataString) && preDataString.compareTo(sPreDataString) <= 0) {
            preDataString = appendString(preDataString, String.valueOf(sPreDataInt++));
        }
        if (!isEmpty(sPreDataString)
                && !isEmpty(preDataString)
                && sPreDataString.length() > preDataString.length()) {
            sPreDataInt = 0;
        }
        sPreDataString = preDataString;
        return appendString(
                getCachePathEnsureExist(), File.separator, sPreDataString, ".", suffix);
    }


    public static String formatDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.getDefault());

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str)
                || str.length() <= 1
                || "0".equals(str)
                || ' ' == str.charAt(0)
                || " ".equals(str)
                || ".".equals(str)
                || "..".equals(str)
                || "...".equals(str)
                || "......".equals(str);
    }

    public static String appendString(String... url) {
        if (url != null && url.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : url) {
                if (!TextUtils.isEmpty(s)) {
                    builder.append(s);
                }
            }
            return builder.toString();
        }
        return "";
    }

    public static String getCachePathEnsureExist() {
        return ensureCachePathExist(_ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_IMAGES));
    }

    public static String ensureCachePathExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (success) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return file.getAbsolutePath();
        }
    }


    public static String getCachePath(int type) {
        switch (type) {
            case TYPE_PATH_CACHE_IMAGES:
                return Config.ensureCachePathExist(ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_IMAGES));
            case TYPE_PATH_CACHE_AUDIOS:
                return Config.ensureCachePathExist(ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_AUDIOS));
            case TYPE_PATH_CACHE_VIDEOS:
                return Config.ensureCachePathExist(ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_VIDEOS));
            case TYPE_PATH_CACHE_FILES:
                return Config.ensureCachePathExist(ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_FILES));
            case TYPE_PATH_CACHE_WEBS:
                return Config.ensureCachePathExist(ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_WEBS));
        }
        throw new RuntimeException("no define this type");
    }

    public static String getCachePathCache(String srcFilePath, String destDir) {
        if (TextUtils.isEmpty(srcFilePath)) {
            throw new RuntimeException("The source file is empty");
        }
        if (TextUtils.isEmpty(destDir)) {
            throw new RuntimeException("The destination folder is empty");
        }
        return FileHelper.copyFile(srcFilePath, destDir.concat(File.separator).concat(FileUtils.getFileName(srcFilePath))
                //.concat("__")
                //.concat(String.valueOf(System.currentTimeMillis()))
                //.concat(".")
                //.concat(FileUtils.getFileExtension(willCopyPath))
                .concat(""));
    }
}
