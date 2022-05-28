package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 좀도와주숙 퀘스트 내용 객체를 담을 Data 클래스.
public class Content_4VO {
    private String item;
    private String qid;
    private String curLocation;

    public Content_4VO() {

    }

    public Content_4VO(String item, String qid, String curLocation) {
        this.item = item;
        this.qid = qid;
        this.curLocation = curLocation;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(String curLocation) {
        this.curLocation = curLocation;
    }

    @NonNull
    @Override
    public String toString() {
        return "Content_4VO{" +
                "item='" + item + '\'' +
                ", qid='" + qid + '\'' +
                ", curLocation='" + curLocation + '\'' +
                "}";
    }
}
