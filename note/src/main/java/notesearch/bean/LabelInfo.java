package notesearch.bean;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public class LabelInfo implements Parcelable{

    private long id;

    private String title;

    private int count;

    private boolean stick;

    private long dateAdded;

    private long dateModified;

    //add with yupeng.zhang
    private boolean isChecked;

    private int position;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isStick() {
        return stick;
    }

    public void setStick(boolean stick) {
        this.stick = stick;
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

    public boolean getIsChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "LabelInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", count=" + count +
                ", stick=" + stick +
                ", check=" + isChecked +
                ", dateAdded=" + dateAdded +
                ", dateModified=" + dateModified +
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
        out.writeInt(count);
        out.writeLong(dateAdded);
        out.writeLong(dateModified);
        out.writeByte((byte) (stick ? 1 : 0));  //if stick == true, byte == 1
        out.writeByte((byte) (isChecked ? 1 : 0));  //if isChecked == true, byte == 1

    }

    public static final Creator<LabelInfo> CREATOR = new Creator<LabelInfo>() {

        @Override
        public LabelInfo[] newArray(int size) {
            return new LabelInfo[size];
        }

        @Override
        public LabelInfo createFromParcel(Parcel in) {
            return new LabelInfo(in);
        }
    };

    public LabelInfo() {
    }

    public LabelInfo(long id) {
        this.id = id;
    }

    public LabelInfo(long id, String title) {
        this.id = id;
        this.title = title;
    }

    public LabelInfo(long id, String title, boolean stick) {
        this.id = id;
        this.title = title;
        this.stick = stick;
    }

    public LabelInfo(String title, int count, long dateAdded, long dateModified) {
        this.title = title;
        this.count = count;
        this.dateAdded = dateAdded;
        this.dateModified = dateModified;
    }

    public LabelInfo(Parcel in) {
        id = in.readLong();
        title = in.readString();
        count = in.readInt();
        dateAdded = in.readLong();
        dateModified = in.readLong();
        stick = in.readByte() != 0;  //stick == true if byte != 0
        isChecked = in.readByte() != 0;  //stick == true if byte != 0
    }

    /*@Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        else {
            if(obj instanceof LabelInfo) {
                LabelInfo labelInfo = (LabelInfo)obj;
                if(labelInfo.getId() == this.id) {
                    return true;
                }
            }
        }
        return false;
    }*/

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof LabelInfo)) return false;
        return ((LabelInfo) obj).getTitle().equals(this.title);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("title", title);
        json.put("count", count);
        json.put("stick", stick);
        json.put("dateAdded", dateAdded);
        json.put("dateModified", dateModified);
        return json;
    }

    public LabelInfo clone() {
        LabelInfo li = new LabelInfo();
        li.setId(id);
        li.setChecked(false);
        li.setStick(stick);
        li.setTitle(title);
        li.setCount(count);
        li.setPosition(0);
        li.setDateAdded(dateAdded);
        li.setDateModified(dateModified);
        return li;
    }

    public static LabelInfo clone(LabelInfo o) {
        if (o != null) {
            return o.clone();
        }
        return null;
    }

}
