package com.gome.note.entity;


import com.gome.note.base.Config;

/**
 * Created on 2016/12/15.
 *
 * @author pythoncat.cheng
 * @apiNote some constant
 */

public interface Forever {
    String main_show_style_key = "main_show_style_key";
    String mainListViewStyle = "mainListViewStyle_1024"; // default
    String mainGridViewStyle = "mainGridViewStyle_1024";
    /**
     * eidt note putted  key (new note have no key 2 put)
     */
    String EDIT_CURRENT_ID = "edit_current_id";

    /**
     * image save head
     */
    String IMG_HEAD = "<IMAGEVIEW>";
    /**
     * img save foot
     */
    String IMG_FOOT = "</IMAGEVIEW>";

    String AUDIO_HEAD = "<AUDIO>";
    String AUDIO_FOOT = "</AUDIO>";

    String VIDEO_HEAD = "<VIDEO>";
    String VIDEO_FOOT = "</VIDEO>";

    String STICK_HEAD = "<STICK>";
    String STICK_FOOT = "</STICK>";

    long minAvilableMemoSize = 25 * 1024 * 1024; // 25M
    //int maxWords = Integer.MAX_VALUE - 1; // 2000
    int maxWords = 3000; // 3000


    String fileAuthority = Config.FILEPROVIDER_AUTHORITIES;
    String DB_AUTHORITIES = Config.DBPROVIDER_AUTHORITIES;
}
