package notesearch.bean;

import android.text.SpannableString;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/6/27
 * DESCRIBE:
 */

public class NoteHiBoardInfo {

    //note id
    private long id;
    //note Modified time
    private long time;
    //show time
    private String timeDate;
    //highlight title
    private SpannableString title;
    // icon path
    private String imgPath;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public SpannableString getTitle() {
        return title;
    }

    public void setTitle(SpannableString title) {
        this.title = title;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getTimeDate() {
        return timeDate;
    }

    public void setTimeDate(String timeDate) {
        this.timeDate = timeDate;
    }
}
