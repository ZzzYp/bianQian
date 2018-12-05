package com.gome.note.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PocketDbHelper extends SQLiteOpenHelper {
    private Context mContext;
    private long mMaxLableId = -1;
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = PocketStore.DATABASE_NAME;

    private static final String SQL_CREATE_CONTENT =
            PocketStore.Pocket.PocketColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PocketStore.Pocket.PocketColumns.TITLE + " TEXT," +
                    PocketStore.Pocket.PocketColumns.SUMMARY + " TEXT," +
                    PocketStore.Pocket.PocketColumns.CONTENT + " TEXT," +
                    PocketStore.Pocket.PocketColumns.THUMBNAIL + " TEXT," +
                    PocketStore.Pocket.PocketColumns.LABLE_IDS + " TEXT," +
                    PocketStore.Pocket.PocketColumns.COME_FROM + " TEXT," +
                    PocketStore.Pocket.PocketColumns.COME_FROM_CLASS + " TEXT," +
                    PocketStore.Pocket.PocketColumns.HAS_AUDIO + " INTEGER NOT NULL DEFAULT 0," +
                    PocketStore.Pocket.PocketColumns.HAS_VIDEO + " INTEGER NOT NULL DEFAULT 0," +
                    PocketStore.Pocket.PocketColumns.URL + " TEXT," +
                    PocketStore.Pocket.PocketColumns.URI_DATA + " TEXT," +
                    PocketStore.Pocket.PocketColumns.ORIGIN_URL + " TEXT," +
                    PocketStore.Pocket.PocketColumns.SCHEME + " TEXT," +
                    PocketStore.Pocket.PocketColumns.LABLE_TITLES + " TEXT," +
                    PocketStore.Pocket.PocketColumns.PATH + " TEXT," +
                    PocketStore.Pocket.PocketColumns.IS_GOODS + " INTEGER NOT NULL DEFAULT 0," +
                    PocketStore.Pocket.PocketColumns.PRICE + " TEXT," +
                    PocketStore.Pocket.PocketColumns.AUDIO_URL + " TEXT," +
                    PocketStore.Pocket.PocketColumns.VIDEO_URL + " TEXT," +
                    PocketStore.Pocket.PocketColumns.MHT_PATH + " TEXT," +
                    PocketStore.Pocket.PocketColumns.TYPE + " TEXT," +
                    PocketStore.Pocket.PocketColumns.CLOUD_SYNC_STATE + " TEXT," +
                    PocketStore.Pocket.PocketColumns.DATE_ADDED + " INTEGER NOT NULL," +
                    PocketStore.Pocket.PocketColumns.DATE_MODIFIED + " INTEGER NOT NULL," +
                    PocketStore.Pocket.PocketColumns.IS_STICK + " INTEGER NOT NULL DEFAULT 0";


    private static final String SQL_CREATE_POCKET_TABLE =
            "CREATE TABLE IF NOT EXISTS " + PocketStore.Pocket.TABLE_POCKET_NAME + " (" +
                    SQL_CREATE_CONTENT + " )";

    private static final String SQL_CREATE_HISTORY_TABLE =
            "CREATE TABLE IF NOT EXISTS " + PocketStore.Pocket.TABLE_HISTORY_NAME + " (" +
                    SQL_CREATE_CONTENT + " )";

    private static final String SQL_CREATE_LABLES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + PocketStore.Lable.TABLE_NAME + " (" +
                    PocketStore.Lable.LablesColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    PocketStore.Lable.LablesColumns._ID + " INTEGER UNIQUE," +
                    PocketStore.Lable.LablesColumns.TITLE + " TEXT NOT NULL UNIQUE," +
                    PocketStore.Lable.LablesColumns.COUNT + " INTEGER NOT NULL DEFAULT -1," +
                    PocketStore.Lable.LablesColumns.STICK + " INTEGER NOT NULL DEFAULT 0," +
                    PocketStore.Lable.LablesColumns.DATE_ADDED + " INTEGER NOT NULL," +
                    PocketStore.Lable.LablesColumns.DATE_MODIFIED + " INTEGER NOT NULL" +
                    ");";


    private static final String SQL_DELETE_POCKET_TABLE =
            "DROP TABLE IF EXISTS " + PocketStore.Pocket.TABLE_POCKET_NAME;

    private static final String SQL_DELETE_HISTORY_TABLE =
            "DROP TABLE IF EXISTS " + PocketStore.Pocket.TABLE_HISTORY_NAME;

    private static final String SQL_DELETE_LABLES_TABLE =
            "DROP TABLE IF EXISTS " + PocketStore.Lable.TABLE_NAME;


    public PocketDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

        if (mMaxLableId == -1) {
            mMaxLableId = initializeMaxItemId(getWritableDatabase());
        }
    }

    public void checkId(String table, ContentValues values) {
        if (values != null) {
            if (PocketStore.Lable.TABLE_NAME.equals(table)) {
                long id = -1;
                try {
                    id = values.getAsLong(PocketStore.Lable.LablesColumns._ID);
                } catch (Exception e) {
                }
                if (id > 0) {
                    mMaxLableId = Math.max(id, mMaxLableId);
                } else {
                    id = generateNewLableId();
                    values.put(PocketStore.Lable.LablesColumns._ID, id);
                    //throw new RuntimeException("when create a lable ,id are not -1");
                }
            }
        }
    }

    private long initializeMaxItemId(SQLiteDatabase db) {
        return PocketContentProvider.getMaxId(db, PocketStore.Lable.TABLE_NAME);
    }

    public long generateNewLableId() {
        if (mMaxLableId < 0) {
            //throw new RuntimeException("Error: max item id was not initialized");
            mMaxLableId = initializeMaxItemId(this.getWritableDatabase());
        }
        mMaxLableId += 1;
        return mMaxLableId;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //create notes table
        db.execSQL(SQL_CREATE_POCKET_TABLE);
        //create histry notes table
        db.execSQL(SQL_CREATE_HISTORY_TABLE);
        //create lables table
        db.execSQL(SQL_CREATE_LABLES_TABLE);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        LogUtils.d("PocketDbHelper--> onUpgrade oldVersion : " + oldVersion + ", newVersion : " + newVersion);
//        // This database is only a cache for online data, so its upgrade policy is
//        // to simply to discard the data and start over
//        String oridata = "/data/data/com.gome.mypocket";
//        String data = "/data/user/0/com.gome.mypocket";
//        FileUtils.deleteFolder(new File(oridata + "/app_webview"));
//        FileUtils.deleteFolder(new File(oridata + "/app_textures"));
//        FileUtils.deleteFolder(new File(data + "/app_webview"));
//        FileUtils.deleteFolder(new File(data + "/app_textures"));
//
//        db.execSQL(SQL_DELETE_POCKET_TABLE);
//        db.execSQL(SQL_DELETE_HISTORY_TABLE);
//        db.execSQL(SQL_DELETE_LABLES_TABLE);
//        onCreate(db);
//        SharedPreferencesUtil.clear(mContext);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}