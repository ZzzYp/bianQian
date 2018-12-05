package com.gome.note.domain;

import android.support.annotation.IntRange;
import android.support.annotation.Nullable;

/**
 * Created on 2017/4/20.
 *
 * @author pythoncat
 * @apiNote the information of the insert position
 */
public class InsertIndex {
    @IntRange(from = -1, to = 1024)
    public int index; // if insert current View is null index == -1

    @Nullable
    public String after;
    // if the insert position is not a EditText, the 'before' and 'after' both are null

    @Nullable
    public String before;
    // if the insert position is not a EditText, the 'before' and 'after' both are null

    public InsertIndex(int index, String after, String before) {
        this.index = index;
        this.after = after;
        this.before = before;
    }

    public InsertIndex() {
    }

    @Override
    public String toString() {
        return "InsertIndex{" +
                "index=" + index +
                ", after='" + after + '\'' +
                '}';
    }
}
