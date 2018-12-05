package com.gome.note.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.icu.text.DecimalFormat;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gome.note.R;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.domain.InsertIndex;
import com.gome.note.entity.EditTextCheckBoxInfo;
import com.gome.note.entity.Forever;
import com.gome.note.entity.RecordPool;
import com.gome.note.manager.AudioPlayManager;
import com.gome.note.manager.ViewManager;
import com.gome.note.ui.create.IEditTextPasteCallback;
import com.gome.note.ui.create.PhotoActivity;
import com.gome.note.ui.create.SetValueToActivityListener;
import com.gome.note.utils.DpUtils;
import com.gome.note.utils.InsertData;
import com.gome.note.utils.RecordPlayAndStopUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class ColoredLinearyLayout extends LinearLayout implements View.OnFocusChangeListener, AudioPlayManager.AudioTrackStopPlayListener, IEditTextPasteCallback {

    public static final String COLORED_LINEARY_LAYOUT = "ColoredLinearyLayout";
    private String TAG = COLORED_LINEARY_LAYOUT;
    private LayoutParams textParams;
    private LayoutParams otherParams;
    private LayoutParams imageParams;
    private boolean isHasCheckedBox;
    private ArrayList<EditTextCheckBoxInfo> zanyEditTexts = null;
    private int focusViewId;
    private boolean focusViewStatus;
    private boolean isEditTextChangeFocus;
    //private static LinkedList<String> nodeList = ViewManager.getNodeList();
    private Context mContext;
    private AudioPlayManager.AudioTrackStopPlayListener audioTrackStopPlayListener;
    private ViewManager viewManager;
    private SetValueToActivityListener mSetValueToActivityListener;
    private RecordPlayAndStopUtils mRecordPlayAndStopUtils;

    public ColoredLinearyLayout(Context context) {
        this(context, null);
    }

    private ColoredLinearyLayout get() {
        return this;
    }

    public ColoredLinearyLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColoredLinearyLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        zanyEditTexts = new ArrayList<>();
        mContext = context;
        audioTrackStopPlayListener = this;
        viewManager = new ViewManager();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.parseColor("#f7f7f7")); // -> window background color !
        super.onDraw(canvas);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        textParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textParams.setMargins(0, 0, 0, 0);

        otherParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        otherParams.setMargins(DpUtils.dp2Px(getContext(), 10),
                DpUtils.dp2Px(getContext(), 10),
                DpUtils.dp2Px(getContext(), 10),
                10); // ->margin may be let created bitmap has a black backgroud !
        imageParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        imageParams.setMargins(DpUtils.dp2Px(getContext(), 10),
                DpUtils.dp2Px(getContext(), 0),
                DpUtils.dp2Px(getContext(), 10),
                10);
    }


    public ZanyEditText addEditText(int index, @Nullable String text,
                                    @Nullable Action1<Integer> delete, boolean hasCheckBox,
                                    boolean isChecked) {
        EditTextCheckBoxInfo editTextCheckBoxInfo = new EditTextCheckBoxInfo();

        // only add EditText
        View checkBoxLayout = LayoutInflater.from(getContext()).inflate(R.layout
                .item_add_checkbox, this, false);
        // ZanyEditText itemEt = new ZanyEditText(getContext());

        CheckBox itemCheckBox = (CheckBox) checkBoxLayout.findViewById(R.id.ck_item_add);
        ZanyEditText itemEt = (ZanyEditText) checkBoxLayout.findViewById(R.id.ze_edittext);
        ProgressBar pbTrasluteProcess = (ProgressBar) checkBoxLayout.findViewById(R.id.pb_traslute_process);


        if (hasCheckBox) {
            itemCheckBox.setVisibility(VISIBLE);
        } else {
            itemCheckBox.setVisibility(GONE);
        }

        itemCheckBox.setChecked(isChecked);

        setEditTextAndCheckListColor(isChecked, itemEt, itemCheckBox);
        itemEt.setiEditTextPasteCallback(this);
        itemEt.setOnFocusChangeListener(this);
        itemEt.setFocusable(true);
        itemEt.setFocusableInTouchMode(true);
        itemEt.requestFocus();//request focus
        itemEt.findFocus();//get focus


        itemEt.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Forever.maxWords)});
        int id = View.generateViewId();
        itemEt.setId(id);


        focusViewId = id;
        focusViewStatus = true;

        itemEt.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                int start = itemEt.getSelectionStart();
                int end = itemEt.getSelectionEnd();
                String te = start + " , " + end;
                if (TextUtils.isEmpty(itemEt.getText().toString()) || (start + end == 0)) {

                    if (itemCheckBox.getVisibility() == VISIBLE) {
                        itemCheckBox.setVisibility(GONE);
                    } else {
                        int pos = get().indexOfChild(checkBoxLayout);
                        // delete self and up AudioView or ImageView
                        if (delete != null) {
                            delete.accept(pos);
                        } else {
                        }
                    }
                    return true;
                }
            }
            return false;
        });


        editTextAddTextChangedListener(itemEt);
        editTextAddEditorActionListener(itemEt);
        checkBoxCheckedListener(itemEt, itemCheckBox);
        editTextClick(itemEt);

        String value = (text == null) ? "" : text;
        itemEt.setText(value);
        if (value.trim().length() > 0) {
            itemEt.setSelection(itemEt.getText().toString().trim().length());
        }

//        itemEt.setBackgroundColor(getResources()
//                .getColor(R.color.color_window_background)); // window bg!
        itemEt.setPadding(
                DpUtils.dp2Px(getContext(), 0),
                DpUtils.dp2Px(getContext(), 0),
                DpUtils.dp2Px(getContext(), 20),
                0);

        editTextCheckBoxInfo.setId(id);
        editTextCheckBoxInfo.setZanyEditText(itemEt);
        editTextCheckBoxInfo.setCheckBox(itemCheckBox);
        editTextCheckBoxInfo.setShowedCheckBox(hasCheckBox);
        zanyEditTexts.add(editTextCheckBoxInfo);

        get().addView(checkBoxLayout, index, textParams);
        return itemEt;
    }


    private void setCheckBoxButtonDrawable(CheckBox checkBox, boolean isChecked, int checkListColorResId) {
        VectorDrawableCompat vectorDrawable;
        if (isChecked) {
            if (checkListColorResId == 0) {
                checkListColorResId = R.color.checklist_checked_color;
            }
            vectorDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    R.drawable.ic_gome_sys_ic_detailed, mContext.getTheme());
        } else {
            if (checkListColorResId == 0) {
                checkListColorResId = R.color.checklist_unchecked_color;
            }
            vectorDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    R.drawable.ic_gome_sys_ic_detailed_unselected, mContext.getTheme());
        }
        vectorDrawable.setTint(getResources().
                getColor(checkListColorResId));
        checkBox.setButtonDrawable(vectorDrawable);
    }

    private void editTextClick(ZanyEditText itemEt) {

        itemEt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemEt.setFocusable(true);
                itemEt.setFocusableInTouchMode(true);
                itemEt.requestFocus();
                itemEt.findFocus();
                ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .showSoftInput(itemEt, InputMethodManager.SHOW_FORCED);

                if (null != mSetValueToActivityListener) {
                    mSetValueToActivityListener.onClickColoredLayoutEditText();
                }
            }
        });
        itemEt.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                itemEt.setFocusable(true);
                itemEt.setFocusableInTouchMode(true);
                itemEt.requestFocus();
                itemEt.findFocus();
                ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .showSoftInput(itemEt, InputMethodManager.SHOW_FORCED);
                if (null != mSetValueToActivityListener) {
                    mSetValueToActivityListener.onClickColoredLayoutEditText();
                }
                return false;
            }
        });
    }

    private void checkBoxCheckedListener(ZanyEditText itemEt, CheckBox itemCheckBox) {

        itemCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setIsDoEdit(true);
                setEditTextAndCheckListColor(isChecked, itemEt, itemCheckBox);
            }
        });
    }


    private void setEditTextAndCheckListColor(boolean isChecked, ZanyEditText itemEt, CheckBox itemCheckBox) {
        int textCheckedColorResId = 0;
        int textUnCheckColorResId = 0;
        int checkListCheckedColorResId = 0;
        int checkListUnCheckColorResId = 0;

        if (null != viewManager) {
            textCheckedColorResId = viewManager.getTextCheckedColorResId();
            textUnCheckColorResId = viewManager.getTextUnCheckColorResId();
            checkListCheckedColorResId = viewManager.getCheckListCheckedColorResId();
            checkListUnCheckColorResId = viewManager.getCheckListUnCheckColorResId();

        }

        if (isChecked) {
            if (textCheckedColorResId == 0) {
                textCheckedColorResId = R.color.font_black_4;
            }
            itemEt.setTextColor(mContext.getColor(textCheckedColorResId));
            setCheckBoxButtonDrawable(itemCheckBox, isChecked, checkListCheckedColorResId);
        } else {
            if (textUnCheckColorResId == 0) {
                textUnCheckColorResId = R.color.font_black_2;
            }
            itemEt.setTextColor(mContext.getColor(textUnCheckColorResId));
            setCheckBoxButtonDrawable(itemCheckBox, isChecked, checkListUnCheckColorResId);
        }
    }


    private void editTextAddEditorActionListener(ZanyEditText itemEt) {

        itemEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (null == event) {
                    return false;
                }
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                        && KeyEvent.ACTION_DOWN == event.getAction())) {

                    //get last EditText is has checkbox
                    boolean isHasCheckBox = previousEditTextCheckboxStatus();
                    if (null != viewManager) {
                        viewManager.addEditText(isHasCheckBox, get());
                    }
                }
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });

    }

    private boolean previousEditTextCheckboxStatus() {

        int childCount = get().getChildCount();
        boolean isHasCheckBox = false;
        for (int index = 0; index < childCount; index++) {
            View child = get().getChildAt(index);
            if (child instanceof RelativeLayout) {
                RelativeLayout rl = (RelativeLayout) child;
                if (rl.getChildAt(0) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                    ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
                    if (editText.isFocused()) {
                        int visibile = checkBox.getVisibility();
                        if (visibile == 0) {
                            //visible
                            isHasCheckBox = true;
                            return isHasCheckBox;
                        } else {
                            //gone or invisible
                            isHasCheckBox = false;
                            return isHasCheckBox;
                        }
                    }
                }
            }
        }
        return isHasCheckBox;
    }

    private boolean previousEditTextCheckboxIsChecked() {

        int childCount = get().getChildCount();
        boolean isHasCheckBox = false;
        for (int index = 0; index < childCount; index++) {
            View child = get().getChildAt(index);
            if (child instanceof RelativeLayout) {
                RelativeLayout rl = (RelativeLayout) child;
                if (rl.getChildAt(0) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                    ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
                    if (editText.isFocused()) {
                        int visibile = checkBox.getVisibility();
                        if (visibile == 0) {
                            //visible
                            isHasCheckBox = true;
                            return checkBox.isChecked();
                        } else {
                            //gone or invisible
                            isHasCheckBox = false;
                            return false;
                        }
                    }
                }
            }
        }
        return isHasCheckBox;
    }

    private void editTextAddTextChangedListener(ZanyEditText itemEt) {


        itemEt.addTextChangedListener(new TextWatcher() {
            String beforeTexts;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                beforeTexts = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String texts = s.toString();
                if (texts.contains("\n")) {
                    //get last EditText is has checkbox
                    boolean isHasCheckBox = previousEditTextCheckboxStatus();
                    boolean isChecked = previousEditTextCheckboxIsChecked();
                    String[] arrStr = texts.split("\n");
                    String sBefore = "";
                    String sAfter = "";
                    if (null != arrStr && arrStr.length <= 2) {
                        if (arrStr.length > 0) {
                            sBefore = arrStr[0];
                        }
                        if (arrStr.length > 1) {
                            sAfter = arrStr[1];
                        }
                        itemEt.setText(sBefore);
                        InsertIndex user = getUserInsertIndex(true);
                        if (null != viewManager) {
                            addEditText(user.index + 1, sAfter, viewManager.delete(get()), isHasCheckBox, false);
                            viewManager.getNodeList().add(user.index + 1, sAfter);
                        }
                    }
                }
            }
        });


    }


    public void setCheckBoxs(boolean isClickEnter) {
        if (null != zanyEditTexts) {
            for (int i = 0; i < zanyEditTexts.size(); i++) {
                EditTextCheckBoxInfo editTextCheckBoxInfo = zanyEditTexts.get(i);
                //   LogUtils.d("editTextCheckBoxInfo.getId()--- " + editTextCheckBoxInfo.getId
                // () + "    ----" + i);
                if (editTextCheckBoxInfo.getId() == focusViewId) {
                    if (focusViewStatus) {
                        if (isClickEnter) {
                            if (zanyEditTexts.get(i - 1).getShowedCheckBox()) {
                                editTextCheckBoxInfo.getCheckBox().setVisibility(VISIBLE);
                            } else {
                                editTextCheckBoxInfo.getCheckBox().setVisibility(GONE);
                            }
                        } else {

                            if (editTextCheckBoxInfo.getShowedCheckBox()) {
                                editTextCheckBoxInfo.setShowedCheckBox(false);
                                editTextCheckBoxInfo.getCheckBox().setVisibility(GONE);
                            } else {
                                editTextCheckBoxInfo.setShowedCheckBox(true);
                                editTextCheckBoxInfo.getCheckBox().setVisibility(VISIBLE);
                            }
                            editTextCheckBoxInfo.getCheckBox().setChecked(false);
                            if (null != viewManager) {
                                if (viewManager.getTextUnCheckColorResId() == 0) {
                                    editTextCheckBoxInfo.getZanyEditText().setTextColor(mContext.getColor(R.color.font_black_2));
                                } else {
                                    editTextCheckBoxInfo.getZanyEditText().setTextColor(mContext.getColor(viewManager.getTextUnCheckColorResId()));
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public ImageView addImage(int index, @NonNull String path, @Nullable Action1<Integer> delete) {
        // only add image
        View imageLayout = LayoutInflater.from(getContext()).inflate(R.layout.item_add_image,
                this, false);
        ImageView itemImg = (ImageView) imageLayout.findViewById(R.id.iv_add_img);
        int id = View.generateViewId();
        itemImg.setId(id);
        // check img file is deleted ?
        File imgFile = new File(path);
        boolean imgMiss = !imgFile.exists() || !imgFile.isFile();
        if (imgMiss) {
            // if img file delete by other app
            ViewTreeObserver vto2 = itemImg.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = itemImg.getWidth();
                    if (width <= 0) {
                        width = get().getWidth();
                    }
                    if (width <= 0) {
                        width = ((View) get().getParent()).getWidth();
                    }
                    int height = width; // height = width !

                    //   LogUtils.e("imgview width ==== " + width);
                    if (path.contains("/")) {
                        if (null != getContext()) {
                            Glide.with(getContext().getApplicationContext()).load(path)
//                            .placeholder(R.drawable.default_icon)
                                    .error(R.drawable.default_icon)
                                    .override(width, height)
                                    .into(itemImg);
                        }
                    } else {
                        if (InsertData.iconMaps.get(path) != null) {
                            itemImg.setImageResource(InsertData.iconMaps.get(path));
                        }
                    }

                    itemImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else {

            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            ViewTreeObserver vto2 = itemImg.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = itemImg.getWidth();
                    if (width <= 0) {
                        width = get().getWidth();
                    }
                    if (width <= 0) {
                        width = ((View) get().getParent()).getWidth();
                    }
                    DecimalFormat df = new DecimalFormat("0.00");
                    float scale = Float.parseFloat(df.format((float) width / (float) opts.outWidth));
                    int height = (int) (scale * (opts.outHeight));

                    if (null != getContext()) {
                        Glide.with(getContext().getApplicationContext()).load(path)
                                //.placeholder(R.drawable.default_icon)
                                .error(R.drawable.default_icon)
                                .override(width - DpUtils.dp2Px(getContext(), 20), height).fitCenter()
                                .into(itemImg);
                    }
                    itemImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });

        }
//        imageLayout.setBackgroundColor(getResources()
//                .getColor(R.color.color_window_background)); // window bg!

        imageLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                //clickImageViewBlank(id);
                if (NoteConfig.ISPREVIEWMODE) {
                    intoImageDetail(path);
                }
                if (null != mSetValueToActivityListener) {
                    mSetValueToActivityListener.onClickColoredLayoutChildrenView();
                }
            }
        });

        get().addView(imageLayout, index, imageParams);
        return itemImg;
    }

    private void intoImageDetail(String path) {

        Intent intent = new Intent(mContext, PhotoActivity.class);
        intent.putExtra("photo_path", path);
        mContext.startActivity(intent);

    }


    public ImageView addVideo(int index, @NonNull String path, @Nullable Action1<Integer> delete) {
        // only add image

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);

        Bitmap bitmap = media.getFrameAtTime();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();


        View imageLayout = LayoutInflater.from(getContext()).inflate(R.layout.item_add_video,
                this, false);
        ImageView itemImg = (ImageView) imageLayout.findViewById(R.id.iv_add_img);
        ZanyEditText itemEditText = (ZanyEditText) imageLayout.findViewById(R.id.et_delete_mark);

        FrameLayout flPlay = (FrameLayout) imageLayout.findViewById(R.id.fl_play);


        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) itemEditText
                .getLayoutParams();

        // ImageView itemImg = new ImageView(getContext());
        int id = View.generateViewId();

        itemImg.setId(id);
        itemEditText.setId(id);

        // check img file is deleted ?
        File imgFile = new File(path);
        boolean imgMiss = !imgFile.exists() || !imgFile.isFile();
        if (null == bitmap) {
            // if img file delete by other app
            ViewTreeObserver vto2 = itemImg.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = itemImg.getWidth();
                    if (width <= 0) {
                        width = get().getWidth();
                    }
                    if (width <= 0) {
                        width = ((View) get().getParent()).getWidth();
                    }
                    int height = itemImg.getWidth(); // height = width !

                    //   LogUtils.e("imgview width ==== " + width);
                    Glide.with(getContext()).load(path)
//                            .placeholder(R.drawable.default_icon)
                            .error(R.drawable.default_icon)
                            .override(width, height)
                            .into(itemImg);

                    params.height = height - DpUtils.dp2Px(getContext(), 40);

//                    itemEditText.setLayoutParams(params);
//                    itemEditText.setTextSize(height);

                    itemImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        } else {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, opts);
            //                LogUtils.e("outWidth = " + opts.outWidth);  // ok
            ViewTreeObserver vto2 = itemImg.getViewTreeObserver();
            vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    int width = itemImg.getWidth();
                    if (width <= 0) {
                        width = get().getWidth();
                    }
                    if (width <= 0) {
                        width = ((View) get().getParent()).getWidth();
                    }
                    int height = (int) ((width + 0.5f) / (bitmap.getWidth() + 0.5f) * (bitmap
                            .getHeight()
                            + 0.5f));


                    Glide.with(getContext()).load(bytes)
//                            .placeholder(R.drawable.default_icon)
                            .error(android.R.drawable.stat_notify_error)
                            .override(width - DpUtils.dp2Px(getContext(), 20), height)
                            .into(itemImg);

                    params.height = height - DpUtils.dp2Px(getContext(), 40);

//                    itemEditText.setLayoutParams(params);
//                    itemEditText.setTextSize(height);

                    itemImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }
        imageLayout.setBackgroundColor(getResources()
                .getColor(R.color.color_window_background)); // window bg!


//        imageLayout.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//               // clickImageViewBlank(id);
//
//            }
//        });

        itemEditTextListener(itemEditText, imageLayout, delete);
        clickPlay(path, flPlay);

        get().addView(imageLayout, index, otherParams);
        return itemImg;
    }

    private void clickPlay(String path, FrameLayout flPlay) {

        flPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(path), "video/*");
                getContext().startActivity(intent);
            }
        });


    }

    public void addWebView(int index, @Nullable String text) {

//        ContentWebView contentWebView = new ContentWebView(getContext());
//        contentWebView.loadUrl(text);
//        get().addView(contentWebView, index, textParams);
    }


    private void itemEditTextListener(ZanyEditText itemEditText, View imageLayout,
                                      Action1<Integer> delete) {
        itemEditText.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                int start = itemEditText.getSelectionStart();
                int end = itemEditText.getSelectionEnd();
                String te = start + " , " + end;
                if (TextUtils.isEmpty(itemEditText.getText().toString()) || (start + end == 0)) {
                    int pos = get().indexOfChild(imageLayout);
                    // delete self and up AudioView or ImageView
                    if (delete != null) {
                        delete.accept(pos);
                    } else {
                    }
                    return true;
                }
            }
            return false;
        });


        itemEditText.addTextChangedListener(new TextWatcher() {
            String text = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    itemEditText.setText(text);
                }

            }
        });


    }


    private void clickImageViewBlank(int id) {

//        for (int i = 0; i < get().getChildCount(); i++) {
//            View child = get().getChildAt(i);
//
//            if (child instanceof LinearLayout) {
//
//                LinearLayout ll = (LinearLayout) child;
//                ImageView imageView = (ImageView) ll.getChildAt(0);
//                if (imageView.getId() == id) {
//                    if (null != get().getChildAt(i + 1)) {
//                        get().get().getChildAt(i + 1).requestFocus();
//                        if (get().getChildAt(i + 1) instanceof RelativeLayout) {
//                            RelativeLayout rl = (RelativeLayout) get().getChildAt(i + 1);
//                            ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
//                            KeyboardUtils.showSoftInput(editText);
//                        }
//                    }
//                }
//            }
//        }


    }

    /**
     * @param audioPath if insert path == null ,else path is a file path !
     */
    public void addAudio(int index, @Nullable String audioPath, @Nullable Action1<Integer> delete, long time) {
        // only add audio
        View audioLayout = LayoutInflater.from(getContext()).inflate(R.layout.item_add_audio,
                this, false);
        AnimationDrawable animationDrawable = (AnimationDrawable) mContext.getResources().getDrawable(
                R.drawable.record_play_anim);

        RelativeLayout rlRecordView = (RelativeLayout) audioLayout.findViewById(R.id.rl_record_view);
        ImageView ivPalyView = (ImageView) audioLayout.findViewById(R.id.iv_paly_view);
        TextView mTvDurationTime = (TextView) audioLayout.findViewById(R.id.tv_duration_time);
        mTvDurationTime.setText(String.valueOf(time));

        int id = View.generateViewId();
        ivPalyView.setId(id);

        ivPalyView.setBackground(animationDrawable);

        playAnimatorGetRes(rlRecordView, ivPalyView, audioPath, animationDrawable);

        addSpreadAnimator(rlRecordView, time);

        //add audiopath and imageview id to map save
        int viewId = ivPalyView.getId();
        RecordPool.map.put(viewId, audioPath);

        // todo: audioLayout.setAudioPath(audioPath);
        get().addView(audioLayout, index);

    }


    private void playAnimatorGetRes(RelativeLayout rlRecordView, ImageView ivPalyView, String audioPath, AnimationDrawable animationDrawable) {

        AudioPlayManager audioPlayManager = AudioPlayManager.getInstance();
        audioPlayManager.setAudioTrackStopPlayListener(audioTrackStopPlayListener);

        rlRecordView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                ImageView imageView = ivPalyView;

                if (null == mRecordPlayAndStopUtils) {
                    mRecordPlayAndStopUtils = new RecordPlayAndStopUtils();
                }
                mRecordPlayAndStopUtils.playOrStop(get(), mContext, imageView, audioPath, animationDrawable, audioPlayManager);
                if (null != mSetValueToActivityListener) {
                    mSetValueToActivityListener.onClickColoredLayoutChildrenView();
                }
            }
        });
    }

    @Override
    public void audioTrackStopPlay(ImageView imageView, AnimationDrawable animationDrawable) {
        if (null != animationDrawable) {
            NoteConfig.AUDIOPLAYID = 0;
            animationDrawable.stop();
            animationDrawable.selectDrawable(0);
        }

    }

    private void addSpreadAnimator(RelativeLayout rlRecordView, long time) {

        DecimalFormat df = new DecimalFormat("0.00");
        float scale = Float.parseFloat(df.format((float) time / 60f));
        int mostLength = DpUtils.dp2Px(mContext, 200);
        int minLength = DpUtils.dp2Px(mContext, 45);
        int length = (int) (scale * mostLength);
        ValueAnimator va;
        va = ValueAnimator.ofInt(0, length + minLength);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int w = (Integer) valueAnimator.getAnimatedValue();
                rlRecordView.getLayoutParams().width = w;
                rlRecordView.requestLayout();
            }
        });
        va.setDuration(500);
        va.start();

    }

    public TextView addTextView(Context context, int index, @Nullable String names) {
        // only add TextView
        View textViewLayout = LayoutInflater.from(getContext()).inflate(R.layout
                .item_add_textview, this, false);
        // todo: audioLayout.setAudioPath(audioPath);
        TextView tvLabelsName = (TextView) textViewLayout.findViewById(R.id.tv_labels_name);
        tvLabelsName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(context, LabelManageActivity.class);
//                context.startActivity(intent);

            }
        });
        addView(textViewLayout, index);

        return tvLabelsName;
    }

    public void addCheckBox(int index) {
        // only add CheckBox
        // only add TextView
        View checkBoxLayout = LayoutInflater.from(getContext()).inflate(R.layout
                .item_add_checkbox, this, false);
        addView(checkBoxLayout, index);

    }


    public InsertIndex getUserInsertIndex(boolean isEnter) {
        InsertIndex obj = new InsertIndex();
        View focusedChild = this.getFocusedChild(); // nullable
        int userIndex = this.indexOfChild(focusedChild);
        if (userIndex < 0) {
            userIndex = this.getChildCount() - 1;
        }
        obj.index = userIndex;
        // if indexOfChild(focusedChild) == null ,index ==0 ; ok it will insert into the first pos
        // if index = length , ok it will insert into the last pos

        if (focusedChild instanceof RelativeLayout) {
            RelativeLayout rl = (RelativeLayout) focusedChild;

            if (rl.getChildAt(0) instanceof CheckBox) {

                CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                ZanyEditText etDynamic = (ZanyEditText) rl.getChildAt(1);

                // EditText etDynamic = (EditText) focusedChild;
                int index = etDynamic.getSelectionStart();
                String beforeStr = etDynamic.getText().subSequence(0, index).toString();
                String afterStr = etDynamic.getText().subSequence(index, etDynamic.getText()
                        .length()
                ).toString();
                obj.after = afterStr;
                obj.before = beforeStr;
                if (!isEnter) {
                    etDynamic.setText(beforeStr);
                    etDynamic.setSelection(index);
                }
            }
        }
        return obj;
    }


    public InsertIndex getUserInsertIndexAndSetText(boolean isEnter, String text) {
        InsertIndex obj = new InsertIndex();
        View focusedChild = this.getFocusedChild(); // nullable
        int userIndex = this.indexOfChild(focusedChild);
        if (userIndex < 0) {
            userIndex = this.getChildCount() - 1;
        }
        obj.index = userIndex;
        // if indexOfChild(focusedChild) == null ,index ==0 ; ok it will insert into the first pos
        // if index = length , ok it will insert into the last pos

        if (focusedChild instanceof RelativeLayout) {
            RelativeLayout rl = (RelativeLayout) focusedChild;

            if (rl.getChildAt(0) instanceof CheckBox) {

                CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                ZanyEditText etDynamic = (ZanyEditText) rl.getChildAt(1);

                // EditText etDynamic = (EditText) focusedChild;
                int index = etDynamic.getSelectionStart();
                String beforeStr = etDynamic.getText().subSequence(0, index).toString();
                String afterStr = etDynamic.getText().subSequence(index, etDynamic.getText()
                        .length()
                ).toString();
                obj.after = afterStr;
                obj.before = beforeStr;

                if (!isEnter) {
                    etDynamic.setText(beforeStr + text);
                    //etDynamic.setSelection(index);
                }
            }
        }
        return obj;
    }


    public void getEdittextAndSetText(boolean isEnter, String text) {
        View focusedChild = this.getFocusedChild(); // nullable

        if (focusedChild instanceof RelativeLayout) {
            RelativeLayout rl = (RelativeLayout) focusedChild;

            if (rl.getChildAt(0) instanceof CheckBox) {

                CheckBox checkBox = (CheckBox) rl.getChildAt(0);
                ZanyEditText etDynamic = (ZanyEditText) rl.getChildAt(1);

                int indexStart = etDynamic.getSelectionStart();
                int indexEnd = etDynamic.getSelectionEnd();
                String beforeStr = etDynamic.getText().subSequence(0, indexStart).toString();
                String afterStr = etDynamic.getText().subSequence(indexEnd, etDynamic.getText()
                        .length()
                ).toString();

                int allLength = etDynamic.getText().length() + text.length();
                if (allLength <= Forever.maxWords) {
                    etDynamic.setText(beforeStr + text + afterStr);
                    etDynamic.setSelection((beforeStr + text).length());
                } else {
                    if (etDynamic.getText().length() <= Forever.maxWords) {
                        int length = Forever.maxWords - etDynamic.getText().length();
                        if (text.length() >= length) {
                            String str = text.substring(0, length);
                            etDynamic.setText(beforeStr + str + afterStr);
                            etDynamic.setSelection((beforeStr + str).length());
                        } else {
                            etDynamic.setText(beforeStr + text + afterStr);
                            etDynamic.setSelection((beforeStr + text).length());
                        }
                    }
                }

            }
        }

    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            focusViewId = v.getId();
            focusViewStatus = hasFocus;
        }

    }

    public void clearZanyEditTexts() {
        if (null != zanyEditTexts) {
            zanyEditTexts.clear();
        }
    }

    @Override
    public void onCut(Object o) {

    }

    @Override
    public void onCopy(Object o) {

    }

    @Override
    public void onPaste(Object o) {
        if (null == o) {
            return;
        }
        String texts = o.toString();
        if (texts == null) {
            return;
        }
        if (texts.contains("\n")) {
            //get last EditText is has checkbox
            boolean isHasCheckBox = previousEditTextCheckboxStatus();
            boolean isChecked = previousEditTextCheckboxIsChecked();
            String afterText = "";
            InsertIndex insertIndex = null;
            String[] arrStr = texts.split("\n");
            if (null != arrStr) {
                for (int i = 0; i < arrStr.length; i++) {
                    if (i == 0) {
                        //get focause exittext and get selection left text and right text
                        insertIndex = getUserInsertIndexAndSetText(false, arrStr[0]);
                        afterText = insertIndex.after;
                    } else if (i == arrStr.length - 1) {
                        if (null != viewManager) {
                            addEditText(insertIndex.index + i, arrStr[i] + afterText, viewManager.delete(get()), isHasCheckBox, false);
                            viewManager.getNodeList().add(insertIndex.index + i, arrStr[i] + afterText);
                        }
                    } else {
                        if (null != viewManager) {
                            addEditText(insertIndex.index + i, arrStr[i], viewManager.delete(get()), isHasCheckBox, false);
                            viewManager.getNodeList().add(insertIndex.index + i, arrStr[i]);
                        }
                    }
                }
            }
        } else {
            getEdittextAndSetText(false, texts);
        }
    }

    public void setValueToActivityListener(SetValueToActivityListener setValueToActivityListener) {
        mSetValueToActivityListener = setValueToActivityListener;
    }

    public void setIsDoEdit(boolean isDoEdit) {
        //((NoteCreateActivity) mActivity).setDoEdit(isDoEdit);
        if (null != mSetValueToActivityListener) {
            mSetValueToActivityListener.setDoEdit(isDoEdit);
        }
    }

    public void setNullListener() {
        audioTrackStopPlayListener = null;
    }

    public interface Action1<T> {
        void accept(T t);
    }

}