package com.gome.note.ui.create.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.Utils;
import com.gome.note.R;
import com.gome.note.base.BasePresenter;
import com.gome.note.base.Config;
import com.gome.note.db.PocketDbHandle;
import com.gome.note.db.PocketStore;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.entity.BackgroundItemInfo;
import com.gome.note.entity.PocketInfo;
import com.gome.note.manager.EditNoteConstans;
import com.gome.note.ui.create.PresentToActivityListener;
import com.gome.note.ui.create.model.NoteCreateModel;
import com.gome.note.utils.HandlerUtils;
import com.gome.note.utils.ImageCompress.ImageCompressUtils;
import com.gome.note.view.ColoredLinearyLayout;
import com.gome.note.view.ZanyEditText;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.graphics.BitmapFactory.decodeFile;
import static com.gome.note.utils.ActivityCommonUtils.Bitmap2Bytes;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/22
 * DESCRIBE:
 */

public class NoteCreatePresenter extends BasePresenter {
    private NoteCreateModel mModel;
    private PresentToActivityListener mPresentToActivityListener;
    private String destPath;
    private PocketInfo mPocketInfo;


    public NoteCreatePresenter(Context context) {
        mContext = context;
        initModel();
    }


    @Override
    public void initModel() {
        mModel = new NoteCreateModel(mContext.getApplicationContext(), mHandler);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case Config.QUERY_SUCCESS:

                    mPresentToActivityListener.setNoteInfoToActivity(mPocketInfo);

                    break;
                case Config.ADD_SUCCESS:
                    break;
                case Config.UPDATE_SUCCESS:
                    break;
                case Config.DELETE_SUCCESS:
                    mPresentToActivityListener.deleteSuccess();
                    break;
                case Config.QAUD_ERROR:
                    break;
                default:
                    break;

            }
        }
    };


    public PocketInfo getPocketInfo(long id) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                mPocketInfo = PocketDbHandle.queryPocketInfoById(mContext.getApplicationContext(), PocketStore.Pocket
                        .TABLE_POCKET_NAME, id);
                HandlerUtils.sendMessage(mHandler, Config.QUERY_SUCCESS);
            }
        }).start();


        return null;

    }

    public boolean updatePocket(PocketInfo pocketInfo) {
        long status = PocketDbHandle.update(mContext.getApplicationContext(), PocketDbHandle.URI_POCKET, pocketInfo);
        //SharedPreferencesUtil.saveLongValue(mContext, PhoneReceiver.CALL_STATE_OFFHOOK_ID, status);
        if (status != -1) {
            return true;
        } else {
            return false;
        }

    }

    public long createPocket(PocketInfo pocketInfo) {
        long noteId = PocketDbHandle.insert(mContext.getApplicationContext(), pocketInfo, PocketStore.Pocket
                .TABLE_POCKET_NAME);

        return noteId;

    }

    public void shareContentWithImage(ScrollView view, int skinBgId, int skinBgColorId, boolean isInbetweening) {


        new AsyncTask<String, Integer, String>() {

            @Override
            protected void onPreExecute() {
                //mView.showLoading("生成图片中...");
            }

            @Override
            protected String doInBackground(String... params) {
                Bitmap bitmap = shotScrollView(view, skinBgId, skinBgColorId, isInbetweening);
                EditNoteConstans.shareBitmap = bitmap;
                String path = saveImageToLocation(bitmap);
                return path;
            }

            @Override
            protected void onPostExecute(String path) {

                mPresentToActivityListener.toShareActivity(path);
            }
        }.execute();

    }


    private String saveImageToLocation(Bitmap bitmap) {
        String imageSaveFolder = Environment.getExternalStorageDirectory().getPath() + "/note/image/";
        File file = new File(imageSaveFolder + "/" + "temp.jpg");
        String path = file.getAbsolutePath();
        boolean isSuccess = ImageUtils.save(bitmap, file, Bitmap.CompressFormat.JPEG);
        if (isSuccess) {
            return path;
        }
        return "";
    }


    public Bitmap getNoteShareBitmap(View view) {
        Bitmap bitmap = ImageUtils.view2Bitmap(view);
        int x = bitmap.getWidth() - SizeUtils.sp2px(72);
        int y = bitmap.getHeight() - SizeUtils.sp2px(16);
        int textWaterMarkColor = Utils.getContext().getResources().getColor(R.color.colorBlackAlpha54);
        bitmap = ImageUtils.addTextWatermark(bitmap, EditNoteConstans.watermarkText, 24, textWaterMarkColor, x, y);
        return bitmap;

    }


    public Bitmap shotScrollView(ScrollView scrollView, int skinBgId, int skinBgColorId, boolean isInbetweening) {

        Bitmap bitmap;
        //if is Inbetweening ，need stitch head image and foot image
        if (isInbetweening) {
            bitmap = getBitmapWithInbetweening(scrollView, skinBgColorId, skinBgId);
        } else {
            bitmap = getBitmapWithColor(scrollView, skinBgColorId);
        }

        return bitmap;
    }

    private Bitmap getBitmapWithColor(ScrollView scrollView, int skinBgColorId) {

        int h = 0;
        Bitmap bitmap = null;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }

        h = h + SizeUtils.dp2px(42);
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);

        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(mContext.getColor(skinBgColorId));
        scrollView.draw(canvas);

        bitmap = addLine(bitmap, mContext.getColor(R.color.actionBar_bottom_line_new),
                0 + SizeUtils.dp2px(20),
                bitmap.getHeight() - SizeUtils.dp2px(42),
                bitmap.getWidth() - SizeUtils.dp2px(20),
                bitmap.getHeight() - SizeUtils.dp2px(42),
                true);


        int x = bitmap.getWidth() - SizeUtils.sp2px(13) * 7 - SizeUtils.dp2px(20);
        int y = bitmap.getHeight() - SizeUtils.dp2px(28);
        int textWaterMarkColor = Utils.getContext().getResources().getColor(R.color.font_black_6);

        bitmap = ImageUtils.addTextWatermark(bitmap,
                mContext.getString(R.string.from_gome_note),
                SizeUtils.sp2px(13),
                textWaterMarkColor,
                x, y, true);

        return bitmap;

    }

    private Bitmap getBitmapWithInbetweening(ScrollView scrollView, int skinBgColorId, int skinBgId) {

        int bitmapHeadResId = getBitmapHeadResId(skinBgId);
        int bitmapFootResId = getBitmapFootResId(skinBgId);

        int bitmapHeadH = 0;
        int bitmapFootH = 0;
        //head bitmap
        Bitmap bitmapHeads = BitmapFactory.decodeResource(mContext.getResources(),
                bitmapHeadResId == 0 ? R.drawable.gome_picture_memo_autumn_tittle_bg : bitmapHeadResId);
        bitmapHeadH = bitmapHeads.getHeight();

        //scrollView bitmap
        int h = 0;
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        Bitmap bitmapscrollView = Bitmap.createBitmap(scrollView.getWidth(), h, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmapscrollView);
        canvas.drawColor(mContext.getColor(skinBgColorId));
        scrollView.draw(canvas);

        //Foot bitmap

        Bitmap bitmapFoots = BitmapFactory.decodeResource(mContext.getResources(),
                bitmapFootResId == 0 ? R.drawable.gome_picture_memo_autumn_edit_bg : bitmapFootResId);
        bitmapFootH = bitmapFoots.getHeight();

        //from gome note bitmap
        Bitmap bitmapFromGomeNote = Bitmap.createBitmap(scrollView.getWidth(), SizeUtils.dp2px(42), Bitmap.Config.ARGB_8888);
        final Canvas canvasColor = new Canvas(bitmapFromGomeNote);
        canvasColor.drawColor(mContext.getColor(skinBgColorId));


        Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(),
                h + bitmapHeadH + bitmapFootH + SizeUtils.dp2px(42), Bitmap.Config.ARGB_8888);
        final Canvas canvaAll = new Canvas(bitmap);


        int bitmapHeadsWidth = bitmapHeads.getWidth();
        int bitmapscrollViewWidth = bitmapscrollView.getWidth();
        int bitmapFootsWidth = bitmapFoots.getWidth();

        int bitmapHeadsDrawLeft = 0;
        int bitmapFootsDrawLeft = 0;

        if (bitmapHeadsWidth < bitmapscrollViewWidth) {
            bitmapHeadsDrawLeft = (bitmapscrollViewWidth - bitmapHeadsWidth) / 2;
        }

        if (bitmapFootsWidth < bitmapscrollViewWidth) {
            bitmapFootsDrawLeft = (bitmapscrollViewWidth - bitmapFootsWidth) / 2;
        }

        canvaAll.drawBitmap(bitmapHeads, bitmapHeadsDrawLeft, 0, null);
        canvaAll.drawBitmap(bitmapscrollView, 0, bitmapHeadH, null);
        canvaAll.drawBitmap(bitmapFoots, bitmapFootsDrawLeft, bitmapHeadH + h, null);
        canvaAll.drawBitmap(bitmapFromGomeNote, 0, bitmapHeadH + h + bitmapFootH, null);


        int x = bitmap.getWidth() - SizeUtils.sp2px(13) * 7 - SizeUtils.dp2px(20);
        int y = bitmap.getHeight() - SizeUtils.dp2px(28);
        int textWaterMarkColor = Utils.getContext().getResources().getColor(R.color.font_black_6);

        bitmap = ImageUtils.addTextWatermark(bitmap,
                mContext.getString(R.string.from_gome_note),
                SizeUtils.sp2px(13),
                textWaterMarkColor,
                x, y, true);


        bitmap = drawBg4Bitmap(mContext.getColor(skinBgColorId), bitmap, true);

        bitmapHeads.recycle();
        bitmapscrollView.recycle();
        bitmapFoots.recycle();
        bitmapFromGomeNote.recycle();


        return bitmap;
    }


    public Bitmap drawBg4Bitmap(int color, Bitmap orginBitmap, boolean recycle) {
        if (isEmptyBitmap(orginBitmap)) return null;
        Paint paint = new Paint();
        paint.setColor(color);
        Bitmap bitmap = Bitmap.createBitmap(orginBitmap.getWidth(),
                orginBitmap.getHeight(), orginBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRect(0, 0, orginBitmap.getWidth(), orginBitmap.getHeight(), paint);
        canvas.drawBitmap(orginBitmap, 0, 0, paint);
        if (recycle && !orginBitmap.isRecycled()) orginBitmap.recycle();
        return bitmap;
    }

    private int getBitmapHeadResId(int skinBgId) {
        int[] skinBgIdArr = NoteConfig.SKIN_BG_ID;
        int resId = 0;
        for (int i = 0; i < skinBgIdArr.length; i++) {
            int id = mContext.getResources().getInteger(skinBgIdArr[i]);
            if (skinBgId == id) {
                resId = NoteConfig.SKIN_BG_HEAD_IMAGE[i];
            }
        }
        return resId;
    }

    private int getBitmapFootResId(int skinBgId) {
        int[] skinBgIdArr = NoteConfig.SKIN_BG_ID;
        int resId = 0;
        for (int i = 0; i < skinBgIdArr.length; i++) {
            int id = mContext.getResources().getInteger(skinBgIdArr[i]);
            if (skinBgId == id) {
                resId = NoteConfig.SKIN_BG_FOOT_IMAGE_WITHOUT9[i];
            }
        }
        return resId;

    }

    public static Bitmap addLine(Bitmap src, int color, float startX,
                                 float startY, float endX,
                                 float endY, boolean recycle) {
        if (isEmptyBitmap(src)) return null;
        Bitmap ret = src.copy(src.getConfig(), true);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Canvas canvas = new Canvas(ret);
        paint.setColor(color);
        canvas.drawLine(startX, startY, endX, endY, paint);
        //canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !src.isRecycled()) src.recycle();
        return ret;
    }


    private static boolean isEmptyBitmap(Bitmap src) {
        return src == null || src.getWidth() == 0 || src.getHeight() == 0;
    }

    public void deleteNoteInfo(PocketInfo pocketInfo) {

        mModel.deleteNoteInfo(pocketInfo);

    }

    public String getViewText(ColoredLinearyLayout layout) {

        int childCount = layout.getChildCount();
        StringBuilder stringBuilder = new StringBuilder();
        for (int index = 0; index < childCount; index++) {
            View child = layout.getChildAt(index);
            if (child instanceof RelativeLayout) {
                RelativeLayout rl = (RelativeLayout) child;

                if (rl.getChildAt(0) instanceof CheckBox) {

                    CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                    ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);

                    boolean isHasCheckBox;
                    int visibile = checkBox.getVisibility();
                    if (visibile == 0) {
                        //visible
                        isHasCheckBox = true;
                    } else {
                        //gone or invisible
                        isHasCheckBox = false;
                    }
                    boolean isChecked = checkBox.isChecked();
                    String text = editText.getText().toString().trim();

                    stringBuilder.append(text);
                }
            }
        }
        return stringBuilder.toString();
    }

    public boolean isHasImage(ColoredLinearyLayout layout) {

        int childCount = layout.getChildCount();
        if (childCount > 0) {
            for (int index = 0; index < childCount; index++) {
                View child = layout.getChildAt(index);
                if (child instanceof LinearLayout) {
                    LinearLayout ll = (LinearLayout) child;
                    if (ll.getChildAt(0) instanceof ImageView) {
                        return true;
                    } else if (ll.getChildAt(0) instanceof RelativeLayout) {
                        RelativeLayout relativeLayout = (RelativeLayout) ll.getChildAt(0);
                        if (relativeLayout.getChildCount() > 0) {
                            View view = relativeLayout.getChildAt(0);
                            if (view instanceof ImageView) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        } else {
            return false;
        }
    }


    public File createFilePath(String suffix, String fileName) {
        String path = mContext.getExternalCacheDir().getPath() + File.separator + fileName + File.separator;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        String filePath = path + System.currentTimeMillis() + "." + suffix;
        return new File(filePath);
    }


    public String compressImage(Bitmap image, String willCopyPath, int count) {
        ImageCompressUtils.from(mContext.getApplicationContext()).load(willCopyPath).execute(new ImageCompressUtils.OnCompressListener() {

            @Override
            public void onSuccess(File file) {
                String path = file.getAbsolutePath();
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                File imageFile = createFilePath("png", Config.PATH_CACHE_IMAGES);
                destPath = imageFile.getAbsolutePath();
                try {
                    write(Bitmap2Bytes(bitmap), destPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mPresentToActivityListener.setCompressImagePath(destPath);
            }

            @Override
            public void onError(Throwable e) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                int options = 50;
                while (baos.toByteArray().length / 1024 > 100) {
                    baos.reset();
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);
                    options -= 10;
                }
                ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
                Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
                File imageFile = createFilePath("png", Config.PATH_CACHE_IMAGES);
                destPath = imageFile.getAbsolutePath();
                try {
                    write(Bitmap2Bytes(bitmap), destPath);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                mPresentToActivityListener.setCompressImagePath(destPath);
            }
        });


        return destPath;
    }

    public static void write(byte[] bs, String destPath) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(destPath));
        out.write(bs);
        out.flush();
        out.close();
    }

    public String getimage(String srcPath, int count) {
        try {

            if (null != srcPath && srcPath.length() > 0) {
                Bitmap bitmap = resizeImage2(srcPath, 480, 800);
                return compressImage(bitmap, srcPath, count);
            }

        } catch (Exception e) {
        }
        return "";
    }

    public Bitmap resizeImage2(String path,
                               int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        options.inDither = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 1;

        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            options.inSampleSize = sampleSize;
        }

        options.inJustDecodeBounds = false;
        bitmap = decodeFile(path, options);
        return bitmap;
    }


    public void setPresnetToActivityListener(PresentToActivityListener presentToActivity) {
        mPresentToActivityListener = presentToActivity;
    }

    public void deleteNoteInfoById(long updateId) {

        mModel.deleteNoteInfoById(updateId);
    }

    public ArrayList<BackgroundItemInfo> setBackgroundData(Context context) {
        ArrayList<BackgroundItemInfo> backgroundItemInfos = new ArrayList<>();
        for (int i = 0; i < NoteConfig.SKIN_CHECKLIST_BG_IMAGE.length; i++) {
            BackgroundItemInfo backgroundItemInfo = new BackgroundItemInfo();
            backgroundItemInfo.setSkinBgId(mContext.getResources().getInteger(NoteConfig.SKIN_BG_ID[i]));
            backgroundItemInfo.setCheckListResId(NoteConfig.SKIN_CHECKLIST_BG_IMAGE[i]);
            backgroundItemInfo.setBgImageResId(NoteConfig.SKIN_BG_IMAGE[i]);
            backgroundItemInfo.setName(context.getString(NoteConfig.SKIN_BG_TEXT[i]));
            backgroundItemInfo.setBgColorResId(NoteConfig.SKIN_BG_COLOR[i]);
            backgroundItemInfo.setHeadIconColorResId(NoteConfig.SKIN_BG_ICON_COLOR[i]);
            backgroundItemInfo.setFootIconColorResId(NoteConfig.SKIN_BG_STYLE_GROUP_ICON_COLOR[i]);
            backgroundItemInfo.setFootTextColorResId(NoteConfig.SKIN_BG_STYLE_GROUP_TEXT_COLOR[i]);
            backgroundItemInfo.setHeadResId(NoteConfig.SKIN_BG_HEAD_IMAGE[i]);
            backgroundItemInfo.setFootResId(NoteConfig.SKIN_BG_FOOT_IMAGE[i]);
            backgroundItemInfo.setTextUnCheckColorResId(NoteConfig.SKIN_BG_TEXT_UNCHECK_COLOR[i]);
            backgroundItemInfo.setTextCheckedColorResId(NoteConfig.SKIN_BG_TEXT_CHECKED_COLOR[i]);
            backgroundItemInfo.setStyleGroupLineColorId(NoteConfig.SKIN_BG_STYLE_GROUP_LINE_COLOR[i]);
            backgroundItemInfo.setIsInbetweening(NoteConfig.SKIN_BG_IS_INBETWEENING[i]);
            backgroundItemInfo.setChecklistCheckedColorResId(NoteConfig.SKIN_BG_CHECKLIST_CHECKED_COLOR[i]);
            backgroundItemInfo.setChecklistUnCheckColorResId(NoteConfig.SKIN_BG_CHECKLIST_UNCHECKED_COLOR[i]);
            backgroundItemInfos.add(backgroundItemInfo);
        }

        return backgroundItemInfos;


    }

    public void destory() {

        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }

    }
}
