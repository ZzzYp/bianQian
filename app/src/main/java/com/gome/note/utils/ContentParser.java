package com.gome.note.utils;

import android.support.annotation.NonNull;


import com.gome.note.domain.Memo;
import com.gome.note.entity.Forever;
import com.gome.note.entity.NodeType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 2016/12/16.
 *
 * @author pythoncat.cheng
 * @apiNote note content parser and save helper
 */

public class ContentParser {


    /**
     * trans list data 2 json
     *
     * @param record list data
     * @return json
     */
    public static String contentMap2Json(List<String> record) {
        String head = "{";
        String end = "}";
        String link = ":";
        String split = ",";
        String refrence = "\"";
        StringBuilder sbJson = new StringBuilder(head);
        for (int key = 0; key < record.size(); key++) {
            String content = record.get(key);
            content = base64Encode(content);
            sbJson
                    .append(refrence).append(key)
                    .append(refrence).append(link)
                    .append(refrence).append(content)
                    .append(refrence).append(split);
        }
        if (sbJson.toString().endsWith(split)) {
            sbJson.deleteCharAt(sbJson.length() - 1);
        }
        sbJson.append(end);
        return sbJson.toString();
    }


    /**
     * after load data from db ,parse json 2 list node
     *
     * @param json str --> json will be {} ,with no data in
     * @return List Node --> is node NOT note !
     */
    public static List<Memo> parseContentJson(String json, long lastModified) {
        // parse json
        //        LogUtils.e("json = " + json);
        List<Memo> data = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(json);
            JSONArray names = object.names(); // ["0","2","1","3"]
            //            LogUtils.e(names);
            if (names == null || names.length() == 0) {
                return data;
            }
            ArrayList<Integer> keys = new ArrayList<>();
            for (int i = 0; i < names.length(); i++) {
                Object o = names.get(i); // default key is object not integer
                keys.add(Integer.parseInt((String) o));
            }
            Collections.sort(keys);
            for (Integer key : keys) {
                String content = object.getString(String.valueOf(key));
                if (Base64.isBase64(content.getBytes())) {
                    //if has been encrypted by base64,pls decode by base64
                    content = base64Decode(content);
                }
                Memo node = new Memo();
                node.key = key;
                node.value = content; // do not remove <IMAGEVIE> </IMAGEVIEW> from here !
                boolean isImg = content.startsWith(Forever.IMG_HEAD) && content.endsWith(Forever.
                        IMG_FOOT);
                boolean isAudio = content.startsWith(Forever.AUDIO_HEAD) && content.endsWith
                        (Forever.AUDIO_FOOT);
                if (isImg) {
                    node.type = NodeType.IMAGE;
                } else if (isAudio) {
                    node.type = NodeType.AUDIO;
                } else {
                    node.type = NodeType.TEXT;
                }
                data.add(node);
            }
            Collections.sort(data, (left, right) -> left.key - right.key);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("json content parse faill, you should debug 2 find " +
                    "question !");
        }
    }


    private static String urlEncode(@NonNull String origin) {
        return URLEncoder.encode(origin);
    }

    private static String urlDecode(@NonNull String urlEncodedStr) {
        return URLDecoder.decode(urlEncodedStr);
    }

    private static String base64Encode(@NonNull String orgin) {
        return orgin;//new String(Base64.encode(orgin.getBytes()));
    }

    private static String base64Decode(@NonNull String encodedStr) {
        return encodedStr;//new String(Base64.decode(encodedStr.getBytes()));
    }
}
