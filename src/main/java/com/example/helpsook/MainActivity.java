package com.example.helpsook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.helpsook.Map.Get_MarkerPop;
import com.example.helpsook.Map.Go_MarkerPop;
import com.example.helpsook.Map.Let_MarkerPopE;
import com.example.helpsook.Map.Let_MarkerPopT;
import com.example.helpsook.Public.User_Info;
import com.example.helpsook.Quest.PopupActivity_Get;
import com.example.helpsook.Quest.PopupActivity_Go;
import com.example.helpsook.Quest.PopupActivity_Let;
import com.example.helpsook.Quest.QuestVO;
import com.example.helpsook.Quest.MyRequestListActivity;
import com.example.helpsook.chatting.ChatRoomListActivity;
import com.example.helpsook.login.LoginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // Firebase
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    // Map
    private NaverMap naverMap; //메인 화면에 표시 및 사용자 위치를 추적, 파악하기 위한 지도 객체

    // Components
    private ImageButton chat_button;
    private Button logout;
    //메인 화면에서 누르면 리퀘스트 팝업창이 뜨는 세 버튼 + 내가 요청한 리퀘스트를 저장한 페이지를 불러올 버튼
    Button gohome, gethelp, letsdo, myRequestList;

    // Components
    //사용자의 현재 위치를 추적하기 위한 변수들(객체 및 코드)
    private FusedLocationSource locationSource;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "MainActivity";
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    double lati, longi;

    //팝업창 확인 버튼 누를 시 마커 생성을 위한 위치 (경도, 위도 )
    double latitude, longitude;
    Intent intent = null;
    private static final int REQUEST_CODE_LOCATION = 2;
    LocationManager locationManager;

    private ArrayList<Marker> markerArrayList;  // Marker 객체들 담을 ArrayList
    private ArrayList<QuestVO> questVOList;     // Quest 객체들 담을 ArrayList

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //지도 객체 생성
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
        if(mapFragment == null){
            mapFragment = mapFragment.newInstance();
            fm.beginTransaction().add(R.id.map, mapFragment).commit();
        }
        // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
        // onMapReady에서 NaverMap 객체를 받음
        mapFragment.getMapAsync(this);

        //위치를 반환하는 구현체인 FusedLocationSource 생성
        locationSource = new FusedLocationSource(this,LOCATION_PERMISSION_REQUEST_CODE);

        //네이버 마커가 정확한 위치를 파악하지 못해서 적은 경고문
        Toast.makeText(getApplicationContext(),"지도 위 보여지는 표시는 오차가 존재합니다.",Toast.LENGTH_LONG).show();

        // 지도에 표시할 퀘스트 목록 생성
        questVOList = new ArrayList<QuestVO>();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Quest");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                questVOList.clear();         // 초기화
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuestVO questVO = snapshot.getValue(QuestVO.class);
                    // 마감된 퀘스트는 뜨지 않도록
                    if (!questVO.getComplete() && !questVO.getRequestor().equals(User_Info.uid)) {
                        questVO.setQid(snapshot.getKey());
                        questVOList.add(questVO);
                    }
                }
                // 표시할 퀘스트가 하나도 없는 경우 Marker 를 그리지 않음.
                if (questVOList.size() > 0) {
                    drawMarker(questVOList);
                }

                // 마커 그리기 - 퀘스트 하나 추가될 때마다
                databaseReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) { // 추가되는 경우
                        Boolean existOrMineOrComplete = false;
                        String key = dataSnapshot.getKey();
                        QuestVO questVO = dataSnapshot.getValue(QuestVO.class);
                        questVO.setQid(key);

                        // 마감되었거나, 요청자가 User 인 퀘스트인 경우
                        if (questVO.getComplete() || questVO.getRequestor().equals(User_Info.uid))
                            existOrMineOrComplete = true;
                        for (QuestVO questVOt: questVOList) {
                            // 배열에 이미 존재하는 경우.
                            if (key.equals(questVOt.getQid())) {
                                existOrMineOrComplete = true;
                                break;
                            }
                        }
                        // 지도에 새로 그려야 하는 퀘스트인 경우
                        if (!existOrMineOrComplete) {
                            questVOList.add(questVO);
                            drawMarker(questVOList);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        String key = dataSnapshot.getKey();
                        QuestVO questVO = dataSnapshot.getValue(QuestVO.class);
                        questVO.setQid(key);
                        Boolean isComplete = questVO.getComplete(); // 마감 여부
                        Boolean isMine = false;                     // 도움 요청자가 자신인 경우
                        if (questVO.getRequestor().equals(User_Info.uid))   // 도움 요청자가 자신인 경우
                            isMine = true;
                        if (isComplete && !isMine) {               // 마감된 경우 => 퀘스트 리스트에서 제거해야함.
                            for (int i = 0; i < questVOList.size(); i++) {
                                if (key.equals(questVOList.get(i).getQid())) {
                                    QuestVO questVO1 = questVOList.get(i);
                                    questVOList.remove(questVO1);           // 퀘스트 리스트에서 제거
                                    markerArrayList.get(i).setMap(null);    // 지도상에서 마커 제거
                                    drawMarker(questVOList);                // 마커 다시 그리기
                                    break;
                                }
                            }
                        } else if (!isComplete && !isMine){        // 마감했다가 다시 풀은 경우 => 지도에 마커 추가해야함.
                            questVOList.add(questVO);
                            drawMarker(questVOList);                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

                    @Override
                    public void onCancelled(DatabaseError databaseError) { }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB 를 가져오던중 에러 발생
                Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
            }
        });

        // 채팅 목록 접속
        chat_button = findViewById(R.id.chat_button);
        chat_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatRoomListActivity.class);
                startActivity(intent);
            }
        });

        // 자기가 요청한 퀘스트 목록 접속
        myRequestList = findViewById(R.id.myRequestList);
        myRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyRequestListActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();
                revokeAccess();         // 구글 & 앱 연결 끊기
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        //메인화면에서 각각의 리퀘스트 창으로 넘어갈 버튼
        Button.OnClickListener btnListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.goHome:   //같이귀가하숙

                        Intent intent1 = new Intent(MainActivity.this, PopupActivity_Go.class);
                        startActivityForResult(intent1,1);

                        break;
                    case R.id.getHelp:  //좀도와주숙

                        Intent intent2 = new Intent(MainActivity.this, PopupActivity_Get.class);
                        startActivityForResult(intent2,2);
                        //startActivity(intent2);

                        break;
                    case R.id.letsDo:   //같이해보숙

                        Intent intent3 = new Intent(MainActivity.this, PopupActivity_Let.class);
                        startActivityForResult(intent3,3);

                        break;
                }
            }
        };

        gohome = findViewById(R.id.goHome);
        gethelp = findViewById(R.id.getHelp);
        letsdo = findViewById(R.id.letsDo);

        gohome.setOnClickListener(btnListener);
        gethelp.setOnClickListener(btnListener);
        letsdo.setOnClickListener(btnListener);

       // 학교 근처에서만 도움 요청 가능하도록 제한.
       locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
       Location userlocation = getMyLocation();
       if(userlocation != null){
           lati = userlocation.getLatitude();
           longi = userlocation.getLongitude();
       }
       if(!((lati <=37.6 && lati >= 37.5) &&(longi <= 127 && longi >= 126.9))) {
           AlertDialog.Builder dlg = new AlertDialog.Builder(this);
           dlg.setMessage("현재 위치에서 퀘스트 생성은 불가합니다.");
           dlg.setCancelable(false);
           dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   gohome.setEnabled(false);
                   gethelp.setEnabled(false);
                   letsdo.setEnabled(false);
               }
           });
           dlg.show();
       }else{
           gohome.setEnabled(true);
           gethelp.setEnabled(true);
           letsdo.setEnabled(true);
       }
    }

    // 구글과 연결 끊기
    private void revokeAccess() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);
        googleSignInClient.revokeAccess()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...;
                    }
                });

    }

    @Override
    public void onMapReady(@NonNull NaverMap mnaverMap) {   // 기본 함수: 앱이 다 준비하면 그려질 default 지도.
        naverMap = mnaverMap;
        naverMap.setLocationSource(locationSource);//현재 위치
        if (questVOList.size() > 0) {
            drawMarker(questVOList);
        }
        ActivityCompat.requestPermissions(this,PERMISSIONS,LOCATION_PERMISSION_REQUEST_CODE);//현재 위치 표시할 때 권한 확인
    }

    //현재 위치를 실시간으로 추적하기 위해 사용자에게 권한 요청하기.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }else{
                naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    // 마커 그리는 함수
    private void drawMarker(ArrayList<QuestVO> questVOList) {
        markerArrayList = new ArrayList<Marker>();
        for (QuestVO questVO: questVOList) {
            Marker marker = new Marker();
            marker.setHeight(100);
            marker.setWidth(75);

            // 퀘스트 종류에 따라 달라지는 마커의 색.
            switch (questVO.getType()) {
                case 1:
                    marker.setIconTintColor(Color.BLUE);
                    break;
                case 2:
                    marker.setIconTintColor(Color.RED);
                    break;
                case 3:
                    marker.setIconTintColor(Color.BLACK);
                    break;
                case 4:
                    marker.setIconTintColor(Color.YELLOW);
                    break;
                default:
                    break;
            }

            // 마커를 그릴 위도, 경도
            latitude = Double.parseDouble(questVO.getLatitude());
            longitude = Double.parseDouble(questVO.getLongitude());

            marker.setPosition(new LatLng(latitude,longitude)); // 마커 위치 설정
            marker.setMap(naverMap);                            // 지도에 마커 설정

            // 마커 클릭시 수행
            marker.setOnClickListener(new Overlay.OnClickListener() {
                @Override
                public boolean onClick(@NonNull Overlay overlay){
                    if(overlay instanceof Marker){
                        // 퀘스트 종류에 따라 달라지는 Intent
                        switch (questVO.getType()) {
                            case 1:
                                intent = new Intent(MainActivity.this, Go_MarkerPop.class);
                                break;
                            case 2:
                                intent = new Intent(MainActivity.this, Let_MarkerPopE.class);
                                break;
                            case 3:
                                intent = new Intent(MainActivity.this, Let_MarkerPopT.class);
                                break;
                            case 4:
                                intent = new Intent(MainActivity.this, Get_MarkerPop.class);
                                break;
                            default:
                                break;
                        }
                        intent.putExtra("qid", questVO.getQid());
                        intent.putExtra("requestor", questVO.getRequestor());
                        startActivity(intent);
                        return true;
                    }return false;
                }
            });
            markerArrayList.add(marker);
        }
    }

    //제한 구역인지 확인하기 위해 현재 사용자의 위치를 위도, 경도를 추적 및 파악함.
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("////////////사용자에게 권한을 요청해야함");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    this.REQUEST_CODE_LOCATION);
            getMyLocation(); //이건 써도되고 안써도 되지만, 전 권한 승인하면 즉시 위치값 받아오려고 썼습니다!
        }
        else {
            System.out.println("////////////권한요청 안해도됨");

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lati = currentLocation.getLongitude();
                double longi = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }
}
