package com.gome.note.ui.label;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gome.note.R;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.ClassifyInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.entity.RecordPool;
import com.gome.note.ui.search.adapter.ItemClickListener;
import com.gome.note.ui.search.adapter.SearchResultAdapter;
import com.gome.note.ui.search.presenter.NoteSearchPresenter;
import com.gome.note.utils.DataUtils;
import com.gome.note.utils.ShowStyle;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class TagSearchActivity extends Activity implements View.OnClickListener, ItemClickListener {

    private Context mContext;
    private ImageView mIvMenuBack;
    private TextView mTvTagSearchTitle;
    private RecyclerView mRcTagSearchResult;
    private TextView mTvNoResult;
    private SearchResultAdapter mSearchResultAdapter;
    private List<PocketInfo> searchResultPocketInfoList = new ArrayList<>();
    private Disposable mSubscribe;
    private List<PocketInfo> mSoftPocketList;
    private TreeMap<Long, ClassifyInfo> classifyMap = new TreeMap<>(new MapKeyComparator());
    private int classifyFrequency;
    private int tempYear = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_search);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();

        initData();
        if (null != RecordPool.map) {
            RecordPool.map.clear();
        }
    }

    private void initView() {
        mIvMenuBack = (ImageView) findViewById(R.id.iv_menu_back);
        mTvTagSearchTitle = (TextView) findViewById(R.id.tv_tag_search_title);
        mTvNoResult = (TextView) findViewById(R.id.tv_no_result);
        mRcTagSearchResult = (RecyclerView) findViewById(R.id.rc_tag_search_result);
        mIvMenuBack.setOnClickListener(this);

        VectorDrawable vectorDrawableMenuBack = (VectorDrawable) mIvMenuBack.getDrawable();
        vectorDrawableMenuBack.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mIvMenuBack.setImageDrawable(vectorDrawableMenuBack);
    }

    private void initData() {
        Intent intent = getIntent();
        String labelName = intent.getStringExtra("labelName");
        if (LabelManageUtils.isVoiceMemosLable(mContext.getApplicationContext(), labelName)) {
            mTvTagSearchTitle.setText(mContext.getString(R.string.lable_type_record));
        } else {
            mTvTagSearchTitle.setText(labelName);
        }
        mSubscribe = Observable.just(PocketDbHandle.queryPocketsListNoStick(this, PocketDbHandle.URI_POCKET))
                .map(pocketInfos -> {
                    searchResultPocketInfoList.clear();
                    pocketInfos.forEach(pocketInfo -> {
                        if (pocketInfo.isContainsTrueInPut("", labelName, getApplicationContext())) {
                            searchResultPocketInfoList.add(pocketInfo);
                        }
                    });
                    return searchResultPocketInfoList;
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pocketInfos -> {
                    if (pocketInfos.size() == 0) {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.GONE);
                    }

                    mSoftPocketList = getSoftPocketList(pocketInfos);

                    if (mSearchResultAdapter == null) {
                        mRcTagSearchResult.setLayoutManager(new LinearLayoutManager(this));
                        mSearchResultAdapter = new SearchResultAdapter(this, mSoftPocketList, "", ShowStyle.LIST_STYLE);
                        mRcTagSearchResult.setAdapter(mSearchResultAdapter);
                        mSearchResultAdapter.setItemClickListener(this);
                    } else {
                        mSearchResultAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        NoteConfig.inMultiWindowDoEditStatus = false;

    }

    @Override
    protected void onStop() {
        super.onStop();
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        NoteConfig.inMultiWindowDoEditStatus = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscribe != null && !mSubscribe.isDisposed()) {
            mSubscribe.dispose();
        }
    }

    @Override
    public void onItemClick(View view, int position) {


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

    class MapKeyComparator implements Comparator<Long> {


        @Override
        public int compare(Long o1, Long o2) {
            return o2.compareTo(o1);
        }
    }
}
