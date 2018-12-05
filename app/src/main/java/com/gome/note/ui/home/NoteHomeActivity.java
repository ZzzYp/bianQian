package com.gome.note.ui.home;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.PocketInfo;
import com.gome.note.entity.RecordPool;
import com.gome.note.ui.create.NoteCreateActivity;
import com.gome.note.ui.history.HistoryNoteActivity;
import com.gome.note.ui.home.adapter.ItemClickListener;
import com.gome.note.ui.home.adapter.NoteHomeAdapter;
import com.gome.note.ui.home.model.NoteHomeModel;
import com.gome.note.ui.home.presenter.NoteHomePresenter;
import com.gome.note.ui.label.LabelManagerActivity;
import com.gome.note.ui.search.NoteSearchActivity;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.utils.FileHelper;
import com.gome.note.utils.OverallSituationPocketInfo;
import com.gome.note.utils.SharedPreferencesUtil;
import com.gome.note.utils.ShowStyle;
import com.gome.note.utils.ToastHelper;
import com.gome.note.view.AlertDialog.CustomAlertDialog;
import com.gome.note.view.FloatActionMenuView.ActionMenuView;
import com.gome.note.view.FloatActionMenuView.CustomFloatActionMenuView;
import com.gome.note.view.FloatActionMenuView.CustomMenuView;

import java.util.ArrayList;
import java.util.List;


public class NoteHomeActivity extends BaseActivity implements CustomFloatActionMenuView.OnFloatActionMenuSelectedListener, View.OnClickListener, ItemClickListener, NoteHomeAdapter.AdapterOnDeleteCheckListener, NoteHomeListener {

    private String TAG = "NoteHomeActivity";
    private NoteHomeModel mModel;
    private Context mContext;
    private NoteHomeActivity mActivity;
    private CustomFloatActionMenuView mNoteHomeBottomMenu, mFrameLayoutBottom, mFrameLayoutBottomFinish;
    private RecyclerView mRecyclerView;
    private ImageView mSearchIcon, mImageStart;
    private TextView mTextNoResult, mTvLeftTitle, mTvMainTitle, mTvRightTitle, mTvNoResult;
    private NoteHomePresenter mPresenter;
    private ArrayList<PocketInfo> mPocketList = new ArrayList<>();//all pocketInfoList
    private NoteHomeAdapter mPocketHomeAdapter;
    private static final int SHOW_HOME_LIST = 0;
    private NoteHomeAdapter.AdapterOnDeleteCheckListener mOnDeleteCheckListener = null;
    private ArrayList<PocketInfo> mSaveitemPockeDeleteChecked = null;
    private CustomAlertDialog mDialog;
    private final int NEED_CAMERA = 201;
    private static final int REQUEST_CAMERA = 102;
    public static final int NO_REQUEST = 0;
    public boolean mIsAllChecked, mCheckedStatus;
    private CheckBox mCbRightTitle;
    private boolean mBottomIsCancelPin;
    private TelephonyManager mTelephonyManager;
    private Toast mToastRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_home);
        mContext = this;
        mActivity = this;
        mOnDeleteCheckListener = this;
        initView();
        initListener();
        initPresenter();
        initData();

    }


    private void initView() {
        mNoteHomeBottomMenu = (CustomFloatActionMenuView) findViewById(R.id.home_frame_bottom_menu);
        mRecyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
        mSearchIcon = (ImageView) findViewById(R.id.image_search);
        mImageStart = (ImageView) findViewById(R.id.image_start);
        mTvLeftTitle = (TextView) findViewById(R.id.tv_left_title);
        mTvMainTitle = (TextView) findViewById(R.id.tv_main_title);
        mTvRightTitle = (TextView) findViewById(R.id.tv_right_title);
        mCbRightTitle = (CheckBox) findViewById(R.id.cb_right_title);
        mTvNoResult = (TextView) findViewById(R.id.tv_no_result);

        mFrameLayoutBottom = (CustomFloatActionMenuView) findViewById(R.id.home_frame_bottom_menu);
        mTextNoResult = (TextView) findViewById(R.id.tv_no_result);
        mFrameLayoutBottomFinish = (CustomFloatActionMenuView) findViewById(R.id
                .home_frame_bottom_finish_menu);


    }

    @SuppressLint("RestrictedApi")
    private void setFloatActionMenuViewIconColor() {

        if (mFrameLayoutBottom.getChildCount() > 0) {
            ActionMenuView menu = (ActionMenuView) mFrameLayoutBottom.getChildAt(0);
            if (null != menu) {
                for (int i = 0; i < menu.getChildCount(); i++) {
                    int id = menu.getChildAt(i).getId();
                    if (id == R.id.home_tv_bottom_record) {
                        CustomMenuView.ItemView bottomRecordView = (CustomMenuView.ItemView) menu.getChildAt(i);
                        VectorDrawable vectorDrawablebottomRecordView = (VectorDrawable) bottomRecordView.getItemData().getIcon();
                        vectorDrawablebottomRecordView.setTint(getResources().getColor(R.color.tag_normal_text_color));
                        bottomRecordView.setIcon(vectorDrawablebottomRecordView);
                    }
                }
            }
        }
    }

    private void initData() {

    }

    private void initListener() {
        mNoteHomeBottomMenu.setOnFloatActionMenuSelectedListener(this);
        mFrameLayoutBottomFinish.setOnFloatActionMenuSelectedListener(this);
        mTvLeftTitle.setOnClickListener(this);
        //mTvRightTitle.setOnClickListener(this);
        mCbRightTitle.setOnClickListener(this);

        mFrameLayoutBottomFinish.setSoundEffectsEnabled(false);
        mFrameLayoutBottomFinish.setOnClickListener(this);
    }

    @Override
    public void initPresenter() {
        mPresenter = new NoteHomePresenter(mContext);
        mPresenter.setNoteHomeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (null != RecordPool.map) {
            RecordPool.map.clear();
        }
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        refreshHomeListShow();

        setFloatActionMenuViewIconColor();
        if (mCbRightTitle.getVisibility() == View.GONE || mCbRightTitle.getVisibility() == View.INVISIBLE) {
            mCbRightTitle.setChecked(false);
        }
    }


    public void refreshHomeListShow() {
        mPresenter.queryNoteInfo();
    }

    //get query noteinfo
//    public void setQueryPocketInfos(ArrayList<PocketInfo> pocketInfos) {
//
//    }
    @Override
    public void setQueryPocketInfos(ArrayList<PocketInfo> pocketInfos) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showPocketInfoList(0, pocketInfos);
                if (null != pocketInfos && pocketInfos.size() > 1) {
                    mTvNoResult.setVisibility(View.GONE);
                } else {
                    mTvNoResult.setVisibility(View.VISIBLE);
                }
                // isNoPocketInfoList();
                mPresenter.queryLabelInfo();
            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_title:

                cancel();


                break;
            case R.id.tv_right_title:
//                String text = mTvRightTitle.getText().toString();
//                //if (text.equals(getString(R.string.all_selected))) {
//                if (mIsAllChecked) {
//                    //mTvRightTitle.setText(R.string.all_no_selected);
//                    mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
//                    mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, false);
//                    mTvMainTitle.setText(R.string.please_check_item);
//                    mIsAllChecked = false;
//                    mFrameLayoutBottomFinish.setMenuItemsDisable(R.id.tv_stick, R.id.tv_delete);
//                } else {
//                    //mTvRightTitle.setText(R.string.all_selected);
//                    mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
//                    mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, true);
//                    int count = mPresenter.getCheckedCount(mPocketList);
//                    String checkedString = getString(R.string.is_checked) + count + getString(R.string.item);
//                    mTvMainTitle.setText(checkedString);
//                    mIsAllChecked = true;
//                    mFrameLayoutBottomFinish.setMenuItemsEnable(R.id.tv_stick, R.id.tv_delete);
//                }
//
//                if (mPocketHomeAdapter != null) {
//                    mPocketHomeAdapter.setData(mPocketList);
//                    mPocketHomeAdapter.notifyDataSetChanged();
//                } else {
//                    mPocketHomeAdapter = setRecyclerViewAdapter(checkShowStyle(false), mPocketList);
//                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                }
//                OverallSituationPocketInfo.getInstance().setPacketList(mPocketList);
//                mPocketHomeAdapter.notifyDataSetChanged();

                break;

            case R.id.cb_right_title:


                //if (text.equals(getString(R.string.all_selected))) {
                if (mIsAllChecked) {
                    //mTvRightTitle.setText(R.string.all_no_selected);
                    //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                    mCbRightTitle.setChecked(false);
                    mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, false);
                    mTvMainTitle.setText(R.string.please_check_item);
                    mIsAllChecked = false;
                    mFrameLayoutBottomFinish.setMenuItemsDisable(R.id.tv_stick, R.id.tv_delete);
                } else {
                    //mTvRightTitle.setText(R.string.all_selected);
                    //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                    mCbRightTitle.setChecked(true);
                    mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, true);

                    if (mPresenter.isHasNoStickItem(mPocketList)) {
                        mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_top);
                        mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_sys_ic_stick);
                        mBottomIsCancelPin = false;
                    } else {
                        mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_cancel_top);
                        mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_ic_unstick);
                        mBottomIsCancelPin = true;
                    }

                    int count = mPresenter.getCheckedCount(mPocketList);
                    String checkedString = getString(R.string.is_checked) + count + getString(R.string.item);
                    mTvMainTitle.setText(checkedString);
                    mIsAllChecked = true;
                    mFrameLayoutBottomFinish.setMenuItemsEnable(R.id.tv_stick, R.id.tv_delete);
                }


                if (mPocketHomeAdapter != null) {
                    mPocketHomeAdapter.setData(mPocketList);
                    mPocketHomeAdapter.notifyDataSetChanged();
                } else {
                    mPocketHomeAdapter = setRecyclerViewAdapter(checkShowStyle(false), mPocketList);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                }
                OverallSituationPocketInfo.getInstance().setPacketList(mPocketList);
                mPocketHomeAdapter.notifyDataSetChanged();


                break;

        }
    }


    private void cancel() {
        mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, false);
        if (null != mSaveitemPockeDeleteChecked) {
            mSaveitemPockeDeleteChecked.clear();
        }
        mPocketHomeAdapter.showDeleteIcon(false, -1);
        mFrameLayoutBottomFinish.setVisibility(View.GONE);
        mFrameLayoutBottom.setVisibility(View.VISIBLE);
        mTvLeftTitle.setVisibility(View.GONE);
        //mTvRightTitle.setVisibility(View.GONE);
        //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
        mCbRightTitle.setVisibility(View.GONE);
        mCbRightTitle.setChecked(false);
        mCbRightTitle.jumpDrawablesToCurrentState();
        mTvMainTitle.setText(R.string.note);
        mIsAllChecked = false;
        mCheckedStatus = false;
        if (null != mPocketHomeAdapter) {
            mPocketHomeAdapter.setCheckedStatus(mCheckedStatus);
        }
    }


    public NoteHomeAdapter showPocketInfoList(int messages, ArrayList<PocketInfo> pocketInfos) {
//        ArrayList<PocketInfo> pocketInfos = (ArrayList<PocketInfo>) PocketDbHandle
//                .queryPocketsList(mContext, URI_POCKET);
        mPocketList.clear();
        mPocketList.addAll(pocketInfos);
        //mPocketList = pocketInfos;
        int count = 0;

        if (null != mSaveitemPockeDeleteChecked) {
            for (int i = 0; i < mPocketList.size(); i++) {
                long id = mPocketList.get(i).getId();
                if (id > 0) {
                    for (int j = 0; j < mSaveitemPockeDeleteChecked.size(); j++) {
                        long idtemp = mSaveitemPockeDeleteChecked.get(j).getId();
                        if (id == idtemp) {
                            mPocketList.get(i).setChecked(mSaveitemPockeDeleteChecked.get(j).isChecked());
                        }
                    }
                }
            }
        }
        if (mPocketHomeAdapter != null) {
            mPocketHomeAdapter.setData(mPocketList);
            mPocketHomeAdapter.notifyDataSetChanged();
        } else {
            mPocketHomeAdapter = setRecyclerViewAdapter(checkShowStyle(false), mPocketList);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            // add static overall situation  PocketInfo
        }
        OverallSituationPocketInfo.getInstance().setPacketList(mPocketList);

        return mPocketHomeAdapter;

    }

    public void isNoPocketInfoList() {
        if (ShowStyle.IS_FROM_ACTIVITY) {
            mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_favorite);
        }
        if (mPocketList.size() == 0) {
        }
    }

    private NoteHomeAdapter setRecyclerViewAdapter(int type, ArrayList<PocketInfo> pocketInfoArrayList) {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        if (mPocketHomeAdapter == null) {
            mPocketHomeAdapter = new NoteHomeAdapter(mContext, pocketInfoArrayList,
                    ShowStyle.LIST_STYLE, mOnDeleteCheckListener);
        } else {
            mPocketHomeAdapter.setData(pocketInfoArrayList);
            mPocketHomeAdapter.setType(ShowStyle.LIST_STYLE);
            mPocketHomeAdapter.notifyDataSetChanged();
        }
        mRecyclerView.setAdapter(mPocketHomeAdapter);
        mPocketHomeAdapter.setItemClickListener(this);
        return mPocketHomeAdapter;
    }

    private int checkShowStyle(boolean isClick) {

        return ShowStyle.LIST_STYLE;
    }


    @Override
    public boolean onFloatActionItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.home_tv_bottom_create:
                Intent intentCreate = new Intent(this, NoteCreateActivity.class);
                startActivity(intentCreate);


                break;
            case R.id.home_tv_bottom_camera:

                if (FileHelper.hasEnoughFreeRoom(() -> ToastHelper.show(getApplicationContext(),
                        getString(R.string.memo_leak_error)))) {
                    if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED
                            || ContextCompat.checkSelfPermission(mContext, Manifest.permission
                            .WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, NEED_CAMERA);
                    } else {
                        openCamera(this);
                    }
                }


                break;
            case R.id.home_tv_bottom_record:

                if (!captureAudio()) {
                    return false;
                }

                // is ScreenRecording
                if (ActivityCommonUtils.isMediaRecorderUsered(mContext.getApplicationContext())) {
                    showToastRecord(mContext.getString(R.string.record_function_is_used));
                    return false;
                }

                if (!phoneIsInUse(mContext)) {
                    Intent intentCreateRecord = new Intent(this, NoteCreateActivity.class);
                    intentCreateRecord.putExtra("openAudio", true);
                    startActivity(intentCreateRecord);
                } else {
                    showToastRecord(mContext.getString(R.string.mic_or_audio_is_in_use));
                }

                break;
            case R.id.home_tv_bottom_label_manager:
                Intent intentLabel = new Intent(this, LabelManagerActivity.class);
                intentLabel.putExtra("isClickItem", true);
                startActivity(intentLabel);

                break;
            case R.id.home_tv_bottom_recently_deleted:
                Intent intentRecentlyDeleted = new Intent(this, HistoryNoteActivity.class);
                startActivity(intentRecentlyDeleted);

                break;

            case R.id.tv_stick:

                if (mBottomIsCancelPin) {
                    cancelstickItem();
                } else {
                    stickItem();
                }


                break;

            case R.id.tv_delete:
                boolean isNotShowDiag = SharedPreferencesUtil.getBooleanValue(mContext.getApplicationContext(),
                        SharedPreferencesUtil.DELETE_HOME_DIALOG_NOT_ALERT, false);
                if (!isNotShowDiag) {
                    removeLabelDialog();
                } else {
                    removeItem();
                }

                break;


        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (null != mPocketHomeAdapter && mCheckedStatus) {
            mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, false);
            mPocketHomeAdapter.showDeleteIcon(false, -1);
            mFrameLayoutBottomFinish.setVisibility(View.GONE);
            mFrameLayoutBottom.setVisibility(View.VISIBLE);
            mTvLeftTitle.setVisibility(View.GONE);
            //mTvRightTitle.setVisibility(View.GONE);
            //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
            mCbRightTitle.setVisibility(View.GONE);
            mCbRightTitle.setChecked(false);
            mTvMainTitle.setText(R.string.note);
            mIsAllChecked = false;
            mCheckedStatus = false;
            if (null != mPocketHomeAdapter) {
                mPocketHomeAdapter.setCheckedStatus(mCheckedStatus);
            }
            mFrameLayoutBottomFinish.setMenuItemsEnable(R.id.tv_stick, R.id.tv_delete);
        } else {
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case NEED_CAMERA:
                // if the permission be refused ,the grantResults will be null
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera(this);
                }
                break;

            case NoteConfig.NEED_AUDIO:
                // if the permission be refused ,the grantResults will be null
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!phoneIsInUse(mContext)) {
                        Intent intentCreateRecord = new Intent(this, NoteCreateActivity.class);
                        intentCreateRecord.putExtra("openAudio", true);
                        startActivity(intentCreateRecord);
                    } else {
                        showToastRecord(mContext.getString(R.string.mic_or_audio_is_in_use));

                    }
                }
                break;
        }
    }

    private void showToastRecord(String toastStr) {
        if (null == toastStr) {
            return;
        }
        if (null != mToastRecord) {
            mToastRecord.setText(toastStr);
        } else {
            mToastRecord = Toast.makeText(mContext, toastStr, Toast.LENGTH_SHORT);
        }
        mToastRecord.setDuration(Toast.LENGTH_SHORT);
        mToastRecord.show();
    }

    private void stickItem() {
        mPresenter.stickItem(mPocketList);
        cancel();
        refreshHomeListShow();
    }

    private void cancelstickItem() {
        mPresenter.cancelstickItem(mPocketList);
        cancel();
        refreshHomeListShow();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NO_REQUEST) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (cameraFile != null) {
                    Intent intent = new Intent(this, NoteCreateActivity.class);
                    if (cameraFile != null) {
                        intent.putExtra("picpath", cameraFile.getAbsolutePath());
                    }
                    startActivity(intent);
                }
                break;

        }

    }


    private void removeLabelDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View dialogCheckbox = factory.inflate(R.layout.dialog_delete_to_history, null);
        final CheckBox checkboxs = (CheckBox) dialogCheckbox.findViewById(R.id.checkbox);
        final TextView tvCheckString = (TextView) dialogCheckbox.findViewById(R.id.tv_check_string);
        tvCheckString.setText(R.string.even_not_alert);
        mDialog = new CustomAlertDialog.Builder(mContext)
                .setView(dialogCheckbox)
                .setCancelable(true)
                .setMessage(R.string.dialog_history_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkboxs.isChecked()) {
                            //no alert
                            SharedPreferencesUtil.saveBooelanValue(mContext.getApplicationContext(),
                                    SharedPreferencesUtil.DELETE_HOME_DIALOG_NOT_ALERT, true);
                        } else {
                            //have alert
                        }
                        removeItem();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                        if (i == KeyEvent.KEYCODE_BACK
                                && keyEvent.getRepeatCount() == 0) {
                            dialogInterface.cancel();
                            return true;
                        }
                        return false;
                    }
                }).create();
        int count = mPresenter.getCheckedCount(mPocketList);
        if (count == 1) {
            mDialog.setTitle(R.string.dialog_delete_title);
        } else {
            mDialog.setTitle(R.string.dialog_delete_titles);
        }
        mDialog.show();
    }


    //delete Item
    public void removeItem() {
        onDeleteBtnCilck();
    }


    public ArrayList<PocketInfo> getPocketList() {
        return mPocketList;
    }

    public void setPocketList(ArrayList<PocketInfo> mPocketList) {
        this.mPocketList = mPocketList;
    }


    @Override
    public void onItemClick(View view, int position) {

        if (mCheckedStatus) {
            if (position != 0) {
                boolean checked = mPocketList.get(position).isChecked();
                adapterOnDeleteCheck(view, !checked, position);
            }

        } else {
            if (position == 0) {
                Intent intentSearch = new Intent(this, NoteSearchActivity.class);
                intentSearch.putExtra(NoteConfig.SEARCH_KEYWORD, "");
                startActivity(intentSearch);
            } else {
                Intent intent = new Intent(mContext, NoteCreateActivity.class);
                long id = 0;
                String text = "";
                id = mPocketList.get(position).getId();
                intent.putExtra("position", position);
                intent.putExtra("detailFlag", true);
                intent.putExtra("id", id);
                intent.putExtra("text", text);
                intent.putExtra("detail", true);
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public void onDeleteBtnCilck() {
        mPresenter.deleteNoteInfos(mPocketList);
        cancel();
    }

    @Override
    public void onLongClick(View view, int position) {
        mPocketHomeAdapter.showDeleteIcon(true, position);
        mFrameLayoutBottomFinish.setVisibility(View.VISIBLE);
        mFrameLayoutBottom.setVisibility(View.GONE);
        mTvLeftTitle.setVisibility(View.VISIBLE);
        //mTvRightTitle.setVisibility(View.VISIBLE);
        mCbRightTitle.setVisibility(View.VISIBLE);
        String checkedString = getString(R.string.is_checked) + "1" + getString(R.string.item);
        mTvMainTitle.setText(checkedString);
        if (mPocketList.size() == 3) {
            mIsAllChecked = true;
            //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
            mCbRightTitle.setChecked(true);
            mPocketList = mPresenter.setPocketListCheckedAll(mPocketList, true);
        }
        mCheckedStatus = true;
        if (null != mPocketHomeAdapter) {
            mPocketHomeAdapter.setCheckedStatus(mCheckedStatus);
        }
        mFrameLayoutBottomFinish.setMenuItemsEnable(R.id.tv_stick, R.id.tv_delete);

        if (mPocketList.get(position).isStick()) {
            mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_cancel_top);
            mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_ic_unstick);
            mBottomIsCancelPin = true;
        } else {
            mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_top);
            mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_sys_ic_stick);
            mBottomIsCancelPin = false;
        }
    }

    @Override
    public void onNoItemList(List<PocketInfo> list) {

    }

    @Override
    public void adapterOnDeleteCheck(View view, boolean isChecked, int postion) {
        if (null == mPocketList) {
            return;
        }
        mPocketList = mPresenter.setPocketListChecked(mPocketList, isChecked, postion);

        boolean isHasNotPinItem = mPresenter.getIsHasnotPinItem(mPocketList);
        if (isHasNotPinItem) {
            mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_cancel_top);
            mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_ic_unstick);
            mBottomIsCancelPin = true;
        } else {
            mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_top);
            mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_sys_ic_stick);
            mBottomIsCancelPin = false;
        }


        int count = mPresenter.getCheckedCount(mPocketList);
        int notPocketCount = mPresenter.getNotPocketCount(mPocketList);

        if (count != 0) {
            if ((count + notPocketCount) == mPocketList.size()) {
                // mTvRightTitle.setText(R.string.all_no_selected);
                //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
                mCbRightTitle.setChecked(true);
                mIsAllChecked = true;
            } else {
                // mTvRightTitle.setText(R.string.all_selected);
                //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
                mCbRightTitle.setChecked(false);
                mIsAllChecked = false;
            }
            String checkedString = getString(R.string.is_checked) + count + getString(R.string.item);
            mTvMainTitle.setText(checkedString);
            mFrameLayoutBottomFinish.setMenuItemsEnable(R.id.tv_stick, R.id.tv_delete);
        } else {
            // mTvRightTitle.setText(R.string.all_selected);
            //mTvRightTitle.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
            mCbRightTitle.setChecked(false);
            mTvMainTitle.setText(R.string.please_check_item);
            mIsAllChecked = false;
            mFrameLayoutBottomFinish.setMenuItemsDisable(R.id.tv_stick, R.id.tv_delete);
            mFrameLayoutBottomFinish.setMenuItemTitle(R.id.tv_stick, R.string.home_put_top);
            mFrameLayoutBottomFinish.setMenuItemIcon(R.id.tv_stick, R.drawable.ic_gome_sys_ic_stick);
            mBottomIsCancelPin = false;
        }

        mPocketHomeAdapter.setData(mPocketList);
        mPocketHomeAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        NoteConfig.inMultiWindowDoEditStatus = false;

        if (null != mSaveitemPockeDeleteChecked) {
            mSaveitemPockeDeleteChecked.clear();
        } else {
            mSaveitemPockeDeleteChecked = new ArrayList<>();
        }
        //save LongClick label checked status
        //mSaveitemPockeDeleteChecked = mPocketList;
        mSaveitemPockeDeleteChecked.addAll(mPocketList);
        if (null != mDialog) {
            mDialog.cancel();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        NoteConfig.onCreateCount = 0;
        NoteConfig.inMultiWindowNoteId = -1;
        NoteConfig.inMultiWindowDoEditStatus = false;
    }

    public boolean phoneIsInUse(Context context) {
        boolean isInCall = false;
        if (null == mTelephonyManager) {
            mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        int type = mTelephonyManager.getCallState();
        if (type == TelephonyManager.CALL_STATE_IDLE) {
            //phone is not have call
            isInCall = false;
        } else if (type == TelephonyManager.CALL_STATE_OFFHOOK) {
            //phone is in call
            isInCall = true;
        } else if (type == TelephonyManager.CALL_STATE_RINGING) {
            //phone is call ringing
            isInCall = true;
        } else {
            //phone is not have call
            isInCall = false;
        }
        return isInCall;
    }

    public boolean captureAudio() {
        //  xxxxxxxxxxxx for 7.0 !!!!
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, NoteConfig.NEED_AUDIO);
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPocketHomeAdapter) {
            mPocketHomeAdapter.showDeleteIcon(false, -1);
            mPocketHomeAdapter = null;
        }
        if (null != mPresenter) {
            mPresenter.setNoteHomeListener(null);
        }
    }

//    public void deleteSuccess() {
//        refreshHomeListShow();
//    }

    @Override
    public void deleteSuccess() {
        refreshHomeListShow();
    }

}
