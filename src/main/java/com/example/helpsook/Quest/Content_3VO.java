package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 같이해보숙 - 수업듣기 퀘스트 내용 객체를 담을 Data 클래스.
public class Content_3VO {
    private String lecture;
    private String qid;
    private String curLocation;


    public Content_3VO() {

    }

    public Content_3VO(String lecture, String qid, String curLocation) {
        this.lecture = lecture;
        this.qid = qid;
        this.curLocation = curLocation;
    }

    public String getLecture() {
        return lecture;
    }

    public void setLecture(String lecture) {
        this.lecture = lecture;
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
        return "Content_3VO{" +
                "lecture='" + lecture + '\'' +
                ", qid='" + qid + '\'' +
                ", curLocation='" + curLocation + '\'' +
                "}";
    }
}
