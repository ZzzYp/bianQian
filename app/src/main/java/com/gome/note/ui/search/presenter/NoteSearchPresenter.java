package com.gome.note.ui.search.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gome.note.base.BasePresenter;
import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.ClassifyInfo;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.search.model.NoteSearchModel;
import com.gome.note.utils.DataUtils;

import java.text.ParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/22
 * DESCRIBE:
 */

public class NoteSearchPresenter extends BasePresenter {
    private NoteSearchModel mModel;
    private TreeMap<Long, ClassifyInfo> classifyMap = new TreeMap<>(new NoteSearchPresenter.MapKeyComparator());
    private int classifyFrequency;
    private int tempYear = 0;


    public NoteSearchPresenter(Context context) {
        mContext = context;
    }

    @Override
    public void initModel() {
        mModel = new NoteSearchModel(mContext.getApplicationContext(), mHandler);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.QUERY_SUCCESS:
                    break;
                case Config.ADD_SUCCESS:
                    break;
                case Config.UPDATE_SUCCESS:
                    break;
                case Config.DELETE_SUCCESS:
                    break;
                case Config.QAUD_ERROR:
                    break;
                default:
                    break;
            }
        }
    };

    public List<LabelInfo> getLabelList() {
        return PocketDbHandle.queryLablesList(mContext.getApplicationContext(), PocketDbHandle.URI_LABLE);
    }


    public List<PocketInfo> getPocketList() {
        return PocketDbHandle.queryPocketsList(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET);
    }

    public List<PocketInfo> getSoftPocketList(List<PocketInfo> pocketInfos) {
        classifyMap.clear();
        classifyFrequency = 0;
        tempYear = 0;
        for (int i = 0; i < pocketInfos.size(); i++) {
            long time = pocketInfos.get(i).getDateModified();
            try {
                if (time != 0 && DataUtils.isCurrentYear(time)) {
                    int mouth = DataUtils.getModifyMouth(time);
                    setClassifyMouth(-1, String.valueOf(mouth), pocketInfos, i, mouth, time);


                } else if (time != 0) {
                    //get year
                    int year = DataUtils.getModifyYear(time);
                    if (year != tempYear) {
                        tempYear = year;
                        setClassifyYear(-1, String.valueOf(year), pocketInfos, i, year, time);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<Long, ClassifyInfo> entry : classifyMap.entrySet()) {

            ClassifyInfo classifyInfo = entry.getValue();

            int classifyPositionPrevious = (classifyInfo.getPosition() + (classifyInfo.getFrequency() - 1) - 1);
            if (classifyPositionPrevious > 0) {
                pocketInfos.get(classifyPositionPrevious).setClassifyLast(true);
            }


            PocketInfo pocketInfo = new PocketInfo();
            pocketInfo.setId(-1);
            pocketInfo.setSummary(classifyInfo.getValue());

            pocketInfos.add(classifyInfo.getPosition() + (classifyInfo.getFrequency() - 1), pocketInfo);

        }
        return pocketInfos;
    }

    private void setClassifyYear(int id, String key, List<PocketInfo> pocketInfos, int i, int year, long time) {
        classifyFrequency = classifyFrequency + 1;
        setClassifyMapValue(-1, String.valueOf(year), pocketInfos, i, time);
    }

    private boolean containsMouth(int mouth) {
        for (Map.Entry<Long, ClassifyInfo> entry : classifyMap.entrySet()) {
            ClassifyInfo classifyInfo = entry.getValue();
            String mouthValue = classifyInfo.getValue();
            if (mouthValue.equals(String.valueOf(mouth))) {
                return true;
            }
        }
        return false;
    }

    private void setClassifyMapValue(int id, String key, List<PocketInfo> pocketInfos, int index, long time) {
        ClassifyInfo classifyInfo = new ClassifyInfo();
        classifyInfo.setPosition(index);
        classifyInfo.setValue(key);
        classifyInfo.setFrequency(classifyFrequency);
        classifyMap.put(time, classifyInfo);
    }

    private void setClassifyMouth(int id, String key, List<PocketInfo> pocketInfos, int i, int mouth, long time) {
        switch (mouth) {
            case 1:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 2:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 3:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 4:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 5:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 6:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 7:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 8:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 9:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 10:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 11:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
            case 12:
                if (!containsMouth(mouth)) {
                    classifyFrequency = classifyFrequency + 1;
                    setClassifyMapValue(-1, String.valueOf(mouth), pocketInfos, i, time);
                }
                break;
        }


    }

    class MapKeyComparator implements Comparator<Long> {


        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }
}
