package note.gome.com.note;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import notesearch.bean.ContentInfo;
import notesearch.bean.ContentType;
import notesearch.bean.NoteHiBoardInfo;
import notesearch.bean.PocketInfo;
import notesearch.bean.PocketStore;
import notesearch.db.ProviderConfig;
import notesearch.utils.DataUtils;

import static java.lang.Long.getLong;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/26
 * DESCRIBE:
 */

public class NoteSearchUtil {

    public static final String NOTE_INFO_ID = "id";
    public static final String NOTE_PACKAGE_NAME = "com.gome.note";
    public static final String NOTE_DETAIL_CLASS = "com.gome.note.ui.create.NoteCreateActivity";
    public static final String NOTE_SEARCH_CLASS = "com.gome.note.ui.search.NoteSearchActivity";
    public static final String SEARCH_KEYWORD = "searchKeyword";

    public void printLog(String log) {
        System.out.print("test jar : " + log);
    }


    public static List<NoteHiBoardInfo> getNoteSearchResult(Context context, String keyword) {
        List<NoteHiBoardInfo> noteHiBoardInfos = new ArrayList<>();
        List<PocketInfo> pocketInfos = queryPocketsListNoStick(context, ProviderConfig.URI_POCKET, keyword);
        if (pocketInfos.size() > 11) {
            pocketInfos.subList(11, pocketInfos.size()).clear();
        }
        for (int i = 0; i < pocketInfos.size(); i++) {
            PocketInfo pocketInfo = pocketInfos.get(i);
            NoteHiBoardInfo noteHiBoardInfo = getNoteHiBoardInfo(context, pocketInfo, keyword);
            noteHiBoardInfos.add(noteHiBoardInfo);
        }
        return noteHiBoardInfos;
    }


    public static void jumpToNoteDetailActivity(Context context, long id) {

        Intent intent = new Intent();
        intent.putExtra(NOTE_INFO_ID, id);
        intent.putExtra("from_launcher", true);
        intent.setComponent(new ComponentName(NOTE_PACKAGE_NAME, NOTE_DETAIL_CLASS));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static Intent getNoteDetailIntent(long id) {

        Intent intent = new Intent();
        intent.putExtra(NOTE_INFO_ID, id);
        intent.putExtra("from_launcher", true);
        intent.setComponent(new ComponentName(NOTE_PACKAGE_NAME, NOTE_DETAIL_CLASS));
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    public static Intent getNoteSearchIntent(String keyword) {

        Intent intent = new Intent();
        intent.putExtra(SEARCH_KEYWORD, keyword);
        intent.putExtra("from_launcher", true);
        intent.setComponent(new ComponentName(NOTE_PACKAGE_NAME, NOTE_SEARCH_CLASS));
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return intent;
    }

    private static List<PocketInfo> queryPocketsListNoStick(Context context, Uri uri, String keyword) {

        return queryPocketsListNoStick(context, uri, null, keyword);

    }

    public static List<PocketInfo> queryPocketsListNoStick(Context context, Uri uri, String[] projection, String keyword) {
        if (projection == null) {
            projection = PocketStore.Pocket.PROJECTION;
        }
        String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";

        String selection = PocketStore.Pocket.PocketColumns.SUMMARY + "  GLOB  " + " \'*" + keyword + "*\' ";
        String[] selectionArgs = new String[]{PocketStore.Pocket.PocketColumns.SUMMARY};

        //Cursor cursor = query(context, uri, projection, null, null, sortOrder);

        Cursor cursor = query(context, uri, projection, selection, null, sortOrder);
        try {
            return getPocketListByCursorNoStick(context, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return new ArrayList<>();
    }

    private static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentResolver cr = context.getContentResolver();

        return cr.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    private static List<PocketInfo> getPocketListByCursorNoStick(Context context, Cursor cursor) {
        List<PocketInfo> pocketInfos = new ArrayList<>();
        while (cursor != null && !cursor.isClosed() && cursor.moveToNext()) {
            PocketInfo pi = getPocketInfoByCursor(context, cursor);
            pocketInfos.add(pi);
        }
        return pocketInfos;
    }

    public static NoteHiBoardInfo getNoteHiBoardInfo(Context context, PocketInfo pocketInfo, String keyword) {

        NoteHiBoardInfo noteHiBoardInfo = new NoteHiBoardInfo();
        if (null != pocketInfo) {
            noteHiBoardInfo.setId(pocketInfo.getId());
            String summry = pocketInfo.getSummary();
            boolean isContains = summry.contains(keyword);
            if (null != summry) {
                if (isContains) {
                    //if (summry.length() > 30) {
                    //    String title = summry.substring(0, 29);
                    //    SpannableString spannableString = getHighLightSpannableString(title, keyword);
                    //    noteHiBoardInfo.setTitle(spannableString);
                    //} else {
                    SpannableString spannableString = getHighLightSpannableString(summry, keyword);
                    noteHiBoardInfo.setTitle(spannableString);
                    //}
                } else {
                    //if (summry.length() > 30) {
                    //    String title = summry.substring(0, 29);
                    //    noteHiBoardInfo.setTitle(new SpannableString(title));
                    //} else {
                    noteHiBoardInfo.setTitle(new SpannableString(summry));
                    //}
                }
            } else {
                noteHiBoardInfo.setTitle(new SpannableString(""));
            }


            noteHiBoardInfo.setTime(pocketInfo.getDateModified());

            String timeDate = getTimeDate(context, pocketInfo.getDateModified());
            noteHiBoardInfo.setTimeDate(timeDate);

            String mIconUrl = pocketInfo.getIcon();
            if (!TextUtils.isEmpty(mIconUrl)) {
                File file = new File(mIconUrl);
                if (null == file || file.length() == 0) {
                    mIconUrl = "";
                }
            } else {
                mIconUrl = "";
            }
            noteHiBoardInfo.setImgPath(mIconUrl);
        }

        return noteHiBoardInfo;
    }


    private static SpannableString getHighLightSpannableString(String title, String keyword) {
        int startIndex = title.indexOf(keyword);
        SpannableString spannableString = new SpannableString(title);
        spannableString.setSpan(new ForegroundColorSpan(0XFF2E76FC),
                startIndex, startIndex + keyword.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableString;
    }

    private static String getTimeDate(Context context, long dateModified) {

//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        Date curDate = new Date(dateModified);
//        String str = formatter.format(curDate);
//
//        ContentResolver cv = context.getContentResolver();
//        String strTimeFormat = android.provider.Settings.System.getString(cv,
//                android.provider.Settings.System.TIME_12_24);
//
//        String time = "";
//
//        try {
//
//            int year = DataUtils.getModifyYear(dateModified);
//            int mouth = DataUtils.getModifyMouth(dateModified);
//            int day = DataUtils.getModifyDay(dateModified);
//
//            if (DataUtils.isCurrentYear(dateModified)) {
//                if (DataUtils.IsToday(str)) {
//                    //00:00
//                    if (("24").equals(strTimeFormat)) {
//                        time = DataUtils.currentTime24(dateModified);
//                    } else {
//                        time = DataUtils.currentTime12(dateModified);
//                    }
//                } else {
//                    //1/1
//                    time = (mouth < 10 ? "0" + mouth : mouth) + "/" + (day < 10 ? "0" + day : day);
//                }
//
//            } else {
//                //2001/01/01
//                time = year + "/" + (mouth < 10 ? "0" + mouth : mouth) + "/" + (day < 10 ? "0" + day : day);
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        String date = getDateString(context, dateModified);
        String time = "";
        String noonStr = "";

        try {
            if (DataUtils.isCurrentYear(dateModified)) {

                ContentResolver cv = context.getContentResolver();
                String strTimeFormat = android.provider.Settings.System.getString(cv,
                        android.provider.Settings.System.TIME_12_24);
                int noon = DataUtils.currentNoon(dateModified);
                if (noon == 0) {
                    noonStr = "上午";
                } else {
                    noonStr = "下午";
                }

                if (("24").equals(strTimeFormat)) {
                    time = DataUtils.currentTime24(dateModified);
                    noonStr = "";
                } else {
                    time = DataUtils.currentTime12(dateModified);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return date + " " + noonStr + " " + time;
    }


    public static String getDateString(Context context, long time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(time);
        String str = formatter.format(curDate);
        String dateString = "";

        try {
            int year = DataUtils.getModifyYear(time);
            int mouth = DataUtils.getModifyMouth(time);
            int day = DataUtils.getModifyDay(time);
            if (DataUtils.IsToday(time)) {
                dateString = "今天";
            } else if (DataUtils.IsYesterday(time)) {
                dateString = "昨天";
            } else if (DataUtils.isCurrentYear(time)) {
                dateString = mouth + "月" + day + "日";
            } else {
                dateString = year + "年" +
                        mouth + "月" +
                        day + "日";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    private static PocketInfo getPocketInfoByCursor(Context context, Cursor cursor) {
        PocketInfo i = new PocketInfo();
        int idIndex = cursor.getColumnIndex(PocketStore.PBaseColums._ID);
        if (idIndex != -1) {
            i.setId(cursor.getLong(idIndex));
        }
        int titleIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.TITLE);
        if (titleIndex != -1) {
            i.setTitle(cursor.getString(titleIndex));
        }
        int summaryIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.SUMMARY);
        if (summaryIndex != -1) {
            i.setSummary(cursor.getString(summaryIndex));
        }


        int iconIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.THUMBNAIL);
        if (iconIndex != -1) {
            i.setIcon(cursor.getString(iconIndex));
        }

//        int lableIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.LABLE_IDS);
//        if (lableIndex != -1) {
//            i.setLabels(null);
//        }

//        int lableTitleIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.LABLE_TITLES);
//        if (lableTitleIndex != -1) {
//            i.setLabelTitles(lable2LableTitles(context, cursor.getString(lableTitleIndex)));
//        }

        int comeFromIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.COME_FROM);
        if (comeFromIndex != -1) {
            i.setComeFrom(cursor.getString(comeFromIndex));
        }

        int comeFromClassIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.COME_FROM_CLASS);
        if (comeFromClassIndex != -1) {
            i.setComeFromClass(cursor.getString(comeFromClassIndex));
        }

        int hasAudioIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.HAS_AUDIO);
        if (hasAudioIndex != -1) {
            i.setHasAudio(cursor.getInt(hasAudioIndex) == 1);
        }
        int hasVideoIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.HAS_VIDEO);
        if (hasVideoIndex != -1) {
            i.setHasVideo(cursor.getInt(hasVideoIndex) == 1);
        }

        int urlIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.URL);
        if (urlIndex != -1) {
            i.setUrl(cursor.getString(urlIndex));
        }

        int uriDataIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.URI_DATA);
        if (uriDataIndex != -1) {
            i.setUriData(cursor.getString(uriDataIndex));
        }

        int originUrlIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.ORIGIN_URL);
        if (originUrlIndex != -1) {
            i.setOriginUrl(cursor.getString(originUrlIndex));
        }

        int schemeIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.SCHEME);
        if (schemeIndex != -1) {
            i.setScheme(cursor.getString(schemeIndex));
        }

        int pathIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.PATH);
        if (pathIndex != -1) {
            i.setPath(cursor.getString(pathIndex));
        }

        int isGoodsIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.IS_GOODS);
        if (isGoodsIndex != -1) {
            i.setGoods(cursor.getInt(isGoodsIndex) == 1);
        }

        int priceIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.PRICE);
        if (priceIndex != -1) {
            i.setPrice(cursor.getString(priceIndex));
        }

        int audioUrlIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.AUDIO_URL);
        if (audioUrlIndex != -1) {
            i.setAudioUrl(cursor.getString(audioUrlIndex));
        }

        int videoUrlIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.VIDEO_URL);
        if (videoUrlIndex != -1) {
            i.setVideoUrl(cursor.getString(videoUrlIndex));
        }

        int mhtIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.MHT_PATH);
        if (mhtIndex != -1) {
            i.setMhtPath(cursor.getString(mhtIndex));
        }

        int typeIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.TYPE);
        if (typeIndex != -1) {
            i.setType(cursor.getString(typeIndex));
        }

        int cloudSyncStateIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.CLOUD_SYNC_STATE);
        if (cloudSyncStateIndex != -1) {
            i.setCloudSyncState(cursor.getString(cloudSyncStateIndex));
        }

        int dateAddIndex = cursor.getColumnIndex(PocketStore.PBaseColums.DATE_ADDED);
        if (dateAddIndex != -1) {
            i.setDateAdded(cursor.getLong(dateAddIndex));
        }
        int dateModifyIndex = cursor.getColumnIndex(PocketStore.PBaseColums.DATE_MODIFIED);
        if (dateModifyIndex != -1) {
            i.setDateModified(cursor.getLong(dateModifyIndex));
        }
        int hasStickIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.IS_STICK);
        if (hasStickIndex != -1) {
            i.setStick(cursor.getInt(hasStickIndex) == 1);
        }

        int contentIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.CONTENT);
        if (contentIndex != -1) {
            i.setContents(json2ContentsList(cursor.getString(contentIndex), true));
        }
        return i;
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

    private static boolean isUserBase64 = false;

    private static String base64Decode(@NonNull String orgin) {
        if (!isUserBase64) {
            return orgin;
        }
        return new String(Base64.decode(orgin.getBytes(), Base64.DEFAULT));
    }

    private static boolean checkExistKey(JSONObject object, String key) {
        if (object.has(key)) {
            return object.optBoolean(key);
        }
        return false;
    }
}
