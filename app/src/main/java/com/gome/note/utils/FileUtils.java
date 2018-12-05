package com.gome.note.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.gome.note.base.Config;
import com.gome.note.entity.Forever;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;


public class FileUtils {
    public static final String TAG = "FileUtils";

    public static final int SIZETYPE_B = 1;
    public static final int SIZETYPE_KB = 2;
    public static final int SIZETYPE_MB = 3;
    public static final int SIZETYPE_GB = 4;

    public static File createFile(String suffix) {
        return new File(Config.getRandomlyGeneratedFilePath(suffix));
    }

    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFolderSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getFormatValue(blockSize, sizeType);
    }

    public static String getAutoFileOrFilesSize(String filePath) {
        long blockSize = getFileOrFilesSizeWithFork(filePath);

        return formatSize(blockSize);
    }

    public static long getFileOrFilesSizeWithFork(String filePath) {
        long blockSize = 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ForkJoinPool forkjoinPool = new ForkJoinPool();

            blockSize = forkjoinPool.invoke(new FileSizeFinder(new File(
                    filePath)));
        } else {
            File file = new File(filePath);
            try {
                if (file.isDirectory()) {
                    blockSize = getFolderSize(file);
                } else {
                    blockSize = getFileSize(file);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return blockSize;
    }

    public static long getFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFolderSize(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blockSize;
    }

    public static void checkFileExist(File file) {

        if (file.isDirectory()) {
        } else if (file.isFile()) {
        }
        if (!file.exists()) {
        }
    }

    public static String getExtension(String s) {
        int idx = s.lastIndexOf('.');
        return idx == -1 ? null : s.substring(idx).toLowerCase();
    }

    private static long getFileSize(File file) throws Exception {
        long size = 0;
        boolean isNew = file.createNewFile();
        if (!isNew && file.isFile()) {
            size = file.length();
        } else {
        }
        return size;
    }

    private static long getFolderSize(File f) throws Exception {
        long size = 0;
        File filesList[] = f.listFiles();
        for (File node : filesList) {
            if (node.isDirectory()) {
                size = size + getFolderSize(node);
            } else {
                size = size + getFileSize(node);
            }
        }
        return size;
    }

    public static String formatSize(long size) {
        if (size == 0) {
            return "0B";
        }

        String ret = "";
        DecimalFormat dcmFmt = new DecimalFormat("#.00");
        double curVal;
        if (size < 1024) {
            curVal = size;
            ret += dcmFmt.format(curVal) + "B";
        } else if (size < (1024 * 1024)) {
            curVal = (double) size / 1024;
            ret += dcmFmt.format(curVal) + "KB";
        } else if (size < (1024 * 1024 * 1024)) {
            curVal = (double) size / (1024 * 1024);
            ret += dcmFmt.format(curVal) + "MB";
        } else {
            curVal = (double) size / (1024 * 1024 * 1024);
            ret += dcmFmt.format(curVal) + "GB";
        }

        return ret;
    }

    private static double getFormatValue(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / (1024 * 1024)));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / (1024 * 1024 * 1024)));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    public static boolean isImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String tmp = path.toLowerCase(Locale.US);
        String[] imageExt = {".png", ".jpg", ".bmp", ".wbmp", ".gif", ".jpeg"};
        for (String node : imageExt) {
            if (tmp.endsWith(node)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAudio(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String tmp = path.toLowerCase(Locale.US);
        String[] audioExt = {".amr", ".3ga", ".wav", ".mp3", ".ogg", ".midi",
                ".m4a", ".rtttl", ".rtx", ".imy", ".aac", ".mid", ".3gpp",
                ".wma", ".flac", ".awb", ".smf", ".ape"};
        for (String node : audioExt) {
            if (tmp.endsWith(node)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVideo(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String tmp = path.toLowerCase(Locale.US);
        String[] videoExt = {".3gp", ".mp4", ".m4v", ".wmv", ".webm", ".avi",
                ".mpeg", ".mpg", ".3gpp", ".3g2", ".3gpp2", ".mkv", ".ts",
                ".m2ts", ".m2t", ".mts", ".mov", ".divx", ".vob", ".rmvb",
                ".flv", ".asf"};
        for (String node : videoExt) {
            if (tmp.endsWith(node)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDoc(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String tmp = path.toLowerCase(Locale.US);
        String[] videoExt = {".htm", ".html", ".txt", ".vcf", ".wml",
                ".webarchivexml", ".doc", ".docx", ".pdf", ".ppt", ".pps",
                ".pptx", ".xls", ".xlsx", ".xhtml", ".vcs", ".rtf"};
        for (String node : videoExt) {
            if (tmp.endsWith(node)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isApk(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        String tmp = path.toLowerCase(Locale.US);
        return tmp.endsWith(".apk");
    }


    private static FileFilter mFileFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(".")) {
                return false;
            }
            return !pathname.isHidden();
        }
    };

    public static PackageInfo getApkPackageName(Context cx, String path) {
        PackageManager pm = cx.getPackageManager();
        PackageInfo packageInfo = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);

        return packageInfo;
    }

    public static boolean isInstallPackage(Context context, String packageName) {
        PackageManager mgr = context.getPackageManager();
        boolean install = false;
        try {
            mgr.getPackageInfo(packageName, 0);
            install = true;
        } catch (NameNotFoundException e) {
        }

        return install;
    }

    public static boolean isApkInstalled(Context cx, String path) {

        if (path == null || "".equals(path)) {
            return false;
        }
        PackageInfo uninstallPackageInfo = getApkPackageName(cx, path);
        try {
            PackageManager pm = cx.getPackageManager();
            PackageInfo installPackageInfo = pm.getPackageInfo(uninstallPackageInfo.packageName,
                    PackageManager.GET_ACTIVITIES);
            if (uninstallPackageInfo.versionName.equalsIgnoreCase(installPackageInfo.versionName)
                    && (uninstallPackageInfo.versionCode == installPackageInfo.versionCode)) {

                return true;
            } else {

                return false;
            }
        } catch (NameNotFoundException e) {

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isSupportHCTTheme() {
        return getHCTThemeLevel() > 0;
    }

    private static int getHCTThemeLevel() {
        try {
            Class.forName("androidhct.R");
            return 1;
        } catch (ClassNotFoundException e) {
            try {
                Field f = Class.forName("android.R$style").getField("Theme_HCT_Light");
                if (f != null) {
                    return 2;
                }
            } catch (SecurityException e1) {
            } catch (NoSuchFieldException e1) {
            } catch (ClassNotFoundException e1) {
            }
        }

        return -1;
    }

    public static int getHCTThemeStyle(String styleName) {
        int level = getHCTThemeLevel();
        try {

            Field field;
            if (level == 1) {
                field = Class.forName("androidhct.R$style").getField(styleName);
            } else if (level == 2) {
                field = Class.forName("android.R$style").getField(styleName);
            } else {
                return -1;
            }
            field.setAccessible(true);
            return field.getInt(null);
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        } catch (ClassNotFoundException e) {
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        }
        return -2;
    }

    public static boolean isDoubleCardProject() {
        return (Build.MODEL.equals("HCT S2004")
                || Build.MODEL.equals("HCT A2015")
                || Build.MODEL.equals("HCT B2015")
                || Build.MODEL.equals("HCT A2016"));
    }

//    public static void setRingtone(Context cx, String path, int type) {
//        Cursor c = DataService.getAudioCursor(cx, path);
//
//        if (c != null) {
//            if (c.moveToFirst()) {
//                long id = c.getLong(c.getColumnIndex(MediaStore.Audio.Media._ID));
//                Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media
// .EXTERNAL_CONTENT_URI, id);
//                HctRingtoneManagerRef.setActualDefaultRingtoneUri(cx, type, ringUri);
//
//                String message;
//                String title = c.getString(c.getColumnIndex(MediaStore.Audio.Media.TITLE));
//                if (type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION")) {
//                    message = cx.getString(R.string.notification_ringtone_set, title);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                } else {
//                    message = cx.getString(R.string.ringtone_set, title);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                String message;
//                if (type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION")) {
//                    message = cx.getString(R.string.notification_ringtone_set_failure, path);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                } else {
//                    message = cx.getString(R.string.ringtone_set_failure, path);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                }
//            }
//            c.close();
//        } else {
//            Log.e(TAG, "setRingtone() cursor is NULL!");
//        }
//    }
//
//    public static void setRingtoneDoubleCard(Context cx, String path, int type) {
//        Cursor c = DataService.getAudioCursor(cx, path);
//
//        if (c != null) {
//            if (c.moveToFirst()) {
//                long id = (long) c.getInt(0);
//                Log.d(TAG, "setRingtoneDoubleCard() data id =" + String.valueOf(id));
//                Uri ringUri = ContentUris.withAppendedId(MediaStore.Audio.Media
// .EXTERNAL_CONTENT_URI, id);
//                HctRingtoneManagerRef.setActualDefaultRingtoneUri(cx, type, ringUri);
//
//                String message;
//                if (type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION")
//                        || type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION_SECOND")) {
//                    message = cx.getString(R.string.notification_ringtone_set, c.getString(2));
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                } else {
//                    message = cx.getString(R.string.ringtone_set, c.getString(2));
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Log.e(TAG, "setRingtoneDoubleCard() cursor has no data");
//                String message;
//                if (type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION")
//                        || type == HctRingtoneManagerRef.getValue("TYPE_NOTIFICATION_SECOND")) {
//                    message = cx.getString(R.string.notification_ringtone_set_failure, path);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                } else {
//                    message = cx.getString(R.string.ringtone_set_failure, path);
//                    Toast.makeText(cx, message, Toast.LENGTH_SHORT).show();
//                }
//            }
//            c.close();
//        } else {
//            Log.e(TAG, "setRingtoneDoubleCard() cursor is null");
//        }
//    }

//    public static int getDrawable(Context context, File f) {
//        String path = f.getAbsolutePath();
//        int resId;
//        if (f.isDirectory()) {
//            resId = R.drawable.ic_folder;
//        } else {
//            resId = getDefaultIconId(context, path);
//        }
//        return resId;
//    }

    public static Bitmap getImageThumbnailClassic(String archiveFilePath) {
        Bitmap ret = null;
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        localOptions.outWidth = 0;
        localOptions.outHeight = 0;
        localOptions.inSampleSize = 1;
        int thumbnailHeight = 90;
        int thumbnailWidth = 90;
        BitmapFactory.decodeFile(archiveFilePath, localOptions);
        if ((localOptions.outWidth > 0) && (localOptions.outHeight > 0)) {
            int k = Math.max(
                    Math.max((localOptions.outWidth + thumbnailWidth - 1) / thumbnailWidth,
                            (localOptions.outHeight
                                    + thumbnailHeight - 1)
                                    / thumbnailHeight), 1);
            localOptions.inSampleSize = k;
            localOptions.inJustDecodeBounds = false;
            Bitmap localBitmap = BitmapFactory.decodeFile(archiveFilePath, localOptions);
            if (localBitmap != null) {
                BitmapDrawable localBitmapDrawable = new BitmapDrawable(localBitmap);
                localBitmapDrawable.setGravity(17);
                localBitmapDrawable.setBounds(0, 0, thumbnailWidth, thumbnailHeight);
                ret = localBitmapDrawable.getBitmap();
            }
        }
        return ret;
    }

    private static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) w) / width;
        float scaleHeight = ((float) h) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    public static Bitmap loadAppShortBmp(Context cx, String archiveFilePath) {
        PackageManager pm = cx.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            appInfo.sourceDir = archiveFilePath;
            appInfo.publicSourceDir = archiveFilePath;

            Bitmap bitmap = ((BitmapDrawable) appInfo.loadIcon(pm)).getBitmap();
            bitmap = resizeImage(bitmap, 80, 80);

            return bitmap;
        }
        return null;
    }

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable.getOpacity() != PixelFormat
                .OPAQUE ?
                Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldBmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(oldBmp, 0, 0, width, height, matrix, true);
    }

    public static Bitmap zoomDrawable(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }

    private static int mIsAbroad = 0; // 0:is not abroad; 1: is abroad; -1: no value

//    public static boolean isAbroadBranch(Context cx) {
//        if (mIsAbroad == -1) {
//            String val = SystemPropertiesProxy.get(cx, "ro.gios.custom");
//            mIsAbroad = val.equalsIgnoreCase("abroad") ? 1 : 0;
//        }
//
//        return (mIsAbroad == 1);
//    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static class FileSizeFinder extends RecursiveTask<Long> {
        final File file;

        public FileSizeFinder(final File theFile) {
            file = theFile;
        }

        @Override
        protected Long compute() {
            long size = 0;
            if (file.isFile()) {
                size = file.length();
            } else {
                File[] children = file.listFiles();
                if (children != null) {
                    List<ForkJoinTask<Long>> tasks = new ArrayList<ForkJoinTask<Long>>();
                    for (File child : children) {
                        if (child.isFile()) {
                            size += child.length();
                        } else {
                            tasks.add(new FileSizeFinder(child));
                        }
                    }
                    for (ForkJoinTask<Long> forkJoinTask : invokeAll(tasks)) {
                        size += forkJoinTask.join();
                    }
                }
            }
            return size;
        }

    }

    public static String formatTime(long msTime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return dateFormat.format(msTime);
    }


    public static Uri getImageStreamFromExternalN(String imageName) {
        //before 6.0

        File picPath = new File(imageName);
        Uri uri = null;
        if (picPath.exists()) {
            uri = Uri.fromFile(picPath);
        }

        return uri;
    }


    public static Uri getImageStreamFromExternal(String imageName, Context context) {
        // after 6.0

        File picPath = new File(imageName);
        Uri uri = null;
        if (picPath.exists()) {
            uri = FileProvider.getUriForFile(context, Forever.fileAuthority, picPath);
        }

        return uri;
    }

    public static void closeIO(Closeable closeable) {
        if (closeable == null) return;
        try {
            closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void deleteFolder(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File files[] = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteFolder(files[i]);
            }
        }
        file.delete();
    }

    public static boolean deleteFile(String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File file = new File(path);
            if (file != null && file.isFile() && file.exists()) {
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean existFile(String path) {
        return existFile(path, 0);
    }

    public static boolean existFile(String path, long greater) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        try {
            File file = new File(path);
            if (file != null
                    && file.isFile()
                    && file.exists()
                    && file.length() > greater) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
