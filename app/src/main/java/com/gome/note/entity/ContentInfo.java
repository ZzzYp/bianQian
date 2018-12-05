package com.gome.note.entity;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by Administrator on 2017/6/19.
 */

public class ContentInfo implements Parcelable {

    private String text;

    private String image;

    private String audio;

    private String audioTime;

    private String video;

    private String file;

    private String webview;

    private boolean hasCheckBox;
    private boolean isFirstLine;
    private boolean isChecked;

    private long index;


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAudio() {
        return audio;
    }

    public void setAudio(String audio) {
        this.audio = audio;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getWebview() {
        return webview;
    }

    public void setWebview(String webview) {
        this.webview = webview;
    }

    public boolean isHasCheckBox() {
        return hasCheckBox;
    }

    public void setHasCheckBox(boolean hasCheckBox) {
        this.hasCheckBox = hasCheckBox;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isFirstLine() {
        return isFirstLine;
    }

    public void setFirstLine(boolean firstLine) {
        isFirstLine = firstLine;
    }


    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public String getAudioTime() {
        return audioTime;
    }

    public void setAudioTime(String audioTime) {
        this.audioTime = audioTime;
    }

    @Override
    public String toString() {
        return "ContentInfo{" +
                "text='" + text + '\'' +
                ", image='" + image + '\'' +
                ", audio='" + audio + '\'' +
                ", video='" + video + '\'' +
                ", file='" + file + '\'' +
                ", webview='" + webview + '\'' +
                ", hasCheckBox=" + hasCheckBox +
                ", isFirstLine=" + isFirstLine +
                ", isChecked=" + isChecked +
                ", audioTime=" + audioTime +
                '}';
    }

    public ContentInfo(String text, String image, String audio, String video, String file, String
            webview) {
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.file = file;
        this.webview = webview;
    }

    public ContentInfo(String text, String image, String audio, String video, String file, String
            webview, boolean hasCheckBox, boolean isFirstLine, boolean isChecked, String audioTime) {
        this.text = text;
        this.image = image;
        this.audio = audio;
        this.video = video;
        this.file = file;
        this.webview = webview;
        this.hasCheckBox = hasCheckBox;
        this.isFirstLine = isFirstLine;
        this.isChecked = isChecked;
        this.audioTime = audioTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(text);
        out.writeString(image);
        out.writeString(audio);
        out.writeString(video);
        out.writeString(file);
        out.writeString(webview);
        out.writeInt(getIntByBoolean(hasCheckBox));
        out.writeInt(getIntByBoolean(isFirstLine));
        out.writeInt(getIntByBoolean(isChecked));
        out.writeString(audioTime);
    }

    private int getIntByBoolean(boolean isTrue) {
        if (isTrue) {
            return 1;
        }
        return 0;
    }

    private boolean getBooleanByInt(int isOne) {
        if (isOne == 1) {
            return true;
        }
        return false;
    }

    public static final Parcelable.Creator<ContentInfo> CREATOR = new Creator<ContentInfo>() {

        @Override
        public ContentInfo[] newArray(int size) {
            return new ContentInfo[size];
        }

        @Override
        public ContentInfo createFromParcel(Parcel in) {
            return new ContentInfo(in);
        }
    };

    public ContentInfo() {
    }


    public ContentInfo(Parcel in) {
        text = in.readString();
        image = in.readString();
        audio = in.readString();
        video = in.readString();
        file = in.readString();
        webview = in.readString();
        hasCheckBox = getBooleanByInt(in.readInt());
        isFirstLine = getBooleanByInt(in.readInt());
        isChecked = getBooleanByInt(in.readInt());
        audioTime = in.readString();
    }

}
