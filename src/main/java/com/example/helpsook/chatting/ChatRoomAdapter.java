package com.example.helpsook.chatting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.Quest.Content_1VO;
import com.example.helpsook.Quest.Content_2VO;
import com.example.helpsook.Quest.Content_3VO;
import com.example.helpsook.Quest.Content_4VO;
import com.example.helpsook.Quest.QuestVO;
import com.example.helpsook.R;
import com.example.helpsook.UserVO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// 채팅 목록에서 각 채팅방 RecyclerView Item 마다 붙어있는 Adapter.
public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Etc
    private ChatRoomListActivity activity;
    private String title = "";                              // Quest 의 제목.
    private String otherNickname = "";                      // User 를 제외한 나머지 사용자.
    private final ArrayList<ChatRoomVO> chatRoomVOList;     // ChatRoom 객체를 담을 리스트.
    private ArrayList<QuestVO> questVOList;                 // Quest 객체를 담을 리스트.
    private ArrayList<String> qidList;                      // Qid 를 담을 리스트.

    public ChatRoomAdapter(ArrayList<ChatRoomVO> chatRoomVOList, ChatRoomListActivity activity) {
        this.chatRoomVOList = chatRoomVOList;

        // Adapter 에서 Activity 액션을 가져올 때 context 가 필요한데 Adapter 에는 context 가 없음.
        // => 선택한 Activity 에 대한 context 를 가져올 때 필요함.
        this.activity = activity;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_chat_room, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // DB 에서 각 Chatting_Room 에 맞는 Quest 을 가져오는 작업.
        questVOList = new ArrayList<QuestVO>();
        qidList = new ArrayList<String>();
        for (ChatRoomVO chatRoomVO: chatRoomVOList) {   // ChatRoom 객체로부터 qid 추출.
            qidList.add(chatRoomVO.getQid());
        }

        // 각 채팅방 목록에 표시할 퀘스트 내용 & 출발지 받아오기.
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Quest");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questVOList.clear();
                for (int i = 0; i < qidList.size(); i++) {  // Chatting_Room 개수만큼만 실행.
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (qidList.get(i).equals(snapshot.getKey())) {     // Chatting_Room 에 맞는 Quest 인지 판단.
                            QuestVO questVO = snapshot.getValue(QuestVO.class);
                            questVO.setQid(snapshot.getKey());
                            questVOList.add(questVO);
                        }
                    }
                }

                // 퀘스트 종류에 따라 다른 DB 에 접근
                DatabaseReference cDatabaseReference = null;
                switch (questVOList.get(position).getType()) {
                    case 1:
                        cDatabaseReference = database.getReference("Content_1");
                        break;
                    case 2:
                        cDatabaseReference = database.getReference("Content_2");
                        break;
                    case 3:
                        cDatabaseReference = database.getReference("Content_3");
                        break;
                    case 4:
                        cDatabaseReference = database.getReference("Content_4");
                        break;
                    default:
                        break;
                }
                // 퀘스트 종류에 따라 다른 VO 를 이용해 DB 에서 데이터를 읽어와 표시하기.
                cDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            switch (questVOList.get(position).getType()) {
                                case 1:
                                    Content_1VO content_1VO = snapshot.getValue(Content_1VO.class);
                                    if (content_1VO.getQid().equals(questVOList.get(position).getQid())) {
                                        holder.content_tv.setText(content_1VO.getDestination());
                                        holder.departure_tv.setText(content_1VO.getDeparture());
                                    }
                                    break;
                                case 2:
                                    Content_2VO content_2VO = snapshot.getValue(Content_2VO.class);
                                    if (content_2VO.getQid().equals(questVOList.get(position).getQid())) {
                                        holder.content_tv.setText(content_2VO.getMealtype());
                                        holder.departure_tv.setText(content_2VO.getCurLocation());
                                    }
                                    break;
                                case 3:
                                    Content_3VO content_3VO = snapshot.getValue(Content_3VO.class);
                                    if (content_3VO.getQid().equals(questVOList.get(position).getQid())) {
                                        holder.content_tv.setText(content_3VO.getLecture());
                                        holder.departure_tv.setText(content_3VO.getCurLocation());
                                    }
                                    break;
                                case 4:
                                    Content_4VO content_4VO = snapshot.getValue(Content_4VO.class);
                                    if (content_4VO.getQid().equals(questVOList.get(position).getQid())) {
                                        holder.content_tv.setText(content_4VO.getItem());
                                        holder.departure_tv.setText(content_4VO.getCurLocation());
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // DB 를 가져오던중 에러 발생
                        Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });

                // 각 채팅방 목록에 표시할 대화 상대의 별명 받아오기.
                String findUid;     // 나머지 한 명의 Uid
                if (chatRoomVOList.get(position).getOfferer().equals(User_Info.uid)) {  // User 가 아닌 다른 사람.
                    findUid = chatRoomVOList.get(position).getRequestor();
                } else
                    findUid = chatRoomVOList.get(position).getOfferer();

                DatabaseReference uDdatabaseReference = database.getReference("User");
                uDdatabaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            UserVO userVO = snapshot.getValue(UserVO.class);
                            if (userVO.getUid().equals(findUid)) {          // 나머지 한 명인 경우.
                                otherNickname = userVO.getNickname();       // 별명 저장.
                            }
                        }

                        // 각 채팅방 RecyclerView Item 에 표시할 정보 지정.
                        holder.uid_tv.setText(otherNickname);                              // 상대방 별명.
                        holder.roomTitle_tv.setText(questVOList.get(position).getTitle()); // Quest 제목.

                        // Quest 종류에 따라 다른 이미지 지정.
                        switch (questVOList.get(position).getType()) {
                            case 1:
                                holder.quest_imgv.setImageResource(R.drawable.content_1);
                                break;
                            case 2:
                                holder.quest_imgv.setImageResource(R.drawable.content_2);
                                break;
                            case 3:
                                holder.quest_imgv.setImageResource(R.drawable.content_3);
                                break;
                            case 4:
                                holder.quest_imgv.setImageResource(R.drawable.content_4);
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // DB 를 가져오던 중 에러 발생
                        Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB 를 가져오던중 에러 발생
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });
    }

    @Override
    public int getItemCount() {
        return (chatRoomVOList != null ? chatRoomVOList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Components
        public ImageView quest_imgv;
        public TextView roomTitle_tv, uid_tv, content_tv, departure_tv;

        public ViewHolder(View view) {
            super(view);
            // XML component 와 변수 연결
            uid_tv = view.findViewById(R.id.uid_tv);
            quest_imgv = view.findViewById(R.id.quest_imgv);
            roomTitle_tv = view.findViewById(R.id.roomTitle_tv);
            content_tv = view.findViewById(R.id.content_tv);
            departure_tv = view.findViewById(R.id.departure_tv);

            // 채팅방 목록에서 클릭했을 때 => 채팅방으로 이동
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAbsoluteAdapterPosition();

                    // 원하는 데이터를 담을 객체
                    Bundle argu = new Bundle();

                    // 채팅방에서 필요한 정보들 넘겨주기.
                    argu.putString("title", questVOList.get(position).getTitle());
                    argu.putString("roomId", chatRoomVOList.get(position).getRoomId());
                    argu.putString("otherNickname", otherNickname);
                    argu.putString("chatting_msg", chatRoomVOList.get(position).getChatting_msg());

                    // 이동할 Fragment 선언
                    ChatRoomFragment chatRoomFragment = new ChatRoomFragment();

                    // 이동할 Fragment 에 데이터 객체 담은 후 화면 전환.
                    chatRoomFragment.setArguments(argu);
                    activity.changeFragment(chatRoomFragment);
                }
            });
        }
    }
}
