package com.example.helpsook.Map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.helpsook.MainActivity;
import com.example.helpsook.Public.User_Info;
import com.example.helpsook.Quest.OffererVO;
import com.example.helpsook.R;
import com.example.helpsook.chatting.ChatRoomVO;
import com.google.android.gms.maps.GoogleMap;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Go_agreeOrdisagree extends AppCompatActivity implements OnMapReadyCallback {

    // Map
    private GoogleMap mMap;
    private Geocoder geocoder;
    private NaverMap get_naverMap;

    // Components
    Button agree, disagree;

    // Etc
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    String address_string;
    double latitude, longitude;
    Marker here;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_go_agree_ordisagree);

        // XML component 와 변수 연결
        agree = (Button) findViewById(R.id.go_agree);
        disagree = (Button) findViewById(R.id.go_disagree);

        Intent get_intent = getIntent();
        address_string = get_intent.getStringExtra("Geocode");

        // 도와주겠다는 버튼을 클릭했을 때 수행.
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(Go_agreeOrdisagree.this);
                ad.setMessage("결정되었습니다!\n확인 버튼을 누르면 채팅방이 생성됩니다.");
                ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        // Go_MarkerPop 로부터 Qid 받아오기
                        String qid = get_intent.getStringExtra("qid");
                        
                        // 채팅방 생성
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
                                        Toast.makeText(Go_agreeOrdisagree.this, "이미 채팅방이 개설되어 있습니다.", Toast.LENGTH_SHORT).show();
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

        //최종 확인에서 취소 시 바로 메인화면으로 전환
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Go_agreeOrdisagree.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"취소되었습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        //지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = mapFragment.newInstance();
            fm.beginTransaction().add(R.id.mapChecking, mapFragment).commit();
        }
        //getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        //onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this);
        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    address_string, // 주소
                    10); // 최대 검색 결과 개수
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        String []splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1,splitStr[0].length() - 2); // 주소
        System.out.println(address);

        String lat = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longi = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도

        latitude = Double.parseDouble(lat);
        longitude = Double.parseDouble(longi);
    }

    //목적지가 어디 있는지 지도에 마커로 표시
    @Override
    public void onMapReady(@NonNull NaverMap mnaverMap) {
        Marker here = new Marker();
        here.setPosition(new LatLng(latitude,longitude));
        here.setMap(mnaverMap);
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude,longitude));
        mnaverMap.moveCamera(cameraUpdate);
    }
}
