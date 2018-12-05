package com.gome.note.ui.home.presenter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.gome.note.R;
import com.gome.note.base.BasePresenter;
import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.entity.ClassifyInfo;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.home.NoteHomeActivity;
import com.gome.note.ui.home.NoteHomeListener;
import com.gome.note.ui.home.model.NoteHomeModel;
import com.gome.note.utils.DataUtils;
import com.gome.note.utils.OverallSituationStickLabel;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/22
 * DESCRIBE:
 */

public class NoteHomePresenter extends BasePresenter {
    private NoteHomeModel mModel;
    private String TAG = "NoteHomePresenter";
    private TreeMap<Long, ClassifyInfo> classifyMap = new TreeMap<>(new MapKeyComparator());
    private List<PocketInfo> mPocketInfos = new ArrayList<>();
    private int classifyFrequency;
    private int tempYear = 0;
    private NoteHomeListener mNoteHomeListener;

    public NoteHomePresenter(Context context) {
        mContext = context;
        initModel();
    }

    @Override
    public void initModel() {
        mModel = new NoteHomeModel(mContext, mHandler);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Config.QUERY_SUCCESS:

                    List<PocketInfo> pocketInfos = mModel.getPocketInfos();
                    classifyMap.clear();
                    classifyFrequency = 0;
                    tempYear = 0;
                    //add head
                    PocketInfo pocketInfo = new PocketInfo();
                    pocketInfo.setId(-2);
                    pocketInfos.add(0, pocketInfo);
                    setClassifyNoteInfo(pocketInfos);


                    break;
                case Config.ADD_SUCCESS:
                    break;
                case Config.UPDATE_SUCCESS:
                    break;
                case Config.DELETE_SUCCESS:

                    //mActivity.deleteSuccess();
                    if (null != mNoteHomeListener) {
                        mNoteHomeListener.deleteSuccess();
                    }


                    break;
                case Config.QAUD_ERROR:
                    break;

                case Config.QUERY_HOME_LABEL_SUCCESS:

                    List<LabelInfo> labelInfos = mModel.getLabelInfos();
                    if (null != labelInfos) {
                        OverallSituationStickLabel.getInstance().setLabelInfoList((ArrayList<LabelInfo>) labelInfos);
                        OverallSituationStickLabel.getInstance().setStickLabelInfo(labelInfos.size() > 0 ? labelInfos.get(0) : null);
                    }

                    break;
                case Config.CLASSIFY_HOME_NOTEINFO:


                    break;

                default:
                    break;


            }
        }
    };

    public void setClassifyNoteInfo(List<PocketInfo> pocketInfos) {
        if (null == pocketInfos) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < pocketInfos.size(); i++) {
                        long time = pocketInfos.get(i).getDateModified();
                        if (pocketInfos.get(i).isStick()) {
                            if (!classifyMap.containsKey(Long.MAX_VALUE)) {
                                classifyFrequency = classifyFrequency + 1;
                                setClassifyMapValue(-1, mContext.getString(R.string.put_top), pocketInfos, i, Long.MAX_VALUE);
                            }

                        } else if (time != 0 && DataUtils.isCurrentYear(time)) {
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

                    }


                    boolean isStick = false;
                    for (Map.Entry<Long, ClassifyInfo> entry : classifyMap.entrySet()) {

                        ClassifyInfo classifyInfo = entry.getValue();

                        int classifyPositionPrevious = (classifyInfo.getPosition() + (classifyInfo.getFrequency() - 1) - 1);
                        if (classifyPositionPrevious > 0) {
                            pocketInfos.get(classifyPositionPrevious).setClassifyLast(true);
                        }


                        PocketInfo pocketInfo = new PocketInfo();
                        pocketInfo.setId(-1);
                        pocketInfo.setSummary(classifyInfo.getValue());
                        if (isStick) {
                            pocketInfo.setStickNextClassify(true);
                            isStick = false;
                        }
                        pocketInfos.add(classifyInfo.getPosition() + (classifyInfo.getFrequency() - 1), pocketInfo);


                        if (classifyInfo.getValue().equals(mContext.getString(R.string.put_top))) {
                            isStick = true;
                        }
                    }
                    //mPocketInfos.clear();
                    // mPocketInfos.addAll(pocketInfos);
                    //mPocketInfos = pocketInfos;
                    if (null != mNoteHomeListener) {
                        mNoteHomeListener.setQueryPocketInfos((ArrayList<PocketInfo>) pocketInfos);
                    }
//                    mActivity.runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            //mActivity.setQueryPocketInfos((ArrayList<PocketInfo>) mPocketInfos);
//
//                        }
//                    });

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setClassifyYear(int id, String key, List<PocketInfo> pocketInfos, int i, int year, long time) {
        classifyFrequency = classifyFrequency + 1;
        setClassifyMapValue(-1, String.valueOf(year), pocketInfos, i, time);
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


    public ArrayList<PocketInfo> setPocketListChecked(ArrayList<PocketInfo> mPocketList, boolean isChecked, int postion) {
        if (null == mPocketList) {
            return null;
        }
        mPocketList.get(postion).setChecked(isChecked);
        return mPocketList;
    }

    public int getCheckedCount(ArrayList<PocketInfo> mPocketList) {
        if (null == mPocketList || mPocketList.size() == 0) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < mPocketList.size(); i++) {
            boolean isCheck = mPocketList.get(i).isChecked();
            long id = mPocketList.get(i).getId();
            if (isCheck && id > 0) {
                count = count + 1;
            }

        }
        return count;
    }

    public int getNotPocketCount(ArrayList<PocketInfo> mPocketList) {
        if (null == mPocketList) {
            return 0;
        }
        int count = 0;
        for (int i = 0; i < mPocketList.size(); i++) {
            boolean isCheck = mPocketList.get(i).isChecked();
            long id = mPocketList.get(i).getId();
            if (!isCheck && id <= 0) {
                count = count + 1;
            }

        }
        return count;
    }


    public ArrayList<PocketInfo> setPocketListCheckedAll(ArrayList<PocketInfo> mPocketList, boolean isAllCheck) {

        if (null == mPocketList) {
            return null;
        }
        for (int i = 0; i < mPocketList.size(); i++) {
            if (isAllCheck && mPocketList.get(i).getId() > 0) {
                mPocketList.get(i).setChecked(true);
            } else {
                mPocketList.get(i).setChecked(false);
            }
        }
        return mPocketList;
    }

    public void queryLabelInfo() {

        mModel.queryLabelInfo();

    }

    public void queryNoteInfo() {
        mModel.query("");

    }

    public void deleteNoteInfo(ArrayList<PocketInfo> pocketInfos) {

        mModel.deleteNoteInfo(pocketInfos);
    }

    public ArrayList<PocketInfo> stickItem(ArrayList<PocketInfo> mPocketList) {

        for (int i = 0; i < mPocketList.size(); i++) {
            PocketInfo pocketInfo = mPocketList.get(i);
            if (pocketInfo.isChecked()) {
                //pocketInfo.setDateModified(System.currentTimeMillis());
                List<LabelInfo> labelInfos = pocketInfo.getLabels();
                if (null == labelInfos) {
                    labelInfos = new ArrayList<>();
                }
//                for (int m = 0; m < labelInfos.size(); m++) {
//                    boolean isStick = labelInfos.get(m).isStick();
//                    if (isStick) {
//                        labelInfos.remove(m);
//                    }
//                }
                Iterator<LabelInfo> it = labelInfos.iterator();
                while (it.hasNext()) {
                    LabelInfo labelInfo = it.next();
                    boolean isStick = labelInfo.isStick();
                    if (isStick) {
                        it.remove();
                    }
                }
                LabelInfo stickLabelInfo = OverallSituationStickLabel.getInstance().getStickLabelInfo();
                if (null == stickLabelInfo) {
                    return null;
                }

                labelInfos.add(stickLabelInfo);
                pocketInfo.setLabels(labelInfos);
                pocketInfo.setStick(true);
                pocketInfo.setChecked(false);
                PocketDbHandle.update(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET, pocketInfo);
            }
        }

        return mPocketList;

    }


    public ArrayList<PocketInfo> cancelstickItem(ArrayList<PocketInfo> mPocketList) {

        for (int i = 0; i < mPocketList.size(); i++) {
            PocketInfo pocketInfo = mPocketList.get(i);
            if (pocketInfo.isChecked()) {
                //pocketInfo.setDateModified(System.currentTimeMillis());
                List<LabelInfo> labelInfos = pocketInfo.getLabels();
                if (null == labelInfos) {
                    labelInfos = new ArrayList<>();
                }

//                for (int m = 0; m < labelInfos.size(); m++) {
//                    boolean isStick = labelInfos.get(m).isStick();
//                    if (isStick) {
//                        labelInfos.remove(m);
//                    }
//                }
                Iterator<LabelInfo> it = labelInfos.iterator();
                while (it.hasNext()) {
                    LabelInfo labelInfo = it.next();
                    boolean isStick = labelInfo.isStick();
                    if (isStick) {
                        it.remove();
                    }
                }


                pocketInfo.setLabels(labelInfos);
                pocketInfo.setStick(false);
                pocketInfo.setChecked(false);
                PocketDbHandle.update(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET, pocketInfo);
            }
        }

        return mPocketList;

    }

    public void deleteNoteInfos(ArrayList<PocketInfo> mPocketList) {

        deleteNoteInfo(mPocketList);
    }

    public boolean getIsHasnotPinItem(ArrayList<PocketInfo> mPocketList) {

        for (int i = 0; i < mPocketList.size(); i++) {
            if (mPocketList.get(i).isChecked() && !mPocketList.get(i).isStick()) {
                return false;
            }
        }
        return true;
    }

    public boolean isHasStickItem(ArrayList<PocketInfo> mPocketList) {

        if (null != mPocketList) {
            for (int i = 0; i < mPocketList.size(); i++) {
                if (mPocketList.get(i).getId() >= 0 && mPocketList.get(i).isStick()) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isHasNoStickItem(ArrayList<PocketInfo> mPocketList) {

        if (null != mPocketList) {
            for (int i = 0; i < mPocketList.size(); i++) {
                if (mPocketList.get(i).getId() >= 0 && !mPocketList.get(i).isStick()) {
                    return true;
                }
            }
        }
        return false;
    }

    class MapKeyComparator implements Comparator<Long> {
        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }


    public NoteHomeListener getmNoteHomeListener() {
        return mNoteHomeListener;
    }

    public void setNoteHomeListener(NoteHomeListener noteHomeListener) {
        this.mNoteHomeListener = noteHomeListener;
    }
}
