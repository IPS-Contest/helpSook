package com.example.helpsook.chatting;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// 채팅 입력하는 화면의 Fragment.
public class ChatRoomFragment extends Fragment implements View.OnClickListener {
    // Firebase
    FirebaseDatabase chatDatabase, database;
    DatabaseReference chatDatabaseReference;

    // Components
    // 채팅을 입력할 입력창과 전송 버튼, 채팅방 제목
    EditText content_et;
    ImageView send_iv;
    TextView roomTitle;

    // 채팅 내용을 뿌려줄 RecyclerView 와 Adapter
    RecyclerView rv;
    ChatMsgAdapter mAdapter;

    // Etc
    // 채팅 방 관련 정보들.
    String roomId = "";
    String title = "";
    String otherNickname = "";  // User 를 제외한 방 내의 다른 사용자의 별명.
    String chatting_msg = "";   // Chatting_msg DB 와 각 Chatting_Room 을 연결할 때 사용할 키값.

    // 채팅 내용을 담을 배열
    List<ChatMsgVO> msgList = new ArrayList<>();

    public ChatRoomFragment() {
    }

    public static ChatRoomFragment newInstance() {
        ChatRoomFragment fragment = new ChatRoomFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);

        // ChatRoomAdapter 에서 전달받은 값들.
        title = getArguments().getString("title");
        roomId = getArguments().getString("roomId");
        otherNickname = getArguments().getString("otherNickname");
        chatting_msg = getArguments().getString("chatting_msg");

        // XML component 와 변수 연결
        content_et = view.findViewById(R.id.content_et);
        roomTitle = view.findViewById(R.id.roomTitle);
        roomTitle.setText(title);
        rv = view.findViewById(R.id.rv);
        send_iv = view.findViewById(R.id.send_iv);
        send_iv.setOnClickListener(this);   // 채팅 보내기 버튼 클릭시 수행

        chatDatabase = FirebaseDatabase.getInstance();
        if (chatting_msg.equals("")) {      // DB 에 메세지 목록이 만들어져있지 않은 경우
            chatDatabaseReference = chatDatabase.getReference("Chatting_msg");

            // 채팅 DB 생성을 위한, Database 에 저장할 빈 객체 생성
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            ChatMsgVO msgVO = new ChatMsgVO(User_Info.uid, df.format(new Date()).toString(), "");

            // 채팅 목록 리스트에 추가
            msgList.add(msgVO);

            // 해당 DB 에 값 저장
            chatting_msg = chatDatabaseReference.push().getKey();
            chatDatabaseReference = chatDatabaseReference.child(chatting_msg);
            chatDatabaseReference.push().setValue(msgVO);

            // Chatting_Room 의 chatting_msg field Update (채팅방 & 채팅 내역 연결)
            FirebaseDatabase roomDatabase = FirebaseDatabase.getInstance();
            DatabaseReference roomDatabaseReference = roomDatabase.getReference("Chatting_Room");
            roomDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // ChatRoomAdapter 에서 받아온 roomId 를 키값으로 하여 매칭.
                        if (snapshot.getKey().equals(roomId)) {
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("chatting_msg", chatting_msg);

                            // Chatting_Room 의 child 에 Chatting_msg 의 키값 추가 => 두 DB 연결.
                            roomDatabaseReference.child(roomId).updateChildren(taskMap);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { // 오류 발생
                    Log.e("Fraglike", String.valueOf(databaseError.toException()));
                }
            });
        } else {    // 메세지 목록이 이미 생성된 경우
            chatDatabaseReference = chatDatabase.getReference("Chatting_msg").child(chatting_msg);
        }

        // Adapter 붙이기.
        mAdapter = new ChatMsgAdapter(msgList, roomId, otherNickname);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.scrollToPosition(msgList.size()-1);
        rv.setAdapter(mAdapter);

        // 채팅 추가될 때마다 ReRendering
        chatDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatMsgVO chatMsgVO = dataSnapshot.getValue(ChatMsgVO.class);
                msgList.add(chatMsgVO);

                // 채팅 메시지 배열에 담고 RecyclerView 다시 그리기
                mAdapter = new ChatMsgAdapter(msgList, roomId, otherNickname);
                rv.setAdapter(mAdapter);
                rv.scrollToPosition(msgList.size()-1);
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
        return view;
    }

    @Override
    public void onClick(View v) {   // 채팅 보내기 버튼 클릭시 수행
        switch(v.getId()){
            case R.id.send_iv:
                if(content_et.getText().toString().trim().length() >= 1){
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

                    // Database 에 저장할 객체 생성
                    ChatMsgVO msgVO = new ChatMsgVO(User_Info.uid, df.format(new Date()).toString(), content_et.getText().toString().trim());

                    // 해당 DB 에 값 저장.
                    database = FirebaseDatabase.getInstance();
                    chatDatabaseReference = database.getReference("Chatting_msg").child(chatting_msg);
                    chatDatabaseReference.push().setValue(msgVO);

                    // 입력 필드 초기화
                    content_et.setText("");
                }else
                {
                    Toast.makeText(getActivity(), "메시지를 입력하세요.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}