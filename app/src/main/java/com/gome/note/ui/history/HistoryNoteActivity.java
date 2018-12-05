package com.gome.note.ui.history;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ThreadPoolUtils;
import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.PocketStore;
import com.gome.note.entity.ContentInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.history.adapter.HistoryAdapter;
import com.gome.note.ui.history.adapter.ItemHistoryClickListener;
import com.gome.note.ui.history.presenter.HistoryNotePresenter;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.utils.ShowStyle;
import com.gome.note.view.AlertDialog.CustomAlertDialog;
import com.gome.note.view.FloatActionMenuView.CustomFloatActionMenuView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public class HistoryNoteActivity extends BaseActivity implements ItemHistoryClickListener, View
        .OnClickListener, CustomFloatActionMenuView .OnFloatActionMenuSelectedListener {
    private String TAG = "HistoryNoteActivity";
    private Context mContext;
    private HistoryNoteActivity mActivity;
    private CustomFloatActionMenuView mFrameLayoutBottom;
    private CheckBox mRightText;
    private TextView mTextTitle, mLeftIcon, mButtonCancel, mTitle, mContent, mButtonRight;
    private LinearLayout mNoResultLinearLayout;
    private ImageView mImageStart;
    private RecyclerView mRecyclerView;
    private CustomAlertDialog mDialog;
    private View inflate;
    private HistoryAdapter mHistoryAdapter;
    private HistoryNotePresenter presenter;
    private ArrayList<PocketInfo> mPocketInfos;
    private PocketInfo mPocketInfo;
    private ArrayList<PocketInfo> mSelectInfos = new ArrayList<>();
    public static boolean isShowDeleteIcon;
    private int selectCount;
    private int updateCount;
    private boolean shareNoSupport;
    private int mType;
    private TextView mTvLeftTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_note);
        mContext = this;
        mActivity = this;
        initView();
        initListener();

    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_history);
        mFrameLayoutBottom = (CustomFloatActionMenuView) findViewById(R.id.history_bar_bottom_menu);
        mRightText = (CheckBox) findViewById(R.id.tv_right_title);
        mLeftIcon = (TextView) findViewById(R.id.tv_left_icon);
        mTextTitle = (TextView) findViewById(R.id.tv_center_title);
        mNoResultLinearLayout = (LinearLayout) findViewById(R.id.ll_no_result);
        mImageStart = (ImageView) findViewById(R.id.image_start);
        mTvLeftTitle = (TextView) findViewById(R.id.tv_left_title);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mHistoryAdapter = new HistoryAdapter(mContext, new ArrayList(), ShowStyle
                .LIST_STYLE);
        mRecyclerView.setAdapter(mHistoryAdapter);

        VectorDrawable vectorDrawableLeftIcon = (VectorDrawable) mLeftIcon.getBackground();
        vectorDrawableLeftIcon.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mLeftIcon.setBackground(vectorDrawableLeftIcon);
    }

    private void initListener() {
        mLeftIcon.setOnClickListener(this);
        mRightText.setOnClickListener(this);
        mFrameLayoutBottom.setOnFloatActionMenuSelectedListener(this);

        mFrameLayoutBottom.setSoundEffectsEnabled(false);
        mFrameLayoutBottom.setOnClickListener(this);
    }

    @Override
    public void initPresenter() {
        presenter = new HistoryNotePresenter(mContext.getApplicationContext());
    }

    public void setItemClickListener() {
        mHistoryAdapter.setItemClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //modified by chenhuaiyu start
        new ThreadPoolUtils(ThreadPoolUtils.CachedThread, 0).execute(new Runnable() {
            @Override
            public void run() {
                mPocketInfos = (ArrayList<PocketInfo>) PocketDbHandle.
                        queryHistoryPocketsList(mContext.getApplicationContext());
                ArrayList<PocketInfo> arrayList = new ArrayList();
                for (PocketInfo pocketInfo : mPocketInfos) {
                    long addTime = pocketInfo.getDateModified();
                    long nowTime = System.currentTimeMillis();
                    int day = (int) ((nowTime - addTime) / 1000 / 60 / 60 / 24);
                    //30 date
                    if (day > 30) {
                        arrayList.add(pocketInfo);
                    }
                }
                for (PocketInfo pocketInfo : arrayList) {
                    PocketDbHandle.delete(mContext.getApplicationContext(), PocketDbHandle.URI_HISTORY, pocketInfo.getId());
                }
                mPocketInfos = removeListObject(mPocketInfos, arrayList);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRecyclerViewAdapter(mPocketInfos);
                    }
                });

                Message message = new Message();
                message.what = 0;
                handler.sendMessage(message);
            }
        });
        //modified by chenhuaiyu end
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mPocketInfos != null) {
                        if (mPocketInfos.size() == 0) {
                            showEmptyHistory();
                            mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
                            mNoResultLinearLayout.setVisibility(View.VISIBLE);
                        } else {
                            setRecyclerViewAdapter(mPocketInfos);
                            mNoResultLinearLayout.setVisibility(View.GONE);
                        }
                    }
                    if (mRightText.getVisibility() != View.VISIBLE || mFrameLayoutBottom.getVisibility() != View.VISIBLE) {
                        if (mHistoryAdapter != null) {
                            mHistoryAdapter.showDeleteIcon(false);
                        }
                        mRightText.setVisibility(View.GONE);
                        mFrameLayoutBottom.setVisibility(View.GONE);
                        mLeftIcon.setBackgroundResource(R.drawable.ic_gome_icon_back);
                    }
                    break;

                case 1:
                    mPocketInfos = removeListObject(mPocketInfos, mSelectInfos);
                    mHistoryAdapter.setSelectItemCheck(false);
                    mHistoryAdapter.notifyDataSetChanged();
                    if (mPocketInfos.size() == 0) {
                        showEmptyHistory();
                        mNoResultLinearLayout.setVisibility(View.VISIBLE);
                        mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
                    }
                    Toast.makeText(mContext, getString(R.string.already_recover_to_home), Toast.LENGTH_SHORT).show();

                    showEmptyHistory();

                    break;

                case 2:

                    mPocketInfos = removeListObject(mPocketInfos, mSelectInfos);
                    mHistoryAdapter.setSelectItemCheck(false);
                    mHistoryAdapter.notifyDataSetChanged();
                    if (mPocketInfos.size() == 0) {
                        showEmptyHistory();
                        mNoResultLinearLayout.setVisibility(View.VISIBLE);
                        mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
                    }

                    showEmptyHistory();


                    break;
            }
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left_icon:
                mTvLeftTitle.setVisibility(View.VISIBLE);
                if (isShowDeleteIcon) {
                    showEmptyHistory();
                } else {
                    if (mRightText.isShown()) {
                        mRightText.setVisibility(View.GONE);
                        mRightText.setChecked(false);
                        mRightText.jumpDrawablesToCurrentState();
                        mLeftIcon.setBackgroundResource(R.drawable.ic_gome_icon_back);
                        mTextTitle.setText("");
                        mFrameLayoutBottom.setVisibility(View.GONE);
                        mSelectInfos.clear();
                        mHistoryAdapter.clearCheckStatusMap();
                        mHistoryAdapter.showDeleteIcon(false);
                    } else {
                        finish();
                    }
                }

                break;

            case R.id.tv_right_title:

                selectAllOrCancel();
                break;

            case R.id.btn_cancel:
                break;
            case R.id.btn_confirm:
                if (mType == ShowStyle.RECOVER_STYLE_BOTTOM) {
                    recoverInfo();
                } else if (mType == ShowStyle.RECOVER_STYLE_SLIDE) {
                    slideRecoverPocket();
                } else if (mType == ShowStyle.DELETE_STYLE_BOTTOM) {
                    deleteBottomSelect();
                } else if (mType == ShowStyle.DELETE_STYLE_SLIDE) {
                    slideDeletePocketInfo();
                }

                break;
        }
        if (null != mDialog) {
            mDialog.dismiss();
        }

    }

    /**
     * click the bottom to recover, pop-up windows
     */
    public void recoverInfo() {

       

        List<PocketInfo> copyOnWritePocketInfos = new CopyOnWriteArrayList<>();
        for (PocketInfo pocket : mSelectInfos) {
            copyOnWritePocketInfos.add(pocket);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (PocketInfo pocket : copyOnWritePocketInfos) {
                    PocketDbHandle.delete(mContext.getApplicationContext(), PocketStore.Pocket.TABLE_HISTORY_NAME, pocket.getId());
                    pocket.setDateAdded(System.currentTimeMillis());
                    pocket.setDateModified(System.currentTimeMillis());
                    PocketDbHandle.insert(mContext.getApplicationContext(), pocket, PocketStore.Pocket.TABLE_POCKET_NAME);
                }

                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();

//        for (PocketInfo pocket : mSelectInfos) {
//            PocketDbHandle.delete(mContext.getApplicationContext(), PocketStore.Pocket.TABLE_HISTORY_NAME, pocket.getId());
//            pocket.setDateAdded(System.currentTimeMillis());
//            pocket.setDateModified(System.currentTimeMillis());
//            PocketDbHandle.insert(mContext.getApplicationContext(), pocket, PocketStore.Pocket.TABLE_POCKET_NAME);
//        }
//        mPocketInfos = removeListObject(mPocketInfos, mSelectInfos);
//        mHistoryAdapter.setSelectItemCheck(false);
//        mHistoryAdapter.notifyDataSetChanged();
//        if (mPocketInfos.size() == 0) {
//            showEmptyHistory();
//            mNoResultLinearLayout.setVisibility(View.VISIBLE);
//            mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
//        }
//        Toast.makeText(mContext, getString(R.string.already_recover_to_home), Toast.LENGTH_SHORT).show();
    }

    /**
     * swipe left to cover ,pop-up windows
     */
    public void slideRecoverPocket() {
        mPocketInfos.remove(mPocketInfo);
        mHistoryAdapter.notifyDataSetChanged();
        PocketDbHandle.delete(mContext.getApplicationContext(), PocketStore.Pocket.TABLE_HISTORY_NAME, mPocketInfo.getId());
        mPocketInfo.setDateAdded(System.currentTimeMillis());
        PocketDbHandle.insert(mContext.getApplicationContext(), mPocketInfo, PocketStore.Pocket.TABLE_POCKET_NAME);
        if (mPocketInfos.size() == 0) {
            showEmptyHistory();
            mNoResultLinearLayout.setVisibility(View.VISIBLE);
            mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
        }
        showEmptyHistory();
    }

    /**
     * click the bottom to delete , pop-up the window
     */
    public void deleteBottomSelect() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                for (PocketInfo pocket : mSelectInfos) {
                    PocketDbHandle.delete(mContext.getApplicationContext(), PocketStore.Pocket.TABLE_HISTORY_NAME, pocket.getId());
                    boolean b = ActivityCommonUtils.deleteFile(pocket.getIcon());


                    List<ContentInfo> contentInfos = pocket.getContents();
                    if (null != contentInfos) {
                        for (ContentInfo contentInfo : contentInfos) {
                            String audioPath = contentInfo.getAudio();
                            String imagePath = contentInfo.getImage();
                            if (null != audioPath && audioPath.length() > 0) {
                                ActivityCommonUtils.deleteFile(audioPath);
                            }
                            if (null != imagePath && imagePath.length() > 0) {
                                ActivityCommonUtils.deleteFile(imagePath);
                            }
                        }
                    }

                    String audios = pocket.getHtml();
                    if (null != audios && audios.contains(".wav") && audios.contains("src=")) {
                        String[] spliteSrcArr = audios.split("src=");
                        if (null != spliteSrcArr && spliteSrcArr.length > 0) {
                            String src = spliteSrcArr[1];
                            String[] pathArr = src.split(".wav");
                            if (null != pathArr) {
                                String path = pathArr[0] + ".wav";
                                ActivityCommonUtils.deleteFile(path);
                            }
                        }
                    }

                    if (null != audios && audios.contains(".3gpp") && audios.contains("src=")) {
                        String[] spliteSrcArr = audios.split("src=");
                        if (null != spliteSrcArr && spliteSrcArr.length > 0) {
                            String src = spliteSrcArr[1];
                            String[] pathArr = src.split(".3gpp");
                            if (null != pathArr) {
                                String path = pathArr[0] + ".3gpp";
                                ActivityCommonUtils.deleteFile(path);
                            }
                        }
                    }

                }


                Message message = new Message();
                message.what = 2;
                handler.sendMessage(message);
            }
        }).start();


    }

    public void slideDeletePocketInfo() {
        ActivityCommonUtils.deleteFile(mPocketInfo.getIcon());
        String audios = mPocketInfo.getHtml();
        if (null != audios && audios.contains(".wav") && audios.contains("src=")) {
            String[] spliteSrcArr = audios.split("src=");
            if (null != spliteSrcArr && spliteSrcArr.length > 0) {
                String src = spliteSrcArr[1];
                String[] pathArr = src.split(".wav");
                if (null != pathArr) {
                    String path = pathArr[0] + ".wav";
                    ActivityCommonUtils.deleteFile(path);
                }
            }
        }

        if (null != audios && audios.contains(".3gpp") && audios.contains("src=")) {
            String[] spliteSrcArr = audios.split("src=");
            if (null != spliteSrcArr && spliteSrcArr.length > 0) {
                String src = spliteSrcArr[1];
                String[] pathArr = src.split(".3gpp");
                if (null != pathArr) {
                    String path = pathArr[0] + ".3gpp";
                    ActivityCommonUtils.deleteFile(path);
                }
            }
        }

        mPocketInfos = removeListObject(mPocketInfos, mPocketInfo);
        mHistoryAdapter.notifyDataSetChanged();
        PocketDbHandle.delete(mContext.getApplicationContext(), PocketStore.Pocket.TABLE_HISTORY_NAME, mPocketInfo.getId());
        if (mPocketInfos.size() == 0) {
            showEmptyHistory();
            mNoResultLinearLayout.setVisibility(View.VISIBLE);
            mImageStart.setImageResource(R.drawable.ic_gome_sys_ic_memo);
        }

        showEmptyHistory();
    }


    public void showEmptyHistory() {
        mRightText.setVisibility(View.GONE);
        if (mHistoryAdapter != null) {
            mHistoryAdapter.showDeleteIcon(false);
            mHistoryAdapter.getMap().clear();
        }
        mTextTitle.setText("");
        mTvLeftTitle.setVisibility(View.VISIBLE);
        mLeftIcon.setBackgroundResource(R.drawable.ic_gome_icon_back);
        mFrameLayoutBottom.setVisibility(View.GONE);
        mSelectInfos.clear();
        isShowDeleteIcon = false;
    }

    public void selectAllOrCancel() {
        if (!mRightText.isSelected()) {
            mHistoryAdapter.setSelectItemCheck(true);
        } else {
            mHistoryAdapter.setSelectItemCheck(false);
        }
        mSelectInfos = mHistoryAdapter.getSelectPocketInfo();
        mRightText.setSelected(!mRightText.isSelected());

//        mRightText.setBackground(!mRightText.isSelected() ? getDrawable(R.drawable.ic_gome_sys_ic_check_box_1)
//                : getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));

        mRightText.setChecked(!mRightText.isSelected() ? false : true);
        if (mSelectInfos.size() == 0) {
            mTextTitle.setText(getString(R.string.please_check_item));
        } else {
            mTextTitle.setText(getString(R.string.history_select) + " " + mSelectInfos.size() + " " + getString(R.
                    string.item));
        }
        if (null == mSelectInfos || mSelectInfos.size() == 0) {
            mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_recover);
            mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_delete);
        } else {
            mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_recover);
            mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_delete);
        }
    }


    private void setRecyclerViewAdapter(ArrayList<PocketInfo> pocketInfoArrayList) {
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (mHistoryAdapter == null) {
            mHistoryAdapter = new HistoryAdapter(mContext, pocketInfoArrayList, ShowStyle
                    .LIST_STYLE);
        } else {
            mHistoryAdapter.setData(pocketInfoArrayList);
            mHistoryAdapter.setType(ShowStyle.LIST_STYLE);
            mHistoryAdapter.notifyDataSetChanged();
        }
        //mRecyclerView.setAdapter(mHistoryAdapter);

        mHistoryAdapter.setItemClickListener(this);

        if (selectCount < pocketInfoArrayList.size()) {
//            mRightText.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
            mRightText.setChecked(false);
            mRightText.setSelected(false);
            updateCount = pocketInfoArrayList.size() - selectCount;
            mHistoryAdapter.cleanAllSelectItem();
            for (int i = 0; i < mSelectInfos.size(); i++) {
                long idTemp = mSelectInfos.get(i).getId();
                for (int j = 0; j < pocketInfoArrayList.size(); j++) {
                    long id = pocketInfoArrayList.get(j).getId();
                    if (idTemp == id) {
                        mHistoryAdapter.updateSelectItem(j);
                    }
                }
            }
            mSelectInfos = mHistoryAdapter.getSelectPocketInfo();
        }
    }

    private ArrayList<PocketInfo> removeListObject(ArrayList<PocketInfo> mPocketInfos, PocketInfo mPocketInfo) {
        long removerId = mPocketInfo.getId();
        for (int i = 0; i < mPocketInfos.size(); i++) {
            long tempId = mPocketInfos.get(i).getId();
            if (removerId == tempId) {
                mPocketInfos.remove(i);
            }
        }
        return mPocketInfos;
    }

    private ArrayList<PocketInfo> removeListObject(ArrayList<PocketInfo> mPocketInfos, ArrayList<PocketInfo> pocketInfos) {
        for (int i = 0; i < pocketInfos.size(); i++) {
            long removerId = pocketInfos.get(i).getId();
            for (int j = 0; j < mPocketInfos.size(); j++) {
                long tempId = mPocketInfos.get(j).getId();
                if (removerId == tempId) {
                    mPocketInfos.remove(j);
                }
            }
        }
        return mPocketInfos;
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemCheckShowClick(View view, int position) {
        boolean isCheckAll;
        mHistoryAdapter.setSelectItem(position);
        mSelectInfos = mHistoryAdapter.getSelectPocketInfo();
        if (mSelectInfos.size() == 0) {
            mTextTitle.setText(getString(R.string.please_check_item));
        } else {
            mTextTitle.setText(getString(R.string.history_select) + " " + mSelectInfos.size() + " " + getString(R.
                    string.item));
        }
        if (mSelectInfos.size() < mPocketInfos.size()) {
            isCheckAll = true;
        } else {
            isCheckAll = false;
        }
        mRightText.setSelected(!isCheckAll);
//        mRightText.setText(isCheckAll ? getString(R.string.history_select_all_title) : getString
//                (R.string.all_no_selected));
//        mRightText.setBackground(isCheckAll ? getDrawable(R.drawable.ic_gome_sys_ic_check_box_1)
//                : getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
        mRightText.setChecked(isCheckAll ? false : true);
        if (null == mSelectInfos || mSelectInfos.size() == 0) {
            mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_recover);
            mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_delete);
        } else {
            mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_recover);
            mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_delete);
        }
    }

    @Override
    public void onLongClick(View view, int position) {
        if (!isShowDeleteIcon) {
            boolean isCheckAll;
            mHistoryAdapter.setSelectItem(position);
            mSelectInfos = mHistoryAdapter.getSelectPocketInfo();
            mHistoryAdapter.showDeleteIcon(true);
            mFrameLayoutBottom.setVisibility(View.VISIBLE);
//            mRightText.setBackground(getDrawable(R.drawable.ic_gome_sys_ic_check_box_1));
            mRightText.setChecked(false);
            mRightText.jumpDrawablesToCurrentState();
            mRightText.setVisibility(View.VISIBLE);
            mLeftIcon.setBackgroundResource(R.drawable.ic_gome_icon_cancel);
            if (mSelectInfos.size() < mPocketInfos.size()) {
                isCheckAll = true;
            } else {
                isCheckAll = false;
            }
            mRightText.setSelected(!isCheckAll);

//            mRightText.setBackground(isCheckAll ? getDrawable(R.drawable.ic_gome_sys_ic_check_box_1)
//                    : getDrawable(R.drawable.ic_gome_sys_ic_check_box_2));
            mRightText.setChecked(isCheckAll ? false : true);
            if (mSelectInfos.size() == 0) {
                mTextTitle.setText(getString(R.string.please_check_item));
            } else {
                mTextTitle.setText(getString(R.string.history_select) + " " + mSelectInfos.size() + " " + getString(R.
                        string.item));
            }
            if (null == mSelectInfos || mSelectInfos.size() == 0) {
                mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_recover);
                mFrameLayoutBottom.setMenuItemsDisable(R.id.tv_delete);
            } else {
                mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_recover);
                mFrameLayoutBottom.setMenuItemsEnable(R.id.tv_delete);
            }
            mTvLeftTitle.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        mTvLeftTitle.setVisibility(View.VISIBLE);
        if (mRightText.isShown()) {
            mRightText.setVisibility(View.GONE);
            mLeftIcon.setBackgroundResource(R.drawable.ic_gome_icon_back);
            mTextTitle.setText("");
            mFrameLayoutBottom.setVisibility(View.GONE);
            mSelectInfos.clear();
            mHistoryAdapter.clearCheckStatusMap();
            mHistoryAdapter.showDeleteIcon(false);
        } else {
            finish();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mDialog) {
            mDialog.dismiss();
            mDialog = null;
        }

        if (null != mSelectInfos) {
            selectCount = mSelectInfos.size();
        }

        Intent intent = new Intent();
        intent.setAction("update home adapter");
        sendBroadcast(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSelectInfos.clear();
        if (mHistoryAdapter != null) {
            mHistoryAdapter.getMap().clear();
            mHistoryAdapter.setItemClickListener(null);
        }
        isShowDeleteIcon = false;
    }

    @Override
    public boolean onFloatActionItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.tv_recover:
                if (mSelectInfos.size() > 0) {
                    showBottomDialog(mContext, ShowStyle.RECOVER_STYLE, ShowStyle.RECOVER_STYLE_BOTTOM);
                }
                break;

            case R.id.tv_delete:
                if (mSelectInfos.size() > 0) {
                    showBottomDialog(mContext, ShowStyle.DELETE_STYLE, ShowStyle.DELETE_STYLE_BOTTOM);
                }
                break;


        }
        return false;
    }


    public void showBottomDialog(Context context, int style, int type) {
        if (null != mDialog && mDialog.isShowing()) {
            return;
        }

        mType = type;
        //mDialog = new Dialog(context, R.style.ActionSheetDialogStyle);
        CustomAlertDialog.Builder alertDialog = new CustomAlertDialog.Builder(context);
        //fill the layout of dialog
        inflate = LayoutInflater.from(context).inflate(R.layout.delete_dialog_layout_recover, null);
        //init the view
        mButtonCancel = (TextView) inflate.findViewById(R.id.btn_cancel);
        mContent = (TextView) inflate.findViewById(R.id.tv_content);
        mTitle = (TextView) inflate.findViewById(R.id.tv_title);
        mButtonRight = (TextView) inflate.findViewById(R.id.btn_confirm);
        if (style == ShowStyle.RECOVER_STYLE) {
            if (mSelectInfos.size() == 1) {
                mTitle.setText(R.string.history_is_recover);
            } else {
                mTitle.setText(R.string.history_is_recovers);
            }
            mButtonRight.setText(R.string.history_dialog_recover);
            mContent.setVisibility(View.GONE);
        } else {
            //mContent.setText(R.string.history_is_delete_content);
            //mTitle.setText(mContext.getResources().getString(R.string.history_is_delete,
            //        mSelectInfos.size() == 0 ? 1 : mSelectInfos.size()));
            if (mSelectInfos.size() == 1) {
                mContent.setText(R.string.confirm_delete_this_note);
            } else {
                mContent.setText(R.string.confirm_delete_these_notes);
            }
            mTitle.setText(mContext.getResources().getString(R.string.thorough_delete));
            mButtonRight.setText(R.string.confirm);
            //mButtonRight.setTextColor(context.getResources().getColor(R.color.common_red_color));
        }
        //set the layout of dialog
        alertDialog.setView(inflate);
        mDialog = alertDialog.create();
        mDialog.show();
        mButtonCancel.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);
    }
}
