package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 퀘스트 객체를 담을 Data 클래스.
public class QuestVO {
    private Boolean complete;
    private String requestor;
    private String title;
    private int type;
    private String qid;
    private String latitude;
    private String longitude;

    public QuestVO() {

    }

    public QuestVO(Boolean complete, String requestor, String title, int type, String qid, String latitude, String longitude) {
        this.complete = complete;
        this.requestor = requestor;
        this.title = title;
        this.type = type;
        this.qid = qid;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @NonNull
    @Override
    public String toString() {
        return "QuestVO{" +
                "complete='" + complete + '\'' +
                ", requestor='" + requestor + '\'' +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", qid='" + qid + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                "}";
    }
}