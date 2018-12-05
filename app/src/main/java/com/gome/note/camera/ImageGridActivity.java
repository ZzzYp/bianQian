package com.gome.note.camera;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.ToastUtils;
import com.gome.note.R;
import com.gome.note.camera.adapter.ImageRecyclerAdapter;
import com.lzy.imagepicker.ImageDataSource;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.adapter.ImageFolderAdapter;
import com.lzy.imagepicker.bean.ImageFolder;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageBaseActivity;
import com.lzy.imagepicker.ui.ImageCropActivity;
import com.lzy.imagepicker.ui.ImagePreviewActivity;
import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.FolderPopUpWindow;
import com.lzy.imagepicker.view.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;


public class ImageGridActivity extends ImageBaseActivity implements ImageDataSource.OnImagesLoadedListener,/* OnImageItemClickListener,*/ ImagePicker.OnImageSelectedListener, View.OnClickListener {

    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";

    private ImagePicker imagePicker;

    private boolean isOrigin = false;
    private View mFooterBar;
    private TextView mBtnOk;
    private TextView mSelect;
    private View mllDir;
    private TextView mtvDir;
    private TextView mBtnPre;
    private ImageFolderAdapter mImageFolderAdapter;
    private FolderPopUpWindow mFolderPopupWindow;
    private List<ImageFolder> mImageFolders;
    private boolean directPhoto = false;
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;
    private SDReceiver sdReceiver;
    private FragmentActivity mActivity;
    private ImageDataSource.OnImagesLoadedListener onImagesLoadedListener;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        mActivity = this;
        onImagesLoadedListener = this;
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        iFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        iFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        iFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        iFilter.addDataScheme("file");
        sdReceiver = new SDReceiver();
        registerReceiver(sdReceiver, iFilter);
        imagePicker = ImagePicker.getInstance();
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);
        Intent data = getIntent();

        if (data != null && data.getExtras() != null) {
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false);
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                }
            }
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
            imagePicker.setSelectedImages(images);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, Utils.dp2px(this.getApplicationContext(), 2), false));
        findViewById(R.id.btn_back).setOnClickListener(this);
        mBtnOk = (TextView) findViewById(R.id.btn_ok);

        VectorDrawable vectorDrawableBtnOk = (VectorDrawable) mBtnOk.getBackground();
        vectorDrawableBtnOk.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mBtnOk.setBackground(vectorDrawableBtnOk);

        mSelect = (TextView) findViewById(R.id.tv_select);
        mBtnOk.setOnClickListener(this);
        mBtnPre = (TextView) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mFooterBar = findViewById(R.id.footer_bar);
        mllDir = findViewById(R.id.ll_dir);
        mllDir.setOnClickListener(this);
        mtvDir = (TextView) findViewById(R.id.tv_dir);
        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }

//        mImageGridAdapter = new ImageGridAdapter(this, null);
        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null);

        onImageSelected(0, null, false);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new ImageDataSource(this, null, this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            new ImageDataSource(this, null, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mRecyclerAdapter.toast != null) {
            mRecyclerAdapter.toast.cancel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                new ImageDataSource(this, null, this);
            } else {
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
            } else {
            }
        }
    }

    @Override
    protected void onDestroy() {
        imagePicker.addOnImageSelectedListener(null);
        imagePicker.removeOnImageSelectedListener(this);
        imagePicker.removeOnImageSelectedListener(null);
        imagePicker.clear();
        imagePicker = null;

        mImageFolderAdapter.setActivityNull();
        mRecyclerAdapter.setActivityNull();
        unregisterReceiver(sdReceiver);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_ok) {
            Intent intent = new Intent();
            intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
            setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
            finish();
        } else if (id == R.id.ll_dir) {
            if (mImageFolders == null) {
                return;
            }
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mImageFolders);
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, imagePicker.getSelectedImages());
            intent.putExtra(ImagePreviewActivity.ISORIGIN, isOrigin);
            intent.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);
        } else if (id == R.id.btn_back) {
            finish();
        }
    }


    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
//                    mImageGridAdapter.refreshData(imageFolder.images);
                    mRecyclerAdapter.refreshData(imageFolder.images);
                    mtvDir.setText(imageFolder.name);
                }
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }

    @Override
    public void onImagesLoaded(List<ImageFolder> imageFolders) {
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        ArrayList<ImageItem> newImages = new ArrayList<ImageItem>();
        if (imageFolders.size() == 0) {
//            mImageGridAdapter.refreshData(null);
            mRecyclerAdapter.refreshData(null);
        } else {
//            mImageGridAdapter.refreshData(imageFolders.get(0).images);
            mRecyclerAdapter.refreshData(imageFolders.get(0).images);
            newImages = imageFolders.get(0).images;
        }
//        mImageGridAdapter.setOnImageItemClickListener(this);
//        mRecyclerAdapter.setOnImageItemClickListener(this);

        mRecyclerView.setAdapter(mRecyclerAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
        //added by chenhuaiyu for PRODUCTION-6895 start
        ArrayList<ImageItem> selectedImages = imagePicker.getSelectedImages();
        for (int i = 0; i < selectedImages.size(); i++) {
            if (!newImages.contains(selectedImages.get(i))) {
                selectedImages.remove(i);
                i--;
            }
        }
        imagePicker.setSelectedImages(selectedImages);
        if (imagePicker.getSelectImageCount() > 0) {
            mSelect.setVisibility(View.VISIBLE);
            mSelect.setText(getString(R.string.ip_select_count, imagePicker.getSelectImageCount()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setTextColor(ContextCompat.getColor(ImageGridActivity.this.getApplicationContext(), R.color.ip_text_primary_inverted));
            mBtnOk.setAlpha(1f);
        } else {
            mBtnOk.setAlpha(0.35f);
            mSelect.setVisibility(View.GONE);
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
            mBtnPre.setTextColor(ContextCompat.getColor(ImageGridActivity.this.getApplicationContext(), R.color.ip_text_secondary_inverted));
        }
        //added by chenhuaiyu for PRODUCTION-6895 end
        if (imageFolders.size() == 0) {
            //ToastUtils.showShort(getString(R.string.no_photo));
            Toast.makeText(this, getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {

        if (imagePicker.getSelectImageCount() > 0) {
            mSelect.setVisibility(View.VISIBLE);
            mSelect.setText(getString(R.string.ip_select_count, imagePicker.getSelectImageCount()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.ip_text_primary_inverted));
            mBtnOk.setAlpha(1f);
//            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
        } else {
            mBtnOk.setAlpha(0.35f);
            mSelect.setVisibility(View.GONE);
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
            mBtnPre.setTextColor(ContextCompat.getColor(this.getApplicationContext(), R.color.ip_text_secondary_inverted));
//            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
        }
//        mImageGridAdapter.notifyDataSetChanged();
//        mRecyclerAdapter.notifyItemChanged(position); // 17/4/21 fix the position while click img to preview
//        mRecyclerAdapter.notifyItemChanged(position + (imagePicker.isShowCamera() ? 1 : 0));// 17/4/24  fix the position while click right bottom preview button
        for (int i = imagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).path != null && mRecyclerAdapter.getItem(i).path.equals(item.path)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.getExtras() != null) {
            if (resultCode == ImagePicker.RESULT_CODE_BACK) {
                isOrigin = data.getBooleanExtra(ImagePreviewActivity.ISORIGIN, false);
            } else {
                if (data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) == null) {
                } else {
                    setResult(ImagePicker.RESULT_CODE_ITEMS, data);
                }
                finish();
            }
        } else {
            if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_TAKE) {
                ImagePicker.galleryAddPic(this.getApplicationContext(), imagePicker.getTakeImageFile());
                String path = imagePicker.getTakeImageFile().getAbsolutePath();
                ImageItem imageItem = new ImageItem();
                imageItem.path = path;
                imagePicker.clearSelectedImages();
                imagePicker.addSelectedImageItem(0, imageItem, true);
                if (imagePicker.isCrop()) {
                    Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                    startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS, imagePicker.getSelectedImages());
                    setResult(ImagePicker.RESULT_CODE_ITEMS, intent);
                    finish();
                }
            } else if (directPhoto) {
                finish();
            }
        }
    }


    public class SDReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.MEDIA_MOUNTED".equals(action)) {
                newImageDataSource();
            } else if ("android.intent.action.MEDIA_REMOVED".equals(action)) {
                newImageDataSource();
            } else if ("android.intent.action.MEDIA_UNMOUNTED".equals(action)) {
                newImageDataSource();
            }
        }

        private void newImageDataSource() {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    new ImageDataSource(mActivity, null, onImagesLoadedListener);
                } else {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
                }
            } else {
                new ImageDataSource(mActivity, null, onImagesLoadedListener);
            }
        }
    }


}