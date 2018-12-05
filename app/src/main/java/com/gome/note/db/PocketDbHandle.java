package com.gome.note.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.SparseArray;

import com.blankj.utilcode.util.CloseUtils;
import com.gome.note.db.cache.NoteCache;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.gome.note.db.ContentParser.contentsList2Json;
import static com.gome.note.db.ContentParser.json2ContentsList;


/**
 * Created by Administrator on 2017/6/19.
 */

public class PocketDbHandle {


    public static final Uri URI_POCKET = Uri.parse(PocketContentProvider.URI_POCKET);
    public static final Uri URI_HISTORY = Uri.parse(PocketContentProvider.URI_HISTORY);
    public static final Uri URI_LABLE = Uri.parse(PocketContentProvider.URI_LABLE);


    private static final Object mLock = new Object();

    private static long strid;

    private static Uri insert(Context context, Uri uri, ContentValues values) {
        ContentResolver cr = context.getContentResolver();

        Uri des = cr.insert(uri, values);

        return des;
    }

    public static Uri insert(Context context, Uri uri, PocketInfo pi) {
        ContentValues values = pocketInfo2ContentValues(pi);
        return insert(context, uri, values);
    }

    public static Uri insert(Context context, Uri uri, LabelInfo li) {
        ContentValues values = labelInfo2ContentValues(li);
        return insert(context, uri, values);
    }

    private static int update(Context context, Uri uri, ContentValues values, String where, String[] selectionArgs) {
        ContentResolver cr = context.getContentResolver();
        return cr.update(uri, values, where, selectionArgs);
    }

    public static int update(Context context, Uri uri, PocketInfo pi) {
        ContentValues values = pocketInfo2ContentValues(pi);
        // Which row to update, based on the title
        String where = PocketStore.Pocket.PocketColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(pi.getId())};
        return update(context, uri, values, where, selectionArgs);
    }

    public static int update(Context context, Uri uri, LabelInfo li) {
        ContentValues values = labelInfo2ContentValues(li);
        // Which row to update, based on the title
        String where = PocketStore.Lable.LablesColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(li.getId())};
        return update(context, uri, values, where, selectionArgs);
    }

    private static int delete(Context context, Uri url, String where, String[] selectionArgs) {
        ContentResolver cr = context.getContentResolver();
        return cr.delete(url, where, selectionArgs);
    }

    public static boolean delete(Context context, Uri url, long id) {
        // Define 'where' part of query.
        String where = PocketStore.PBaseColums._ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(id)};
        return 1 == delete(context, url, where, selectionArgs);
    }

    public static boolean delete(Context context, Uri url, long[] ids) {
        if (ids != null && ids.length > 0) {
            ContentResolver cr = context.getContentResolver();
            int N = ids.length;
            StringBuffer buffer = new StringBuffer();
            buffer.append(PocketStore.PBaseColums._ID + " IN ( ");
            for (int j = 0; j < N; j++) {
                buffer.append("'" + ids[j] + "'");
                if (j < N - 1) {
                    buffer.append(",");
                }
            }
            buffer.append(")");
            String where = buffer.toString();
            return cr.delete(url, where, null) > 0;
        }
        return false;
    }

    public static boolean delete(Context context, Uri url, List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            ContentResolver cr = context.getContentResolver();
            int N = ids.size();
            StringBuffer buffer = new StringBuffer();
            buffer.append(PocketStore.PBaseColums._ID + " IN ( ");
            for (int j = 0; j < N; j++) {
                buffer.append("'" + ids.get(j) + "'");
                if (j < N - 1) {
                    buffer.append(",");
                }
            }
            buffer.append(")");
            String where = buffer.toString();
            return cr.delete(url, where, null) > 0;
        }
        return false;
    }

    private static Cursor query(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        ContentResolver cr = context.getContentResolver();
        return cr.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    public static List<PocketInfo> queryPocketsList(Context context, Uri uri) {

        return queryPocketsList(context, uri, null);
    }

    public static List<PocketInfo> queryPocketsListNoStick(Context context, Uri uri) {
        return queryPocketsListNoStick(context, uri, null);
    }

    public static List<PocketInfo> queryPocketsList(Context context, Uri uri, String[] projection) {
        if (projection == null) {
            projection = PocketStore.Pocket.PROJECTION;
        }
        String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
        Cursor cursor = query(context, uri, projection, null, null, sortOrder);
        try {

            return getPocketListByCursor(context, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
        }

        return new ArrayList<>();
    }

    public static List<PocketInfo> queryPocketsListNoStick(Context context, Uri uri, String[] projection) {
        if (projection == null) {
            projection = PocketStore.Pocket.PROJECTION;
        }
        String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
        Cursor cursor = query(context, uri, projection, null, null, sortOrder);
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

    public static PocketInfo queryPocketInfoById(Context context, Uri uri, long id) {
        String selection = PocketStore.PBaseColums._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] projection = PocketStore.Pocket.PROJECTION;

        Cursor cursor = query(context, uri, projection, selection, selectionArgs, null);
        try {
            if (cursor != null && cursor.moveToNext()) {
                return getPocketInfoByCursor(context, cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
        }


        return new PocketInfo();
    }

    public static boolean checkPocketInfoExistByUrl(Context context, Uri uri, String url) {
        if (!TextUtils.isEmpty(url)) {
            String selection = PocketStore.Pocket.PocketColumns.URL + " like ?";
            String[] selectionArgs = {url};
            String[] projection = {PocketStore.Pocket.PocketColumns._ID, PocketStore.Pocket.PocketColumns.URL};

            Cursor cursor = query(context, uri, projection, selection, selectionArgs, null);
            try {
                if (cursor != null && cursor.getCount() > 0) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != cursor)
                    cursor.close();
            }

        }
        return false;
    }

    public static LabelInfo queryLabelInfoById(Context context, Uri uri, long id) {
        String selection = PocketStore.Lable.LablesColumns._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        String[] projection = PocketStore.Lable.PROJECTION;
        Cursor cursor = query(context, uri, projection, selection, selectionArgs, null);
        try {
            if (cursor != null && cursor.moveToNext()) {
                return getLabelInfoByCursor(cursor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
        }


        return new LabelInfo();
    }

    public static List<LabelInfo> queryLablesList(Context context, Uri uri) {
        return queryLablesList(context, uri, null);
    }

    public static List<LabelInfo> queryLablesList(Context context, Uri uri, String[] projection) {
        if (projection == null) {
            projection = PocketStore.Lable.PROJECTION;
        }

        String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
        Cursor cursor = query(context, uri, projection, null, null, sortOrder);
        try {
            List<LabelInfo> labelInfos = new ArrayList<>();
            while (cursor != null && cursor.moveToNext()) {
                LabelInfo info = getLabelInfoByCursor(cursor);
                labelInfos.add(info);
            }
            labelInfos = reorderLableList(labelInfos);
            return labelInfos;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != cursor)
                cursor.close();
        }
        return new ArrayList<>();
    }

    public static List<LabelInfo> reorderLableList(List<LabelInfo> labelInfos) {
        if (labelInfos != null && !labelInfos.isEmpty()) {
            int desIndex = -1;
            LabelInfo desLable = null;
            int N = labelInfos.size();
            for (int i = 0; i < N; i++) {
                LabelInfo li = labelInfos.get(i);
                if (li.isStick()) {
                    desIndex = i;
                    desLable = li;
                }
            }
            if (desIndex > 0) {
                labelInfos.remove(desIndex);
                labelInfos.add(0, desLable);
            }
        }
        return labelInfos;
    }


    /**
     * @param context
     * @param pi
     * @param table   PocketStore.Pocket.TABLE_POCKET_NAME orPocketStore.Pocket.TABLE_HISTORY_NAME;
     * @return
     */
    public static long insert(Context context, PocketInfo pi, String table) {
        synchronized (mLock) {
            // Gets the data repository in write mode
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                ContentValues values = pocketInfo2ContentValues(pi);
                // Insert the new row, returning the primary key value of the new row
                long num = db.insert(table, null, values);

                if (num > 0) {
                    if (PocketStore.Pocket.TABLE_POCKET_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_POCKET, null);
                    } else if (PocketStore.Pocket.TABLE_HISTORY_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_HISTORY, null);
                    }
                }

                Cursor cursor = db.rawQuery("select last_insert_rowid() from " + table, null);

                if (cursor.moveToFirst()) {
                    strid = cursor.getLong(0);
                }


                return strid;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return -1;
        }
    }

    public static long insert(Context context, LabelInfo li) {
        synchronized (mLock) {
            // Gets the data repository in write mode
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                ContentValues values = labelInfo2ContentValues(li);
                // Insert the new row, returning the primary key value of the new row
                long num = db.insert(PocketStore.Lable.TABLE_NAME, null, values);
                if (num > 0) {
                    context.getContentResolver().notifyChange(URI_LABLE, null);
                    return values.getAsLong(PocketStore.Lable.LablesColumns._ID);
                }
                return -1;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return -1;
        }
    }

    public static int update(Context context, PocketInfo pi) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                ContentValues values = pocketInfo2ContentValues(pi);

                // Which row to update, based on the title
                String selection = PocketStore.Pocket.PocketColumns._ID + " = ?";
                String[] selectionArgs = {String.valueOf(pi.getId())};
                int num = db.update(PocketStore.Pocket.TABLE_POCKET_NAME, values, selection,
                        selectionArgs);
                if (num > 0) {
                    context.getContentResolver().notifyChange(URI_POCKET, null);
                }
                return num;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return -1;
        }
    }

    public static int update(Context context, LabelInfo li) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                ContentValues values = labelInfo2ContentValues(li);

                // Which row to update, based on the title
                String selection = PocketStore.Lable.LablesColumns._ID + " = ?";
                String[] selectionArgs = {String.valueOf(li.getId())};
                int num = db.update(PocketStore.Lable.TABLE_NAME, values, selection, selectionArgs);
                if (num > 0) {
                    context.getContentResolver().notifyChange(URI_LABLE, null);
                }
                return num;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return -1;
        }
    }

    public static boolean delete(Context context, String table, long id) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                // Define 'where' part of query.
                String selection = PocketStore.PBaseColums._ID + " = ?";
                // Specify arguments in placeholder order.
                String[] selectionArgs = {String.valueOf(id)};
                // Issue SQL statement.
                int num = db.delete(table, selection, selectionArgs);
                if (num > 0) {
                    if (PocketStore.Pocket.TABLE_POCKET_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_POCKET, null);
                    } else if (PocketStore.Pocket.TABLE_HISTORY_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_HISTORY, null);
                    } else if (PocketStore.Lable.TABLE_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_LABLE, null);
                    }
                }
                return num > 0;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return false;
        }
    }

    public static boolean delete(Context context, String table, List<Long> idSet) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                int N = idSet.size();
                StringBuffer buffer = new StringBuffer();
                buffer.append(PocketStore.PBaseColums._ID + " IN ( ");
                for (int j = 0; j < N; j++) {
                    buffer.append("'" + idSet.get(j) + "'");
                    if (j < idSet.size() - 1) {
                        buffer.append(",");
                    }
                }
                buffer.append(")");
                int num = db.delete(table, buffer.toString(), null);
                if (num > 0) {
                    if (PocketStore.Pocket.TABLE_POCKET_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_POCKET, null);
                    } else if (PocketStore.Pocket.TABLE_HISTORY_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_HISTORY, null);
                    } else if (PocketStore.Lable.TABLE_NAME.equals(table)) {
                        context.getContentResolver().notifyChange(URI_LABLE, null);
                    }
                }
                return num == N;
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                CloseUtils.closeIO(db);
            }
            return false;
        }
    }

    public static List<PocketInfo> queryPocketsList(Context context, String table, String[] projection) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                if (projection == null) {
                    projection = PocketStore.Pocket.PROJECTION;
                }
                String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
                cursor = db.query(table, projection, null, null, null, null, sortOrder);
                return getPocketListByCursor(context, cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }
            return new ArrayList<PocketInfo>();
        }
    }


    public static List<PocketInfo> queryHistoryPocketsList(Context context, String table, String[] projection) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                if (projection == null) {
                    projection = PocketStore.Pocket.PROJECTION;
                }
                String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
                cursor = db.query(table, projection, null, null, null, null, sortOrder);
                return getHistoryPocketListByCursor(context, cursor);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }
            return new ArrayList<PocketInfo>();
        }
    }

    private static List<PocketInfo> getPocketListByCursor(Context context, Cursor cursor) {
        List<PocketInfo> pocketInfos = new ArrayList<>();
        List<PocketInfo> stickList = new ArrayList<>();
        List<PocketInfo> normalList = new ArrayList<>();
        while (cursor != null && !cursor.isClosed() && cursor.moveToNext()) {
            PocketInfo pi = getPocketInfoByCursor(context, cursor);
            if (pi.getLabels() != null
                    && !pi.getLabels().isEmpty()
                    && pi.getLabels().get(0) != null
                    && pi.getLabels().get(0).isStick()) {
                stickList.add(pi);
            } else {
                normalList.add(pi);
            }
        }
        pocketInfos.addAll(stickList);
        pocketInfos.addAll(normalList);

        return pocketInfos;
    }

    private static List<PocketInfo> getHistoryPocketListByCursor(Context context, Cursor cursor) {
        List<PocketInfo> pocketInfos = new ArrayList<>();
        List<PocketInfo> stickList = new ArrayList<>();
        List<PocketInfo> normalList = new ArrayList<>();
        while (cursor != null && !cursor.isClosed() && cursor.moveToNext()) {
            PocketInfo pi = getPocketInfoByCursor(context, cursor);
            pocketInfos.add(pi);
//            if (pi.getLabels() != null
//                    && !pi.getLabels().isEmpty()
//                    && pi.getLabels().get(0) != null
//                    && pi.getLabels().get(0).isStick()) {
//                stickList.add(pi);
//            } else {
//                normalList.add(pi);
//            }
        }
//        pocketInfos.addAll(stickList);
//        pocketInfos.addAll(normalList);

        return pocketInfos;
    }

    private static List<PocketInfo> getPocketListByCursorNoStick(Context context, Cursor cursor) {
        List<PocketInfo> pocketInfos = new ArrayList<>();
        while (cursor != null && !cursor.isClosed() && cursor.moveToNext()) {
            PocketInfo pi = getPocketInfoByCursor(context, cursor);
            pocketInfos.add(pi);
        }
        return pocketInfos;
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

        int lableIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.LABLE_IDS);
        if (lableIndex != -1) {
            i.setLabels(lable2LablesList(context, cursor.getString(lableIndex)));
        }

        int lableTitleIndex = cursor.getColumnIndex(PocketStore.Pocket.PocketColumns.LABLE_TITLES);
        if (lableTitleIndex != -1) {
            i.setLabelTitles(lable2LableTitles(context, cursor.getString(lableTitleIndex)));
        }

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

    public static PocketInfo queryPocketInfoById(Context context, String table, long id) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();

                String selection = PocketStore.PBaseColums._ID + " = ?";
                String[] selectionArgs = {String.valueOf(id)};
                String[] projection = PocketStore.Pocket.PROJECTION;

                cursor = db.query(table, projection, selection, selectionArgs, null, null, null);
                if (cursor != null && cursor.moveToNext()) {
                    return getPocketInfoByCursor(context, cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }
            return null;
        }
    }

    public static List<PocketInfo> queryPocketsList(Context context) {
        return queryPocketsList(context, PocketStore.Pocket.TABLE_POCKET_NAME, null);
    }

    public static List<PocketInfo> queryHistoryPocketsList(Context context) {
        return queryHistoryPocketsList(context, PocketStore.Pocket.TABLE_HISTORY_NAME, null);
    }

    public static List<LabelInfo> queryLablesList(Context context) {
        return queryLablesList(context, PocketStore.Lable.TABLE_NAME, null);
    }

    public static List<LabelInfo> queryLablesList(Context context, String table,
                                                  String[] projection) {
        synchronized (mLock) {
            List<LabelInfo> labelInfos = new ArrayList<>();
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                if (projection == null) {
                    projection = PocketStore.Lable.PROJECTION;
                }
                String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
                cursor = db.query(table, projection, null, null, null, null, sortOrder);

                while (cursor != null && cursor.moveToNext()) {
                    LabelInfo info = getLabelInfoByCursor(cursor);
                    labelInfos.add(info);
                }
                labelInfos = reorderLableList(labelInfos);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }

            return labelInfos;
        }
    }

    public static SparseArray<LabelInfo> queryLablesSparseArray(Context context) {
        return queryLablesSparseArray(context, PocketStore.Lable.TABLE_NAME, null);
    }

    public static SparseArray<LabelInfo> queryLablesSparseArray(Context context, String
            table, String[] projection) {
        synchronized (mLock) {
            SparseArray<LabelInfo> sparseArray = new SparseArray<>();
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();
                if (projection == null) {
                    projection = PocketStore.Lable.PROJECTION;
                }
                String sortOrder = PocketStore.PBaseColums.DATE_MODIFIED + " DESC";
                cursor = db.query(table, projection, null, null, null, null, sortOrder);
                while (cursor != null && cursor.moveToNext()) {
                    LabelInfo info = getLabelInfoByCursor(cursor);
                    sparseArray.put((int) info.getId(), info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);

            }
            return sparseArray;
        }
    }

    private static LabelInfo getLabelInfoByCursor(Cursor cursor) {
        LabelInfo i = new LabelInfo();
        int idIndex = cursor.getColumnIndex(PocketStore.Lable.LablesColumns._ID);
        if (idIndex != -1) {
            i.setId(cursor.getLong(idIndex));
        }
        int titleIndex = cursor.getColumnIndex(PocketStore.Lable.LablesColumns.TITLE);
        if (titleIndex != -1) {
            i.setTitle(cursor.getString(titleIndex));
        }
        int countIndex = cursor.getColumnIndex(PocketStore.Lable.LablesColumns.COUNT);
        if (countIndex != -1) {
            i.setCount(cursor.getInt(countIndex));
        }
        int stickIndex = cursor.getColumnIndex(PocketStore.Lable.LablesColumns.STICK);
        if (stickIndex != -1) {
            i.setStick(cursor.getInt(stickIndex) == 1);
        }
        int dateAddIndex = cursor.getColumnIndex(PocketStore.PBaseColums.DATE_ADDED);
        if (dateAddIndex != -1) {
            i.setDateAdded(cursor.getLong(dateAddIndex));
        }
        int dateModifyIndex = cursor.getColumnIndex(PocketStore.PBaseColums.DATE_MODIFIED);
        if (dateModifyIndex != -1) {
            i.setDateModified(cursor.getLong(dateModifyIndex));
        }
        return i;
    }

    public static LabelInfo queryLabelInfoById(Context context, long id) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();

                String selection = PocketStore.PBaseColums._ID + " = ?";
                String[] selectionArgs = {String.valueOf(id)};
                String[] projection = PocketStore.Lable.PROJECTION;

                cursor = db.query(PocketStore.Lable.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);

                if (cursor != null && cursor.moveToNext()) {
                    return getLabelInfoByCursor(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }
            return null;
        }
    }

    public static LabelInfo queryLabelInfoByTitle(Context context, String title) {
        synchronized (mLock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = PocketContentProvider.getPocketDbHelper().getWritableDatabase();

                String selection = PocketStore.Lable.LablesColumns.TITLE + " = ?";
                String[] selectionArgs = {title};
                String[] projection = PocketStore.Lable.PROJECTION;

                cursor = db.query(PocketStore.Lable.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);

                if (cursor != null && cursor.moveToNext()) {
                    return getLabelInfoByCursor(cursor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
                CloseUtils.closeIO(db);
            }
            return null;
        }
    }

    public static long updateContentLabels(Context context, long pocketId, List<LabelInfo> record) {
        try {
            // New value for one column
            ContentValues values = new ContentValues();
            values.put(PocketStore.Pocket.PocketColumns.LABLE_IDS, lablesList2lable(record));
            values.put(PocketStore.Pocket.PocketColumns.LABLE_TITLES, lablesList2lableTitle(record));
            values.put(PocketStore.Pocket.PocketColumns.DATE_MODIFIED, System.currentTimeMillis());
            // Which row to update, based on the title
            String selection = PocketStore.Pocket.PocketColumns._ID + " = ?";
            String[] selectionArgs = {String.valueOf(pocketId)};
            return update(context, URI_POCKET, values, selection, selectionArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static ContentValues pocketInfo2ContentValues(PocketInfo pi) {

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PocketStore.Pocket.PocketColumns.TITLE, pi.getTitle());
        values.put(PocketStore.Pocket.PocketColumns.SUMMARY, pi.getSummary());
        values.put(PocketStore.Pocket.PocketColumns.THUMBNAIL, pi.getIcon());
        values.put(PocketStore.Pocket.PocketColumns.LABLE_IDS, lablesList2lable(pi.getLabels()));
        values.put(PocketStore.Pocket.PocketColumns.LABLE_TITLES, lablesList2lableTitle(pi.getLabels()));
        values.put(PocketStore.Pocket.PocketColumns.COME_FROM, pi.getComeFrom());
        values.put(PocketStore.Pocket.PocketColumns.COME_FROM_CLASS, pi.getComeFromClass());
        values.put(PocketStore.Pocket.PocketColumns.HAS_AUDIO, pi.isHasAudio() ? 1 : 0);
        values.put(PocketStore.Pocket.PocketColumns.HAS_VIDEO, pi.isHasVideo() ? 1 : 0);

        values.put(PocketStore.Pocket.PocketColumns.URL, pi.getUrl());
        values.put(PocketStore.Pocket.PocketColumns.URI_DATA, pi.getUriData());
        values.put(PocketStore.Pocket.PocketColumns.ORIGIN_URL, pi.getOriginUrl());
        values.put(PocketStore.Pocket.PocketColumns.SCHEME, pi.getScheme());
        values.put(PocketStore.Pocket.PocketColumns.PATH, pi.getPath());
        values.put(PocketStore.Pocket.PocketColumns.IS_GOODS, pi.isGoods() ? 1 : 0);
        values.put(PocketStore.Pocket.PocketColumns.PRICE, pi.getPrice());
        values.put(PocketStore.Pocket.PocketColumns.AUDIO_URL, pi.getAudioUrl());
        values.put(PocketStore.Pocket.PocketColumns.VIDEO_URL, pi.getVideoUrl());
        values.put(PocketStore.Pocket.PocketColumns.MHT_PATH, pi.getMhtPath());
        values.put(PocketStore.Pocket.PocketColumns.TYPE, pi.getType());
        values.put(PocketStore.Pocket.PocketColumns.CLOUD_SYNC_STATE, pi.getCloudSyncState());
        values.put(PocketStore.Pocket.PocketColumns.IS_STICK, pi.isStick() ? 1 : 0);
        String content = contentsList2Json(pi.getContents(), true);
        values.put(PocketStore.Pocket.PocketColumns.CONTENT, content);
        if (pi.getDateModified() <= 0) {
            long modifyTime = System.currentTimeMillis();
            pi.setDateAdded(modifyTime);
            pi.setDateModified(modifyTime);
        }
        values.put(PocketStore.Pocket.PocketColumns.DATE_ADDED, pi.getDateAdded());
        values.put(PocketStore.Pocket.PocketColumns.DATE_MODIFIED, pi.getDateModified());
        return values;
    }

    private static String ensureEmpty(String s) {
        return TextUtils.isEmpty(s) ? "" : s;
    }


    public static ContentValues labelInfo2ContentValues(LabelInfo li) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PocketStore.Lable.LablesColumns.TITLE, li.getTitle());
        values.put(PocketStore.Lable.LablesColumns.COUNT, li.getCount());
        values.put(PocketStore.Lable.LablesColumns.STICK, li.isStick() ? 1 : 0);
        if (li.getId() <= 0) {
            li.setId(PocketContentProvider.getPocketDbHelper().generateNewLableId());
            values.put(PocketStore.Lable.LablesColumns._ID, li.getId());
            PocketContentProvider.getPocketDbHelper().checkId(PocketStore.Lable.TABLE_NAME, values);
        }
        values.put(PocketStore.Lable.LablesColumns._ID, li.getId());
        if (li.getDateModified() <= 0) {
            long addTime = System.currentTimeMillis();
            li.setDateAdded(addTime);
            li.setDateModified(addTime);
        }
        values.put(PocketStore.Lable.LablesColumns.DATE_ADDED, li.getDateAdded());
        values.put(PocketStore.Lable.LablesColumns.DATE_MODIFIED, li.getDateModified());
        return values;
    }


    public static ContentValues defaultLabelInfo2ContentValues(LabelInfo li) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(PocketStore.Lable.LablesColumns.TITLE, li.getTitle());
        values.put(PocketStore.Lable.LablesColumns.COUNT, li.getCount());
        values.put(PocketStore.Lable.LablesColumns.STICK, li.isStick() ? 1 : 0);
        if (li.getId() <= 0) {
            li.setId(PocketContentProvider.getPocketDbHelper().generateNewLableId());
            values.put(PocketStore.Lable.LablesColumns._ID, li.getId());
            PocketContentProvider.getPocketDbHelper().checkId(PocketStore.Lable.TABLE_NAME, values);
        }
        values.put(PocketStore.Lable.LablesColumns._ID, li.getId());
        if (li.getDateModified() <= 0) {
            long addTime = 0;
            li.setDateAdded(addTime);
            li.setDateModified(addTime);
        }
        values.put(PocketStore.Lable.LablesColumns.DATE_ADDED, li.getDateAdded());
        values.put(PocketStore.Lable.LablesColumns.DATE_MODIFIED, li.getDateModified());
        return values;
    }


    public static String[] getTagArray(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            if (tag.contains(PocketStore.TAG_SYMBOL_SEPARATOR)) {
                try {
                    return tag.split(PocketStore.TAG_SYMBOL_SEPARATOR);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                return new String[]{tag};
            }
        }
        return null;
    }

    public static String getTagFromArray(String[] tags) {
        if (tags != null && tags.length > 0) {
            try {
                StringBuilder builder = new StringBuilder();
                int N = tags.length;
                for (int i = 0; i < N; i++) {
                    builder.append(tags[i]);
                    if (i != N - 1) {
                        builder.append(PocketStore.TAG_SYMBOL_SEPARATOR);
                    }
                }
                return builder.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String lablesList2lable(List<LabelInfo> lableList) {
        if (lableList != null && !lableList.isEmpty()) {
            try {
                int N = lableList.size();
                String[] tagArr = new String[N];
                for (int i = 0; i < N; i++) {
                    LabelInfo li = lableList.get(i);
                    tagArr[i] = String.valueOf(li.getId());
                }
                String res = getTagFromArray(tagArr);
                return base64Encode(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String lablesList2lableTitle(List<LabelInfo> lableList) {
        if (lableList != null && !lableList.isEmpty()) {
            try {
                int N = lableList.size();
                String[] tagArr = new String[N];
                for (int i = 0; i < N; i++) {
                    LabelInfo li = lableList.get(i);
                    tagArr[i] = String.valueOf(li.getTitle());
                }
                String res = getTagFromArray(tagArr);
                return base64Encode(res);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static List<LabelInfo> lable2LablesList(Context context, String lables) {
        ArrayList<LabelInfo> contentInfos = new ArrayList<>();
        try {
            if (TextUtils.isEmpty(lables)) {
                return contentInfos;
            }
            lables = base64Decode(lables);
            String[] ids = getTagArray(lables);
            Arrays.sort(ids);
            if (ids != null && ids.length > 0) {
                int N = ids.length;
                for (int i = 0; i < N; i++) {
                    long id = getLong(ids[i]);
                    if (id != -1) {
                        LabelInfo li = NoteCache.getLableInfoById(id);
                        if (li == null) {
                            li = queryLabelInfoById(context, PocketDbHandle.URI_LABLE, id);
                        }

                        if (li != null && li.getId() != 0) {
                            contentInfos.add(li);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return contentInfos;
    }

    public static String[] lable2LableTitles(Context context, String lables) {
        try {
            if (TextUtils.isEmpty(lables)) {
                return null;
            }
            lables = base64Decode(lables);
            return getTagArray(lables);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static boolean isUserBase64 = false;

    private static long getLong(String id) {
        try {
            return Long.decode(id);
        } catch (Exception e) {
        }
        return -1;
    }

    private static String base64Encode(String orgin) {
        if (!TextUtils.isEmpty(orgin) && isUserBase64) {
            return new String(Base64.encode(orgin.getBytes(), Base64.DEFAULT));
        }
        return orgin;
    }

    private static String base64Decode(String orgin) {
        if (!TextUtils.isEmpty(orgin) && isUserBase64) {
            return new String(Base64.decode(orgin.getBytes(), Base64.DEFAULT));
        }
        return orgin;
    }

}
