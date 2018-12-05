package com.gome.note.db;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;


import com.gome.note.db.config.ContentType;
import com.gome.note.db.config.LableType;
import com.gome.note.entity.ContentInfo;
import com.gome.note.entity.LabelInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ContentParser {

    public static String lablesList2Json(List<LabelInfo> record) {
        return lablesList2Json(record, false);
    }

    protected static String lablesList2Json(List<LabelInfo> record, boolean isNeedEncode) {
        if (record == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (LabelInfo l : record) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(LableType.KEY_ID, l.getId());
                //jsonObject.put(LableType.KEY_TITLE,l.getTitle());
                //jsonObject.put(LableType.KEY_STICK,l.isStick());
                jsonArray.put(jsonObject);
            }
            String res = jsonArray.toString();
            if (isNeedEncode) {
                return base64Encode(res);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<LabelInfo> json2LablesList(Context context, String lables) {
        return json2LablesList(context, lables, false);
    }

    protected static List<LabelInfo> json2LablesList(Context context, String lables, boolean
            isNeedDecode) {
        ArrayList<LabelInfo> contentInfos = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(lables)) {
                return contentInfos;
            }
            if (isNeedDecode) {
                lables = base64Decode(lables);
            }
            JSONArray jsonArray = new JSONArray(lables);
            int N = jsonArray.length();
            for (int i = 0; i < N; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                long id = object.getLong(LableType.KEY_ID);
                LabelInfo li = PocketDbHandle.queryLabelInfoById(context, PocketDbHandle.
                        URI_LABLE, id);
                contentInfos.add(li);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentInfos;
    }

    private static String ensureEmpty(String s) {
        return TextUtils.isEmpty(s) ? "" : s;
    }

    public static String contentsList2Json(List<ContentInfo> record) {
        return contentsList2Json(record, false);
    }

    protected static String contentsList2Json(List<ContentInfo> record, boolean isNeedEncode) {
        if (record == null) {
            return null;
        }
        try {
            JSONArray jsonArray = new JSONArray();
            for (ContentInfo c : record) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(ContentType.KEY_TEXT, ensureEmpty(c.getText()));
                jsonObject.put(ContentType.KEY_IMAGE, ensureEmpty(c.getImage()));
                jsonObject.put(ContentType.KEY_AUDIO, ensureEmpty(c.getAudio()));
                jsonObject.put(ContentType.KEY_VIDEO, ensureEmpty(c.getVideo()));
                jsonObject.put(ContentType.KEY_FILE, ensureEmpty(c.getFile()));
                jsonObject.put(ContentType.KEY_WEBVIEW, ensureEmpty(c.getWebview()));
                jsonObject.put(ContentType.KEY_HAS_CHECK_BOX, c.isHasCheckBox());
                jsonObject.put(ContentType.KEY_IS_FIRST_LINE, c.isFirstLine());
                jsonObject.put(ContentType.KEY_IS_CHECKED, c.isChecked());
                jsonObject.put(ContentType.KEY_AUDIOTIME, c.getAudioTime());
                jsonArray.put(jsonObject);
            }
            String res = jsonArray.toString();
            if (isNeedEncode) {
                return base64Encode(res);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<ContentInfo> json2ContentsList(String contents) {
        return json2ContentsList(contents, false);
    }

    protected static List<ContentInfo> json2ContentsList(String contents, boolean isNeedDecode) {
        ArrayList<ContentInfo> contentInfos = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(contents)) {
                return contentInfos;
            }
            if (isNeedDecode) {
                contents = base64Decode(contents);
            }
            JSONArray jsonArray = new JSONArray(contents);
            int N = jsonArray.length();
            for (int i = 0; i < N; i++) {
                JSONObject object = (JSONObject) jsonArray.get(i);
                ContentInfo info = new ContentInfo(
                        object.getString(ContentType.KEY_TEXT),
                        object.getString(ContentType.KEY_IMAGE),
                        object.getString(ContentType.KEY_AUDIO),
                        object.getString(ContentType.KEY_VIDEO),
                        object.getString(ContentType.KEY_FILE),
                        object.getString(ContentType.KEY_WEBVIEW),
                        checkExistKey(object, ContentType.KEY_HAS_CHECK_BOX),
                        checkExistKey(object, ContentType.KEY_IS_FIRST_LINE),
                        checkExistKey(object, ContentType.KEY_IS_CHECKED),
                        object.optString(ContentType.KEY_AUDIOTIME)
                );
                contentInfos.add(info);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentInfos;
    }

    private static boolean checkExistKey(JSONObject object, String key) {
        if (object.has(key)) {
            return object.optBoolean(key);
        }
        return false;
    }

    private static boolean isUserBase64 = false;

    private static String base64Encode(@NonNull String orgin) {
        if (!isUserBase64) {
            return orgin;
        }
        return new String(Base64.encode(orgin.getBytes(), Base64.DEFAULT));
    }

    private static String base64Decode(@NonNull String orgin) {
        if (!isUserBase64) {
            return orgin;
        }
        return new String(Base64.decode(orgin.getBytes(), Base64.DEFAULT));
    }
}
