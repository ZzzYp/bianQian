package com.gome.note.db.cache;

import android.content.Context;
import android.util.SparseArray;

import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2017/12/6.
 */

public class NoteCache {

    private NoteCache() {
    }


    public interface OnGetPocketListListener {
        void onGetPocketList(List<PocketInfo> pocketList);
    }

    public interface OnGetLableListListener {
        void onGetLableList(List<LabelInfo> labelList);
    }

    public interface OnGetSameLablePocketListListener {
        void onGetSameLablePocketList(ConcurrentHashMap<Long, ArrayList<PocketInfo>> sameLableInfoMap);
    }

    private static final LinkedBlockingQueue<PocketInfo> sPocketInfoLists = new LinkedBlockingQueue<>();
    private static final LinkedBlockingQueue<LabelInfo> sLableInfoLists = new LinkedBlockingQueue<>();
    private static final ConcurrentHashMap<Long, PocketInfo> sPocketInfoMaps = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, LabelInfo> sLableInfoMaps = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Long, ArrayList<PocketInfo>> sSameLableInfoMaps = new ConcurrentHashMap<>();

    public static ArrayList<LabelInfo> getLableList() {
        return new ArrayList<>(sLableInfoLists);
    }

    public static int getLableListSize() {
        return sLableInfoLists.size();
    }

    public static boolean hasValue() {
        return !sPocketInfoLists.isEmpty()
                || !sLableInfoLists.isEmpty()
                || !sPocketInfoMaps.isEmpty()
                || !sLableInfoMaps.isEmpty()
                || !sSameLableInfoMaps.isEmpty();
    }

    public static LabelInfo getLableInfoById(long lableId) {
        LabelInfo li = sLableInfoMaps.get(lableId);
        return LabelInfo.clone(li);
    }

    public static PocketInfo getPocketInfoById(long pocketId) {
        PocketInfo pi = sPocketInfoMaps.get(pocketId);
        return pi;
    }

    public static int getCountById(long lableId) {
        ArrayList<PocketInfo> list = sSameLableInfoMaps.get(lableId);
        return list != null ? list.size() : 0;
    }

    public static ArrayList<PocketInfo> getSameLablePocketInfoListById(long lableId) {
        return sSameLableInfoMaps.get(lableId);
    }

    private static void callBack(NoteCache.OnGetLableListListener onGetLableListListener, List<LabelInfo> labelList) {
        if (onGetLableListListener != null) {
            if (isEmpty(labelList)) {
                onGetLableListListener.onGetLableList(new ArrayList<LabelInfo>());
            } else {
                onGetLableListListener.onGetLableList(labelList);
            }
        }
    }

    private static void callBack(NoteCache.OnGetPocketListListener onGetPocketListListener, List<PocketInfo> pocketList) {
        if (onGetPocketListListener != null) {
            if (isEmpty(pocketList)) {
                onGetPocketListListener.onGetPocketList(new ArrayList<PocketInfo>());
            } else {
                onGetPocketListListener.onGetPocketList(pocketList);
            }
        }
    }

    private static void callBack(NoteCache.OnGetSameLablePocketListListener onGetSameLablePocketListListener, ConcurrentHashMap<Long, ArrayList<PocketInfo>> sameLableInfoMap) {
        if (onGetSameLablePocketListListener != null) {
            if (isEmpty(sameLableInfoMap)) {
                onGetSameLablePocketListListener.onGetSameLablePocketList(new ConcurrentHashMap<Long, ArrayList<PocketInfo>>());
            } else {
                onGetSameLablePocketListListener.onGetSameLablePocketList(sameLableInfoMap);
            }
        }
    }

    private static void clearArray(Object object) {
        if (object != null) {
            if (object instanceof Collection) {
                ((Collection) object).clear();
            } else if (object instanceof SparseArray) {
                ((SparseArray) object).clear();
            } else if (object instanceof Map) {
                ((Map) object).clear();
            }
        }
    }

    public static boolean isEmpty(Object object) {
        if (object != null) {
            if (object instanceof Collection) {
                return ((Collection) object).isEmpty();
            } else if (object instanceof SparseArray) {
                return ((SparseArray) object).size() <= 0;
            } else if (object instanceof Map) {
                return ((Map) object).isEmpty();
            }
        }
        return true;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        clearArray(sPocketInfoLists);
        clearArray(sLableInfoLists);
        clearArray(sPocketInfoMaps);
        clearArray(sLableInfoMaps);
        clearArray(sSameLableInfoMaps);
    }
}
