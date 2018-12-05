package com.gome.note.db;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


import com.gome.note.domain.NoteInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2016/12/15.
 *
 * @author pythoncat.cheng
 * @apiNote CRUD
 */

class NoteDao {

    /**
     * insert
     *
     * @param context context
     * @param ni      domian
     * @return row
     */
    static long insert(@NonNull Context context, @NonNull NoteInfo ni) {
        // Gets the data repository in write mode
        return 0;
    }

    /**
     * @param c  context
     * @param ni new data wait 2 be updated
     * @param id whitch will be updated
     * @return row
     */
    static long update(@NonNull Context c, @NonNull NoteInfo ni, long id) {
        return 0;
    }

    /**
     * delete one
     *
     * @param c  context
     * @param id note id
     * @return row
     */
    static int delete(@NonNull Context c, long id) {

        return 0;
    }

    /**
     * delete multi
     *
     * @param c     context
     * @param idSet note ids
     * @return row
     */
    static int delete(@NonNull Context c, @NonNull List<Long> idSet) {

        return 0;
    }

    /**
     * delete multi
     *
     * @param c     context
     * @param idSet note ids
     * @return row
     */
    static boolean deleteMulti(@NonNull Context c, @NonNull List<Long> idSet) {
        return false;
    }


    /**
     * queryAll
     *
     * @param c context
     * @return List of notes
     */
    static List<NoteInfo> queryAll(@NonNull Context c) {
        return new ArrayList<>();
    }

    /**
     * queryById
     *
     * @param c context
     * @return current Note
     */
    static NoteInfo queryById(@NonNull Context c, @IntRange(from = 1) long id) {
        return new NoteInfo();
    }

}
