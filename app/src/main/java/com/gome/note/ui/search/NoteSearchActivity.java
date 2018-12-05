package com.gome.note.ui.search;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.entity.RecordPool;
import com.gome.note.ui.label.adapter.LabelManageAdapter;
import com.gome.note.ui.search.adapter.ItemClickListener;
import com.gome.note.ui.search.adapter.SearchResultAdapter;
import com.gome.note.ui.search.adapter.TagAdapter;
import com.gome.note.ui.search.presenter.NoteSearchPresenter;
import com.gome.note.ui.search.view.EditChangeListener;
import com.gome.note.ui.search.view.FlowLayout;
import com.gome.note.ui.search.view.TagFlowLayout;
import com.gome.note.utils.AntiShake;
import com.gome.note.utils.ShowStyle;
import com.gome.note.view.NotesEditText;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NoteSearchActivity extends BaseActivity implements View.OnClickListener, ItemClickListener {


    private NotesEditText mEtSearchNote;
    private ImageView mImageClear;
    private TextView mTvSearchActivityRecommend;
    private RecyclerView mRcSearchResult;
    private TextView mTvNoResult;
    private Context mContext;
    private List<LabelInfo> mLabelInfoList = new ArrayList<>();
    private NoteSearchPresenter mNoteSearchPresenter;
    private List<PocketInfo> searchResultPocketInfoList = new ArrayList<>();
    private SearchResultAdapter mSearchResultAdapter;
    private List<PocketInfo> mSoftPocketList;
    private CharSequence mLabel = "";
    private boolean isShowSearchResult = false;
    private TagFlowLayout mTfTagResult;
    private LinearLayout mLlTagGroup;
    private TextView mTvSearchCancel;
    private Disposable mTagSubscribe;
    private Disposable mSearchResultSubscribe;
    //private AntiShake mAntiShakeUtils = new AntiShake();
    private boolean isClickItem;
    private IntentFilter intentFilter;
    private TimeChangeReceiver timeChangeReceiver;
    private String keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_search);
        mContext = this;

        keyword = getIntent().getStringExtra(NoteConfig.SEARCH_KEYWORD);

        intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeChangeReceiver = new TimeChangeReceiver();
        registerReceiver(timeChangeReceiver, intentFilter);


        ButterKnife.bind(this);
        initPresenter();
        initView();
        showSoftInputFromWindow(this, mEtSearchNote);
        initData();
    }

    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

    private void initView() {
        mLlTagGroup = (LinearLayout) findViewById(R.id.ll_tag_group);
        mEtSearchNote = (NotesEditText) findViewById(R.id.et_search_note);
        mTfTagResult = (TagFlowLayout) findViewById(R.id.tf_tag_result);
        mImageClear = (ImageView) findViewById(R.id.image_clear);
        mTvSearchActivityRecommend = (TextView) findViewById(R.id.tv_search_activity_recommend);
        mRcSearchResult = (RecyclerView) findViewById(R.id.rc_search_result);
        mTvNoResult = (TextView) findViewById(R.id.tv_no_result);

        mTvSearchCancel = (TextView) findViewById(R.id.tv_search_cancel);
        mTvSearchCancel.setOnClickListener(this);
        mImageClear.setOnClickListener(this);

        if (null != keyword && keyword.length() > 0) {
            mEtSearchNote.setText(keyword);
            isShowSearchResult = true;
            isClickItem = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (null != RecordPool.map) {
            RecordPool.map.clear();
        }
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        if (isShowSearchResult && isClickItem) {
            isClickItem = false;
            int childCount = mLlTagGroup.getChildCount();
            if (childCount >= 1) {
                TextView childAt = (TextView) mLlTagGroup.getChildAt(0);
                mLabel = childAt.getText();
            } else {
                mLabel = "";
            }
            String searchSting = mEtSearchNote.getText().toString().replace("\uFEFF", "").trim();
            showSearchResultNote(searchSting, mLabel.toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        NoteConfig.inMultiWindowDoEditStatus = false;

    }


    private void initData() {
        //get label list from db
        //mLabelInfoList = mNoteSearchPresenter.getLabelList();

        mTagSubscribe = Observable.just(getTagList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mTfTagResult.setAdapter(new TagAdapter<LabelInfo>(list) {
                    @Override
                    public View getView(FlowLayout parent, int position, LabelInfo labelInfo) {

                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.tag_flow_layout_item, parent, false);
                        TextView tv_tag_name = (TextView) view.findViewById(R.id.tv_tag_name);
                        String title = labelInfo.getTitle();

                        if (TextUtils.isEmpty(title)) {
                            tv_tag_name.setText("");
                        } else {
                            if ((title.equals(mContext.getString(R.string.lable_type_record_en)))
                                    || (title.equals(mContext.getString(R.string.lable_type_record_cn)))) {
                                tv_tag_name.setText(mContext.getString(R.string.lable_type_record));
                            } else {
                                tv_tag_name.setText(labelInfo.getTitle());
                            }
                        }


                        return view;
                    }
                }));

        mTfTagResult.setOnTagClickListener((view, position, parent) -> {
            String tagName = mLabelInfoList.get(position).getTitle();
            addTagToSearchView(tagName);
            displayTagResult(position);
            return true;
        });

        mEtSearchNote.addTextChangedListener(new EditChangeListener() {

            @Override
            public void onEditTextChange(CharSequence s, int start, int before, int count) {

                int childCount = mLlTagGroup.getChildCount();
                if (childCount >= 1) {
                    TextView childAt = (TextView) mLlTagGroup.getChildAt(0);
                    CharSequence mLabel = childAt.getText();
                    showSearchResultNote(s.toString(), mLabel.toString());
                } else {

                    if (s.toString().trim().equals("")) {
                        showSearchResultNote("", "");
                        //showAllNote();
                    } else {
                        showSearchResultNote(s.toString(), "");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                super.afterTextChanged(s);
                int childCount = mLlTagGroup.getChildCount();
                if (childCount == 0) {
                    mImageClear.setVisibility("".equals(s.toString().trim()) ? View.GONE : View.VISIBLE);
                }
            }
        });

        mEtSearchNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            CharSequence mLabel = "";

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //                    searchResultPocketInfoList.clear();
                    int childCount = mLlTagGroup.getChildCount();
                    if (childCount >= 1) {
                        TextView childAt = (TextView) mLlTagGroup.getChildAt(0);
                        mLabel = childAt.getText();
                    }
                    String searchSting = mEtSearchNote.getText().toString().replace("\uFEFF", "").trim();

                    showSearchResultNote(searchSting, mLabel.toString());
                    return true;
                }
                return false;
            }
        });

        mEtSearchNote.setBackKeyListener(new NotesEditText.BackKeyListener() {
            @Override
            public void onBackPressedDown() {
                int selectionStart = mEtSearchNote.getSelectionStart();
                if (selectionStart == 0 && mLlTagGroup.getChildCount() > 0) {
                    mLlTagGroup.removeAllViews();
                    mEtSearchNote.setHint(R.string.search_label_title);
                    String searchStr = mEtSearchNote.getText().toString();
                    showSearchResultNote(searchStr, "");
                }

                if (TextUtils.isEmpty(mEtSearchNote.getText()) && mLlTagGroup.getChildCount() == 0) {
                    mImageClear.setVisibility(View.GONE);
                }
            }
        });
    }

    private List<LabelInfo> getTagList() {

        List<PocketInfo> pocketList = mNoteSearchPresenter.getPocketList();
        for (PocketInfo pocketInfo : pocketList) {
            List<LabelInfo> labelList = pocketInfo.getLabels();
            Iterator<LabelInfo> it = labelList.iterator();
            while (it.hasNext()) {
                LabelInfo labelInfo = it.next();
                if (labelInfo.getId() == 0) {
                    it.remove();
                }
            }
            mLabelInfoList.addAll(labelList);
        }
        HashSet<LabelInfo> labelInfoSet = new HashSet<>(mLabelInfoList);
        CopyOnWriteArrayList<LabelInfo> labelInfoList = new CopyOnWriteArrayList<>(labelInfoSet);
        String tagIgnore = getResources().getString(R.string.put_top);
        for (LabelInfo labelInfo : labelInfoList) {
            if (tagIgnore.equals(labelInfo.getTitle())) {
                labelInfoList.remove(labelInfo);
            }
        }
        mLabelInfoList.clear();
        mLabelInfoList.addAll(labelInfoList);
        return mLabelInfoList;
    }

    private void displayTagResult(int position) {
        mImageClear.setVisibility(View.VISIBLE);
        String label = mLabelInfoList.get(position).getTitle();
        String searchSting = mEtSearchNote.getText().toString().replace("\uFEFF", "").trim();
        mEtSearchNote.setHint("");
        showSearchResultNote(searchSting, label);
    }


    private boolean isSameId;

    private void showSearchResultNote(String searchSting, String label) {
//        if (mAntiShakeUtils.check()) {
//            return;
//        }

        isShowSearchResult = true;
        mTvSearchActivityRecommend.setVisibility(View.GONE);
        mTfTagResult.setVisibility(View.GONE);
        mRcSearchResult.setVisibility(View.VISIBLE);
        searchResultPocketInfoList.clear();

        if (null != mSearchResultSubscribe && !mSearchResultSubscribe.isDisposed()) {
            mSearchResultSubscribe.dispose();
        }

        mSearchResultSubscribe = Observable.create((ObservableOnSubscribe<List<PocketInfo>>) e -> {
            List<PocketInfo> pocketInfos = PocketDbHandle.queryPocketsListNoStick(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET);
            for (PocketInfo pocketInfo : pocketInfos) {
                if (pocketInfo.isContainsTrueInPut(searchSting, label, getApplicationContext())) {
                    isSameId = false;
                    long id = pocketInfo.getId();
                    if (searchResultPocketInfoList.size() > 0) {
                        for (int i = 0; i < searchResultPocketInfoList.size(); i++) {
                            long tempId = searchResultPocketInfoList.get(i).getId();
                            if (id == tempId) {
                                isSameId = true;
                                break;
                            }
                        }
                        if (!isSameId) {
                            searchResultPocketInfoList.add(pocketInfo);
                        }
                    } else {
                        searchResultPocketInfoList.add(pocketInfo);
                    }

                }
            }
            e.onNext(searchResultPocketInfoList);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pocketInfos -> {
                    if (pocketInfos.size() == 0) {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.GONE);
                    }

                    mSoftPocketList = mNoteSearchPresenter.getSoftPocketList(pocketInfos);

//                    if (mSearchResultAdapter == null) {
                    mRcSearchResult.setLayoutManager(new LinearLayoutManager(mContext));
                    mSearchResultAdapter = new SearchResultAdapter(mContext,
                            mSoftPocketList, searchSting, ShowStyle.LIST_STYLE);
                    mRcSearchResult.setAdapter(mSearchResultAdapter);
                    mSearchResultAdapter.setItemClickListener(this);
//                    } else {
//                        mSearchResultAdapter.setSearchStr(searchSting);
//                        mSearchResultAdapter.notifyDataSetChanged();
//                    }
                });
    }

    private void addTagToSearchView(String tagName) {
        View view = LayoutInflater.from(this).inflate(R.layout.tag_flow_layout_searchview, null);
        TextView textViewForTag = (TextView) view.findViewById(R.id.tv_tag_name);
        if (TextUtils.isEmpty(tagName)) {
            textViewForTag.setText("");
        } else {
            if ((tagName.equals(mContext.getString(R.string.lable_type_record_en)))
                    || (tagName.equals(mContext.getString(R.string.lable_type_record_cn)))) {
                textViewForTag.setText(mContext.getString(R.string.lable_type_record));
            } else {
                textViewForTag.setText(tagName);
            }
        }
        //textViewForTag.setText(tagName);
        mLlTagGroup.addView(textViewForTag);
    }

    @Override
    public void initPresenter() {
        mNoteSearchPresenter = new NoteSearchPresenter(mContext);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_search_cancel:
                finish();
                break;
            case R.id.image_clear:
                mLlTagGroup.removeAllViews();
                mEtSearchNote.setHint(R.string.search_label_title);
                mEtSearchNote.setText(null);
                break;
        }
    }

    private void showAllNote() {
        Observable.just(PocketDbHandle.queryPocketsList(mContext, PocketDbHandle.URI_POCKET))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pocketInfos -> {
                    if (pocketInfos.size() == 0) {
                        mTvNoResult.setVisibility(View.VISIBLE);
                    } else {
                        mTvNoResult.setVisibility(View.GONE);
                        mTfTagResult.setVisibility(View.GONE);
                    }

                    mSoftPocketList = mNoteSearchPresenter.getSoftPocketList(pocketInfos);

//                    if (mSearchResultAdapter == null) {
                    mRcSearchResult.setLayoutManager(new LinearLayoutManager(mContext));
                    mSearchResultAdapter = new SearchResultAdapter(mContext,
                            mSoftPocketList, "", ShowStyle.LIST_STYLE);
                    mRcSearchResult.setAdapter(mSearchResultAdapter);
                    mSearchResultAdapter.setItemClickListener(this);
//                    } else {
//                        mSearchResultAdapter.setSearchStr("");
//                        mSearchResultAdapter.notifyDataSetChanged();
//                    }
                });
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
        if (null != mTagSubscribe && !mTagSubscribe.isDisposed()) {
            mTagSubscribe.dispose();
        }
        if (null != mSearchResultSubscribe && !mSearchResultSubscribe.isDisposed()) {
            mSearchResultSubscribe.dispose();
        }
        unregisterReceiver(timeChangeReceiver);
    }

    @Override
    public void onItemClick(View view, int position) {
        KeyboardUtils.hideSoftInput(this);
        isClickItem = true;
    }


    class TimeChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_TIME_CHANGED:
                    isClickItem = true;
                    break;
                case Intent.ACTION_TIMEZONE_CHANGED:
                    break;

            }
        }
    }
}
