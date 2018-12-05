package com.gome.note.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/5/15
 * DESCRIBE:
 */

public class RecordPool {
    public static Map<Integer, String> map = new HashMap<>();

    private RecordPool() {
    }

    private static final RecordPool RECORD_POOL = new RecordPool();

    public static RecordPool getInstance() {
        return RECORD_POOL;
    }



}
