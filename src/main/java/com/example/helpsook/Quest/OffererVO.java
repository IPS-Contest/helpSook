package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 도움을 주는 사람과 그 퀘스트를 연결하는 객체를 담을 Data 클래스.
public class OffererVO {
    private String offerer;
    private String qid;

    public OffererVO() {

    }

    public OffererVO(String offerer, String qid) {
        this.offerer = offerer;
        this.qid = qid;
    }

    public String getOfferer() {
        return offerer;
    }

    public void setOfferer(String offerer) {
        this.offerer = offerer;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    @NonNull
    @Override
    public String toString() {
        return "OffererVO{" +
                "offerer='" + offerer + '\'' +
                ", qid='" + qid + '\'' +
                "}";
    }
}
