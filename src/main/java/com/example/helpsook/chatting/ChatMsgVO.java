package com.example.helpsook.chatting;

import androidx.annotation.NonNull;

// 채팅 객체를 담을 클래스.
public class ChatMsgVO {
    private String content;
    private String userId;
    private String createdAt;

    public ChatMsgVO() {

    }

    public ChatMsgVO(String userId, String createdAt, String content) {
        this.userId = userId;
        this.createdAt = createdAt;
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @NonNull
    @Override
    public String toString() {
        return "ChatMsgVO{" +
                "userId='" + userId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", content='" + content + '\'' +
                "}";
    }
}
