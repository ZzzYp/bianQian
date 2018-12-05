package com.gome.note.domain;


import com.gome.note.entity.NodeType;

/**
 * Created on 2017/4/19.
 *
 * @author pythoncat
 * @apiNote the concrete content of every not POJO --> it's none business of db
 * --> it's a object of node in actually 2017-05-11
 */
public class Memo {
    /**
     * node key position -->current node position
     */
    public int key;
    /**
     * node value --> bitmap path or EditText text
     */
    public String value;
    public NodeType type; // is text,image or audio
}
