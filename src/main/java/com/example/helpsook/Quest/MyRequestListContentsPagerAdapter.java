package com.example.helpsook.Quest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import org.jetbrains.annotations.NotNull;

// Fragment (탭) 의 생성 및 이동을 구현하는 Adapter.
public class MyRequestListContentsPagerAdapter extends FragmentStateAdapter {
    private int mPageCount = 2;
    MyRequestListActivity activity;

    public MyRequestListContentsPagerAdapter(AppCompatActivity fm, MyRequestListActivity activity) {
        super(fm);
        this.activity = activity;
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {

            case 0: // 마감 안 한 퀘스트
                return new MyRequestListFragment(false);
            case 1: // 마감한 퀘스트
                return new MyRequestListFragment(true);

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