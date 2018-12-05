package com.gome.note.base;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/1/18
 * DESCRIBE:
 */

public interface BaseModel {

    void query(String keywords);

    void add(Object obj);

    void update(Object obj, String id);

    void delete(String id);

}
