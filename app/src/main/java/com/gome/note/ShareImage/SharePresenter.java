package com.gome.note.ShareImage;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.manager.EditNoteConstans;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.utils.PermissionUtils;

import java.io.File;


public class SharePresenter implements ISharePresenter {

    private final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String imageSaveFolder = Environment.getExternalStorageDirectory().getPath() + "/note/image/";
    private final int REQUEST_PERMISSION_CODE_SAVE = 1;

    private Bitmap mBitmap = EditNoteConstans.shareBitmap;
    private Context mContext;
    public String mPath;
    private Toast toastSavePath;
    private String mShareImagePath;

    public SharePresenter(Context context) {
        mContext = context;

    }

    @Override
    public Bitmap getBitmap() {
        return mBitmap;
    }

    @Override
    public void sendBitmap() {
        shareBitmap();
    }

    private void shareBitmap() {
        new AsyncTask<String, Integer, Uri>() {

            @Override
            protected void onPreExecute() {
                // mView.showLoadingDialog("加载中...");
            }

            @Override
            protected Uri doInBackground(String... params) {
                Uri uri = saveImageAndGetUri();
                return uri;
            }

            @Override
            protected void onPostExecute(Uri uri) {
                // mView.unShowLoadingDialog();
                //   mView.showShareDialog(uri);
            }
        }.execute();
    }

    @Override
    public void saveImage() {
        PermissionUtils.checkPermission(mContext, PERMISSION_STORAGE, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                saveImageToLocation(mBitmap);
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                saveImageToLocation(mBitmap);
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                PermissionUtils.requestPermission(mContext, PERMISSION_STORAGE, REQUEST_PERMISSION_CODE_SAVE);
            }
        });
    }

    @Override
    public Uri saveImageAndGetUri() {
        String filePath = mContext.getExternalFilesDir("share").getPath() + "/" + TimeUtils.getNowMills() + ".jpg";
        mShareImagePath = filePath;
        File file = new File(filePath);
        ImageUtils.save(mBitmap, file, Bitmap.CompressFormat.JPEG);

        Uri contentUri = getImageContentUri(mContext, file);
        return contentUri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CODE_SAVE:
                requestPermissionForSaveImageResult(grantResults);
                break;
        }
    }

    @Override
    public void onDestroy() {
        mBitmap.recycle();
        mBitmap = null;
        if (!TextUtils.isEmpty(mShareImagePath)) {
            ActivityCommonUtils.deleteFile(mShareImagePath);
        }


    }

    private void requestPermissionForSaveImageResult(@NonNull int[] grantResults) {

        PermissionUtils.onRequestPermissionResult(mContext, PERMISSION_STORAGE, grantResults, new PermissionUtils.PermissionCheckCallBack() {
            @Override
            public void onHasPermission() {
                saveImage();
            }

            @Override
            public void onUserHasAlreadyTurnedDown(String... permission) {
                //ToastUtils.showShort(mContext.getString(R.string.save_failed));
                Toast.makeText(mContext, mContext.getString(R.string.save_failed), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUserHasAlreadyTurnedDownAndDontAsk(String... permission) {
                showToAppSettingDialog();
            }
        });
    }

    private void saveImageToLocation(Bitmap bitmap) {
        File file = new File(imageSaveFolder + "/" + TimeUtils.getNowMills() + ".jpg");
        mPath = file.getAbsolutePath();
        ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
        //ToastUtils.showLong(mContext.getString(R.string.save_to_local) + mPath + mContext.getString(R.string.in_in));
        if (null != toastSavePath) {
            toastSavePath.setText(mContext.getString(R.string.save_to_local) + mPath + mContext.getString(R.string.in_in));
        } else {
            toastSavePath = Toast.makeText(mContext,
                    mContext.getString(R.string.save_to_local) + mPath + mContext.getString(R.string.in_in),
                    Toast.LENGTH_SHORT);
        }
        toastSavePath.setDuration(Toast.LENGTH_SHORT);
        toastSavePath.show();

        updateEncryptDB(mPath);

    }

    private void sendScanBroadcast(Context context, String path) {
        Uri mUri = Uri.parse(path);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, mUri);
        context.sendBroadcast(intent);
    }

    private void updateEncryptDB(String absPath) {
        MediaScannerConnection.scanFile(mContext,
                new String[]{absPath}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {

                    }
                });
    }


    /**
     * Gets the content:// URI from the given corresponding path to a file * * @param context * @param imageFile * @return content Uri
     */
    public static Uri getImageContentUri(Context context, java.io.File imageFile) {
        if (null == imageFile) {
            return null;
        }
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ", new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


    public void showToAppSettingDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle(mContext.getString(R.string.permission_setting))
                .setMessage(mContext.getString(R.string.permission_setting_1))
                .setPositiveButton(mContext.getString(R.string.go_to),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                PermissionUtils.toAppSetting(mContext);
                            }
                        })
                .setNegativeButton(mContext.getString(R.string.cancel), null)
                .show();
    }
}
