package com.example.helpsook;

import androidx.annotation.NonNull;

// User 객체를 담을 Data 클래스.
public class UserVO {
    private String uid = "";
    private String nickname = "";

    public UserVO() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @NonNull
    @Override
    public String toString() {
        return "UserVO{" +
                "uid='" + uid + '\'' +
                ", nickname='" + nickname + '\'' +
                "}";
    }
}
