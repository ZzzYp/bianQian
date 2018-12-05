package com.gome.note.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gome.note.R;
import com.gome.note.base.Config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.graphics.BitmapFactory.decodeFile;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Author：viston on 2017/9/26 10:14
 */
public class ActivityCommonUtils {
    static ProgressBar mProgress;
    private static Handler mHandler;
    private static Handler mHandlerOnly;


    /**
     * got file from filemanagerActivity, and add to slide
     **/
    public static void getFilerResult(Intent data, Context activity) {
        Uri uri = null;
        String currentDir = null;
        String fileName = null;
        String path = null;
        ArrayList<String> arrayList = new ArrayList<>();
        if (data.hasExtra("result_dir_or_file")) {
            path = data.getStringExtra("result_dir_or_file");
            if (TextUtils.isEmpty(path)) {
                return;
            }
            File file = new File(path);
            uri = Uri.fromFile(file);
            currentDir = file.getParent();
            fileName = file.getName();
        } else {
            uri = data.getData();
            currentDir = data.getStringExtra("currentDir");
            fileName = data.getStringExtra("fileName");
        }
        File f = new File(path);
        String nameType = f.getName();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!isWithRightTypeFile(prefix)) {
            if (null != activity) {
                Toast.makeText(activity, activity.getString(R.string.add_right_type), Toast.LENGTH_SHORT).show();
            }
            return;
        }
    }

    public static boolean isWithRightTypeFile(String type) {
        switch (type) {
            case "txt":
                return true;
            case "pdf":
                return true;
            case "pptx":
                return true;
            case "docx":
                return true;
            case "doc":
                return true;
            case "xls":
                return true;
            case "ppt":
                return true;
            case "xlsx":
                return true;
            default:
                return false;
        }
    }

    public static String replaceContent(String content, int i) {
        String contentReplace = "";
        contentReplace = content.replace("&amp;", "&").replace("&nbsp;", " ").replace("&gt;", ">").replace("&lt;", "<").replaceAll("\\s*", "");
        if (contentReplace.length() >= 100) {
            if (i == 0) {
                return contentReplace.substring(0, 100);
            } else {
                if (contentReplace.length() >= 130) {
                    return contentReplace.substring(0, 130);
                } else {
                    return contentReplace.substring(0, contentReplace.length());
                }
            }
        } else {
            return contentReplace;
        }
    }


    public static String replacetitle(String content, int i) {
        String contentReplace = "";
        contentReplace = content.replace("&amp;", "&").replace("&nbsp;", " ").replace("&gt;", ">").replace("&lt;", "<").replaceAll("\\s*", "");
        if (contentReplace.length() >= 30) {
            if (i == 0) {
                return contentReplace.substring(0, 30);
            } else {
                if (contentReplace.length() >= 50) {
                    return contentReplace.substring(0, 50);
                } else {
                    return contentReplace.substring(0, contentReplace.length());
                }
            }
        } else {
            return contentReplace;
        }
    }

    public static void showProgress(final ProgressBar progress) {
        progress.setVisibility(VISIBLE);
        progress.postDelayed(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(GONE);
            }
        }, 700);
    }

    public static void hideProgress(ProgressBar progress) {
        progress.setVisibility(GONE);
    }


    public static String useNewPhotoPath(String path, int count) {
        return getimage(path, count);
    }


    public static Bitmap resizeImage2(String path,
                                      int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        bitmap = decodeFile(path, options);
        return bitmap;
    }

    public static String getimage(String srcPath, int count) {
        try {
            Bitmap bitmap = resizeImage2(srcPath, 480, 800);
            return compressImage(bitmap, srcPath, count);
        } catch (Exception e) {
        }
        return "";
    }

    public static String compressImage(Bitmap image, String willCopyPath, int count) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        int options = 50;
        while (baos.toByteArray().length / 1024 > 100) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        String destPath = Config.getCachePath(Config.TYPE_PATH_CACHE_IMAGES);
        File cameraFile = FileUtils.createFile("png");
        destPath = Config.getCachePathCache(cameraFile.getAbsolutePath(), destPath);
        try {
            write(Bitmap2Bytes(bitmap), destPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return destPath;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);//png类型
        return baos.toByteArray();
    }

    // 写到sdcard中
    public static void write(byte[] bs, String destPath) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(destPath));
        out.write(bs);
        out.flush();
        out.close();
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    public static void clearFolder(String folderPath) {
        File file = new File(folderPath);
        if (null != file && file.isDirectory()) {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (null != f && f.exists()) {
                    f.delete();
                }
            }
        }
    }

    public static boolean isMediaRecorderUsered(Context context) {
        MediaRecorder mediaRecorder = new MediaRecorder();
        String path = context.getExternalCacheDir().getPath() + File.separator + Config.PATH_CACHE_VIDEOS + File.separator;
        try {
            File fileDir = new File(path);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            String filePath = path + System.currentTimeMillis() + ".mp4";
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setOutputFile(filePath);
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        } finally {
            mediaRecorder.release();
            mediaRecorder = null;
            deleteFile(path);
        }
        return false;
    }
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
