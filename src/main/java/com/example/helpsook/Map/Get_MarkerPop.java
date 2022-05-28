package com.example.helpsook.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.Quest.Content_4VO;
import com.example.helpsook.Quest.OffererVO;
import com.example.helpsook.R;
import com.example.helpsook.chatting.ChatRoomVO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Get_MarkerPop extends Activity {
    // Components
    TextView closeBtn, curLocation, thing;
    Button check_get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_get_marker_pop);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // XML component 와 변수 연결
        curLocation = findViewById(R.id.location_get);
        thing = findViewById(R.id.thing);

        // MainActivity 로부터 전달받은 Qid 를 이용해 퀘스트 내용을 DB 로부터 읽어와 화면에 표시
        Intent get_intent = getIntent();
        String qid = get_intent.getStringExtra("qid");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Content_4");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Content_4VO content_4VO = snapshot.getValue(Content_4VO.class);
                    if (content_4VO.getQid().equals(qid)) {
                        curLocation.setText(content_4VO.getCurLocation());
                        thing.setText(content_4VO.getItem());
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

        // 닫기 버튼 클릭시 수행.
        closeBtn = (TextView) findViewById(R.id.closeBtn1);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 도와주겠다는 버튼을 클릭했을 때 수행.
        check_get = (Button) findViewById(R.id.check_get);
        check_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Get_MarkerPop.this);
                ad.setMessage("정말 선택하시겠습니까?");
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // 채팅방 생성.
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference databaseReference = database.getReference("Chatting_Room");

                        final Boolean[] exist = {false};
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    ChatRoomVO chatRoomVO = snapshot.getValue(ChatRoomVO.class);
                                    // 이미 도와주고자 연락을 넣었던 퀘스트인 경우. (채팅방이 이미 개설되어 있는 경우.)
                                    if (chatRoomVO.getQid().equals(qid) && chatRoomVO.getOfferer().equals(User_Info.uid)) {
                                        Toast.makeText(Get_MarkerPop.this, "이미 채팅방이 개설되어 있습니다.", Toast.LENGTH_SHORT).show();
                                        exist[0] = true;
                                        break;
                                    }
                                }
                                // 아직 도와준다고 연락한 적이 없는 퀘스트인 경우. (채팅방이 개설되어 있지 않은 경우.)
                                if (!exist[0]) {
                                    FirebaseDatabase databaseT = FirebaseDatabase.getInstance();
                                    DatabaseReference databaseReferenceT = databaseT.getReference("Chatting_Room");

                                    // 채팅 DB 생성을 위한, Database 에 저장할 빈 객체 생성
                                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    ChatRoomVO chatRoomVO = new ChatRoomVO(qid, "",  df.format(new Date()).toString(), User_Info.uid, get_intent.getStringExtra("requestor"));

                                    // 해당 DB 에 값 저장
                                    databaseReferenceT.push().setValue(chatRoomVO);

                                    // Offer_List 추가
                                    databaseReferenceT = database.getReference("Offerer_list");

                                    // Database 에 저장할 Offerer 객체 생성
                                    OffererVO offererVO = new OffererVO(User_Info.uid, qid);

                                    // 해당 DB 에 값 저장
                                    databaseReferenceT.push().setValue(offererVO);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // DB 를 가져오던중 에러 발생
                                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                            }
                        });
                        finish();
                    }
                });
                ad.show();
            }
        });
    }
}
