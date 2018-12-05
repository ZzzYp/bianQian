package com.gome.note.entity;

/**
 * ProjectName:Note_IUV
 * CREATE BY:yupeng.zhang
 * CREATE DATE:2018/3/9
 * DESCRIBE:
 */

public class SpeechResultInfo {

    private String corpus_no;
    private String err_msg;
    private String err_no;
    private String[] result;
    private String sn;


    public String getCorpus_no() {
        return corpus_no;
    }

    public void setCorpus_no(String corpus_no) {
        this.corpus_no = corpus_no;
    }

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getErr_no() {
        return err_no;
    }

    public void setErr_no(String err_no) {
        this.err_no = err_no;
    }

    public String[] getResult() {
        return result;
    }

    public void setResult(String[] result) {
        this.result = result;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
}
