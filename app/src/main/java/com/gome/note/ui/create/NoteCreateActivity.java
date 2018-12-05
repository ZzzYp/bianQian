package com.gome.note.ui.create;

import android.Manifest;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gome.note.R;
import com.gome.note.ShareImage.ShareImageActivity;
import com.gome.note.base.BaseActivity;
import com.gome.note.base.Config;
import com.gome.note.camera.ImageGridActivity;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.BackgroundItemInfo;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.entity.RecordPool;
import com.gome.note.keyboard.KeyboardHeightObserver;
import com.gome.note.keyboard.KeyboardHeightProvider;
import com.gome.note.manager.AudioPlayManager;
import com.gome.note.manager.ViewManager;
import com.gome.note.service.RecordServiceBinder;
import com.gome.note.ui.create.adapter.BackgroundItemsAdapter;
import com.gome.note.ui.create.presenter.NoteCreatePresenter;
import com.gome.note.ui.home.NoteHomeActivity;
import com.gome.note.ui.label.LabelManagerActivity;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.utils.DpUtils;
import com.gome.note.utils.FileUtils;
import com.gome.note.utils.SetResizeHeigth;
import com.gome.note.utils.ShareHelper;
import com.gome.note.utils.SharedPreferencesUtil;
import com.gome.note.utils.SystemUtils;
import com.gome.note.utils.XunFeiSpeechUtils;
import com.gome.note.view.AlertDialog.CustomAlertDialog;
import com.gome.note.view.ColoredLinearyLayout;
import com.gome.note.view.ImageAndCameraDialog;
import com.gome.note.view.RecordDialog;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


import static com.gome.note.db.config.NoteConfig.NEED_CAMERA;
import static com.gome.note.db.config.NoteConfig.REQUEST_LABELS;
import static com.lzy.imagepicker.ImagePicker.REQUEST_CODE_PREVIEW;

public class NoteCreateActivity extends BaseActivity implements View.OnClickListener, KeyboardHeightObserver,
        XunFeiSpeechUtils.SpeechHelperListener, SetValueToActivityListener,
        PresentToActivityListener, BackgroundItemsAdapter.ItemOnClickListener, DialogActionToActivityListener {
    private Context mContext;
    private String TAG = "NoteCreateActivity";
    private NoteCreatePresenter presenter;
    private NoteCreateActivity mActivity;
    private TextView mTvMenuMore, mTvMenuLable, mIvAddMark, mTvMenuDone;
    private ImageView mIvMenuBack, mIvSelectImage, mIvImagePhoto, mIvImageAddRecord;
    private ColoredLinearyLayout dynamicContainer;
    private LinearLayout mStyleGroupLinearLayout, mLlSelectImage, mLlImagePhoto, mLlImageAddRecord, mLlAddMark;
    private ProgressBar contentLoadingView;
    private PopupWindow mMorepopupWindow;
    private int TYPE_ADD_LABELS = 1;
    private ArrayList<LabelInfo> mItemLabelInfos, mItemLabelInfosFrom = new ArrayList<>();
    private ArrayList<LabelInfo> itemLabelInfos = null;
    private PocketInfo mPocketInfo;
    private boolean isKeyboardShowing;
    //keyboardHeightProvider when keyboard change notify layout
    private KeyboardHeightProvider keyboardHeightProvider;
    private long mId;
    private ScrollView mContentMemoEdit;
    private TextView mDetailDelete;
    private RecordDialog recordDialog;
    private Intent serviceIntent;
    private RecordServiceBinder serviceHelper;
    private String audioPath;
    private boolean openAudio;
    private String mPicPath;
    private LinearLayout mLlNoteDynamicContainer;
    private CustomAlertDialog dialog;
    private AudioPlayManager audioPlayManager;
    private String text;
    private boolean mShare;
    private String mShareContent;
    private String mSharePicPath;
    private boolean isDoEdit;
    private boolean isLabelChange;
    private SetValueToActivityListener setValueToActivityListener = null;
    private PresentToActivityListener presentToActivity = null;
    private AudioRecord mRecorder;
    private TelephonyManager manager;
    private Toast mToastRecord;

    private boolean mInMultiWindow;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock = null;
    private RelativeLayout mLlContentMemoEdit;
    private XunFeiSpeechUtils xunFeiSpeechUtils;
    private View keyboardHideOrShowFocusView;
    private ViewManager viewManager;
    private LinearLayout mLlInsertBackground;
    private LinearLayout mLlBackgroundItemsLayout;
    private RecyclerView mRecyclerviewBackgroundItems;
    private ArrayList<BackgroundItemInfo> mBackgroundItemInfos;
    private BackgroundItemsAdapter mBackgroundItemsAdapter;
    private BackgroundItemsAdapter.ItemOnClickListener mItemOnLongClickListener = null;
    private RelativeLayout mContentMemoEditBg;
    private RelativeLayout mRlCreatRootView;
    private ImageView mIvFootBg;
    private ImageView mInsertBackground;
    private View mStyleGroupLine;
    private String skinBgName;
    private int skinBgId;
    private int skinBgColorId;
    private boolean isInbetweening;
    private int currentHeadIconColorResId;
    private int skinBgPositon;
    private ImageAndCameraDialog mImageAndCameraDialog;
    private boolean mIsFromLauncher;
    private AsyncTask mAsyncTaskGetLabelInfos;
    private LinearLayout mClCoordinatorLayout;
    private View mTitlteBarLine;
    private boolean isContentSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setImmersionBar();
        skinBgName = getResources().getString(R.string.standard_bg);
        skinBgId = getResources().getInteger(R.integer.skin_bg_id_standard);
        skinBgColorId = R.color.white;
        skinBgPositon = 0;

        setContentView(R.layout.activity_create_note);
        //solve "adjustResize" and immersion Titlebar`s conflict
        SetResizeHeigth.assistActivity(findViewById(android.R.id.content));

        mContext = this;
        mActivity = this;
        keyboardHeightProvider = new KeyboardHeightProvider(this);
        if (null != keyboardHeightProvider) {
            keyboardHeightProvider.setKeyboardHeightObserver(this);
        }
        mInMultiWindow = isInMultiWindowMode();
        Intent intent = getIntent();
        mId = intent.getLongExtra("id", -1);
        mIsFromLauncher = intent.getBooleanExtra(NoteConfig.FROM_LAUNCHER, false);
        if (mId == -1) {
            isDoEdit = true;
            NoteConfig.inMultiWindowDoEditStatus = isDoEdit;
        }
        text = intent.getStringExtra("text");
        if (intent.getType() != null) {
            mShare = intent.getType().equals("text/plain");
        }

        mShareContent = intent.getStringExtra(Intent.EXTRA_TEXT);
        mSharePicPath = intent.getStringExtra("path");

        //if stringcontent and image from other`s share, init NoteConfig.inMultiWindowNoteId = -1
        initMultiWindowNoteId();

        openAudio = intent.getBooleanExtra("openAudio", false);

        mPicPath = intent.getStringExtra("picpath");

        viewManager = new ViewManager();
        viewManager.setValueToActivityListener(this);
        if (null != viewManager.getNodeList()) {
            viewManager.getNodeList().clear();
        }
        powerManager = (PowerManager) mContext.getApplicationContext().getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");

        initPresenter();
        initSkinBg();
        initView();
        initListener();


    }

    private void initMultiWindowNoteId() {
        if ((null != mShareContent && mShareContent.length() > 0)
                || (null != mSharePicPath && mSharePicPath.length() > 0)) {
            NoteConfig.inMultiWindowNoteId = -1;
        }
    }


    private void setImmersionBar() {

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    private void initView() {

        mTvMenuMore = (TextView) findViewById(R.id.tv_menu_more);
        mTvMenuLable = (TextView) findViewById(R.id.tv_menu_lable);
        mIvSelectImage = (ImageView) findViewById(R.id.iv_select_image);
        mIvImagePhoto = (ImageView) findViewById(R.id.image_photo);
        mIvImageAddRecord = (ImageView) findViewById(R.id.image_add_record);
        mIvAddMark = (TextView) findViewById(R.id.iv_add_mark);
        dynamicContainer = (ColoredLinearyLayout) findViewById(R.id.note_dynamic_container);
        contentLoadingView = (ProgressBar) findViewById(R.id.content_loading);
        mStyleGroupLinearLayout = (LinearLayout) findViewById(R.id.ll_style_group);
        mContentMemoEdit = (ScrollView) findViewById(R.id.content_memo_edit);
        mDetailDelete = (TextView) findViewById(R.id.detail_delete);
        mTvMenuDone = (TextView) findViewById(R.id.tv_menu_done);

        mLlSelectImage = (LinearLayout) findViewById(R.id.ll_select_image);
        mLlImagePhoto = (LinearLayout) findViewById(R.id.ll_image_photo);
        mLlImageAddRecord = (LinearLayout) findViewById(R.id.ll_image_add_record);
        mLlAddMark = (LinearLayout) findViewById(R.id.ll_add_mark);
        mLlInsertBackground = (LinearLayout) findViewById(R.id.ll_insert_background);
        mInsertBackground = (ImageView) findViewById(R.id.insert_background);

        mLlNoteDynamicContainer = (LinearLayout) findViewById(R.id.ll_note_dynamic_container);
        mLlContentMemoEdit = (RelativeLayout) findViewById(R.id.ll_content_memo_edit);

        mLlBackgroundItemsLayout = (LinearLayout) findViewById(R.id.ll_background_items_layout);
        mRecyclerviewBackgroundItems = (RecyclerView) findViewById(R.id.recyclerview_background_items);

        mContentMemoEditBg = (RelativeLayout) findViewById(R.id.content_memo_edit_bg);
        mRlCreatRootView = (RelativeLayout) findViewById(R.id.rl_creat_root_view);
        mIvFootBg = (ImageView) findViewById(R.id.iv_foot_bg);
        mStyleGroupLine = (View) findViewById(R.id.style_group_line);

        mClCoordinatorLayout = (LinearLayout) findViewById(R.id.cl_coordinatorLayout);
        mTitlteBarLine = findViewById(R.id.titlte_bar_line);

        mIvMenuBack = (ImageView) findViewById(R.id.iv_menu_back);

        mIvMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveContent();
//                if ((null != mShareContent && mShareContent.length() > 0)
//                        || (null != mSharePicPath && mSharePicPath.length() > 0)) {
//                    Intent intent = new Intent(mContext, NoteHomeActivity.class);
//                    startActivity(intent);
//                    overridePendingTransition(R.anim.gome_activity_close_enter, R.anim.gome_activity_close_exit);
//                }
                finish();

            }
        });

        mLlContentMemoEdit.post(new Runnable() {
            @Override
            public void run() {
                if (null != keyboardHeightProvider) {
                    keyboardHeightProvider.start();
                }
            }
        });

        //MultiWindow mode
        if (NoteConfig.inMultiWindowNoteId > 0 && !mIsFromLauncher) {
            mId = NoteConfig.inMultiWindowNoteId;
        }


        initDynamicContainer(mId);
    }


    @Override
    public void withoutData() {
        initData();
    }

    @Override
    public void setNoteInfoToActivity(PocketInfo pocketInfo) {
        ViewManager.pocketInfo = pocketInfo;
        if (null != mActivity && !mActivity.isDestroyed()) {
            viewManager.showDataFromInfos(pocketInfo, dynamicContainer);
        }

        if (mId > 0) {
            // preview  pattern
            accessPreviewMode();
        }
        if (null != RecordPool.map) {
            RecordPool.map.clear();
        }
        initData();
    }


    private void initData() {

        setLabelInfosToCurrentPage();

        if (openAudio) {
            if ((NoteConfig.onCreateCount++) == 0) {
                addRecord();
            } else {
                openAudio = false;
            }
        }
        if (!TextUtils.isEmpty(mPicPath) && !isSamePicPath()) {
            setImageToCurrentPage(mPicPath, 0);
        }

        if (!TextUtils.isEmpty(mSharePicPath)) {
            setImageToCurrentPage(mSharePicPath, 2);
        }
        if (NoteConfig.inMultiWindowDoEditStatus) {
            accessEditMode(true);
        }

    }

    private void setImageToCurrentPage(String picPath, int cunstomSampleSize) {
        if (null == picPath) {
            return;
        }
        String copyPath = getimage(picPath, 0, cunstomSampleSize);
        viewManager.addImageCouple(copyPath, dynamicContainer);
        accessEditMode(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    }

    private boolean isSamePicPath() {
        if (null != mPicPath && mPicPath.length() > 0
                && null != NoteConfig.sPicPath && mPicPath.equals(NoteConfig.sPicPath)) {
            return true;
        }
        return false;
    }

    private void setLabelInfosToCurrentPage() {


        mAsyncTaskGetLabelInfos = new AsyncTask<String, Integer, PocketInfo>() {

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected PocketInfo doInBackground(String... params) {
                if (mId > 0) {
                    PocketInfo tempPocketInfo = PocketDbHandle.queryPocketInfoById(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET, mId);
                    return tempPocketInfo;
                }
                return null;
            }

            @Override
            protected void onPostExecute(PocketInfo tempPocketInfo) {
                mPocketInfo = tempPocketInfo;
                initLabelInfosToCurrentPage();
            }
        }.execute();

    }

    private void initLabelInfosToCurrentPage() {
        if (mPocketInfo != null) {
            itemLabelInfos = (ArrayList<LabelInfo>) mPocketInfo.getLabels();
        }
        if (null == itemLabelInfos) {
            itemLabelInfos = new ArrayList<>();
        }

        Iterator<LabelInfo> it = itemLabelInfos.iterator();
        while (it.hasNext()) {
            LabelInfo labelInfo = it.next();
            if (labelInfo.getId() == 0) {
                it.remove();
            }
        }

        setItemLabelInfosToViewManager(itemLabelInfos);

        int count = itemLabelInfos.size();
        for (int i = 0; i < itemLabelInfos.size(); i++) {
            boolean isStick = itemLabelInfos.get(i).isStick();
            if (isStick) {
                count = count - 1;
            }
        }

        if (count > 99) {
            mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_tag_number));
            setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
            mTvMenuLable.setText(R.string.nine_plus);
            setTexViewtColor(mTvMenuLable, currentHeadIconColorResId);

        } else {
            if (count == 0) {
                mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.gome_icon_add_tag));
                setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                mTvMenuLable.setText("");
            } else {
                mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_tag_number));
                setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                mTvMenuLable.setText(count + "");
                setTexViewtColor(mTvMenuLable, currentHeadIconColorResId);
            }
        }
    }


    private void setItemLabelInfosToViewManager(ArrayList<LabelInfo> itemLabelInfos) {
        if (null != itemLabelInfos) {
            ViewManager.mItemLabelInfos.clear();
            for (int i = 0; i < itemLabelInfos.size(); i++) {
                LabelInfo labelInfo = itemLabelInfos.get(i);
                ViewManager.mItemLabelInfos.add(labelInfo);
            }
        }
    }


    private void initListener() {
        mTvMenuMore.setOnClickListener(this);
        mTvMenuLable.setOnClickListener(this);
        mDetailDelete.setOnClickListener(this);

        mLlSelectImage.setOnClickListener(this);
        mLlImagePhoto.setOnClickListener(this);
        mLlImageAddRecord.setOnClickListener(this);
        mLlAddMark.setOnClickListener(this);
        mTvMenuDone.setOnClickListener(this);
        mLlNoteDynamicContainer.setOnClickListener(this);
        mLlInsertBackground.setOnClickListener(this);
        mLlContentMemoEdit.setOnClickListener(this);

        //viewManager.setValueToActivityListener(this);
        dynamicContainer.setValueToActivityListener(this);
        presentToActivity = this;
        presenter.setPresnetToActivityListener(presentToActivity);
        mItemOnLongClickListener = this;
    }


    @Override
    public void initPresenter() {
        presenter = new NoteCreatePresenter(mContext);
    }


    private void initSkinBg() {

        mBackgroundItemInfos = presenter.setBackgroundData(mContext.getApplicationContext());

    }


    private void initDynamicContainer(long updateId) {

        viewManager.initView(presenter, mContext.getApplicationContext(),
                updateId, mShareContent == null ? "" : mShareContent, dynamicContainer,
                () -> contentLoadingView.setVisibility(View.VISIBLE),
                () -> {
                    contentLoadingView.setVisibility(View.GONE);
                });

    }


    @Override
    protected void onResume() {
        super.onResume();

    }


    private void saveContent() {
        isContentSaved = true;
        stopPlayAudio();
        if (isDoEdit) {
            keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
            KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
            viewManager.exitCurrentPage(mId, mContext.getApplicationContext(),
                    dynamicContainer, null, false, skinBgId);
        } else {
            if (isLabelChange) {
                viewManager.exitCurrentPage(mId, mContext.getApplicationContext(),
                        dynamicContainer, null, false, skinBgId);
            }
        }
    }

    private void previewSaveContent() {
        isContentSaved = true;
        stopPlayAudio();
        if (isDoEdit) {
            keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
            KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
            viewManager.previewSaveContent(mId, mContext.getApplicationContext(),
                    dynamicContainer, null, false, skinBgId);
        } else {
            if (isLabelChange) {
                viewManager.previewSaveContent(mId, mContext.getApplicationContext(),
                        dynamicContainer, null, false, skinBgId);
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        saveContent();
//        if ((null != mShareContent && mShareContent.length() > 0)
//                || (null != mSharePicPath && mSharePicPath.length() > 0)) {
//            Intent intent = new Intent(mContext, NoteHomeActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.gome_activity_close_enter, R.anim.gome_activity_close_exit);
//
//        }
        finish();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (null != mImageAndCameraDialog && mImageAndCameraDialog.isShowing()) {
            mImageAndCameraDialog.cancel();
        }
        if (mInMultiWindow) {
            keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
            KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
        }

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (openAudio && !mInMultiWindow) {
            recordCancel();
        }

        stopPlayAudio();

        if (null != recordDialog && recordDialog.isShowing()) {
            recordDialog.setActivityNull();
            recordDialog.cancel();
            openAudio = false;
        }
        if (null != serviceHelper) {
            serviceHelper.stopService();
        }
        keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
        KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
//        if (isDoEdit) {
//            viewManager.saveContentInfo(mId, mContext.getApplicationContext(), dynamicContainer, false, skinBgId);
//        } else if (isLabelChange) {
//            viewManager.saveContentInfo(mId, mContext.getApplicationContext(), dynamicContainer, false, skinBgId);
//        }
        if (!isContentSaved && (isDoEdit || isLabelChange || mInMultiWindow)) {
            viewManager.saveContentInfo(mId, mContext.getApplicationContext(), dynamicContainer, false, skinBgId);
        }
    }

    private void stopPlayAudio() {
        audioPlayManager = AudioPlayManager.getInstance();
        if (null != audioPlayManager) {
            audioPlayManager.stopPlay();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != RecordPool.map) {
            RecordPool.map.clear();
        }
        if (!mIsFromLauncher) {
            NoteConfig.inMultiWindowNoteId = mId;
        }
        NoteConfig.sPicPath = mPicPath;
        if (null != keyboardHeightProvider) {
            keyboardHeightProvider.close();
        }
        isDoEdit = false;
        isLabelChange = false;
        NoteConfig.ISPREVIEWMODE = false;
        keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
        KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
        if (null != xunFeiSpeechUtils) {
            xunFeiSpeechUtils.releaseSource();
        }

        if (keyboardHideOrShowFocusView != null) {
            keyboardHideOrShowFocusView = null;
        }

        if (null != viewManager) {
            viewManager.setValueToActivityListener(null);
        }

        if (null != dynamicContainer) {
            dynamicContainer.setValueToActivityListener(null);
            dynamicContainer.setNullListener();
        }

        audioPlayManager = AudioPlayManager.getInstance();

        if (null != audioPlayManager) {
            audioPlayManager.setAnimationDrawable(null);
            audioPlayManager.setAudioTrackStopPlayListener(null);
            audioPlayManager.setImageView(null);
        }

        if (null != presenter) {
            presenter.destory();
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (null != mAsyncTaskGetLabelInfos) {
            mAsyncTaskGetLabelInfos.cancel(true);
        }

        if ((null != mShareContent && mShareContent.length() > 0)
                || (null != mSharePicPath && mSharePicPath.length() > 0)) {
            //android.os.Process.killProcess(android.os.Process.myPid());

//            int pid = android.os.Process.myPid();
//            String command = "kill -9 " + pid;
//            try {
//                Runtime.getRuntime().exec(command);
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
            System.exit(0);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_menu_more:

                //change share type
                changeShareType(false);

                break;
            case R.id.tv_menu_lable:
                stopPlayAudio();
                dissMissPopu();
                isLabelChange = true;
                Intent intent = new Intent(mContext, LabelManagerActivity.class);
                intent.putExtra("id", mId);
                intent.putParcelableArrayListExtra("itemLabelInfos", itemLabelInfos);
                intent.putExtra("type", TYPE_ADD_LABELS);
                startActivityForResult(intent, REQUEST_LABELS);

                break;

            case R.id.ll_select_image:
                //stopPlayAudio();
                //capturePhoto();

                break;
            case R.id.ll_image_photo:
                //pop-up dialog
                popUpDialog();

                //stopPlayAudio();
                //openGallery();

                break;
            case R.id.ll_image_add_record:
                //add  estimate of source is userd by other app
                if (!captureAudio()) {
                    return;
                }

                // is ScreenRecording
                if (ActivityCommonUtils.isMediaRecorderUsered(mContext.getApplicationContext())) {
                    showToast(mContext.getString(R.string.record_function_is_used));
                    return;
                }


                if (!phoneIsInUse(mContext)) {
                    addRecord();
                } else {
                    //Toast.makeText(mContext, mContext.getString(R.string.mic_or_audio_is_in_use), Toast.LENGTH_SHORT).show();
                    showToast(mContext.getString(R.string.mic_or_audio_is_in_use));
                }

                break;

            case R.id.ll_add_mark:

                changeColoredLinearyLayoutEditTextColor(skinBgPositon);
                viewManager.setCheckBox(dynamicContainer);

                break;
            case R.id.ll_insert_background:

                showBackgroundSeleteItem();

                break;


            case R.id.detail_delete:
                stopPlayAudio();

                boolean isNotShowDiag = SharedPreferencesUtil.getBooleanValue(mContext.getApplicationContext(),
                        SharedPreferencesUtil.DELETE_CREATE_DIALOG_NOT_ALERT, false);
                if (!isNotShowDiag) {
                    removeLabelDialog();
                } else {
                    removeItem();
                }

                break;

            case R.id.tv_menu_done:
                //preview mode switch
                accessPreviewMode();
                //mLlBackgroundItemsLayout.setVisibility(View.GONE);
                setLlBackgroundItemsLayoutState(View.GONE);
                NoteConfig.inMultiWindowDoEditStatus = false;
                //save note
                if (mId <= 0) {
                    previewSaveContent();
                }

                break;

            case R.id.ll_note_dynamic_container:
                //in edit mode
                accessEditMode(true);

                break;
            case R.id.ll_content_memo_edit:
                //access edit mode
                accessEditMode(true);


                break;

        }
    }

    private void showToast(String toastStr) {
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

    private void showBackgroundSeleteItem() {
        //mLlBackgroundItemsLayout.setVisibility(View.VISIBLE);
        //mStyleGroupLinearLayout.setVisibility(View.GONE);
        setLlBackgroundItemsLayoutState(View.VISIBLE);
        setStyleGroupVisibilityState(View.GONE);

        if (null == mBackgroundItemInfos || mBackgroundItemInfos.size() == 0) {
            mBackgroundItemInfos = presenter.setBackgroundData(mContext.getApplicationContext());
        }
        if (null == mBackgroundItemsAdapter) {
            mBackgroundItemsAdapter = new BackgroundItemsAdapter(mContext, mBackgroundItemInfos, mItemOnLongClickListener);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setOrientation(LinearLayout.HORIZONTAL);
            mRecyclerviewBackgroundItems.setLayoutManager(mLayoutManager);
            mRecyclerviewBackgroundItems.setAdapter(mBackgroundItemsAdapter);
        } else {
            mBackgroundItemsAdapter.setData(mBackgroundItemInfos);
            mBackgroundItemsAdapter.notifyDataSetChanged();
        }

    }


    private void popUpDialog() {
        stopPlayAudio();
        keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
        KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);

        mImageAndCameraDialog = new ImageAndCameraDialog(mContext, R.style.MyGomeDialog);
        mImageAndCameraDialog.setDialogActionToActivityListener(this);
        Window window = mImageAndCameraDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        mImageAndCameraDialog.show();
    }

    @Override
    public void clickCamera() {
        capturePhoto();
    }

    @Override
    public void clickImage() {
        openGallery();
    }

    private void addRecord() {
        //where record is usered by other apps ,get focus and release
        getRecordFocusAndRelease();

        keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
        KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
        //start record
        startRecord();
    }

    private void startRecord() {
        stopPlayAudio();

        if (null != wakeLock) {
            wakeLock.acquire();
        }
        recordDialog = new RecordDialog(mContext, R.style.MyDialog, mActivity, mHandler);

        Window window = recordDialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setGravity(Gravity.TOP);
        lp.y = DpUtils.dp2Px(mContext.getApplicationContext(), 146);
        window.setAttributes(lp);
        recordDialog.show();
        //start service
        File file = createFilePath("pcm", Config.PATH_CACHE_AUDIOS);
        audioPath = file.getAbsolutePath();
        if (TextUtils.isEmpty(audioPath)) {
            return;
        }
        serviceHelper = new RecordServiceBinder(mContext.getApplicationContext());
        serviceHelper.bindservice(audioPath);

    }

    public void recordComplete() {
        openAudio = false;
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
        }
        long time = 0;
        if (null != serviceHelper) {
            serviceHelper.stopService();
        }
        if (null != recordDialog) {
            time = recordDialog.getTime();
            recordDialog.setActivityNull();
            recordDialog.cancel();
        }

        File file = new File(audioPath);

        if (null != audioPath && null != file && file.exists() && file.length() > 0) {
            viewManager.addAudioCouple(audioPath, dynamicContainer, time);

            //is has net
            if (SystemUtils.isNetworkConnected(mContext.getApplicationContext())) {

                if (RecordPool.map.size() == 1) {
                    viewManager.setTranslateProgress(audioPath, dynamicContainer);
                    //get translate result
                    if (xunFeiSpeechUtils == null) {
                        xunFeiSpeechUtils = new XunFeiSpeechUtils();
                    }
                    xunFeiSpeechUtils.startTranslation(mContext, audioPath, this);
                }
            } else {
                RecordPool.map.clear();
            }
        } else {
            RecordPool.map.clear();
        }
    }


    public void recordCancel() {
        if (null != recordDialog && recordDialog.isShowing()) {
            recordDialog.setActivityNull();
            recordDialog.cancel();
        }
        if (null != serviceHelper) {
            serviceHelper.stopService();
        }
        if (openAudio) {
            viewManager.removeEditTextFocus(dynamicContainer);
            finish();
        }
    }

    @Override
    public void getTranslateResults(String result, boolean isError, String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewManager.stopTranslateProgress(path, dynamicContainer, result, isError);
                //is has record not Translated
                if (null != RecordPool.map && RecordPool.map.size() > 0) {
                    if (isError) {
                        removeRecordPoolItem(path);
                    }
                    recordpoolGoOnTranslate(path);
                }
            }
        });
    }

    private void removeRecordPoolItem(String path) {
        Iterator<Map.Entry<Integer, String>> maps = RecordPool.map.entrySet().iterator();
        while (maps.hasNext()) {

            Map.Entry<Integer, String> entry = maps.next();
            int id = entry.getKey();
            String recordPath = entry.getValue();
            if (null != recordPath && null != path && recordPath.equals(path)) {
                maps.remove();
            }
        }

    }

    private void recordpoolGoOnTranslate(String path) {
        Iterator<Map.Entry<Integer, String>> maps = RecordPool.map.entrySet().iterator();
        while (maps.hasNext()) {

            Map.Entry<Integer, String> entry = maps.next();

            int id = entry.getKey();
            String recordPath = entry.getValue();
            if (null != recordPath && null != path && recordPath.equals(path)) {
                maps.remove();
            }
        }

        if (RecordPool.map.size() > 0) {
            //go on Translate
            String mapFirstPath = getFirstValueFromMap(RecordPool.map);
            if (null != mapFirstPath && mapFirstPath.length() > 0) {
                viewManager.setTranslateProgress(mapFirstPath, dynamicContainer);
                //get translate result
                if (xunFeiSpeechUtils == null) {
                    xunFeiSpeechUtils = new XunFeiSpeechUtils();
                }
                xunFeiSpeechUtils.startTranslation(mContext, mapFirstPath, this);
            }
        }

    }

    private String getFirstValueFromMap(Map<Integer, String> map) {

        Iterator<Map.Entry<Integer, String>> maps = RecordPool.map.entrySet().iterator();
        while (maps.hasNext()) {

            Map.Entry<Integer, String> entry = maps.next();

            int id = entry.getKey();
            String recordPath = entry.getValue();
            if (null != recordPath && recordPath.length() > 0) {
                return recordPath;
            }
        }

        return "";

    }


    private void removeLabelDialog() {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View dialogCheckbox = factory.inflate(R.layout.dialog_delete_to_history, null);
        final CheckBox checkboxs = (CheckBox) dialogCheckbox.findViewById(R.id.checkbox);
        final TextView tvCheckString = (TextView) dialogCheckbox.findViewById(R.id.tv_check_string);
        tvCheckString.setText(R.string.even_not_alert);
        dialog = new CustomAlertDialog.Builder(mContext)
                .setView(dialogCheckbox)
                .setCancelable(true)
                .setTitle(R.string.dialog_delete_title)
                .setMessage(R.string.dialog_history_title)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkboxs.isChecked()) {
                            //no alert
                            SharedPreferencesUtil.saveBooelanValue(mContext.getApplicationContext(),
                                    SharedPreferencesUtil.DELETE_CREATE_DIALOG_NOT_ALERT, true);
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
                }).show();
    }


    //delete Item
    public void removeItem() {
        if (mId > 0) {
            PocketInfo pocketInfo = ViewManager.pocketInfo;
            if (null == pocketInfo || pocketInfo.getId() == 0 || pocketInfo.getId() != mId) {
                pocketInfo = new PocketInfo();
                pocketInfo.setId(mId);
            }
            deleteNoteInfo(pocketInfo);
        } else {
            finish();
        }

    }


    public void deleteNoteInfo(PocketInfo pocketInfo) {
        presenter.deleteNoteInfo(pocketInfo);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (null != data) {
            if (resultCode == RESULT_OK) {
                itemLabelInfos = data.getParcelableArrayListExtra("itemLabelInfos");
                if (null != itemLabelInfos) {
                    int count = itemLabelInfos.size();
                    for (int i = 0; i < itemLabelInfos.size(); i++) {
                        boolean isStick = itemLabelInfos.get(i).isStick();
                        if (isStick) {
                            count = count - 1;
                        }
                    }

                    if (count > 99) {
                        mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_tag_number));
                        setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                        mTvMenuLable.setText(R.string.nine_plus);
                        setTexViewtColor(mTvMenuLable, currentHeadIconColorResId);
                    } else {
                        if (count == 0) {
                            mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.gome_icon_add_tag));
                            setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                            mTvMenuLable.setText("");
                        } else {
                            mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.ic_gome_sys_ic_tag_number));
                            setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                            mTvMenuLable.setText(count + "");
                            setTexViewtColor(mTvMenuLable, currentHeadIconColorResId);
                        }
                    }
                } else {
                    mTvMenuLable.setBackground(mContext.getDrawable(R.drawable.gome_icon_add_tag));
                    setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
                    mTvMenuLable.setText("");
                }

                setItemLabelInfosToViewManager(itemLabelInfos);
            }

            switch (resultCode) {
                case ImagePicker.RESULT_CODE_ITEMS:
                    if (requestCode == NoteConfig.REQUEST_CODE_SELECT) {
                        ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker
                                .EXTRA_RESULT_ITEMS);
                        if (null != images) {
                            for (int i = 0; i < images.size(); i++) {
                                String copyPath = getimage(images.get(i).path, 0, 0);
                                if (null != copyPath && copyPath.length() > 0) {
                                    viewManager.addImageCouple(copyPath, dynamicContainer);
                                }
                            }
                        }
                        // change share type
                        changeShareType(true);
                    }
                    break;
                case ImagePicker.RESULT_CODE_BACK:
                    if (requestCode == REQUEST_CODE_PREVIEW) {
                        ArrayList images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker
                                .EXTRA_IMAGE_ITEMS);
                    }
                    break;
            }
        }
        if (resultCode != RESULT_OK) {
            return;
        }
        String willCopyPath = null;
        String destPath = null;
        String destDir = null;
        switch (requestCode) {
            case NoteConfig.REQUEST_CAMERA:
                if (null != this.cameraFile) {
                    willCopyPath = this.cameraFile.getAbsolutePath();
                    if (FileUtils.isImage(willCopyPath)) {
                        destDir = Config.getCachePath(Config.TYPE_PATH_CACHE_IMAGES);
                        destPath = Config.getCachePathCache(willCopyPath, destDir);
                    } else if (FileUtils.isVideo(willCopyPath)) {
                        destDir = Config.getCachePath(Config.TYPE_PATH_CACHE_VIDEOS);
                        destPath = Config.getCachePathCache(willCopyPath, destDir);
                    }
                }

                break;

            case NoteConfig.REQUEST_CODE_SELECT:
                if (FileUtils.isImage(willCopyPath)) {
                    destDir = Config.getCachePath(Config.TYPE_PATH_CACHE_IMAGES);
                    destPath = Config.getCachePathCache(willCopyPath, destDir);
                } else if (FileUtils.isVideo(willCopyPath)) {
                    destDir = Config.getCachePath(Config.TYPE_PATH_CACHE_VIDEOS);
                    destPath = Config.getCachePathCache(willCopyPath, destDir);
                }
                break;
        }
        final int requestType = requestCode;
        final String _destPath = destPath;
        switch (requestType) {
            case NoteConfig.REQUEST_CAMERA:
                if (FileUtils.isImage(willCopyPath)) {
                    viewManager.addImageCouple(willCopyPath, dynamicContainer);

                }
                break;
        }
        if (null != willCopyPath && willCopyPath.length() > 0) {
            File file = new File(willCopyPath);
        }
    }


    public void capturePhoto() {
        //  xxxxxxxxxxxx for 7.0 !!!!
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, NEED_CAMERA);
        } else {
            keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
            KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
            openCamera(this);
        }
    }


    public boolean captureAudio() {
        //  xxxxxxxxxxxx for 7.0 !!!!
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case NoteConfig.NEED_CAMERA:
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
                        addRecord();
                    } else {
                        showToast(mContext.getString(R.string.mic_or_audio_is_in_use));
                    }
                }
                break;
        }
    }

    public void openGallery() {
        Intent intent1 = new Intent(mContext, ImageGridActivity.class);
        startActivityForResult(intent1, NoteConfig.REQUEST_CODE_SELECT);
    }


    private void changeShareType(boolean isInsertImage) {

        String text = presenter.getViewText(dynamicContainer);
        boolean isHasString = false;
        if (null == text || text.length() == 0) {
            isHasString = false;
        } else {
            isHasString = true;
        }
        //if has image
        if (isHasImage()) {
            shareItemStatus(isHasString, true, isInsertImage);
        } else {
            shareItemStatus(isHasString, false, isInsertImage);
        }

    }

    private boolean isHasImage() {

        return presenter.isHasImage(dynamicContainer);

    }


    private void shareItemStatus(boolean isHasString, boolean isHasImage, boolean isInsertImage) {

        if (!isInsertImage) {
            if (mMorepopupWindow != null && mMorepopupWindow.isShowing()) {
                mMorepopupWindow.dismiss();
                mMorepopupWindow = null;
            } else {
                showMorePopu(isHasString, isHasImage);
            }
        }
    }

    private void showMorePopu(boolean isHasString, boolean isHasImage) {
        //estimate activity whether or not exist
        if (mActivity == null || mActivity.isFinishing() || mActivity.isDestroyed()) {
            return;
        }
        mMorepopupWindow = new PopupWindow(this);
        mMorepopupWindow.setContentView(LayoutInflater.from(this).inflate(R.layout.layout_popupwindow_style01, null));
        setCurrentBackground(mMorepopupWindow);
        mMorepopupWindow.setWidth(DpUtils.dp2Px(this, mContext.getResources().getInteger(R.integer.more_popuwindow_width)));
        mMorepopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mMorepopupWindow.setFocusable(false);
        mMorepopupWindow.setOutsideTouchable(true);
        mMorepopupWindow.setElevation(DpUtils.dp2Px(this, 2));
        //get ContentView  Width
        mMorepopupWindow.getContentView().measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int i = mMorepopupWindow.getContentView().getMeasuredWidth();
        i = mContext.getResources().getInteger(R.integer.more_popu_padding);
        //popupwindow show
        mMorepopupWindow.showAsDropDown(mTvMenuMore,
                DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.more_popuwindow_xoff) - i),
                DpUtils.dp2Px(mContext, mContext.getResources().getInteger(R.integer.more_popuwindow_yoff)));
        mMorepopupWindow.getContentView().findViewById(R.id.rl_copy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copyText = getCopyText();
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(copyText);
                Toast.makeText(mContext, getString(R.string.copy_success), Toast
                        .LENGTH_SHORT).show();
                dissMissPopu();
            }
        });
        mMorepopupWindow.getContentView().findViewById(R.id.rl_share_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String shareText = getShareText();

                ShareHelper.shareText(NoteCreateActivity.this, shareText);
                dissMissPopu();
            }
        });
        mMorepopupWindow.getContentView().
                findViewById(R.id.rl_share_image).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stopPlayAudio();
                        shareContentWithImage();
                        dissMissPopu();
                    }
                });
        if (isHasString) {
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_text).setEnabled(true);
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_text).setAlpha(1.0f);
        } else {
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_text).setEnabled(false);
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_text).setAlpha(0.4f);
        }
        if (isHasImage) {
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_image).setEnabled(true);
            mMorepopupWindow.getContentView().findViewById(R.id.rl_share_image).setAlpha(1.0f);
            mMorepopupWindow.getContentView().findViewById(R.id.rl_copy).setVisibility(View.GONE);
        } else {
            if (isHasString) {
                mMorepopupWindow.getContentView().findViewById(R.id.rl_copy).setVisibility(View.VISIBLE);
            } else {
                mMorepopupWindow.getContentView().findViewById(R.id.rl_copy).setVisibility(View.GONE);
            }
        }
    }

    private void setCurrentBackground(PopupWindow popupWindow) {

        GradientDrawable gradientDrawable = (GradientDrawable) popupWindow.getContentView().getBackground();
        gradientDrawable.setColor(mContext.getColor(skinBgColorId));
        popupWindow.setBackgroundDrawable(gradientDrawable);
    }

    private String getCopyText() {


        return presenter.getViewText(dynamicContainer);
    }

    private String getShareText() {


        return presenter.getViewText(dynamicContainer);
    }


    private void shareContentWithImage() {
        //set bg type to shareImage
        presenter.shareContentWithImage(mContentMemoEdit, skinBgId, skinBgColorId, isInbetweening);
    }


    private void dissMissPopu() {
        if (mMorepopupWindow != null && mMorepopupWindow.isShowing()) {
            mMorepopupWindow.dismiss();
            mMorepopupWindow = null;
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {


        isKeyboardShowing = height > 0;
        if (height < 0) {
            height = height + Utils.getNavigationBarHeight(mContext);
        } else if (height > 0) {
            accessEditMode(false);
        }


    }


    private void getRecordFocusAndRelease() {
        ((AudioManager) getSystemService(AUDIO_SERVICE))
                .requestAudioFocus(null, AudioManager.STREAM_RING, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        mRecorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try {
            if (mRecorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                mRecorder.stop();
            }
            mRecorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mRecorder.release();
            mRecorder = null;
        }
    }

    public boolean phoneIsInUse(Context context) {
        boolean isInCall = false;
        if (null == manager) {
            manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        }

        int type = manager.getCallState();
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


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //get system language change status
        ((TextView) findViewById(R.id.iv_add_mark_text)).setText(R.string.detailed_list);
        ((TextView) findViewById(R.id.image_photo_text)).setText(R.string.image);
        ((TextView) findViewById(R.id.iv_select_image_text)).setText(R.string.camera);
        ((TextView) findViewById(R.id.image_add_record_text)).setText(R.string.record);
        ((TextView) findViewById(R.id.insert_background_text)).setText(R.string.background);

        if (null != mBackgroundItemsAdapter) {
            //language change,bgAdapter`s text change
            mBackgroundItemsAdapter.notifyDataSetChanged();
        }

        if (null != dialog) {
            dialog.cancel();
        }

    }


    @Override
    public boolean isInMultiWindowMode() {
        return super.isInMultiWindowMode();

    }

    @Override
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
        super.onMultiWindowModeChanged(isInMultiWindowMode);
        if (!isInMultiWindowMode) {
            if (null != recordDialog && recordDialog.isShowing()) {
                recordDialog.setActivityNull();
                recordDialog.cancel();
                openAudio = false;
            }
            if (null != serviceHelper) {
                serviceHelper.stopService();
            }
        }


    }

    public ArrayList<LabelInfo> getItemLabelInfos() {
        return itemLabelInfos;
    }

    public void setItemLabelInfos(ArrayList<LabelInfo> itemLabelInfos) {
        this.itemLabelInfos = itemLabelInfos;
    }

    @Override
    public void deleteSuccess() {
        finish();
    }

    @Override
    public void toShareActivity(String path) {
        Intent intent = new Intent(mContext, ShareImageActivity.class);
        intent.putExtra(NoteConfig.SKIN_BG_COLORID_KEY, skinBgColorId);
        intent.putExtra(NoteConfig.SKIN_BG_ID_KEY, skinBgId);
        intent.putExtra(NoteConfig.SCREENSHOT_IMAGE_PATH, path == null ? "" : path);
        startActivity(intent);
    }

    @Override
    public void setCompressImagePath(String path) {

    }


    @Override
    public void setNoteId(long mId) {
        this.mId = mId;
    }

    @Override
    public void setDoEdit(boolean doEdit) {
        isDoEdit = doEdit;
        NoteConfig.inMultiWindowDoEditStatus = doEdit;
    }

    @Override
    public void onClickColoredLayoutChildrenView() {
        if (isDoEdit) {
            setLlBackgroundItemsLayoutState(View.GONE);
            setStyleGroupVisibilityState(View.VISIBLE);
            //mStyleGroupLinearLayout.setVisibility(View.VISIBLE);
            //mLlBackgroundItemsLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void setSkinBgName(String name) {


    }

    @Override
    public void setSkinBgId(int id) {
        setCurrentSkinBg(id);
    }

    @Override
    public void onClickColoredLayoutEditText() {
        accessEditMode(false);
    }


    public View getKeyboardHideOrShowFocusView(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        return view;
    }


    @Override
    public void bgItemOnClick(View view, int postion) {
        skinBgPositon = postion;
        setSkinBgToView(postion);
    }

    private void setSkinBgToView(int postion) {
        if (null != mBackgroundItemInfos) {
            int bgImageResId = mBackgroundItemInfos.get(postion).getBgImageResId();
            //mRlCreatRootView.setBackgroundResource(bgImageResId);
            skinBgName = mBackgroundItemInfos.get(postion).getName() == null
                    ? mContext.getResources().getString(R.string.standard_bg)
                    : mBackgroundItemInfos.get(postion).getName();
            skinBgColorId = mBackgroundItemInfos.get(postion).getBgColorResId();
            isInbetweening = mBackgroundItemInfos.get(postion).getIsInbetweening();
            skinBgId = mBackgroundItemInfos.get(postion).getSkinBgId();
            //action bar icon color change
            changeActionBarIconColor(postion);
            changeHeadBg(postion);
            changeFootBg(postion);
            changeStyleGroupIconAndTextColor(postion);
            changeColoredLinearyLayoutEditTextColor(postion);

        }
    }


    private void changeColoredLinearyLayoutEditTextColor(int postion) {
        int textCheckedColorResId = mBackgroundItemInfos.get(postion).getTextCheckedColorResId();
        int textUnCheckColorResId = mBackgroundItemInfos.get(postion).getTextUnCheckColorResId();
        int checkListCheckedColorResId = mBackgroundItemInfos.get(postion).getChecklistCheckedColorResId();
        int checkListUnCheckColorResId = mBackgroundItemInfos.get(postion).getChecklistUnCheckColorResId();
        viewManager.setColoredLinearyLayoutEditTextColor(textCheckedColorResId, textUnCheckColorResId, dynamicContainer
                , checkListCheckedColorResId, checkListUnCheckColorResId);
    }


    private void changeHeadBg(int postion) {
        //type == RES_TYPE_COLOR :title bar need color, or type == RES_TYPE_DRAWABLE :title bar need drawble
        String type = getResources().getResourceTypeName(mBackgroundItemInfos.get(postion).getFootResId());
        int bgColor = mBackgroundItemInfos.get(postion).getBgColorResId();

        switch (type) {
            case NoteConfig.RES_TYPE_COLOR:
                int color = mBackgroundItemInfos.get(postion).getHeadResId();
                mClCoordinatorLayout.setBackgroundColor(getResources().getColor(color, getTheme()));
                mTitlteBarLine.setVisibility(View.VISIBLE);
                break;
            case NoteConfig.RES_TYPE_DRAWABLE:
                int resId = mBackgroundItemInfos.get(postion).getHeadResId();
                mClCoordinatorLayout.setBackgroundResource(resId);
                mTitlteBarLine.setVisibility(View.GONE);
                break;
        }

        setTitleBarBgDisAttribute();
        mClCoordinatorLayout.setFitsSystemWindows(true);
        mRlCreatRootView.setBackgroundColor(getResources().getColor(bgColor, getTheme()));
    }

    private void setTitleBarBgDisAttribute() {
        int statusBarHeight = ActivityCommonUtils.getStatusBarHeight(getApplicationContext());
        mClCoordinatorLayout.setPadding(0, statusBarHeight, 0, 0);


    }

    private void changeFootBg(int postion) {


        String type = getResources().getResourceTypeName(mBackgroundItemInfos.get(postion).getFootResId());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlContentMemoEdit.getLayoutParams();
        int lineColorId = mBackgroundItemInfos.get(postion).getStyleGroupLineColorId();
        mStyleGroupLine.setBackgroundColor(getResources().getColor(lineColorId));
        int h = 0;

        switch (type) {
            case NoteConfig.RES_TYPE_COLOR:
                mIvFootBg.setBackgroundResource(0);
                mIvFootBg.setVisibility(View.GONE);
                mStyleGroupLinearLayout.setBackgroundColor(
                        getResources().getColor(mBackgroundItemInfos.get(postion).getFootResId(), getTheme()));

                h = getResources().getInteger(R.integer.background_item_h);
                if (mLlBackgroundItemsLayout.getVisibility() == View.VISIBLE) {
                    params.setMargins(0, 0, 0, DpUtils.dp2Px(mContext.getApplicationContext(), h));
                } else {
                    params.setMargins(0, 0, 0, 0);
                }


                mLlContentMemoEdit.setLayoutParams(params);

                break;
            case NoteConfig.RES_TYPE_DRAWABLE:

                mIvFootBg.setBackgroundResource(mBackgroundItemInfos.get(postion).getFootResId());
                mIvFootBg.setVisibility(View.VISIBLE);
                mStyleGroupLinearLayout.setBackgroundColor(getResources().getColor(R.color.transparent, getTheme()));
                h = getResources().getInteger(R.integer.bg_foot_h);
                params.setMargins(0, 0, 0, DpUtils.dp2Px(mContext.getApplicationContext(), h));
                mLlContentMemoEdit.setLayoutParams(params);

                break;
        }

    }

    private void changeActionBarIconColor(int postion) {
        //mIvMenuBack mDetailDelete mTvMenuLable mTvMenuMore mTvMenuDone

        currentHeadIconColorResId = mBackgroundItemInfos.get(postion).getHeadIconColorResId();

        VectorDrawable vectorDrawableCompatIvMenuBack = (VectorDrawable) mIvMenuBack.getDrawable();
        vectorDrawableCompatIvMenuBack.setTint(getResources().
                getColor(currentHeadIconColorResId));
        mIvMenuBack.setImageDrawable(vectorDrawableCompatIvMenuBack);


        VectorDrawable vectorDrawableCompatDetailDelete = (VectorDrawable) mDetailDelete.getBackground();
        vectorDrawableCompatDetailDelete.setTint(getResources().
                getColor(currentHeadIconColorResId));
        mDetailDelete.setBackground(vectorDrawableCompatDetailDelete);

        setTvMenuLableBackground(mTvMenuLable, currentHeadIconColorResId);
        setTexViewtColor(mTvMenuLable, currentHeadIconColorResId);

        VectorDrawable vectorDrawableCompatTvMenuMore = (VectorDrawable) mTvMenuMore.getBackground();
        vectorDrawableCompatTvMenuMore.setTint(getResources().
                getColor(currentHeadIconColorResId));
        mTvMenuMore.setBackground(vectorDrawableCompatTvMenuMore);

        VectorDrawable vectorDrawableCompatTvMenuDone = (VectorDrawable) mTvMenuDone.getBackground();
        vectorDrawableCompatTvMenuDone.setTint(getResources().
                getColor(currentHeadIconColorResId));
        mTvMenuDone.setBackground(vectorDrawableCompatTvMenuDone);
    }


    private void changeStyleGroupIconAndTextColor(int postion) {
        // iv_add_mark image_photo   image_add_record  insert_background
        int resId = mBackgroundItemInfos.get(postion).getFootIconColorResId();
        int textResId = mBackgroundItemInfos.get(postion).getFootTextColorResId();

        VectorDrawable vectorDrawableCompatAddMark = (VectorDrawable) mIvAddMark.getBackground();
        vectorDrawableCompatAddMark.setTint(getResources().getColor(resId));
        mIvAddMark.setBackground(vectorDrawableCompatAddMark);

        VectorDrawable vectorDrawableCompatIvImagePhoto = (VectorDrawable) mIvImagePhoto.getDrawable();
        vectorDrawableCompatIvImagePhoto.setTint(getResources().getColor(resId));
        mIvImagePhoto.setImageDrawable(vectorDrawableCompatIvImagePhoto);

        VectorDrawable vectorDrawableCompatIvImageAddRecord = (VectorDrawable) mIvImageAddRecord.getDrawable();
        vectorDrawableCompatIvImageAddRecord.setTint(getResources().getColor(resId));
        mIvImageAddRecord.setImageDrawable(vectorDrawableCompatIvImageAddRecord);


        VectorDrawable vectorDrawableCompatInsertBackground = (VectorDrawable) mInsertBackground.getDrawable();
        vectorDrawableCompatInsertBackground.setTint(getResources().getColor(resId));
        mInsertBackground.setImageDrawable(vectorDrawableCompatInsertBackground);


        ((TextView) findViewById(R.id.iv_add_mark_text)).setTextColor(getResources().getColor(textResId));
        ((TextView) findViewById(R.id.image_photo_text)).setTextColor(getResources().getColor(textResId));
        ((TextView) findViewById(R.id.image_add_record_text)).setTextColor(getResources().getColor(textResId));
        ((TextView) findViewById(R.id.insert_background_text)).setTextColor(getResources().getColor(textResId));
    }


    private void setStyleGroupVisibilityState(int state) {
        mStyleGroupLinearLayout.setVisibility(state);
        int h = getResources().getInteger(R.integer.style_group_h);
        setContentMemoEditLayoutParams(state, h);
    }


    private void setLlBackgroundItemsLayoutState(int state) {
        mLlBackgroundItemsLayout.setVisibility(state);
        int h = getResources().getInteger(R.integer.background_item_h);
        setContentMemoEditLayoutParams(state, h);
    }


    private void setContentMemoEditLayoutParams(int state, int h) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mLlContentMemoEdit.getLayoutParams();
        int bottomMargin = params.bottomMargin;

        switch (state) {
            case View.VISIBLE:
                if (h > DpUtils.px2Dp(mContext.getApplicationContext(), (float) bottomMargin)) {
                    params.setMargins(0, 0, 0, DpUtils.dp2Px(mContext.getApplicationContext(), h));
                    mLlContentMemoEdit.setLayoutParams(params);
                }
                break;
            case View.GONE:
                if (h == DpUtils.px2Dp(mContext.getApplicationContext(), (float) bottomMargin)) {
                    params.setMargins(0, 0, 0, 0);
                    mLlContentMemoEdit.setLayoutParams(params);

                }
                break;
            case View.INVISIBLE:
                break;
        }
    }

    private void setCurrentSkinBg(int skinBgId) {
        int[] skinBgIdArr = NoteConfig.SKIN_BG_ID;
        for (int i = 0; i < skinBgIdArr.length; i++) {
            int id = mContext.getResources().getInteger(skinBgIdArr[i]);
            if (skinBgId == id) {
                skinBgPositon = i;
                setSkinBgToView(i);
            }
        }
    }

    private void setTvMenuLableBackground(TextView mTvMenuLable, int currentHeadIconColorResId) {
        if (this.currentHeadIconColorResId > 0) {
            VectorDrawable vectorDrawableCompatTvMenuLable = (VectorDrawable) mTvMenuLable.getBackground();
            vectorDrawableCompatTvMenuLable.setTint(getResources().getColor(currentHeadIconColorResId));
            mTvMenuLable.setBackground(vectorDrawableCompatTvMenuLable);

        }
    }

    private void setTexViewtColor(TextView mTvMenuLable, int currentHeadIconColorResId) {
        if (currentHeadIconColorResId > 0) {
            mTvMenuLable.setTextColor(mContext.getColor(currentHeadIconColorResId));
        }

    }

    /**
     * @param isNeedRequestFocus if is true , get Last EditText Focus
     */
    private void accessEditMode(boolean isNeedRequestFocus) {
        dissMissPopu();
        isDoEdit = true;
        NoteConfig.inMultiWindowDoEditStatus = isDoEdit;
        //mStyleGroupLinearLayout.setVisibility(View.VISIBLE);
        //mLlBackgroundItemsLayout.setVisibility(View.GONE);
        setLlBackgroundItemsLayoutState(View.GONE);
        setStyleGroupVisibilityState(View.VISIBLE);
        NoteConfig.ISPREVIEWMODE = false;
        mTvMenuDone.setVisibility(View.VISIBLE);
        mDetailDelete.setVisibility(View.GONE);
        mTvMenuMore.setVisibility(View.GONE);
        if (isNeedRequestFocus) {
            viewManager.getLastEdittext(dynamicContainer);
            isContentSaved = false;
        }
    }

    private void accessPreviewMode() {
        //isDoEdit = false;
        NoteConfig.ISPREVIEWMODE = true;
        keyboardHideOrShowFocusView = getKeyboardHideOrShowFocusView(mActivity);
        KeyboardUtils.hideSoftInput(keyboardHideOrShowFocusView);
        mTvMenuDone.setVisibility(View.GONE);
        mDetailDelete.setVisibility(View.VISIBLE);
        mTvMenuMore.setVisibility(View.VISIBLE);
        viewManager.removeEditTextFocus(dynamicContainer);
        //mStyleGroupLinearLayout.setVisibility(View.GONE);
        setStyleGroupVisibilityState(View.GONE);

    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case NoteConfig.RECORD_COMPLETE:
                    recordComplete();
                    break;
                case NoteConfig.RECORD_CANCEL:
                    recordCancel();
                    break;

            }
        }
    };


}
