package com.example.helpsook.Public;

import android.app.Application;

// 앱 전체에서 사용될 전역 변수들.
public class User_Info extends Application {
    public static String uid = "";          // uid
    public static String nickname = "";     // user 의 별명

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
