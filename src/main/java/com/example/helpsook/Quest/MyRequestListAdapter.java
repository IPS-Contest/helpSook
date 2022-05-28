package com.example.helpsook.Quest;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helpsook.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyRequestListAdapter extends RecyclerView.Adapter<MyRequestListAdapter.ViewHolder> {
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Etc
    private ArrayList<QuestVO> questVOList;                 // Quest 객체를 담을 리스트.

    public MyRequestListAdapter(ArrayList<QuestVO> questVOList) {
        this.questVOList = questVOList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_quest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (questVOList.size() != 0 && position < questVOList.size() && position > -1) {
            // 퀘스트 종류에 따라 다른 DB 에 접근
            database = FirebaseDatabase.getInstance();
            switch (questVOList.get(position).getType()) {
                case 1:
                    databaseReference = database.getReference("Content_1");
                    break;
                case 2:
                    databaseReference = database.getReference("Content_2");
                    break;
                case 3:
                    databaseReference = database.getReference("Content_3");
                    break;
                case 4:
                    databaseReference = database.getReference("Content_4");
                    break;
                default:
                    break;
            }
            // 퀘스트 종류에 따라 다른 VO 를 이용해 DB 에서 데이터를 읽어와 표시하기.
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (position < questVOList.size() && position > -1) {
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

                        // 각 채팅방 RecyclerView Item 에 표시할 정보 지정..
                        holder.title_tv.setText(questVOList.get(position).getTitle());           // Quest 제목.
                        holder.complete.setChecked(questVOList.get(position).getComplete());     // Quest 마감 여부

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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // DB 를 가져오던중 에러 발생
                    Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (questVOList != null ? questVOList.size() : 0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Components
        public ImageView quest_imgv;
        public TextView title_tv, content_tv, departure_tv;
        public Switch complete;

        public ViewHolder(View view) {
            super(view);
            // XML component 와 변수 연결
            title_tv = view.findViewById(R.id.title_tv);
            content_tv = view.findViewById(R.id.content_tv);
            quest_imgv = view.findViewById(R.id.quest_imgv);
            complete = view.findViewById(R.id.complete);
            departure_tv = view.findViewById(R.id.departure_tv);

            complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (getAbsoluteAdapterPosition() < questVOList.size() && getAbsoluteAdapterPosition() > -1) {
                        // 스위치 버튼의 상태에 따라 DB Update.
                        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference mDatabaseReference = mDatabase.getReference("Quest").child(questVOList.get(getAbsoluteAdapterPosition()).getQid());
                        if (isChecked) {    // 체크한 경우. (마감된 경우)
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("complete", true);

                            mDatabaseReference.updateChildren(taskMap);
                        } else {            // 체크 안 한 경우. (마감 안 한 경우)
                            Map<String, Object> taskMap = new HashMap<String, Object>();
                            taskMap.put("complete", false);

                            mDatabaseReference.updateChildren(taskMap);
                        }
                    }
                }
            });
        }
    }
}
