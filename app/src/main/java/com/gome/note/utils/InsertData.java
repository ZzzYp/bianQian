package com.gome.note.utils;

import android.content.Context;


import com.gome.note.R;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.ContentInfo;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * ProjectName:MyPocket
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2017/6/20
 * DESCRIBE:
 */

public class InsertData {
    private static String ADD_POCKET_ICON = "add_pocket_icon";
    private static String FAST_ADD_POCKET_ICON = "fast_add_pocket_icon";
    private static String INTELLIGENT_ICON = "intelligent_icon";
    private static String STEALTH_ICON = "stealth_icon";
    private static String PRIVACY_ICON = "privacy_icon";
    public static HashMap<String, Integer> iconMaps = new HashMap<>();
    private static int add_pocket_guide = R.string.add_pocket_guide;
//    private static int add_pocket_title =;
//    private static int add_pocket_content = R.string.add_pocket_content;
//    private static int fast_add_pocket_title = R.string.fast_add_pocket_title;
//    private static int fast_add_pocket_content = R.string.fast_add_pocket_content;
//    private static int intelligent_title = R.string.intelligent_title;
//    private static int intelligent_content = R.string.intelligent_content;
//    private static int stealth_title = R.string.stealth_title;
//    private static int stealth_content = R.string.stealth_content;
//    private static int privacy_title = R.string.privacy_title;
//    private static int privacy_content = R.string.privacy_content;

//    static {
//        iconMaps.put(ADD_POCKET_ICON, R.drawable.icon_gome_three_create);
//        iconMaps.put(FAST_ADD_POCKET_ICON, R.drawable.icon_gome_second_create);
//        iconMaps.put(INTELLIGENT_ICON, R.drawable.icon_gome_lable_create);
//        iconMaps.put(STEALTH_ICON, null);
//        iconMaps.put(PRIVACY_ICON, null);
//    }

    public static ArrayList<LabelInfo> insertLabel(Context context) {

        ArrayList<LabelInfo> labelInfos = new ArrayList<>();
        LabelInfo labelInfo2 = new LabelInfo();
        labelInfo2.setTitle(context.getString(add_pocket_guide));
        labelInfo2.setDateAdded(System.currentTimeMillis());
        PocketDbHandle.insert(context, labelInfo2);
        labelInfos.add(labelInfo2);
        return labelInfos;
    }

    public static void insertPocketInfo(Context context) {

//
//        pocketInfo.setSummary("在内容页面“三指下滑，即可唤出智能全局收藏");
//        pocketInfo.setIcon(ADD_POCKET_ICON);
//        NoteDbHandle.insert(context, pocketInfo, NoteStore.Pocket.TABLE_POCKET_NAME);
//        LogUtils.d("iconMaps", iconMaps.size());
    }

    public static void addPocketList(Context context) {
//        insertLabel(context);
//        ArrayList<LabelInfo> labelInfos = new ArrayList<>();
//        labelInfos.add(PocketDbHandle.queryLabelInfoByTitle(context, context.getString
//                (add_pocket_guide)));
//        ArrayList<ArrayList<ContentInfo>> contentInfos = new ArrayList<>();
//        contentInfos.add(getContentInfo(context.getString(add_pocket_title), ADD_POCKET_ICON,
//                context.getString(add_pocket_content),true));
//        contentInfos.add(getContentInfo(context.getString(fast_add_pocket_title),
//                FAST_ADD_POCKET_ICON, context.getString(fast_add_pocket_content),true));
//        contentInfos.add(getContentInfo(context.getString(intelligent_title), INTELLIGENT_ICON,
//                context.getString(intelligent_content),true));
//        contentInfos.add(getContentInfo(context.getString(stealth_title), STEALTH_ICON,
//                context.getString(stealth_content),false));
//        contentInfos.add(getContentInfo(context.getString(privacy_title), PRIVACY_ICON,
//                context.getString(privacy_content),false));
//        for (int i = 4; i >=0 ; i--) {
//            PocketInfo pocketInfo = new PocketInfo();
//            pocketInfo.setContents(contentInfos.get(i));
//            pocketInfo.setLabels(labelInfos);
//            pocketInfo.setSummary(contentInfos.get(i).get(0).getText());
//            pocketInfo.setIcon(contentInfos.get(i).get(0).getImage());
//            PocketDbHandle.insert(context, PocketDbHandle.URI_POCKET, pocketInfo);
//        }
    }

    public static ArrayList<ContentInfo> getContentInfo(String title, String icon, String content, boolean hasIcon) {
        ArrayList<ContentInfo> contentInfos = new ArrayList();
        ContentInfo contentInfo = new ContentInfo();
        ContentInfo contentInfo3 = new ContentInfo();
        contentInfo.setText(title);
        ContentInfo contentInfo2 = new ContentInfo();
        contentInfo2.setText(content);
        contentInfos.add(contentInfo);
        contentInfos.add(contentInfo2);
        if (hasIcon){
            contentInfo3.setImage(icon);
            contentInfos.add(contentInfo3);
        }
        return contentInfos;
    }
}
