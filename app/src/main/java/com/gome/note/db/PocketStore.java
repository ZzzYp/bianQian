package com.gome.note.db;

/**
 * Created by Administrator on 2017/6/19.
 */

public final class PocketStore {

    public static final String DATABASE_NAME = "note_iuv.db";

    public static final String TAG_SYMBOL_SEPARATOR = "#&#";

    public interface PBaseColums {

        public static final String _ID = "_id";
        /**
         * The time the file was added to the media provider
         * Units are seconds since 1970.
         * <P>Type: INTEGER (long)</P>
         */
        public static final String DATE_ADDED = "date_added";

        /**
         * The time the file was last modified
         * Units are seconds since 1970.
         * NOTE: This is for internal use by the media scanner.  Do not modify this field.
         * <P>Type: INTEGER (long)</P>
         */
        public static final String DATE_MODIFIED = "date_modified";

    }


    public static final class Lable {

        public static final String TABLE_NAME = "lables"; // table name

        public static final String[] PROJECTION = {
                LablesColumns._ID,
                LablesColumns.TITLE,
                LablesColumns.COUNT,
                LablesColumns.STICK,
                LablesColumns.DATE_ADDED,
                LablesColumns.DATE_MODIFIED
        };


        public interface LablesColumns extends PBaseColums {

            /**
             * The id of the label
             */
            public static final String ID = "id";

            /**
             * The name of the label
             */
            public static final String TITLE = "title";

            /**
             * The number of the labels
             */
            public static final String COUNT = "count";

            /**
             * To determine whether the top label
             */
            public static final String STICK = "stick";
        }

    }


    public static final class Pocket {

        public static final String TABLE_HISTORY_NAME = "history"; // history table name

        public static final String TABLE_POCKET_NAME = "note"; // pocket table name


        public static final String[] PROJECTION = {
                PocketColumns._ID,
                PocketColumns.TITLE,
                PocketColumns.SUMMARY,
                PocketColumns.CONTENT,
                PocketColumns.THUMBNAIL,
                PocketColumns.LABLE_IDS,
                PocketColumns.LABLE_TITLES,
                PocketColumns.COME_FROM,
                PocketColumns.COME_FROM_CLASS,
                PocketColumns.HAS_AUDIO,
                PocketColumns.HAS_VIDEO,
                PocketColumns.URL,
                PocketColumns.URI_DATA,
                PocketColumns.ORIGIN_URL,
                PocketColumns.SCHEME,
                PocketColumns.PATH,
                PocketColumns.IS_GOODS,
                PocketColumns.PRICE,
                PocketColumns.AUDIO_URL,
                PocketColumns.VIDEO_URL,
                PocketColumns.MHT_PATH,
                PocketColumns.TYPE,
                PocketColumns.CLOUD_SYNC_STATE,
                PocketColumns.DATE_ADDED,
                PocketColumns.DATE_MODIFIED,
                PocketColumns.IS_STICK,

        };

        public interface PocketColumns extends PBaseColums {
            /**
             * The title of the document
             */
            public static final String TITLE = "title";

            /**
             * The summary of the document
             */
            public static final String SUMMARY = "summary";

            /**
             * The content of the document
             */
            public static final String CONTENT = "content";

            /**
             * The thumbnail of a document
             */
            public static final String THUMBNAIL = "thumbnail";

            /**
             * The content of the label ids  ,A specific string of strings
             * <P>  eg:  value:[id1#&#id2#&#id3...] </P>
             * see  TAG_SYMBOL_SEPARATOR
             */
            public static final String LABLE_IDS = "lables";

            /**
             * The source of the collection of articles applied to the name of the package
             */
            public static final String COME_FROM = "come_from";

            /**
             * The source of the collection of articles applied to the name of the class
             */
            public static final String COME_FROM_CLASS = "come_from_class";

            /**
             * Determine if there is an audio resource
             */
            public static final String HAS_AUDIO = "has_audio";

            /**
             * Determine if there is an video resource
             */
            public static final String HAS_VIDEO = "has_video";

            /**
             * The URL to load
             */
            public static final String URL = "url";

            /**
             * Uri data carried by intent
             */
            public static final String URI_DATA = "uri_data";

            /**
             * Original web site
             */
            public static final String ORIGIN_URL = "origin_url";

            /**
             * The agreement between the Activity jump
             */
            public static final String SCHEME = "scheme";

            /**
             * The content of the label  ,A specific string of strings
             * <P>  eg:  value:[lable1#&#lable2#&#lable3...] </P>
             * see  TAG_SYMBOL_SEPARATOR
             */
            public static final String LABLE_TITLES = "lable_titles";

            /**
             * The absolute path to the collection of documents
             */
            public static final String PATH = "path";
            /**
             * Judge whether it is a commodity
             */
            public static final String IS_GOODS = "is_goods";

            /**
             * prices for goods
             */
            public static final String PRICE = "price";

            /**
             * Audio address contained in a web page
             */
            public static final String AUDIO_URL = "audio_url";

            /**
             * The video address contained in a web page
             */
            public static final String VIDEO_URL = "video_url";

            /**
             * File path for saving the offline Web cache
             */
            public static final String MHT_PATH = "mht_path";

            /**
             * four states
             * 1 - TYPE_ARTICLE , 2 - TYPE_IMAGE , 3 - TYPE_AUDIO , 4 - TYPE_VIDEO
             */
            public static final String TYPE = "type";

            /**
             * four states
             * 1 - Normal , 2 - Add , 3 - Modify , 4 - Delete
             */
            public static final String CLOUD_SYNC_STATE = "cloud_sync_state";
            /**
             * Determine if there is a stick note
             */
            public static final String IS_STICK = "is_stick";
        }
    }
}
