package com.gome.note.db.config;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.util.SparseLongArray;
import android.webkit.URLUtil;

import com.gome.note.R;
import com.gome.note.base.Config;
import com.gome.note.base.NoteApplication;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.PocketStore;
import com.gome.note.entity.LabelInfo;
import com.gome.note.utils.PrivateUtil;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * Created by Administrator on 2017/6/15.
 */

public class NoteConfig {

    public static final boolean LOG_ENABLE_ALL = false;
    public static final String APPLICATION_ID = "com.gome.note";
    public static boolean ISPREVIEWMODE = false;
    public static boolean ISAUDIOPLAY = false;
    public static int AUDIOPLAYID = 0;
    public static int onCreateCount = 0;
    public static long inMultiWindowNoteId = -1;
    public static boolean inMultiWindowDoEditStatus = false;

    public static final String DATABASE_NAME = "my_pocket.db";
    //as cache
    public static final String ROOT_PATH_CACHE = "/storage/emulated/0/.note_iuv/";
    public static final String TEST_PATH_CACHE = "/storage/emulated/0/note_iuv/";

    public static final long minAvilableMemoSize = 25 * 1024 * 1024; // 25M

    public static final String DB_AUTHORITIES = Config.DBPROVIDER_AUTHORITIES;

    public static final String FROM_TYPE = "type";
    public static final String FROM_TYPE_SHARE = "share";
    public static final String FROM_TYPE_COPY = "copy";
    public static final String FROM_TYPE_COLLECT = "collect";

    public static final String INTENT_KEY_URL = "url";
    public static final String INTENT_KEY_TITLE = "title";
    public static final String INTENT_KEY_SUMMARY = "summary";
    public static final String INTENT_KEY_THUMB = "thumb";
    public static final String AUDIO_STORE_PATH = "audio_store_path";

    public static final int KEY_PACKAGE = 0;
    public static final int KEY_CLASS = 1;
    public static final int KEY_URL = 2;
    public static final int KEY_TITLE = 3;
    public static final int KEY_PATH = 4;
    public static final int KEY_THUMBNAIL = 5;
    public static final int KEY_SUMMARY = 6;
    public static final int KEY_TAG = 7;
    public static final int KEY_PRICE = 8;
    public static final int KEY_ORIGION_URL = 9;
    public static final int KEY_AUDIO_URL = 10;
    public static final int KEY_VIDEO_URL = 11;
    public static final int KEY_URI_DATA = 12;
    public static final int KEY_MHT_PATH = 13;
    public static final int KEY_SCHEME = 14;

    public static final int KEY_TYPE = 20;
    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_AUDIO = "audio";
    public static final String TYPE_VIDEO = "video";

    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_GALLERY = 101;
    public static final int REQUEST_CAMERA = 102;
    public static final int REQUEST_AUDIO = 103;
    public static final int REQUEST_VIDEO = 104;
    public static final int REQUEST_FILE = 105;
    public static final int REQUEST_WEB = 106;
    public static final int REQUEST_LABELS = 107;
    public static final int REQUEST_POPUWINDOW = 108;
    public static final int REQUEST_PHONEPOPWINDOW = 109;
    public static final int REQUEST_EDIT_PHOTO = 110;

    public static final int NEED_CAMERA = 201;
    public static final int NEED_GALLERY = 202;
    public static final int NEED_CALLPHONE = 203;
    public static final int NEED_AUDIO = 205;
    public static final int NEED_PAUSESAVE = 204;
    public static final int NEED_EXTERNAL_STORAGE = 205;

    public static final int RECORD_COMPLETE = 701;
    public static final int RECORD_CANCEL = 702;


    public static final String RES_TYPE_COLOR = "color";
    public static final String RES_TYPE_DRAWABLE = "drawable";

    public static String SKIN_BG_COLORID_KEY = "skinBgColorId";
    public static String SKIN_BG_ID_KEY = "skinBgId";

    public static String SEARCH_KEYWORD = "searchKeyword";

    public static String FROM_LAUNCHER = "from_launcher";
    public static String SCREENSHOT_IMAGE_PATH = "path";

    public static String sPicPath = "";

    private static final int[] KEY_DATA = {
            KEY_PACKAGE, KEY_CLASS, KEY_URL,
            KEY_ORIGION_URL, KEY_TITLE, KEY_SUMMARY,
            KEY_THUMBNAIL, KEY_PATH, KEY_VIDEO_URL,
            KEY_AUDIO_URL, KEY_TAG, KEY_URI_DATA,
            KEY_MHT_PATH, KEY_SCHEME, KEY_TYPE
    };

    private static final String[] TAG_DATA = {
            "pkg", "clazz", "url",
            "ori_url", "title", "summary",
            "thumb", "path", "video_url",
            "audio_url", "tag", "uri_data",
            "mht_path", "scheme", "type"
    };

    public static void printData(Object object, SparseArray data, String method) {
        if (!isEmpty(method) && KEY_DATA != null && TAG_DATA != null && TAG_DATA.length == KEY_DATA.length) {
            int N = KEY_DATA.length;
            for (int i = 0; i < N; i++) {
                print(object, method, LOG_ENABLE_ALL, toArray(TAG_DATA[i]), getString(data, KEY_DATA[i]));
            }
        }
    }


    public static final String _ROOT_PATH_CACHE = "/storage/emulated/0/.my_pocket/cache/";
    public static final String DIR_PATH_CACHE_IMAGES = "images";

    public static String getCachePathEnsureExist() {
        return ensureCachePathExist(_ROOT_PATH_CACHE.concat(DIR_PATH_CACHE_IMAGES));
    }

    public static String ensureCachePathExist(String path) {
        File file = new File(path);
        if (!file.exists()) {
            boolean success = file.mkdirs();
            if (success) {
                return file.getAbsolutePath();
            } else {
                return null;
            }
        } else {
            return file.getAbsolutePath();
        }
    }


    public static ActivityInfo getTopActivityInfoForMyPocket(boolean getTop) {
        ActivityInfo activityInfo = null;
        try {
            activityInfo = (ActivityInfo) PrivateUtil.invoke2(ActivityManager.class,
                    "getTopActivityInfoForMyPocket", new Class[]{boolean.class}, new Object[]{getTop});
        } catch (Exception e) {

        }
        return activityInfo;
    }


    private static ComponentName createComponentName(String pkg, String clazz) {
        return new ComponentName(pkg, clazz);
    }

    private static final int[] ID_DEFAULT_LABLE_TITLES = {
            R.string.put_top,
            R.string.lable_type_record
    };


    private static final int[] IMAGE_OR_CAMERA = {
            R.string.image,
            R.string.camera
    };

    public static final int[] SKIN_CHECKLIST_BG_IMAGE = {
            R.drawable.gome_picture_memo_normal,
            R.drawable.gome_picture_memo_yellow,
            R.drawable.gome_picture_memo_green,
            R.drawable.gome_picture_memo_autumn,
            R.drawable.gome_picture_memo_romance
    };

    public static final int[] SKIN_BG_IMAGE = {
            R.drawable.skin_background_image_normal,
            R.drawable.skin_background_image_yellow,
            R.drawable.skin_background_image_green,
            R.drawable.skin_background_image_golden_autumn,
            R.drawable.skin_background_image_romance
    };

    public static final int[] SKIN_BG_HEAD_IMAGE = {
            R.color.colorPrimary,
            R.color.skin_background_yellow,
            R.color.skin_background_green,
            R.drawable.gome_picture_memo_autumn_tittle_bg,
            R.drawable.gome_picture_memo_romance_tittle_bg
    };

    public static final int[] SKIN_BG_FOOT_IMAGE = {
            R.color.white,
            R.color.skin_background_yellow,
            R.color.skin_background_green,
            R.drawable.gome_picture_memo_autumn_edit_bg,
            R.drawable.gome_picture_memo_romance_edit_bg
    };

    public static final int[] SKIN_BG_FOOT_IMAGE_WITHOUT9 = {
            R.color.white,
            R.color.skin_background_yellow,
            R.color.skin_background_green,
            R.drawable.gome_picture_memo_autumn_edit_bg_without9,
            R.drawable.gome_picture_memo_romance_edit_bg_without9
    };


    public static final int[] SKIN_BG_COLOR = {
            R.color.skin_background_normal,
            R.color.skin_background_yellow,
            R.color.skin_background_green,
            R.color.skin_background_golden_autumn,
            R.color.skin_background_romance
    };


    public static final int[] SKIN_BG_ICON_COLOR = {
            R.color.icon_background_normal,
            R.color.icon_background_yellow,
            R.color.icon_background_green,
            R.color.icon_background_golden_autumn,
            R.color.icon_background_romance
    };

    public static final int[] SKIN_BG_STYLE_GROUP_ICON_COLOR = {
            R.color.style_group_icon_background_normal,
            R.color.style_group_icon_background_yellow,
            R.color.style_group_icon_background_green,
            R.color.style_group_icon_background_golden_autumn,
            R.color.style_group_icon_background_romance
    };


    public static final int[] SKIN_BG_STYLE_GROUP_TEXT_COLOR = {
            R.color.font_black_4,
            R.color.style_group_icon_background_yellow,
            R.color.style_group_icon_background_green,
            R.color.style_group_icon_background_golden_autumn,
            R.color.style_group_icon_background_romance
    };

    public static final int[] SKIN_BG_TEXT_UNCHECK_COLOR = {
            R.color.text_uncheck_background_normal,
            R.color.text_uncheck_background_yellow,
            R.color.text_uncheck_background_green,
            R.color.text_uncheck_background_golden_autumn,
            R.color.text_uncheck_background_romance
    };

    public static final int[] SKIN_BG_TEXT_CHECKED_COLOR = {
            R.color.text_checked_normal,
            R.color.text_checked_yellow,
            R.color.text_checked_green,
            R.color.text_checked_golden_autumn,
            R.color.text_checked_romance
    };


    public static final int[] SKIN_BG_TEXT = {
            R.string.standard_bg,
            R.string.yellow_bg,
            R.string.green_bg,
            R.string.golden_autumn_bg,
            R.string.romance_bg
    };

    public static final int[] SKIN_BG_ID = {
            R.integer.skin_bg_id_standard,
            R.integer.skin_bg_id_yellow,
            R.integer.skin_bg_id_green,
            R.integer.skin_bg_id_golden_autumn,
            R.integer.skin_bg_id_romance
    };

    public static final Boolean[] SKIN_BG_IS_INBETWEENING = {
            false,
            false,
            false,
            true,
            true
    };


    public static final int[] SKIN_BG_STYLE_GROUP_LINE_COLOR = {
            R.color.line_color_1,
            R.color.line_color_1,
            R.color.line_color_2,
            R.color.transparent,
            R.color.transparent
    };

    public static final int[] SKIN_BG_CHECKLIST_CHECKED_COLOR = {
            R.color.checklist_checked_color,
            R.color.text_checked_yellow,
            R.color.text_checked_green,
            R.color.text_checked_golden_autumn,
            R.color.text_checked_romance
    };


    public static final int[] SKIN_BG_CHECKLIST_UNCHECKED_COLOR = {
            R.color.checklist_unchecked_color,
            R.color.text_uncheck_background_yellow,
            R.color.text_uncheck_background_green,
            R.color.text_uncheck_background_golden_autumn,
            R.color.text_uncheck_background_romance
    };

    public static void firstStartAppInsertStickLable() {
        int[] ids = ID_DEFAULT_LABLE_TITLES;
        if (ids != null && ids.length > 0) {
            Context context = NoteApplication.getAppContext();
            for (int i = 0; i < ids.length; i++) {
                LabelInfo li = new LabelInfo();
                if (R.string.put_top == ids[i]) {
                    li.setStick(true);
                }
                li.setTitle(context.getString(ids[i]));
                context.getContentResolver().insert(PocketDbHandle.URI_LABLE, PocketDbHandle.defaultLabelInfo2ContentValues(li));
            }
        }
    }

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.getDefault());

    public static String formatDate(long time) {
        return DATE_FORMAT.format(new Date(time));
    }

    private static String sPreDataString = "";
    private static int sPreDataInt = 0;

    public static String getRandomlyGeneratedFilePath(String suffix) {
        String preDataString = formatDate(System.currentTimeMillis());
        if (!isEmpty(sPreDataString) && preDataString.compareTo(sPreDataString) <= 0) {
            preDataString = appendString(preDataString, String.valueOf(sPreDataInt++));
        }
        if (!isEmpty(sPreDataString)
                && !isEmpty(preDataString)
                && sPreDataString.length() > preDataString.length()) {
            sPreDataInt = 0;
        }
        sPreDataString = preDataString;
        return appendString(Config.getCachePathEnsureExist(), File.separator, sPreDataString, ".", suffix);
    }

    public static String appendString(String... url) {
        if (url != null && url.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : url) {
                if (!TextUtils.isEmpty(s)) {
                    builder.append(s);
                }
            }
            return builder.toString();
        }
        return "";
    }

    public static String getString(SparseArray<Object> content, int key) {
        Object object = getObject(content, key);
        if (object != null) {
            return getNonNullStr(object.toString());
        }
        return "";
    }

    public static Object getObject(SparseArray content, int key) {
        try {
            return content.get(key);
        } catch (Exception ex) {
        }
        return null;
    }


    public static void startService(Context c, String className) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(c.getPackageName(), className));
            c.startService(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static String REGEX_URL = "(http[s]{0,1}://[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)|(wap.[a-zA-Z0-9\\.\\-]+\\.([a-zA-Z]{2,4})(:\\d+)?(/[a-zA-Z0-9\\.\\-~!@#$%^&*+?:_/=<>]*)?)";

    public static final String REGULAR_LETTERS_OR_NUMBER = "[A-Za-z0-9]+";
    public static final String REGULAR_CHINESES = "[\\u4e00-\\u9fa5]*";
    public static final String REGULAR_LETTERS_AND_NUMBER = "[a-zA-Z]*[_]*[0-9]*";
    public static final String REGULAR_NON_BLANK = "[\\S]+";
    public static final String REGULAR_NUMBERS = "[0-9]+";
    public static final String REGULAR_LOWER_LETTERS = "[a-z]+";
    public static final String REGULAR_UPPER_LETTERS = "[A-Z]+";
    public static final String REGULAR_LOWER_LETTERS_OR_NUMBER = "[a-z0-9]+";
    public static final String REGULAR_UPPER_LETTERS_OR_NUMBER = "[A-Z0-9]+";

    public static boolean isContain(String str, String regex) {
        if (!isEmpty(str)) {
            try {
                str = str.replaceAll(" ", "");
                Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(str);
                if (m.find()) {
                    return true;
                }
            } catch (PatternSyntaxException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static List<String> getUrls(CharSequence content) {
        List<String> urls = new ArrayList<String>();
        if (TextUtils.isEmpty(content)) {
            return urls;
        }
        try {
            Pattern p = Pattern.compile(REGEX_URL);
            Matcher m = p.matcher(content);
            while (m.find()) {
                String find = m.group();
                urls.add(find);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urls;
    }

    public static String getStringByParseString(String content, String coreRegular, String prefixKey, String sufixKey) {
        if (!TextUtils.isEmpty(content)) {
            try {
                content = content.replaceAll(" ", "");
                String regular = appendString(prefixKey, coreRegular, sufixKey);
                Matcher matcher = Pattern.compile(regular).matcher(content);
                StringBuffer sb = new StringBuffer();
                while (matcher.find()) {
                    sb.append(matcher.group());
                }
                String s = sb.toString();
                if (!TextUtils.isEmpty(s)) {
                    s = s.trim();
                    if (!TextUtils.isEmpty(prefixKey)) {
                        s = s.replace(prefixKey, "");
                    }
                    if (!TextUtils.isEmpty(sufixKey)) {
                        s = s.replace(sufixKey, "");
                    }
                }
                return s;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String getUrl(CharSequence content) {
        return getUrl(content, 0);
    }

    public static String getUrl(CharSequence content, int pos) {
        List<String> urls = getUrls(content);
        if (urls != null && !urls.isEmpty()) {
            if (pos >= 0 && pos < urls.size()) {
                return urls.get(pos);
            }
        }
        return "";
    }

    private static final Pattern REG_UNICODE = Pattern.compile("[0-9A-Fa-f]{4}");

    public static String unicode2String(String str) {
        StringBuilder sb = new StringBuilder();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c1 = str.charAt(i);
            if (c1 == '\\' && i < len - 1) {
                char c2 = str.charAt(++i);
                if (c2 == 'u' && i <= len - 5) {
                    String tmp = str.substring(i + 1, i + 5);
                    Matcher matcher = REG_UNICODE.matcher(tmp);
                    if (matcher.find()) {
                        sb.append((char) Integer.parseInt(tmp, 16));
                        i = i + 4;
                    } else {
                        sb.append(c1).append(c2);
                    }
                } else {
                    sb.append(c1).append(c2);
                }
            } else {
                sb.append(c1);
            }
        }
        return sb.toString();
    }

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            String str = Integer.toHexString(c);
            switch (4 - str.length()) {
                case 0:
                    unicode.append("\\u" + str);
                    break;
                case 1:
                    str = "0" + str;
                    unicode.append("\\u" + str);
                    break;
                case 2:
                case 3:
                default:
                    str = String.valueOf(c);
                    unicode.append(str);
                    break;
            }
        }
        return unicode.toString();
    }

    public static String[] toArray(String... tags) {
        return tags;
    }

    public static void print(Object object, String method, boolean enable, String[] tags, Object... contents) {
        if (LOG_ENABLE_ALL && enable
                && !isEmpty(method)
                && object != null) {
            StringBuilder builder = new StringBuilder();
            builder.append(object.getClass().getSimpleName());
            builder.append("--> ");
            builder.append(method);
            builder.append("  ");
            if (tags != null && tags.length > 0
                    && contents != null && contents.length > 0
                    && tags.length == contents.length) {
                int N = tags.length;
                for (int i = 0; i < N; i++) {
                    builder.append("[ ");
                    builder.append(tags[i]);
                    builder.append(" : ");
                    builder.append(contents[i]);
                    if (N - 1 != i) {
                        builder.append(" ] , ");
                    } else {
                        builder.append(" ]");
                    }
                }
            }
        }
    }

    public static String unescape(String url) {
        if (!isEmpty(url)) {
            try {
                return URLDecoder.decode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static String escape(String url) {
        if (!isEmpty(url)) {
            try {
                return URLEncoder.encode(url, "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str)
                || str.length() <= 1
                || "0".equals(str)
                || ' ' == str.charAt(0)
                || " ".equals(str)
                || ".".equals(str)
                || "..".equals(str)
                || "...".equals(str)
                || "......".equals(str);
    }

    private static boolean isEmpty(String... str) {
        if (str == null || str.length == 0) {
            return true;
        }
        return false;
    }

    public static boolean isEmpty(Object object) {
        if (object == null
                || (object instanceof String && ((String) object).length() <= 0)
                || (object instanceof String[] && ((String[]) object).length <= 0)
                || (object instanceof ContentValues && ((ContentValues) object).size() <= 0)
                || (object instanceof Object[] && ((Object[]) object).length <= 0)
                || (object instanceof List && ((List) object).isEmpty())
                || (object instanceof SparseArray && ((SparseArray) object).size() <= 0)
                || (object instanceof SparseIntArray && ((SparseIntArray) object).size() <= 0)
                || (object instanceof SparseLongArray && ((SparseArray) object).size() <= 0)
                || (object instanceof SparseBooleanArray && ((SparseBooleanArray) object).size() <= 0)
                || (object instanceof Map && ((Map) object).size() <= 0)
                || (object instanceof Collection && ((Collection) object).size() <= 0)) {
            return true;
        }
        return false;
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
        try {
            if (tags != null && tags.length > 0) {
                StringBuilder builder = new StringBuilder();
                int N = tags.length;
                for (int i = 0; i < N; i++) {
                    builder.append(tags[i]);
                    if (i != N - 1) {
                        builder.append(PocketStore.TAG_SYMBOL_SEPARATOR);
                    }
                }
                return builder.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static final int MAX_TITLE_LENGTH = 35;
    public static final int MAX_SUMMARY_LENGTH = 175;

    public static String getMaxLengthString(String content, int maxLength) {
        if (!isEmpty(content) && content.length() > maxLength) {
            return content.substring(0, maxLength);
        }
        return content;
    }


    public static boolean isEmpty(SparseArray data, int key) {
        return isEmpty(getString(data, key));
    }

    private static String toString(Object object) {
        return object != null ? getNonNullStr(object.toString()) : "";
    }

    private static String getNonNullStr(String s) {
        return isEmpty(s) ? "" : s;
    }


    public static boolean isURL(String input) {
        return URLUtil.isNetworkUrl(input);
    }

    public static String getApplicationName(String pkg, String defaultName) {
//        try {
//            PackageManager pm = PocketApplication.getAppContext().getPackageManager();
//            ApplicationInfo info = pm.getApplicationInfo(pkg, 0);
//            CharSequence name = pm.getApplicationLabel(info);
//            if (!isEmpty(name)) {
//                return name.toString();
//            }
//        } catch (Exception e) {
//        }
        return defaultName;
    }

    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]+");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static String https2Http(String url) {
        if (!isEmpty(url)) {
            if (url.startsWith("//")) {
                return url.replace("//", "http://");
            }
            if (url.startsWith("https")) {
                return url.replace("https", "http");
            }
        }
        return url;
    }

    public static String[] list2StringArray(Object object) {
        if (object != null) {
            if (object instanceof String[]) {
                return (String[]) object;
            } else if (object instanceof List) {
                List list = (List) object;
                if (list != null && !list.isEmpty()) {
                    int size = list.size();
                    String[] arr = new String[size];
                    for (int i = 0; i < size; i++) {
                        arr[i] = toString(list.get(i));
                    }
                    return arr;
                }
            }
        }
        return null;
    }

}
