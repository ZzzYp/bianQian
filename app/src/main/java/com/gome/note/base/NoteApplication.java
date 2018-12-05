package com.gome.note.base;

import android.app.Application;

import com.blankj.utilcode.util.Utils;
import com.facebook.stetho.Stetho;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.utils.CustomPrinterForGetBlockInfo;
import com.gome.note.utils.GlideImageLoader;
import com.gome.note.utils.SharedPreferencesUtil;
import com.iflytek.cloud.SpeechUtility;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.squareup.leakcanary.LeakCanary;

import android.support.multidex.MultiDexApplication;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class NoteApplication extends MultiDexApplication implements Thread.UncaughtExceptionHandler {
    private static NoteApplication myApplication = null;

    public static NoteApplication getAppContext() {
        return myApplication;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SpeechUtility.createUtility(NoteApplication.this, "appid=5ab37c97");
        Stetho.initializeWithDefaults(this);
        CustomPrinterForGetBlockInfo.start();
        myApplication = this;
        Utils.init(this);
        initDataFirst();
        initImagePicker();
        //初始化LeakCanary
        LeakCanary.install(this);
    }


    public void initDataFirst() {
        if (SharedPreferencesUtil.getBooleanValue(this.getApplicationContext(),
                SharedPreferencesUtil.KEY_FIRST_START_APP, true)) {
            SharedPreferencesUtil.saveBooelanValue(this.getApplicationContext(), SharedPreferencesUtil.
                    KEY_FIRST_START_APP, false);
            NoteConfig.firstStartAppInsertStickLable();
            // InsertData.insertPocketInfo(this);
        }

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(false);
        imagePicker.setCrop(true);
        imagePicker.setSaveRectangle(true);
        imagePicker.setSelectLimit(3);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(1000);
        imagePicker.setOutPutY(1000);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }
}
