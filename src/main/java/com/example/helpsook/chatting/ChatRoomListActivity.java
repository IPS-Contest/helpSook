package com.example.helpsook.chatting;

import android.os.Bundle;

import com.example.helpsook.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

// 채팅 리스트 띄워주는, 탭으로 구성된 화면의 Activity.
public class ChatRoomListActivity extends AppCompatActivity {
    // Components
    private TabLayout mTabLayout;
    private ViewPager2 mViewPager;

    // Etc
    private ContentsPagerAdapter mContentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_list);

        // XML component 와 변수 연결
        mTabLayout = (TabLayout) findViewById(R.id.layout_tab);
        mViewPager = (ViewPager2) findViewById(R.id.pager_content);

        // Fragment (탭) 이동 구현
        mContentPagerAdapter = new ContentsPagerAdapter(this, this);
        mViewPager.setAdapter(mContentPagerAdapter);

        // 각 탭 이름
        final List<String> tabElement = Arrays.asList("퀘스트 도우미", "퀘스트 요청자");

        //tabLyout와 viewPager 연결
        new TabLayoutMediator(mTabLayout, mViewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                tab.setText(tabElement.get(position));
            }
        }).attach();
    }

    // 채팅방 & 채팅 목록 간 화면 전환 함수.
    public void changeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainFrame, fragment)
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .addToBackStack(null)
                .commit();
    }
}