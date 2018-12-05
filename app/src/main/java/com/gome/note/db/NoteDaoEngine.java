package com.gome.note.db;

import android.content.Context;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;


import com.gome.note.domain.NoteInfo;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Created on 2016/12/15.
 *
 * @apiNote crud op in sub thread ! powered by RxJAVA
 */

public class NoteDaoEngine {


    public static Observable<Long> insert(@NonNull Context c, @NonNull NoteInfo ni) {
        return Observable.defer(() -> Observable.just(NoteDao.insert(c, ni)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Long> update(@NonNull Context c, @NonNull NoteInfo ni, @IntRange(from = 1) long id) {
        return Observable.defer(() -> Observable.just(NoteDao.update(c, ni, id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Long> insertOrUpdate(@NonNull Context c,
                                                  @NonNull NoteInfo ni,
                                                  @IntRange(from = Integer.MIN_VALUE) long id) {
        if (id > 0) {
            // update
            return update(c, ni, id);
        } else {
            // insert
            return insert(c, ni);
        }
    }

    public static Observable<Integer> delete(@NonNull Context c, long id) {
        return Observable.defer(() -> Observable.just(NoteDao.delete(c, id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<Integer> delete(@NonNull Context c, @NonNull List<Long> idSet) {
        return Observable.defer(() -> Observable.just(NoteDao.delete(c, idSet)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    public static Observable<Boolean> deleteMulti(@NonNull Context c, @NonNull List<Long> idSet) {
        return Observable.defer(() -> Observable.just(NoteDao.deleteMulti(c, idSet)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<List<NoteInfo>> queryAll(@NonNull Context c) {
        return Observable.defer(() -> Observable.just(NoteDao.queryAll(c)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable<NoteInfo> queryById(@NonNull Context c, @IntRange(from = 1) long id) {
        return Observable.defer(() -> Observable.just(NoteDao.queryById(c, id)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
