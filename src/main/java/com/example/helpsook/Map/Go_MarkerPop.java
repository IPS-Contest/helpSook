package com.example.helpsook.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.helpsook.Quest.Content_1VO;
import com.example.helpsook.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Go_MarkerPop extends Activity {
    // Google
    private GoogleMap mMap;
    private Geocoder geocoder;

    // Components
    TextView closeBtn, destination, departure;

    // Etc
    String result;
    String[] address;
    Intent intent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_go_marker_pop);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // XML component 와 변수 연결
        destination = (TextView) findViewById(R.id.destination);
        departure = findViewById(R.id.location_go);

        // 닫기 버튼 클릭시 수행.
        closeBtn = (TextView) findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // MainActivity 로부터 전달받은 Qid 를 이용해 퀘스트 내용을 DB 로부터 읽어와 화면에 표시
        Intent get_intent = getIntent();
        String qid = get_intent.getStringExtra("qid");
        String requestor = get_intent.getStringExtra("requestor");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("Content_1");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Content_1VO content_1VO = snapshot.getValue(Content_1VO.class);
                    if (content_1VO.getQid().equals(qid)) {
                        destination.setText(content_1VO.getDestination());
                        departure.setText(content_1VO.getDeparture());
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

        // 목적지 클릭했을 때 수행.
        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Go_MarkerPop.this,Go_agreeOrdisagree.class);
                intent.putExtra("Geocode",destination.getText());
                intent.putExtra("qid", qid);
                intent.putExtra("requestor", requestor);
                startActivity(intent);
            }
        });
    }

}
