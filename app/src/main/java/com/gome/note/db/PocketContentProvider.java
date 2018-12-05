package com.gome.note.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.StrictMode;
import android.support.annotation.NonNull;

import com.gome.note.base.Config;

public class PocketContentProvider extends ContentProvider {

    private static final boolean canWriteMyDB = true;

    public static final int POCKET = 31;
    public static final int HISTORY = 32;
    public static final int LABLE = 33;

    public static final String TABLE_POCKET_NAME = PocketStore.Pocket.TABLE_POCKET_NAME;
    public static final String TABLE_HISTORY_NAME = PocketStore.Pocket.TABLE_HISTORY_NAME;
    public static final String TABLE_LABLES_NAME = PocketStore.Lable.TABLE_NAME;


    public static final String DBPROVIDER_AUTHORITIES = Config.DBPROVIDER_AUTHORITIES;
    private static UriMatcher matcher;

    public static final String URI_POCKET = "content://" + DBPROVIDER_AUTHORITIES + "/" +
            TABLE_POCKET_NAME;
    public static final String URI_HISTORY = "content://" + DBPROVIDER_AUTHORITIES + "/" +
            TABLE_HISTORY_NAME;
    public static final String URI_LABLE = "content://" + DBPROVIDER_AUTHORITIES + "/" +
            TABLE_LABLES_NAME;


    private static PocketDbHelper mPocketDbHelper;

    public static PocketDbHelper getPocketDbHelper() {
        return mPocketDbHelper;
    }

    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(Config.DBPROVIDER_AUTHORITIES, TABLE_POCKET_NAME, POCKET);
        matcher.addURI(Config.DBPROVIDER_AUTHORITIES, TABLE_HISTORY_NAME, HISTORY);
        matcher.addURI(Config.DBPROVIDER_AUTHORITIES, TABLE_LABLES_NAME, LABLE);
    }

    public PocketContentProvider() {
    }

    @Override
    public boolean onCreate() {
        StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskWrites();
        mPocketDbHelper = new PocketDbHelper(getContext());
        StrictMode.setThreadPolicy(oldPolicy);


        return true;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        if (canWriteMyDB) {
            SQLiteDatabase db = null;
            int deleteRows = 0;
            switch (matcher.match(uri)) {
                case POCKET:
                    db = mPocketDbHelper.getWritableDatabase();
                    deleteRows = db.delete(TABLE_POCKET_NAME, selection, selectionArgs);
                    break;
                case HISTORY:
                    db = mPocketDbHelper.getWritableDatabase();
                    deleteRows = db.delete(TABLE_HISTORY_NAME, selection, selectionArgs);
                    break;
                case LABLE:
                    db = mPocketDbHelper.getWritableDatabase();
                    deleteRows = db.delete(TABLE_LABLES_NAME, selection, selectionArgs);
                    break;
            }
            if (deleteRows > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return deleteRows;
        }
        return 0;
    }

    public static long generateNewLableId() {
        if (mPocketDbHelper != null) {
            return mPocketDbHelper.generateNewLableId();
        }
        return -1;
    }

    /**
     * @return the max id in the provided table.
     */
    public static long getMaxId(SQLiteDatabase db, String table) {
        long id = -1;
        try {
            Cursor c = db.rawQuery("SELECT MAX(" + PocketStore.Lable.LablesColumns._ID + ") FROM " + table, null);
            // get the result
            if (c != null && c.moveToNext()) {
                id = c.getLong(0);
            }
            if (c != null) {
                c.close();
            }

            if (id == -1) {
                throw new RuntimeException("Error: could not query max id in " + table);
            }
        } catch (Exception e) {
            id = 10000;
        }
        return id;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        switch (matcher.match(uri)) {
            case POCKET:
                return "vnd.android.cursor.dir/vnd." + DBPROVIDER_AUTHORITIES + "." +
                        TABLE_POCKET_NAME;
            case HISTORY:
                return "vnd.android.cursor.dir/vnd." + DBPROVIDER_AUTHORITIES + "." +
                        TABLE_HISTORY_NAME;
            case LABLE:
                return "vnd.android.cursor.dir/vnd." + DBPROVIDER_AUTHORITIES + "." +
                        TABLE_LABLES_NAME;
        }
        return "";
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        if (canWriteMyDB) {
            Uri u = null;
            switch (matcher.match(uri)) {
                case POCKET:
                    u = insert(URI_POCKET, TABLE_POCKET_NAME, values);
                    break;
                case HISTORY:
                    u = insert(URI_HISTORY, TABLE_HISTORY_NAME, values);
                    break;
                case LABLE:
                    u = insert(URI_LABLE, TABLE_LABLES_NAME, values);
                    break;
            }
            if (u != null) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return u;
        }
        return null;
    }

    public Uri insert(String uriString, String tableName, ContentValues values) {
        SQLiteDatabase db = mPocketDbHelper.getWritableDatabase();
        mPocketDbHelper.checkId(tableName, values);
        long insertRows = db.insert(tableName, null, values);
        return Uri.parse(uriString + "/" + insertRows);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        switch (matcher.match(uri)) {
            case POCKET:
                return query(TABLE_POCKET_NAME, projection, selection, selectionArgs, sortOrder);
            case HISTORY:
                return query(TABLE_HISTORY_NAME, projection, selection, selectionArgs, sortOrder);
            case LABLE:
                return query(TABLE_LABLES_NAME, projection, selection, selectionArgs, sortOrder);
        }
        return null;
    }

    private Cursor query(String tableName, String[] projection, String selection,
                         String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mPocketDbHelper.getWritableDatabase();
        return db.query(tableName, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (canWriteMyDB) {
            int updateRows = 0;
            switch (matcher.match(uri)) {
                case POCKET:
                    updateRows = update(TABLE_POCKET_NAME, values, selection, selectionArgs);
                    break;
                case HISTORY:
                    updateRows = update(TABLE_HISTORY_NAME, values, selection, selectionArgs);
                    break;
                case LABLE:
                    updateRows = update(TABLE_LABLES_NAME, values, selection, selectionArgs);
                    break;
            }
            if (updateRows > 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            return updateRows;
        }
        return -1;
    }

    private int update(String tableName, ContentValues values, String selection,
                       String[] selectionArgs) {
        SQLiteDatabase db = mPocketDbHelper.getWritableDatabase();
        return db.update(tableName, values, selection, selectionArgs);
    }
}
