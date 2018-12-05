package com.gome.note.ui.share;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.ui.create.NoteCreateActivity;
import com.gome.note.ui.home.NoteHomeActivity;
import com.gome.note.utils.FileUtils;

import java.io.File;
import java.util.HashMap;

public class ExteriorShareCreateActivity extends Activity {

    private static final String TEXT_PLAIN = "text/plain";
    public static final String SHARE_NO_SUPPORT = "sharenosupport";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handlerIntent();
    }

    private void handlerIntent() {
        NoteConfig.inMultiWindowNoteId = -1;
        Intent intent = new Intent(getIntent());
        String action = intent.getAction();
        String type = intent.getType();
        if (!Intent.ACTION_SEND.equals(action)) {
            startMainActivity(this);
            finish();
            return;
        }
        String url = intent.getStringExtra("url");
        String summary = intent.getStringExtra("summary");
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (isEmpty(text)) {
            CharSequence sequence = intent.getCharSequenceExtra(Intent.EXTRA_TEXT);
            if (!isEmpty(sequence)) {
                text = sequence.toString();
            }
        }

        String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
        Uri data = intent.getData();
        Uri stream = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String path = getRealFilePath(this, stream);
        if (isEmpty(path)) {
            File file = getFileForUri(stream);
            if (file != null && file.exists()) {
                path = file.getAbsolutePath();
            }
        }
        if (!FileUtils.isImage(path)) {
            path = "";
        }

        if (!isEmpty(text) || !isEmpty(path)) {
            startCreateHomePocketActivity(this, text, path);
        } else {
            startMainActivity(this);
        }
        finish();
    }

    private String uriToString(Uri uri) {
        if (uri != null) {
            return uri.toString();
        }
        return "";
    }

    private boolean isEmpty(CharSequence str) {
        return TextUtils.isEmpty(str);
    }

    private String getTitle(String text, String url) {
        if (!isEmpty(text) && !isEmpty(url)) {

            String pre = NoteConfig.unescape("%E3%80%90");
            String suf = NoteConfig.unescape("%E3%80%91");
            int indexPre = text.indexOf(pre);
            int indexSuf = text.indexOf(suf);
            if (indexPre > 0 && indexPre < text.length() && indexSuf > 0 && indexSuf < text.length()) {
                return replaceAll(text.substring(indexPre, indexSuf).replace(pre, "").replace(suf, ""));
            }
            pre = NoteConfig.unescape("%E5%88%86%E4%BA%AB%EF%BC%9A");
            indexPre = text.indexOf(pre);
            if (indexPre > 0 && indexPre < text.length()) {
                return replaceAll(text.substring(indexPre, text.length()).replace(pre, "").replace(url, ""));
            }
            pre = NoteConfig.unescape("%E3%80%8A");
            suf = NoteConfig.unescape("%E3%80%8B");
            indexPre = text.indexOf(pre);
            indexSuf = text.indexOf(suf);
            if (indexPre > 0 && indexPre < text.length() && indexSuf > 0 && indexSuf < text.length()) {
                return replaceAll(text.substring(indexPre, indexSuf).replace(pre, "").replace(suf, ""));
            }
            return replaceAll(text.replace(url, ""));
        }
        return "";
    }

    private String replaceAll(String text) {
        if (text != null) {
            text = text.replaceAll(" ", "");
            text = text.replaceAll("\u3000", "");
            text = text.replaceAll("<p>", "");
            text = text.replaceAll("</p>", "");
            text = text.replaceAll("<b>", "");
            text = text.replaceAll("</b>", "");
        }
        return text;
    }

    private String[] getStringArray(String text, String split) {
        if (!isEmpty(text)) {
            if (text.contains(split)) {
                return text.split(split);
            }
        }
        return null;
    }


    private void startMainActivity(Context context) {
        Intent i = new Intent();
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        i.setClass(context, NoteHomeActivity.class);
        i.putExtra(SHARE_NO_SUPPORT, true);
        context.startActivity(i);
        //ToastUtils.showShortSafe(R.string.not_collect_file_tip);
        Toast.makeText(this, R.string.not_collect_file_tip, Toast.LENGTH_SHORT).show();
    }

    private void startCreateHomePocketActivity(Context context, String text, String path) {
        Intent i = new Intent(context, NoteCreateActivity.class);
        i.setType("text/plain");
        i.putExtra("path", path);
        i.putExtra(Intent.EXTRA_TEXT, text);
        i.putExtra("isNewCreate", true);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.e("ExteriorShareCreateActivity", "logNote onDestroy  android.os.Process.myPid()  : " + android.os.Process.myPid());
    }

    public static String getRealFilePath(Context context, Uri uri) {
        if (null == uri) {
            return null;
        }
        String scheme = uri.getScheme();
        String data = "";
        if (scheme == null) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, null, null, null, null);
                if (NoteConfig.LOG_ENABLE_ALL) {
                    print(cursor);
                }
                if (cursor != null && !cursor.isClosed()) {
                    cursor.moveToFirst();
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                    return data;
                }
            } catch (Exception e) {
                NoteConfig.print("", "handlerIntent getRealFilePath ie error", true, NoteConfig.toArray("message"), e.toString());
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        return data;
    }

    private static void print(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.moveToFirst();
            ContentValues values = new ContentValues();
            int N = cursor.getColumnCount();
            for (int i = 0; i < N; i++) {
                String columnName = cursor.getColumnName(i);
                int type = cursor.getType(i);
                switch (type) {
                    case Cursor.FIELD_TYPE_INTEGER: {
                        long value = 0;
                        try {
                            value = cursor.getLong(i);
                        } catch (Exception e) {
                            NoteConfig.print("", "handlerIntent getRealFilePath", true, NoteConfig.toArray("index", "columnName", "type", "error"), i, columnName, type, e.getMessage());
                        }
                        values.put(columnName, String.valueOf(value));
                    }
                    break;
                    case Cursor.FIELD_TYPE_FLOAT: {
                        float value = 0;
                        try {
                            value = cursor.getFloat(i);
                        } catch (Exception e) {
                            NoteConfig.print("", "handlerIntent getRealFilePath", true, NoteConfig.toArray("index", "columnName", "type", "error"), i, columnName, type, e.getMessage());
                        }
                        values.put(columnName, String.valueOf(value));
                    }
                    break;
                    case Cursor.FIELD_TYPE_STRING: {
                        values.put(columnName, cursor.getString(i));
                    }
                    break;
                }
            }
        }
    }

    private static final HashMap<String, File> mRoots = new HashMap<String, File>();

    static {
        addRoot("external_files", buildPath(Environment.getExternalStorageDirectory(), ""));
    }

    private static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (segment != null) {
                cur = new File(cur, segment);
            }
        }
        return cur;
    }

    public static void addRoot(String name, File root) {
        try {
            // Resolve to canonical path to keep path checking fast
            root = root.getCanonicalFile();
            mRoots.put(name, root);
        } catch (Exception e) {
        }
    }

    public static File getFileForUri(Uri uri) {
        try {
            String path = uri.getEncodedPath();

            final int splitIndex = path.indexOf('/', 1);
            final String tag = Uri.decode(path.substring(1, splitIndex));
            path = Uri.decode(path.substring(splitIndex + 1));
            //Environment.getExternalStorageDirectory()
            File root = mRoots.get(tag);
            if (root == null) {
                return null;
            }

            File file = new File(root, path);
            try {
                file = file.getCanonicalFile();
            } catch (Exception e) {
            }

            if (!file.getPath().startsWith(root.getPath())) {
                return null;
            }
            if (!file.exists()) {
                return null;
            }
            return file;
        } catch (Exception e) {

        }
        return null;
    }
}
