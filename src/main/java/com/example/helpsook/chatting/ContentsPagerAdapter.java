package com.example.helpsook.chatting;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

// Fragment (탭) 의 생성 및 이동을 구현하는 Adapter.
public class ContentsPagerAdapter extends FragmentStateAdapter {
    // Etc
    private int mPageCount = 2;
    ChatRoomListActivity activity;

    public ContentsPagerAdapter(AppCompatActivity fm, ChatRoomListActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {

            case 0: // 도움을 주는 리스트
                return new ChatRoomListFragment(activity, "offer");
            case 1: // 도움을 받는 리스트
                return new ChatRoomListFragment(activity, "request");

            default:
                return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemCount() {
        return mPageCount;
    }
}