package notesearch.bean;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PocketInfo implements Parcelable {

    private long id;

    private String title = "";

    private String summary = "";

    private String searchString;

    private String html;

    private String htmlContent;

    private List<LabelInfo> labels;

    private String[] labelTitles;

    private String icon;

    private String comeFrom;

    private String comeFromClass;

    private String url;

    private String uriData;

    private String originUrl;

    private String scheme;

    private String path;

    private boolean isGoods;

    private String price;

    private String audioUrl;

    private String videoUrl;

    private String mhtPath;

    private String type;

    private String cloudSyncState;

    private boolean isStick;

    private boolean isWebView;

    private boolean hasAudio;

    private boolean hasVideo;

    private long dateAdded;

    private long dateModified;

    private boolean isChecked;

    private boolean isClassifyFirst;
    private boolean isClassifyLast;
    private boolean isStickNextClassify;

    private List<ContentInfo> contents;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(@NonNull String summary) {
        this.summary = summary;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public List<LabelInfo> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelInfo> labels) {
        this.labels = labels;
        this.labelTitles = lablesList2lableTitles(labels);
    }


    public void setLabelTitles(String[] labelTitles) {
        this.labelTitles = labelTitles;
    }

    private String[] lablesList2lableTitles(List<LabelInfo> lableList) {
        if (lableList != null && !lableList.isEmpty()) {
            try {
                int N = lableList.size();
                String[] tagArr = new String[N];
                for (int i = 0; i < N; i++) {
                    LabelInfo li = lableList.get(i);
                    tagArr[i] = String.valueOf(li.getTitle());
                }
                return tagArr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String[] getLabelTitles() {
        return labelTitles;
    }

    public String getIcon() {
        return getNonEmptyString(icon);
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getComeFrom() {
        return comeFrom;
    }

    public void setComeFrom(String comeFrom) {
        this.comeFrom = comeFrom;
    }

    public String getMhtPath() {
        return mhtPath;
    }

    public void setMhtPath(String mhtPath) {
        this.mhtPath = mhtPath;
    }

    public String getCloudSyncState() {
        return cloudSyncState;
    }

    public void setCloudSyncState(String cloudSyncState) {
        this.cloudSyncState = cloudSyncState;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isGoods() {
        return isGoods;
    }

    public void setGoods(boolean goods) {
        isGoods = goods;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getOriginUrl() {
        return originUrl;
    }

    public void setOriginUrl(String originUrl) {
        this.originUrl = originUrl;
    }

    public String getUriData() {
        return uriData;
    }

    public void setUriData(String uriData) {
        this.uriData = uriData;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getComeFromClass() {
        return comeFromClass;
    }

    public void setComeFromClass(String comeFromClass) {
        this.comeFromClass = comeFromClass;
    }

    public boolean isWebView() {
        return isWebView;
    }

    public void setWebView(boolean webView) {
        isWebView = webView;
    }

    public boolean isHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(boolean hasAudio) {
        this.hasAudio = hasAudio;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(long dateAdded) {
        this.dateAdded = dateAdded;
    }

    public long getDateModified() {
        return dateModified;
    }

    public void setDateModified(long dateModified) {
        this.dateModified = dateModified;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }


    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    public boolean isStick() {
        return isStick;
    }

    public void setStick(boolean stick) {
        isStick = stick;
    }

    public List<ContentInfo> getContents() {
        return contents;
    }

    public void setContents(List<ContentInfo> contents) {
        this.contents = contents;
    }

    public boolean isClassifyFirst() {
        return isClassifyFirst;
    }

    public void setClassifyFirst(boolean classifyFirst) {
        isClassifyFirst = classifyFirst;
    }

    public boolean isClassifyLast() {
        return isClassifyLast;
    }

    public void setClassifyLast(boolean classifyLast) {
        isClassifyLast = classifyLast;
    }

    public boolean isStickNextClassify() {
        return isStickNextClassify;
    }

    public void setStickNextClassify(boolean stickNextClassify) {
        isStickNextClassify = stickNextClassify;
    }

    public static String guideImage = "/storage/emulated/0/.my_pocket/images//icon_1.png";

    @Override
    public String toString() {
        return "PocketInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", searchString='" + searchString + '\'' +
                ", html='" + html + '\'' +
                ", htmlContent='" + htmlContent + '\'' +
                ", labels=" + labels +
                ", labelTitles=" + Arrays.toString(labelTitles) +
                ", icon='" + icon + '\'' +
                ", comeFrom='" + comeFrom + '\'' +
                ", comeFromClass='" + comeFromClass + '\'' +
                ", url='" + url + '\'' +
                ", uriData='" + uriData + '\'' +
                ", originUrl='" + originUrl + '\'' +
                ", scheme='" + scheme + '\'' +
                ", path='" + path + '\'' +
                ", isGoods=" + isGoods +
                ", price='" + price + '\'' +
                ", audioUrl='" + audioUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", mhtPath='" + mhtPath + '\'' +
                ", type='" + type + '\'' +
                ", cloudSyncState='" + cloudSyncState + '\'' +
                ", isWebView=" + isWebView +
                ", hasAudio=" + hasAudio +
                ", hasVideo=" + hasVideo +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
                ", isChecked=" + isChecked +
                ", isStick=" + isStick +
                ", contents=" + contents +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(title);
        out.writeString(summary);
        out.writeString(html);
        out.writeString(htmlContent);
        out.writeList(labels);
        out.writeString(icon);
        out.writeString(comeFrom);
        out.writeString(mhtPath);
        out.writeInt(isWebView ? 1 : 0);
        out.writeInt(hasAudio ? 1 : 0);
        out.writeInt(hasVideo ? 1 : 0);
        out.writeLong(dateAdded);
        out.writeLong(dateModified);
        out.writeInt(isChecked ? 1 : 0);
        out.writeInt(isStick ? 1 : 0);
        out.writeList(contents);
        out.writeInt(isClassifyFirst ? 1 : 0);
        out.writeInt(isClassifyLast ? 1 : 0);
        out.writeInt(isStickNextClassify ? 1 : 0);

    }

    public static final Creator<PocketInfo> CREATOR = new Creator<PocketInfo>() {

        @Override
        public PocketInfo[] newArray(int size) {
            return new PocketInfo[size];
        }

        @Override
        public PocketInfo createFromParcel(Parcel in) {
            return new PocketInfo(in);
        }
    };

    public PocketInfo() {
    }

    public PocketInfo(String title, String summary, List<LabelInfo>
            labels, String icon, String comeFrom, long dateAdded, long dateModified) {
        this.title = title;
        this.summary = summary;
        this.labels = labels;
        this.icon = icon;
        this.comeFrom = comeFrom;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
        this.contents = contents;
    }

    public PocketInfo(Parcel in) {
        id = in.readLong();
        title = in.readString();
        summary = in.readString();
        html = in.readString();
        htmlContent = in.readString();
        in.readList(contents, ClassLoader.getSystemClassLoader());
        in.readList(labels, ClassLoader.getSystemClassLoader());
        icon = in.readString();
        comeFrom = in.readString();
        mhtPath = in.readString();
        isWebView = in.readInt() == 1;
        hasAudio = in.readInt() == 1;
        hasVideo = in.readInt() == 1;
        dateAdded = in.readLong();
        dateModified = in.readLong();
        isChecked = in.readInt() == 1;
        isStick = in.readInt() == 1;
        isClassifyFirst = in.readInt() == 1;
        isClassifyLast = in.readInt() == 1;
        isStickNextClassify = in.readInt() == 1;
    }

    public boolean isContains(String... labelInfos) {
        int count = 0;
        if (labelInfos.length == 0) {
            return false;
        }
        for (int i = 0; i < labelInfos.length; i++) {
            for (int j = 0; j < labels.size(); j++) {
                if ((labels.get(j).getTitle().equals(labelInfos[i]))) {
                    count++;
                }
            }
        }
        return count == labelInfos.length;
    }

    public boolean isContainsTag(String tag) {
        Iterator<LabelInfo> it = labels.iterator();
        while (it.hasNext()) {
            LabelInfo labelInfo = it.next();
            if (labelInfo.getId() == 0) {
                it.remove();
            }
        }
        for (LabelInfo label : labels) {
            if (label.getTitle().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean isContainsGroomTag(Context context, String[] labelInfos) {
        ArrayList<String> groomTags = new ArrayList<>();
        String searchImageTag = "";
        String searchWebTag = "";
        String searchAudioTag = "";
        String searchFileTag = "";
        int count = 0;
        if (labelInfos.length == 0) {
            return false;
        }
        if (!TextUtils.isEmpty(getIcon())) {
            // searchImageTag = context.getString(R.string.groom_pic);
        }
        if (isWebView() && !getIcon().equals(guideImage)) {
            //searchWebTag = context.getString(R.string.groom_web);
        }
        if (isHasAudio()) {
            // searchAudioTag = context.getString(R.string.search_type_music);
        }
        if (isHasVideo()) {
            // searchFileTag = context.getString(R.string.search_type_doc);
        }

        groomTags.add(searchImageTag);
        groomTags.add(searchWebTag);
        groomTags.add(searchAudioTag);
        groomTags.add(searchFileTag);

        for (int i = 0; i < labelInfos.length; i++) {
            /*for (int j=0;j<groomTags.size();j++){
                if ((groomTags.get(j).equals(labelInfos[i]))) {
                    count++;
                }
            }*/
            if (groomTags.contains(labelInfos[i])) {
                count++;
            }
        }
        return count == labelInfos.length;
    }

    public boolean isContainContents(String str) {
        if (!TextUtils.isEmpty(str) && (!TextUtils.isEmpty(getSummary()) || !TextUtils.isEmpty(getTitle()))) {
            if (getSummary().contains(str) || getTitle().contains(str)) {
                setSearchString(str);
                return true;
            }
        }
        return false;
    }


    public boolean isContainsTrueInPut( String str, String labelInfo) {
        /*if (TextUtils.isEmpty(str)&& searchGroomArr.length == 0) {
            return isContains(labelInfos);
        }
        if (labelInfos == null || labelInfos.length == 0 && searchGroomArr.length == 0){
            return isContainContents(str);
        }
        if (TextUtils.isEmpty(str)&&labelInfos == null || labelInfos.length == 0) {
            return isContainsGroomTag(searchGroomArr);
        }
        return isContains(labelInfos) && isContainContents(str)&&isContainsGroomTag(searchGroomArr);*/
        boolean searcherStr = !TextUtils.isEmpty(str);
        boolean searcherInfo = !TextUtils.isEmpty(labelInfo);
        /*if (!searcherStr && !searcherInfo && !searcherGroom){
            return false;
        }*/
        return (!searcherStr || isContainContents(str)) && (!searcherInfo || isContainsTag(labelInfo));
    }

    private static String getNonEmptyString(String s) {
        return TextUtils.isEmpty(s) ? "" : s;
    }

    public PocketInfo clone() {
        PocketInfo pi = new PocketInfo();
        pi.setId(id);
        pi.setTitle(title);
        pi.setSummary(summary);
        pi.setLabels(labels);
        pi.setLabelTitles(labelTitles);
        pi.setIcon(icon);
        pi.setComeFrom(comeFrom);
        pi.setComeFromClass(comeFromClass);
        pi.setWebView(isWebView);
        pi.setUrl(url);
        pi.setOriginUrl(originUrl);
        pi.setUriData(uriData);
        pi.setHtml(html);
        pi.setHtmlContent(htmlContent);
        pi.setScheme(scheme);
        pi.setHasAudio(hasAudio);
        pi.setHasVideo(hasVideo);
        pi.setVideoUrl(videoUrl);
        pi.setAudioUrl(audioUrl);
        pi.setPath(path);
        pi.setGoods(isGoods);
        pi.setPrice(price);
        pi.setMhtPath(mhtPath);
        pi.setType(type);
        pi.setCloudSyncState(cloudSyncState);
        pi.setDateAdded(dateAdded);
        pi.setDateModified(dateModified);
        pi.setChecked(isChecked);
        pi.setStick(isStick);
        pi.setContents(contents);
        pi.setClassifyFirst(isClassifyFirst);
        pi.setClassifyLast(isClassifyLast);
        pi.setStickNextClassify(isStickNextClassify);
        return pi;
    }

    public static PocketInfo clone(PocketInfo o) {
        return o.clone();
    }

}
