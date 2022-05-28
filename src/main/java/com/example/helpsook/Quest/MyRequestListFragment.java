package com.example.helpsook.Quest;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyRequestListFragment extends androidx.fragment.app.Fragment {
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Components
    private RecyclerView recyclerView;
    private TextView title;

    // Etc
    private RecyclerView.LayoutManager layoutManager;
    private MyRequestListAdapter adapter;
    private ArrayList<QuestVO> myRequestList;       // 채탕방 객체들을 담을 ArrayList.
    private Boolean isComplete;

    public MyRequestListFragment() {
        // Required empty public constructor
    }

    public MyRequestListFragment(Boolean isComplete) {
        // Required empty public constructor
        this.isComplete = isComplete;
    }

    public static MyRequestListFragment newInstance() {
        MyRequestListFragment fragment = new MyRequestListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_my_request_list, container, false);

        // 종류에 따라 방 제목 달기
        title = layout.findViewById(R.id.title);
        if (isComplete) {
            title.setText("마감한 도움요청 목록");
        } else {
            title.setText("마감하지 않은 도움요청 목록");
        }

        // RecyclerView 에 LayoutManager 붙이기.
        recyclerView = (RecyclerView) layout.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);             // RecyclerView 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 종류에 맞는 퀘스트 리스트 생성
        myRequestList = new ArrayList<QuestVO>();       // 퀘스트 객체를 담을 ArrayList 생성.
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Quest");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myRequestList.clear();         // 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuestVO questVO = snapshot.getValue(QuestVO.class);
                    questVO.setQid(snapshot.getKey());

                    if (questVO.getRequestor().equals(User_Info.uid)) {
                        if (isComplete && questVO.getComplete()) {
                            myRequestList.add(questVO);
                        } else if (!isComplete && !questVO.getComplete()) {
                            myRequestList.add(questVO);
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // ArrayList 저장 및 새로고침해야 반영됨.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB 를 가져오던중 에러 발생
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // RecyclerView 에 Adapter 연결
        adapter = new MyRequestListAdapter(myRequestList);
        recyclerView.setAdapter(adapter);

        // Quest DB 에 변화가 있을시 ReRendering
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myRequestList.clear();         // 초기화
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            QuestVO questVO = snapshot.getValue(QuestVO.class);
                            questVO.setQid(snapshot.getKey());

                            if (questVO.getRequestor().equals(User_Info.uid)) {
                                if (isComplete && questVO.getComplete()) {
                                    myRequestList.add(questVO);
                                } else if (!isComplete && !questVO.getComplete()) {
                                    myRequestList.add(questVO);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged(); // ArrayList 저장 및 새로고침해야 반영됨.
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // DB 를 가져오던중 에러 발생
                        Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });
                // RecyclerView 다시 그리기
                adapter = new MyRequestListAdapter(myRequestList);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(myRequestList.size()-1);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
        return layout;
    }
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
