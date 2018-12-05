package com.gome.note.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.Formatter;

import com.blankj.utilcode.util.ConvertUtils;
import com.blankj.utilcode.util.FileUtils;
import com.gome.note.db.config.NoteConfig;

import java.io.File;


public class FileHelper {

    @NonNull
    public static String copyFile(@NonNull String srcPath, @NonNull String destPath) {

        boolean copyFile = FileUtils.copyFile(srcPath, destPath);

        if (null != destPath && destPath.length() > 0) {
            return destPath;
        } else {
            if (copyFile) {
                return destPath;
            } else {
                throw new RuntimeException(FileUtils.getFileNameNoExtension(srcPath) + " copy " +
                        "fail !");
            }
        }


    }


    /**
     * get Available <br/>
     * from http://www.devnote.cn/article/238.html
     *
     * @return /data dir  Available size(：byte)
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    /**
     * get all
     *
     * @return /data dir all size (：byte)
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }

    public static boolean hasEnoughFreeRoom(@Nullable Runnable error) {
        long availableMemorySize = FileHelper.getAvailableInternalMemorySize();
        if (availableMemorySize <= NoteConfig.minAvilableMemoSize && error != null) {
            error.run();
        }
        return availableMemorySize > NoteConfig.minAvilableMemoSize;
    }

    public static boolean hasEnoughFreeRoom() {
        long availableMemorySize = FileHelper.getAvailableInternalMemorySize();
        return availableMemorySize > NoteConfig.minAvilableMemoSize;
    }

    public static long getAvailMemory(@NonNull Context context) {

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);

        return mi.availMem;
    }

    public static boolean isLowMemoryNow(@NonNull Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.lowMemory;
    }
}
