package com.gome.note.ui.create;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.gome.note.R;
import com.gome.note.view.ZoomImageView;

import java.io.File;


public class PhotoActivity extends Activity implements View.OnClickListener {
    private ImageView mBackImage;
    private String mPhotoPath;
    private static int REQUEST_CODE = 100;
    private static int SHOW_CODE = 200;
    private ZoomImageView mImage;
    private TextView textView;
    public String PHOTO_PATH = "photo_path";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        initView();
        showPhoto();
    }

    private void showPhoto() {
        textView.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra(PHOTO_PATH);
        Glide.with(this).load(mPhotoPath).into(mImage);
    }

    public void initView() {
        mImage = (ZoomImageView) findViewById(R.id.image_photo);
        textView = (TextView) findViewById(R.id.iv_menu_complete);
        mBackImage = (ImageView) findViewById(R.id.iv_menu_back);
        mBackImage.setOnClickListener(this);
       // mImage.setOnClickListener(this);


        VectorDrawable vectorDrawableBackImage = (VectorDrawable) mBackImage.getDrawable();
        vectorDrawableBackImage.setTint(getResources().getColor(R.color.common_title_bar_icon_color));
        mBackImage.setImageDrawable(vectorDrawableBackImage);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_menu_back:
                finish();
                break;
            case R.id.image_photo:
                Intent intent = new Intent();
                File file = new File(mPhotoPath);
                Uri fileUri = Uri.fromFile(file);
                intent.setData(fileUri);
                intent.setComponent(new ComponentName("com.android.gallery3d", "com.android.gallery3d.edit.EditImageActivity"));
                startActivityForResult(intent, REQUEST_CODE);
                break;
        }
    }


}
