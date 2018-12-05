package com.gome.note.base;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.Forever;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.BitmapFactory.decodeFile;
import static com.gome.note.utils.ActivityCommonUtils.Bitmap2Bytes;

public abstract class BaseActivity extends Activity {
    public File cameraFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public abstract void initPresenter();


    public void openCamera(Activity activity) {
        //cameraFile = FileUtils.createFile("png");
        cameraFile = createFilePath("png", Config.PATH_CACHE_IMAGES);
        if (TextUtils.isEmpty(cameraFile.getAbsolutePath())) {
            return;
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri cameraUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                cameraUri = FileProvider.getUriForFile(this.getApplicationContext(), Forever.fileAuthority, cameraFile);
                // only be valid after 5.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    ClipData clip =
                            ClipData.newUri(getContentResolver(), "A photo", cameraUri);
                    intent.setClipData(clip);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
            } else {
                cameraUri = Uri.fromFile(cameraFile);
            }
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                try {
                    activity.startActivityForResult(intent, NoteConfig.REQUEST_CAMERA);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    public File createFilePath(String suffix, String fileName) {
        String path = getExternalCacheDir().getPath() + File.separator + fileName + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        String filePath = path + System.currentTimeMillis() + "." + suffix;
        return new File(filePath);
    }

    public String compressImage(Bitmap image, String willCopyPath, int count) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while (baos.toByteArray().length / 1024 > 500) {
            baos.reset();
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            options -= 10;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        String destPath = "";
        File imageFile = createFilePath("png", Config.PATH_CACHE_IMAGES);
        //destPath = Config.getCachePathCache(cameraFile.getAbsolutePath(), destPath);
        destPath = imageFile.getAbsolutePath();
        try {
            write(Bitmap2Bytes(bitmap), destPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null != image) {
            image.recycle();
        }
        if (null != bitmap) {
            bitmap.recycle();
        }
        return destPath;
    }

    public static void write(byte[] bs, String destPath) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(destPath));
        out.write(bs);
        out.flush();
        out.close();
    }

    public String getimage(String srcPath, int count, int cunstomSampleSize) {
        try {
            if (null != srcPath && srcPath.length() > 0) {
                Bitmap bitmap = resizeImage2(srcPath, 480, 800, cunstomSampleSize);
                return compressImage(bitmap, srcPath, count);
            }

        } catch (Exception e) {
        }
        return "";
    }

    public Bitmap resizeImage2(String path,
                               int width, int height, int cunstomSampleSize) {
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
            if (cunstomSampleSize > 0) {
                options.inSampleSize = cunstomSampleSize;
            } else {
                options.inSampleSize = sampleSize;
            }

        }

        options.inJustDecodeBounds = false;
        bitmap = decodeFile(path, options);
        return bitmap;
    }
}
