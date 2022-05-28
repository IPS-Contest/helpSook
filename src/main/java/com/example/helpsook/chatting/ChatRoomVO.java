package com.example.helpsook.chatting;

import androidx.annotation.NonNull;

// 채팅방 객체를 담을 Data 클래스.
public class ChatRoomVO {
    private String qid = "";
    private String roomId;
    private String createdAt;
    private String offerer;
    private String requestor;
    private String chatting_msg = "";

    public ChatRoomVO() {

    }

    public ChatRoomVO(String qid, String roomId, String createdAt, String offerer, String requestor) {
        this.qid = qid;
        this.roomId = roomId;
        this.createdAt = createdAt;
        this.offerer = offerer;
        this.requestor = requestor;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getOfferer() {
        return offerer;
    }

    public void setOfferer(String offerer) {
        this.offerer = offerer;
    }

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getChatting_msg() {
        return chatting_msg;
    }

    public void setChatting_msg(String chatting_msg) {
        this.chatting_msg = chatting_msg;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatRoomVO{" +
                "qid='" + qid + '\'' +
                ", roomId='" + roomId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", offerer='" + offerer + '\'' +
                ", requestor='" + requestor + '\'' +
                ", chatting_msg='" + chatting_msg + '\'' +
                "}";
    }
}