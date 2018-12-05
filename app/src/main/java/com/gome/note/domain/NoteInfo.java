package com.gome.note.domain;


/**
 * Created  on 2016/12/15.
 *
 * @author pythoncat.cheng
 * @apiNote db domain
 */
public class NoteInfo {

    public long id;
    public String title;
    public String contentHead; // first edit text
    /**
     * a json string dynamic added img：url text: string
     */
    public String content; //
    public long lastModified;// update or created time

    public boolean checked; //  --> only for UI !!! not 2 db !
    public boolean showCheckBox; //  --> only for UI !!! not 2 db !

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo info = (NoteInfo) o;

        if (!title.equals(info.title)) return false;
        if (!contentHead.equals(info.contentHead)) return false;
        return content.equals(info.content);

    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + contentHead.hashCode();
        result = 31 * result + content.hashCode();
        return result;
    }
}
