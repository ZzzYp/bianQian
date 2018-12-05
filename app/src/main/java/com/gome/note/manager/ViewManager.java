package com.gome.note.manager;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.KeyboardUtils;
import com.gome.note.R;
import com.gome.note.base.BaseActivity;
import com.gome.note.db.NoteDaoEngine;
import com.gome.note.db.config.NoteConfig;
import com.gome.note.domain.InsertIndex;
import com.gome.note.domain.Memo;
import com.gome.note.entity.ContentInfo;
import com.gome.note.entity.EditContentInfo;
import com.gome.note.entity.Forever;
import com.gome.note.entity.LabelInfo;
import com.gome.note.entity.NodeType;
import com.gome.note.entity.PocketInfo;
import com.gome.note.ui.create.NoteCreateActivity;
import com.gome.note.ui.create.SetValueToActivityListener;
import com.gome.note.ui.create.presenter.NoteCreatePresenter;
import com.gome.note.utils.ActivityCommonUtils;
import com.gome.note.utils.ContentParser;
import com.gome.note.utils.FileUtils;
import com.gome.note.utils.StrHelper;
import com.gome.note.view.ColoredLinearyLayout;
import com.gome.note.view.ZanyEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created on 2017/4/19.
 *
 * @author pythoncat
 */

public class ViewManager {

    private static final String TAG = "ViewManager";
    private static LinkedList<String> nodeList = new LinkedList<>();
    private NoteCreatePresenter presenter;
    public static String summary;
    private Context mContext;
    private static long mUpdateId;
    private static boolean isHasWebView;
    private static boolean count;
    private static String removedText;
    public static PocketInfo pocketInfo;
    private static boolean isHasAudio;
    public static ArrayList<LabelInfo> mItemLabelInfos = new ArrayList<>();
    private SetValueToActivityListener mSetValueToActivityListener;
    private static int textCheckedColorResId, textUnCheckColorResId, checkListCheckedColorResId, checkListUnCheckColorResId;

    public void initView(NoteCreatePresenter mPresenter, Context context, long updateId, String
            content, @NonNull final ColoredLinearyLayout layout,
                         @NonNull Runnable init,
                         @NonNull Runnable finish) {

        presenter = mPresenter;
        mContext = context;
        mUpdateId = updateId;

        if (updateId <= 0) {
            textCheckedColorResId = NoteConfig.SKIN_BG_TEXT_CHECKED_COLOR[0];
            textUnCheckColorResId = NoteConfig.SKIN_BG_TEXT_UNCHECK_COLOR[0];
            init.run();
            if (null != content && content.contains("\n")) {
                String[] contentArr = content.split("\n");
                if (null != contentArr && contentArr.length > 0) {
                    for (int i = 0; i < contentArr.length; i++) {
                        ZanyEditText et = layout.addEditText(i, contentArr[i], delete(layout),
                                false, false);
                        nodeList.add(i, contentArr[i]);
                    }
                } else {
                    addSingleString(layout, content);
                }

            } else {
                addSingleString(layout, content);
            }
            if (null != mSetValueToActivityListener) {
                mSetValueToActivityListener.setSkinBgId(mContext.getResources().getInteger(NoteConfig.SKIN_BG_ID[0]));
                mSetValueToActivityListener.withoutData();
            }
            finish.run();
        } else {
            //get pockinfo
            getPockInfoData(updateId, layout);
        }
    }


    public void addSingleString(ColoredLinearyLayout layout, String content) {
        ZanyEditText editText = layout.addEditText(0, content, null, false, false);
        editText.setHint(R.string.click_2_add_content);
        nodeList.clear();
        nodeList.add(0, "");
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        KeyboardUtils.showSoftInput(editText);
    }


    private void getPockInfoData(long updateId, ColoredLinearyLayout layout) {

        //pocketInfo = presenter.getPocketInfo(updateId);
        //showDataFromInfos(pocketInfo, layout);
        presenter.getPocketInfo(updateId);
    }

    public void showDataFromInfos(PocketInfo info, ColoredLinearyLayout layout) {
        isHasWebView = info.isWebView();
        String webView = "";
        List<ContentInfo> contentInfos = info.getContents();

        if (isHasWebView) {
            for (int i = 0; i < contentInfos.size(); i++) {
                webView = contentInfos.get(i).getWebview();
            }

            if (null != webView && webView.length() > 0) {
                // load webView
                layout.addWebView(0, webView);
            }
        } else {

            nodeList.clear();
            boolean firstEditText = true;
            int index = 0;
            for (int i = 0; i < contentInfos.size(); i++) {
                String text = contentInfos.get(i).getText();
                String image = contentInfos.get(i).getImage();
                String video = contentInfos.get(i).getVideo();
                String audio = contentInfos.get(i).getAudio();
                String audioTime = contentInfos.get(i).getAudioTime();
                boolean hasCheckBox = contentInfos.get(i).isHasCheckBox();
                boolean isChecked = contentInfos.get(i).isChecked();

                if (null != mSetValueToActivityListener && null != info.getScheme()) {
                    mSetValueToActivityListener.setSkinBgId(Integer.parseInt(info.getScheme()) == 0
                            ? mContext.getResources().getInteger(NoteConfig.SKIN_BG_ID[0])
                            : Integer.parseInt(info.getScheme()));
                } else if (null != mSetValueToActivityListener && null == info.getScheme()) {
                    mSetValueToActivityListener.setSkinBgId(mContext.getResources().getInteger(NoteConfig.SKIN_BG_ID[0]));
                }

                if (null != text && text.length() >= 0 && (null == image || image.length() == 0)
                        && (null == audio || audio.length() == 0)) {

                    ZanyEditText et = layout.addEditText(index, text, delete(layout),
                            hasCheckBox, isChecked);
                    nodeList.add(index, text);
                    index = index + 1;
                }
                if (null != image && image.length() > 0) {
                    // load imageView
                    if (image.contains(";")) {
                        String[] images = image.split(";");
                        for (int j = 0; j < images.length; j++) {
                            String imagePath = images[j];
                            layout.addImage(index, imagePath, delete(layout));
                            nodeList.add(index, getImagePathString(imagePath));
                            index = index + 1;
                        }
                    } else {
                        layout.addImage(index, image, delete(layout));
                        nodeList.add(index, getImagePathString(image));
                        index = index + 1;
                    }
                }
                if (null != video && video.length() > 0) {
                    // load imageView
                    if (video.contains(";")) {
                        String[] videos = video.split(";");
                        for (int j = 0; j < videos.length; j++) {
                            String videoPath = videos[j];
                            layout.addVideo(index, videoPath, delete(layout));
                            nodeList.add(index, getVideoPathString(videoPath));
                            index = index + 1;
                        }

                    } else {
                        layout.addVideo(index, video, delete(layout));
                        nodeList.add(index, getVideoPathString(video));
                        index = index + 1;
                    }
                }

                if (null != audio && audio.length() > 0) {
                    // load imageView
                    if (audio.contains(";")) {
                        String[] audios = audio.split(";");
                        String[] audioTimes = audioTime.split(";");
                        for (int j = 0; j < audios.length; j++) {
                            String audioPath = audios[j];
                            String audioTimeStr = audioTimes[j];
                            layout.addAudio(index, audioPath, delete(layout),
                                    Integer.parseInt(audioTimeStr == null || audioTimeStr.length() == 0 ? "0" : audioTimeStr));
                            nodeList.add(index, getAudioPathString(audioPath));
                            index = index + 1;
                        }

                    } else {
                        layout.addAudio(index, audio, delete(layout),
                                Integer.parseInt(audioTime == null || audioTime.length() == 0 ? "0" : audioTime));
                        nodeList.add(index, getAudioPathString(audio));
                        index = index + 1;
                    }
                }

            }
            //add edittext
            boolean lastViewIsEditText = lastViewIsEditText(layout);

            if (!lastViewIsEditText) {
                ZanyEditText et = layout.addEditText(index, "", delete(layout), false, false);
                nodeList.add(index, "");
            }


        }
    }

    @NonNull
    public ColoredLinearyLayout.Action1<Integer> delete(ColoredLinearyLayout layout) {
        return position -> {
            int pos = position;
            // delete view | delete records | delete file !

            if (pos - 1 < 0) { // first item can not be delete !

                return;
            }
            // delete file
            String value = nodeList.get(pos);

            String realValue = null;
            if (StrHelper.isContainsImageKey(value)) {
                realValue = StrHelper.pickupImageKey(value);
            } else if (StrHelper.isContainsAudioKey(value)) {
                realValue = StrHelper.pickupAudioKey(value);
            }

            // delete nodes

            String text = "";

            View childRemoved = layout.getChildAt(pos);
            if (childRemoved instanceof RelativeLayout) {
                RelativeLayout rl = (RelativeLayout) childRemoved;
                ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
                editText.requestFocus();
                editText.setSelection(editText.getText().toString().trim().length());
                removedText = editText.getText().toString().trim();
            }


            // delete nodes
            nodeList.remove(pos);
            // delete views
            layout.removeViews(pos, 1);


            View child = layout.getChildAt(pos - 1);
            String previousValue = nodeList.get(pos - 1);

            if (null != previousValue && (previousValue.contains(Forever.IMG_HEAD) && previousValue.contains(Forever.IMG_FOOT)
                    || previousValue.contains(Forever.AUDIO_HEAD) && previousValue.contains(Forever.AUDIO_FOOT))) {
                ifRecordPlayingAndStopPlay(layout.getChildAt(pos - 1));
                nodeList.remove(pos - 1);
                // delete views
                layout.removeViews(pos - 1, 1);
                //delete file
                String path = "";
                if (previousValue.contains(Forever.IMG_HEAD) && previousValue.contains(Forever.IMG_FOOT)) {
                    path = previousValue.replace(Forever.IMG_HEAD, "").replace(Forever.IMG_FOOT, "");
                } else if (previousValue.contains(Forever.AUDIO_HEAD) && previousValue.contains(Forever.AUDIO_FOOT)) {
                    path = previousValue.replace(Forever.AUDIO_HEAD, "").replace(Forever.AUDIO_FOOT, "");
                }
                ActivityCommonUtils.deleteFile(path);
                if (null != layout.getChildAt(pos - 2)) {
                    if (layout.getChildAt(pos - 2) instanceof RelativeLayout) {
                        RelativeLayout rl = (RelativeLayout) layout.getChildAt(pos - 2);
                        ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
                        int index = editText.getText().toString().trim().length();
                        editText.setText(editText.getText().append(removedText));
                        removedText = "";
                        editText.setSelection(index);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                        editText.requestFocus();
                        editText.findFocus();
                    } else {
                        layout.getChildAt(pos - 2).requestFocus();
                    }
                } else {
                    if (layout.getChildCount() == 0) {
                        addSingleString(layout, removedText);
                    }
                }
            } else {

                if (child instanceof RelativeLayout) {
                    RelativeLayout rl = (RelativeLayout) child;
                    ZanyEditText editText = (ZanyEditText) rl.getChildAt(1);
                    int index = editText.getText().toString().trim().length();
                    editText.setText(editText.getText().append(removedText));
                    removedText = "";
                    editText.setSelection(index);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.findFocus();
                } else {

                    child.requestFocus();
                }
            }
        };
    }

    private void ifRecordPlayingAndStopPlay(View childAt) {

        if (null != childAt && childAt instanceof LinearLayout) {
            if (childAt instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) childAt;

                if (ll.getChildAt(0) instanceof RelativeLayout) {
                    RelativeLayout relativeLayout = (RelativeLayout) ll.getChildAt(0);
                    if (relativeLayout.getChildCount() > 0) {
                        View view = relativeLayout.getChildAt(0);
                        if (view instanceof ImageView) {

                            ImageView imageViewChild = (ImageView) view;
                            AnimationDrawable drawable = (AnimationDrawable) imageViewChild.getBackground();
                            if (drawable.isRunning()) {
                                drawable.stop();
                                drawable.selectDrawable(0);
                                //stop playing
                                AudioPlayManager audioPlayManager = AudioPlayManager.getInstance();
                                audioPlayManager.stopPlay();
                            }
                        }
                    }
                }
            }
        }


    }

    private void appendTextInRemovedView(ColoredLinearyLayout layout, int pos) {
        View removeV = layout.getChildAt(pos);
        String removeText = "";
        if (removeV instanceof EditText) {
            EditText rv = (EditText) removeV;
            removeText = rv.getText().toString().trim();
        }
        View childAt = layout.getChildAt(pos - 2);
        if (childAt instanceof EditText) {
            EditText ee = (EditText) childAt;
            ee.append(removeText);
            ee.requestFocus();
            KeyboardUtils.showSoftInput(ee);
        }
    }

    public void addImageCouple(@NonNull String imagePath,
                               @NonNull ColoredLinearyLayout layout) {
        InsertIndex user = layout.getUserInsertIndex(false);

        //if this is first image ,remove first edittext
        ifFirstImageRemoveEdittext(layout, user);
        // add imageView
        layout.addImage(user.index + 1, imagePath, delete(layout));
        nodeList.add(user.index + 1, getImagePathString(imagePath));
        // add editText
        String text = user.after == null ? "" : user.after;
        layout.addEditText(user.index + 1 + 1, text, delete(layout), false, false);
        nodeList.add(user.index + 1 + 1, text);
    }

    private void ifFirstImageRemoveEdittext(ColoredLinearyLayout layout, InsertIndex user) {

        int childCount = layout.getChildCount();
        if (childCount == 1) {
            View view = layout.getChildAt(0);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                String text = editText.getText().toString();
                if (null == text || text.length() == 0) {
                    layout.removeView(view);
                    nodeList.clear();
                    user.index = -1;
                }
            }
        }
    }


    public void addVideoCouple(@NonNull String videoPath,
                               @NonNull ColoredLinearyLayout layout) {
        InsertIndex user = layout.getUserInsertIndex(false);
        // add imageView
        layout.addVideo(user.index + 1, videoPath, delete(layout));
        nodeList.add(user.index + 1, getVideoPathString(videoPath));
        // add editText
        String text = user.after == null ? "" : user.after;
        layout.addEditText(user.index + 1 + 1, text, delete(layout), false, false);
        nodeList.add(user.index + 1 + 1, text);
    }

    @NonNull
    private String getImagePathString(String path) {
        return Forever.IMG_HEAD
                .concat(path)
                .concat(Forever.IMG_FOOT);
    }

    @NonNull
    private String getAudioPathString(String path) {
        return Forever.AUDIO_HEAD
                .concat(path)
                .concat(Forever.AUDIO_FOOT);
    }


    @NonNull
    private String getVideoPathString(String path) {
        return Forever.VIDEO_HEAD
                .concat(path)
                .concat(Forever.VIDEO_FOOT);
    }

    public void addEditText(boolean isSetCheckBox, @NonNull ColoredLinearyLayout layout) {
        InsertIndex user = layout.getUserInsertIndex(false);
        // add editText
        String text = user.after == null ? "" : user.after;
        layout.addEditText(user.index + 1, text, delete(layout), isSetCheckBox, false);
        nodeList.add(user.index + 1, text);
    }


    public void addAudioCouple(@Nullable String audioPath, @NonNull ColoredLinearyLayout
            layout, long time) {
        InsertIndex user = layout.getUserInsertIndex(false);
        // add audioView
        layout.addAudio(user.index + 1, audioPath, delete(layout), time);
        nodeList.add(user.index + 1, getAudioPathString(audioPath));
        // add editText
        String text = user.after == null ? "" : user.after;
        layout.addEditText(user.index + 1 + 1, text, delete(layout), false, false);
        nodeList.add(user.index + 1 + 1, text);

    }


    public void addCheckBoxCouple(@NonNull ColoredLinearyLayout layout) {
        InsertIndex user = layout.getUserInsertIndex(false);
        // add checkBox
        layout.addCheckBox(user.index + 1);
        nodeList.add(user.index + 1, "<isCheckBox>");
        // add editText
        String text = user.after == null ? "" : user.after;
        layout.addEditText(user.index + 1 + 1, text, delete(layout), false, false);
        nodeList.add(user.index + 1 + 1, text);
    }


    /**
     * it is a bad method !!!
     */
    private void resetTextNodeValues(@NonNull ColoredLinearyLayout layout) {
        int childCount = layout.getChildCount();
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

                    EditContentInfo editContentInfo = new EditContentInfo();
                    editContentInfo.setText(text);
                    editContentInfo.setHasCheckBox(isHasCheckBox);
                    editContentInfo.setChecked(isChecked);

                    //create a character string of jason
                    JSONObject jsonObj = new JSONObject();//jsonObj，json form
                    try {
                        jsonObj.put("text", text);
                        jsonObj.put("isHasCheckBox", isHasCheckBox);
                        jsonObj.put("isChecked", isChecked);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String value = jsonObj.toString();

                    if (index < nodeList.size()) {
                        nodeList.set(index, value);
                    }

                }
            }
        }
    }


    private void resetAudioNodeValues(ColoredLinearyLayout layout) {

        int childCount = layout.getChildCount();
        long time = 0;
        for (int index = 0; index < childCount; index++) {
            View child = layout.getChildAt(index);
            if (child instanceof LinearLayout) {
                LinearLayout ll = (LinearLayout) child;

                if (ll.getChildAt(0) instanceof RelativeLayout) {
                    if (ll.getChildCount() > 1) {
                        View view = ll.getChildAt(1);
                        if (view instanceof TextView) {
                            String timeStr = ((TextView) view).getText().toString().trim();
                            if (null != timeStr && timeStr.length() > 0) {
                                time = Long.parseLong(timeStr);
                            }

                        }
                    }
                    String audioPath = nodeList.get(index);
                    String value = audioPath + "-" + time;
                    if (index < nodeList.size()) {
                        nodeList.set(index, value);
                    }
                }
            }
        }
    }

    private ArrayList<ContentInfo> getContainerContent(@NonNull ColoredLinearyLayout
                                                               layout) {
        isHasAudio = false;
        resetTextNodeValues(layout);
        resetAudioNodeValues(layout);
        boolean isIconUri;
        String iconUri;
        ContentInfo contentInfo = null;
        ArrayList<ContentInfo> contentInfos = new ArrayList<>();
        int index = 0;
        int mark = 0;
        boolean setSummary = false;

        for (int i = 0; i < nodeList.size(); i++) {
            String text = nodeList.get(i);
            if (text.contains(Forever.IMG_HEAD) && text.contains(Forever.IMG_HEAD)) {

                String imagePath = text.substring(Forever.IMG_HEAD.length(), text.length() -
                        Forever.IMG_FOOT.length());

                if (null == contentInfo || i == 1) {
                    contentInfo = new ContentInfo();
                    contentInfo.setImage(imagePath);
                    Random rand = new Random(25);
                    index = rand.nextInt(10000);
                    contentInfo.setIndex(index);
                    contentInfos.add(contentInfo);

                } else {
                    String tempath = contentInfo.getImage();
                    if (null != tempath && tempath.length() > 0) {
                        contentInfo.setImage(tempath + ";" + imagePath);
                    } else {
                        contentInfo = new ContentInfo();
                        contentInfo.setImage(imagePath);
                        Random rand = new Random(25);
                        index = rand.nextInt(10000);
                        contentInfo.setIndex(index);
                        contentInfos.add(contentInfo);
                    }
                }


            } else if (text.contains(Forever.VIDEO_HEAD) && text.contains(Forever.VIDEO_FOOT)) {
                String videoPath = text.substring(Forever.VIDEO_HEAD.length(), text.length() -
                        Forever.VIDEO_FOOT.length());

                if (null == contentInfo || i == 1) {
                    contentInfo = new ContentInfo();
                    contentInfo.setVideo(videoPath);

                    Random rand = new Random(25);
                    index = rand.nextInt(10000);
                    contentInfo.setIndex(index);

                    contentInfos.add(contentInfo);

                } else {
                    String tempath = contentInfo.getVideo();
                    if (null != tempath && tempath.length() > 0) {
                        contentInfo.setVideo(tempath + ";" + videoPath);
                    } else {
                        contentInfo.setVideo(videoPath);
                    }
                }

            } else if (text.contains(Forever.AUDIO_HEAD) && text.contains(Forever.AUDIO_FOOT)) {

                isHasAudio = true;
                String[] audioArr = text.split("-");
                if (null != audioArr && audioArr.length > 1) {
                    String audioPath = audioArr[0].substring(Forever.AUDIO_HEAD.length(), audioArr[0].length() -
                            Forever.AUDIO_FOOT.length());
                    int time = Integer.parseInt(audioArr[1]);

                    if (null == contentInfo || i == 1) {
                        contentInfo = new ContentInfo();
                        contentInfo.setAudio(audioPath);
                        contentInfo.setAudioTime(String.valueOf(time));
                        Random rand = new Random(25);
                        index = rand.nextInt(10000);
                        contentInfo.setIndex(index);
                        contentInfos.add(contentInfo);

                    } else {
                        String tempath = contentInfo.getAudio();
                        String tempTime = contentInfo.getAudioTime();
                        if (null != tempath && tempath.length() > 0) {
                            contentInfo.setAudio(tempath + ";" + audioPath);
                            contentInfo.setAudioTime(tempTime + ";" + tempTime);
                        } else {
                            contentInfo = new ContentInfo();
                            contentInfo.setAudio(audioPath);
                            contentInfo.setAudioTime(String.valueOf(time));
                            Random rand = new Random(25);
                            index = rand.nextInt(10000);
                            contentInfo.setIndex(index);
                            contentInfos.add(contentInfo);
                        }
                    }
                }
            } else {

                contentInfo = new ContentInfo();
                // parse Json
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    String edittext = jsonObject.getString("text");
                    boolean isHasCheckBox = jsonObject.getBoolean("isHasCheckBox");
                    boolean isChecked = jsonObject.getBoolean("isChecked");


                    contentInfo.setText(edittext);
                    contentInfo.setHasCheckBox(isHasCheckBox);
                    contentInfo.setChecked(isChecked);
                    if (null != edittext && edittext.length() > 0 && !setSummary) {
                        summary = edittext;
                        setSummary = true;
                    }
                    Random rand = new Random(25);
                    index = rand.nextInt(10000);
                    contentInfo.setIndex(index);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                contentInfos.add(contentInfo);
            }

            if (i == 0) {
                contentInfos.set(0, contentInfo);
            } else {
                if (index == contentInfo.getIndex()) {
                    contentInfos.set(contentInfos.size() - 1, contentInfo);
                } else {
                    contentInfos.add(contentInfo);
                }
            }


        }

        String json = ContentParser.contentMap2Json(nodeList);
        return contentInfos;
    }


    private PocketInfo saveContent2NoteInfo(@NonNull ColoredLinearyLayout layout, long
            updateId, int skinBgId) {

        PocketInfo pocketInfo = new PocketInfo();
        if (updateId > 0) {
            pocketInfo.setId(updateId);
        }

        ArrayList<LabelInfo> itemLabelInfos = new ArrayList<>();
        for (int i = 0; i < mItemLabelInfos.size(); i++) {
            LabelInfo labelInfo = mItemLabelInfos.get(i);
            itemLabelInfos.add(labelInfo);
        }
        if (null != itemLabelInfos) {
            pocketInfo.setLabels(itemLabelInfos);
            for (int i = 0; i < itemLabelInfos.size(); i++) {
                String title = itemLabelInfos.get(i).getTitle();
                if (null != title && title.equals(mContext.getString(R.string.put_top))) {
                    pocketInfo.setStick(true);
                }
            }
        }


        ArrayList<ContentInfo> contentInfos = getContainerContent(layout);
        String title = ""; // now has no title !!!
        String contentHead = "";// now has no contentHead !!!
        com.gome.note.domain.NoteInfo info = new com.gome.note.domain.NoteInfo();

        info.title = title;
        info.contentHead = contentHead;
        info.lastModified = new Date().getTime();
        pocketInfo.setContents(contentInfos);
        pocketInfo.setIcon(getSummaryIcon(contentInfos));
        pocketInfo.setDateAdded(System.currentTimeMillis());
        pocketInfo.setDateModified(System.currentTimeMillis());
        pocketInfo.setSummary(getSummary(contentInfos));
        pocketInfo.setHasAudio(isHasAudio);
        pocketInfo.setScheme(String.valueOf(skinBgId));
        isHasAudio = false;


        return pocketInfo;
    }

    public String getSummaryIcon(ArrayList<ContentInfo> contentInfos) {
        for (ContentInfo content : contentInfos) {
            if (!TextUtils.isEmpty(content.getImage())) {
                return content.getImage();
            }
        }
        return "";
    }

    public String getSummary(ArrayList<ContentInfo> contentInfos) {
        for (ContentInfo content : contentInfos) {
            if (!TextUtils.isEmpty(content.getText())) {
                return content.getText();
            }
        }
        return "";
    }

    private boolean isEmpty(@NonNull ColoredLinearyLayout layout) {
        boolean empty = false;
        int count = layout.getChildCount();
        if (count == 0) {
            empty = true;
        } else if (count == 1) {
            View v = layout.getChildAt(0);
            if (v instanceof RelativeLayout) {
                if (null != ((RelativeLayout) v).getChildAt(1)) {
                    EditText et = (EditText) ((RelativeLayout) v).getChildAt(1);
                    empty = et.getText().toString().trim().isEmpty();
                }
            }
        } else {
            empty = false;
        }
        return empty;
    }

    public void exitCurrentPage(long updateId,
                                @NonNull Context context,
                                @NonNull ColoredLinearyLayout layout,
                                @NonNull Runnable finish,
                                boolean fromTele, int skinBgId) {
        if (updateId > 0) {
            if (isEmpty(layout)) {
                // do delete
                presenter.deleteNoteInfoById(updateId);
            } else {
                // do update
                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
                boolean isSuccess = presenter.updatePocket(pocketInfo);
            }
        } else {
            if (isEmpty(layout)) {
                // do not insert
                //finish.run();
            } else {
                // do insert
                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
                long noteId = presenter.createPocket(pocketInfo);
                if (noteId != -1) {
                    if (null != mSetValueToActivityListener) {
                        mSetValueToActivityListener.setNoteId(noteId);
                    }
                }
            }
        }
    }


    public void previewSaveContent(long updateId,
                                   @NonNull Context context,
                                   @NonNull ColoredLinearyLayout layout,
                                   @NonNull Runnable finish,
                                   boolean fromTele, int skinBgId) {
        if (updateId > 0) {
            if (isEmpty(layout)) {
                // do delete
                presenter.deleteNoteInfo(pocketInfo);
            } else {
                // do update
                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
                // boolean isSuccess = presenter.updatePocket(pocketInfo, fromTele);
                boolean isSuccess = presenter.updatePocket(pocketInfo);

            }
        } else {
            if (isEmpty(layout)) {
                // do not insert
                //finish.run();
            } else {
                // do insert
                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
                long noteId = presenter.createPocket(pocketInfo);
                if (noteId != -1) {
                    if (null != mSetValueToActivityListener) {
                        mSetValueToActivityListener.setNoteId(noteId);
                    }
                }
            }
        }
    }


    public void saveContentInfo(long updateId,
                                @NonNull Context context,
                                @NonNull ColoredLinearyLayout layout,
                                boolean fromTele, int skinBgId) {
        if (updateId > 0) {
//            if (isEmpty(layout)) {
//                // do delete
//            } else {
//                // do update
//                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
//                boolean isSuccess = presenter.updatePocket(pocketInfo);
//            }
            PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
            boolean isSuccess = presenter.updatePocket(pocketInfo);
        } else {
            if (isEmpty(layout)) {
                // do not insert
            } else {
                // do insert
                PocketInfo pocketInfo = saveContent2NoteInfo(layout, updateId, skinBgId);
                long noteId = presenter.createPocket(pocketInfo);
                if (noteId != -1) {
                    if (null != mSetValueToActivityListener) {
                        mSetValueToActivityListener.setNoteId(noteId);
                    }

                }
            }
        }
    }

    public void setCheckBox(@NonNull ColoredLinearyLayout layout) {
        layout.setCheckBoxs(false);
    }


    public void setTranslateProgress(String audioPath, ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < nodeList.size(); i++) {
            String value = nodeList.get(i);
            if (value.contains(audioPath)) {
                View view = dynamicContainer.getChildAt(i + 1);
                if (view != null && view instanceof RelativeLayout) {
                    ProgressBar progressBar = (ProgressBar) ((RelativeLayout) view).getChildAt(2);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    public void stopTranslateProgress(String audioPath, ColoredLinearyLayout dynamicContainer, String result, boolean isError) {
        if (null == audioPath) {
            return;
        }
        if (null == result) {
            result = "";
        }
        for (int i = 0; i < nodeList.size(); i++) {
            String value = nodeList.get(i);
            if (value.contains(audioPath)) {
                View view = dynamicContainer.getChildAt(i + 1);
                if (view != null && view instanceof RelativeLayout) {

                    ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                    if (!isError && null != editText) {
                        StringBuffer buffer = new StringBuffer();
                        String textInput = editText.getText().toString().trim();
                        editText.setText(buffer.append(result.trim()).append(textInput));
                        editText.setSelection(editText.getText().length());
                    }
                    ProgressBar progressBar = (ProgressBar) ((RelativeLayout) view).getChildAt(2);
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
    }

    public void removeEditTextFocus(ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                editText.clearFocus();
                editText.setFocusable(false);
            }
        }
    }

    public void addEditTextFocus(ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
            }
        }
    }

    public boolean lastViewIsEditText(ColoredLinearyLayout dynamicContainer) {
        View view = dynamicContainer.getChildAt(dynamicContainer.getChildCount() - 1);
        if (view instanceof RelativeLayout) {
            View child1 = ((RelativeLayout) view).getChildAt(1);
            if (child1 instanceof ZanyEditText) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void getLastEdittext(ColoredLinearyLayout dynamicContainer) {
        int count = dynamicContainer.getChildCount() - 1;
        if (count >= 0) {
            for (int i = count; i < dynamicContainer.getChildCount(); i--) {
                View view = dynamicContainer.getChildAt(i);
                if (view != null && view instanceof RelativeLayout) {
                    ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.findFocus();
                    ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE))
                            .showSoftInput(editText, InputMethodManager.SHOW_FORCED);
                    break;
                }
            }
        }
    }


    public int getFocusEditTextViewId(ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                if (editText.isFocused()) {
                    return editText.getId();
                }
            }
        }
        return 0;
    }

    public EditText getFocusEditTextView(ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                if (editText.isFocused()) {
                    return editText;
                }
            }
        }
        return null;
    }

    public void setFocuseToEditTextInId(int id, ColoredLinearyLayout dynamicContainer) {
        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                if (editText.getId() == id) {
                    editText.setFocusable(true);
                    editText.setFocusableInTouchMode(true);
                    editText.requestFocus();
                    editText.findFocus();
                }
            }
        }
    }


    public void setIsDoEdit(boolean isDoEdit) {
        //((NoteCreateActivity) mActivity).setDoEdit(isDoEdit);
        if (null != mSetValueToActivityListener) {
            mSetValueToActivityListener.setDoEdit(isDoEdit);
        }

    }


    public LinkedList<String> getNodeList() {
        return nodeList;
    }

    public void setNodeList(LinkedList<String> nodeList) {
        ViewManager.nodeList = nodeList;
    }


    public void setValueToActivityListener(SetValueToActivityListener setValueToActivityListener) {
        mSetValueToActivityListener = setValueToActivityListener;
    }

    public void setColoredLinearyLayoutEditTextColor(int textCheckedColorResId, int textUnCheckColorResId,
                                                     ColoredLinearyLayout dynamicContainer,
                                                     int checkListCheckedColorResId, int checkListUnCheckColorResId) {

        this.textCheckedColorResId = textCheckedColorResId;
        this.textUnCheckColorResId = textUnCheckColorResId;
        this.checkListCheckedColorResId = checkListCheckedColorResId;
        this.checkListUnCheckColorResId = checkListUnCheckColorResId;

        for (int i = 0; i < dynamicContainer.getChildCount(); i++) {
            View view = dynamicContainer.getChildAt(i);
            if (view != null && view instanceof RelativeLayout) {
                ZanyEditText editText = (ZanyEditText) ((RelativeLayout) view).getChildAt(1);
                CheckBox checkBox = (CheckBox) ((RelativeLayout) view).getChildAt(0);
                if (checkBox.isChecked()) {
                    editText.setTextColor(mContext.getColor(textCheckedColorResId));
                    setCheckBoxButtonDrawable(checkBox, checkBox.isChecked(), checkListCheckedColorResId);
                } else {
                    editText.setTextColor(mContext.getColor(textUnCheckColorResId));
                    setCheckBoxButtonDrawable(checkBox, checkBox.isChecked(), checkListUnCheckColorResId);
                }

            }
        }
    }


    private void setCheckBoxButtonDrawable(CheckBox checkBox, boolean isChecked, int textColorResId) {
        VectorDrawableCompat vectorDrawable;
        if (isChecked) {
            vectorDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    R.drawable.ic_gome_sys_ic_detailed, mContext.getTheme());
        } else {
            vectorDrawable = VectorDrawableCompat.create(mContext.getResources(),
                    R.drawable.ic_gome_sys_ic_detailed_unselected, mContext.getTheme());
        }
        vectorDrawable.setTint(mContext.getResources().getColor(textColorResId));
        checkBox.setButtonDrawable(vectorDrawable);
    }

    public int getTextCheckedColorResId() {
        return textCheckedColorResId;
    }

    public int getTextUnCheckColorResId() {
        return textUnCheckColorResId;
    }

    public int getCheckListCheckedColorResId() {
        return checkListCheckedColorResId;
    }

    public int getCheckListUnCheckColorResId() {
        return checkListUnCheckColorResId;
    }
}