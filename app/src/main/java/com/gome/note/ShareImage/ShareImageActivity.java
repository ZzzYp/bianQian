package com.gome.note.ShareImage;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.manager.EditNoteConstans;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.view.subscaleview.ImageSource;
import com.gome.note.view.subscaleview.SubsamplingScaleImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class ShareImageActivity extends BaseActivity {

    private Context mContext;
    private ShareImageActivity mActivity;
    private SharePresenter presenter;
    private SubsamplingScaleImageView mIvSharePreview;
    private LinearLayout mLlMenuShare;
    private ImageView mIvMenuBack;
    private LinearLayout mLlDetailSave;
    private boolean isSaveImage;
    private int skinBgColorId;
    private int skinBgId;
    private LinearLayout mLlImageShare;
    private TextView mDetailSave;
    private String mPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_share);
        mContext = this;
        mActivity = this;

        //skinBgColorId = getIntent().getIntExtra(NoteConfig.SKIN_BG_COLORID_KEY, mContext.getColor(R.color.white));
        //skinBgId = getIntent().getIntExtra(NoteConfig.SKIN_BG_ID_KEY, mContext.getResources().getInteger(R.integer.skin_bg_id_standard));

        mPath = getIntent().getStringExtra(NoteConfig.SCREENSHOT_IMAGE_PATH);

        initPresenter();
        initView();
        initData();

    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    private void initView() {

        mLlImageShare = (LinearLayout) findViewById(R.id.ll_image_share);
        mIvSharePreview = (SubsamplingScaleImageView) findViewById(R.id.iv_share_preview);

        //mIvSharePreview.setImage(ImageSource.bitmap(EditNoteConstans.shareBitmap));


        mLlMenuShare = (LinearLayout) findViewById(R.id.ll_menu_share);
        mIvMenuBack = (ImageView) findViewById(R.id.iv_menu_back);
        mLlDetailSave = (LinearLayout) findViewById(R.id.ll_detail_save);
        mDetailSave = (TextView) findViewById(R.id.detail_save);

        VectorDrawable vectorDrawableDetailSave = (VectorDrawable) mDetailSave.getBackground();
        vectorDrawableDetailSave.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mDetailSave.setBackground(vectorDrawableDetailSave);

        mLlMenuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != presenter) {
                    if (!captureExternalStorage()) {
                        return;
                    }
                    toShareMenu();
                }
            }
        });


        mIvMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeActivity();
            }
        });


        mLlDetailSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isSaveImage) {
                    presenter.saveImage();
                    isSaveImage = true;
                }

            }
        });

    }


    private void initData() {
        if (!TextUtils.isEmpty(mPath)) {
            mIvSharePreview.setImage(ImageSource.uri(mPath));
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        closeActivity();
    }

    private void closeActivity() {
        if (null != EditNoteConstans.shareBitmap) {
            EditNoteConstans.shareBitmap.recycle();
            EditNoteConstans.shareBitmap = null;
        }
        finish();
    }

    @Override
    public void initPresenter() {
        presenter = new SharePresenter(mContext);

    }

    public boolean captureExternalStorage() {
        //  xxxxxxxxxxxx for 7.0 !!!!
        if (ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(mContext.getApplicationContext(), Manifest.permission
                .WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, NoteConfig.NEED_EXTERNAL_STORAGE);
            return false;
        } else {
            return true;
        }
    }


    public void toShareMenu() {

        if (null != presenter && !TextUtils.isEmpty(mPath)) {
            File file = new File(mPath);
            Uri uri = presenter.getImageContentUri(mContext.getApplicationContext(), file);
            createIntentShare(uri);
        }
    }


    public void createIntentShare(Uri shareUri) {
        if (null == shareUri) {
            return;
        }
        String pkg = mContext.getPackageName();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.setPackage(pkg);
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(
                sharingIntent, PackageManager.MATCH_DEFAULT_ONLY);
        ArrayList<ComponentName> excludeLists = new ArrayList<>();
        if (resInfo != null && !resInfo.isEmpty()) {
            for (ResolveInfo info : resInfo) {
                if (info != null) {
                    ActivityInfo activityInfo = info.activityInfo;
                    if (activityInfo != null && activityInfo.packageName.equals(mContext.getPackageName())) {
                        excludeLists.add(new ComponentName(activityInfo.packageName, activityInfo.name));
                    }
                }
            }
        }
        sharingIntent.setPackage(null);
        sharingIntent.removeExtra(Intent.EXTRA_STREAM);
        Intent chooserIntent = Intent.createChooser(sharingIntent, null);
        if (chooserIntent == null) {
            return;
        }
        if (excludeLists != null && !excludeLists.isEmpty()) {
            chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludeLists.toArray(new Parcelable[]{}));
        }

        sharingIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        sharingIntent.setAction(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_STREAM, shareUri);
        sharingIntent.setType("image/*");
        startActivity(chooserIntent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case NoteConfig.NEED_EXTERNAL_STORAGE:
                // if the permission be refused ,the grantResults will be null
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toShareMenu();
                }
                break;

        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPath && mPath.length() > 0) {
            ActivityCommonUtils.deleteFile(mPath);
        }
        if (null != presenter) {
            presenter.onDestroy();
        }
    }
}
