package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 같이귀가하숙 퀘스트 내용 객체를 담을 Data 클래스.
public class Content_1VO {
    private String departure;
    private String destination;
    private String qid;

    public Content_1VO() {

    }

    public Content_1VO(String departure, String destination, String qid) {
        this.departure = departure;
        this.destination = destination;
        this.qid = qid;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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
        return "Content_1VO{" +
                "departure='" + departure + '\'' +
                "destination='" + destination + '\'' +
                ", qid='" + qid + '\'' +
                "}";
    }
}
