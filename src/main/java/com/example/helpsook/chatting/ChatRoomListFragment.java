package com.example.helpsook.chatting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// 도움을 받고자 하여 생성한 채팅방 목록을 보여주는 화면의 Fragment.
public class ChatRoomListFragment extends androidx.fragment.app.Fragment {
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Components
    private RecyclerView recyclerView;
    private TextView title;

    // Etc
    private ChatRoomListActivity activity;
    private RecyclerView.LayoutManager layoutManager;
    private ChatRoomAdapter adapter;
    private ArrayList<ChatRoomVO> chatRoomVOList;       // 채탕방 객체들을 담을 ArrayList.
    private String type;                                // 퀘스트 종류 (1, 2, 3, 4)

    public ChatRoomListFragment() {
        // Required empty public constructor
    }

    public ChatRoomListFragment(ChatRoomListActivity activity, String type) {
        // Required empty public constructor
        this.activity = activity;
        this.type = type;
    }

    public static ChatRoomListFragment newInstance() {
        ChatRoomListFragment fragment = new ChatRoomListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_chat_room_list, container, false);

        // 종류에 따라 방 제목 달기
        title = layout.findViewById(R.id.title);
        if (type.equals("request")) {
            title.setText("도움 받기 채팅 목록");
        } else if (type.equals("offer")) {
            title.setText("도움 주기 채팅 목록");
        }

        // RecyclerView 에 LayoutManager 붙이기.
        recyclerView = (RecyclerView) layout.findViewById(R.id.rv);
        recyclerView.setHasFixedSize(true);             // RecyclerView 기존성능 강화
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // 종류에 맞는 채팅방 리스트 생성
        chatRoomVOList = new ArrayList<ChatRoomVO>();   // 채팅방 객체를 담을 ArrayList 생성.
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Chatting_Room");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRoomVOList.clear();         // 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatRoomVO chatRoomVO = snapshot.getValue(ChatRoomVO.class);
                    chatRoomVO.setRoomId(snapshot.getKey());

                    if (!chatRoomVO.getQid().equals("")) {
                        if (type.equals("request")) {
                            if (chatRoomVO.getRequestor().equals(User_Info.uid))
                                chatRoomVOList.add(chatRoomVO);
                        } else if (type.equals("offer")) {
                            if (chatRoomVO.getOfferer().equals(User_Info.uid))
                                chatRoomVOList.add(chatRoomVO);
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
        adapter = new ChatRoomAdapter(chatRoomVOList, activity);
        recyclerView.setAdapter(adapter);

        // Chatting_Room DB 에 변화가 있을시 ReRendering
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Boolean exist = false;
                for (ChatRoomVO chatRoomVO: chatRoomVOList) {
                    // chatRoomVOList 에 이미 추가되어 있는 경우.
                    if (dataSnapshot.getKey().equals(chatRoomVO.getRoomId())) {
                        exist = true;
                        break;
                    }
                }

                // chatRoomVOList 에 추가되어 있지 않은 경우. 즉 새로 개설된 채팅방의 경우.
                if (!exist) {
                    ChatRoomVO chatRoomVO = dataSnapshot.getValue(ChatRoomVO.class);
                    if (!chatRoomVO.getQid().equals("")) {
                        chatRoomVO.setRoomId(dataSnapshot.getKey());
                        if (type.equals("request")) {
                            if (chatRoomVO.getRequestor().equals(User_Info.uid))
                                chatRoomVOList.add(chatRoomVO);
                        } else if (type.equals("offer")) {
                            if (chatRoomVO.getOfferer().equals(User_Info.uid))
                                chatRoomVOList.add(chatRoomVO);
                        }
                    }
                }
                // 채팅 메시지 배열에 담고 RecyclerView 다시 그리기
                adapter = new ChatRoomAdapter(chatRoomVOList, activity);
                recyclerView.setAdapter(adapter);
                recyclerView.scrollToPosition(chatRoomVOList.size()-1);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

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
