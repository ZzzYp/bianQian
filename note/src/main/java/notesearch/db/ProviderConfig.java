package notesearch.db;

import android.net.Uri;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/26
 * DESCRIBE:
 */

public class ProviderConfig {
    public static final Uri URI_POCKET = Uri.parse("content://" + "com.gome.note.dbProvider" + "/" +
            "note");

}
