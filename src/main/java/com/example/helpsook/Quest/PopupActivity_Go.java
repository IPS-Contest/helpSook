package com.example.helpsook.Quest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import android.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.helpsook.Map.AddressApiActivity;
import com.example.helpsook.Public.NetworkStatus;
import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 같이 귀가하숙
public class PopupActivity_Go extends Activity {
    // Firebase
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    // Components
    Button checkBtn1, WhereBtn,imHereBtn;
    ImageButton closeButton1;
    EditText whereAmI;
    EditText whereToGo;

    // Etc
    String[] location;
    double longitude , latitude;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    // 주소 요청코드 상수 requestCode
    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    String data;

    public static final String ARRAYS_COUNT = "com.yourname.ARRAYS_COUNT";
    public static final String ARRAY_INDEX = "com.yourname.ARRAY_INDEX";

    // 도로명 주소
    String destination = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_popup_go);

        closeButton1 = (ImageButton)findViewById(R.id.closeButton1);
        checkBtn1 = (Button)findViewById(R.id.goHomeFin);
        whereAmI = findViewById(R.id.whereAmI);

        //x창 클릭시 창을 닫기
        closeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //인터넷 연결 확인
        whereToGo= (EditText) findViewById(R.id.whereToGo);
        //터치안되게 막기
        whereToGo.setFocusable(false);
        whereToGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("주소설정페이지","주소 입력창 클릭");
                //목적지의 주소를 검색하기 위해 카카오 지도 웹 뷰를 연결
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    Log.i("주소설정페이지","주소 입력창 클릭");
                    Intent i = new Intent(getApplicationContext(), AddressApiActivity.class);
                    //화면전환 애니메이션 없애기
                    overridePendingTransition(0,0);
                    //주소결과
                    startActivityForResult(i,SEARCH_ADDRESS_ACTIVITY);
                }else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //현재 위치 좌표 전달하기
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        //사용자의 현재 위치
        Location userLocation = getMyLocation();
        if(userLocation != null){
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();
        }
        location = new String[]{Double.toString(latitude),Double.toString(longitude)};

        //확인 버튼 클릭 시 현재위치를 좌표로 전달하고 창닫기
        checkBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 한 칸이라도 빈 칸이 있는 경우
                if (destination.equals("") || whereAmI.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity_Go.this);
                    AlertDialog dialog = builder.setMessage("모든 칸을 채워주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else {
                    // 퀘스트 생성 type = 1
                    try {
                        // DB 설정
                        database = FirebaseDatabase.getInstance();
                        databaseReference = database.getReference("Quest");

                        // Database 에 저장할 Quest 객체 생성
                        QuestVO questVO = new QuestVO(false, User_Info.uid, "#같이귀가하숙", 1, "", location[0], location[1]);

                        // 해당 DB 에 값 저장
                        String qid = databaseReference.push().getKey();
                        databaseReference = databaseReference.child(qid);
                        databaseReference.setValue(questVO);

                        // 퀘스트 내용 객체 생성
                        Content_1VO content_1VO = new Content_1VO(whereAmI.getText().toString(), destination, qid);

                        // DB 설정 및 해당 DB 에 값 저장
                        databaseReference = database.getReference("Content_1");
                        databaseReference.push().setValue(content_1VO);

                        Toast.makeText(PopupActivity_Go.this, "도움 요청 퀘스트를 생성했습니다..", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("forGeocoding", data);
                        intent.putExtra("location_go", location);
                        intent.putExtra("departure", location);
                        intent.putExtra("destination", location);
                        setResult(RESULT_OK, intent);

                        //팝업창 닫기
                        finish();

                    } catch(Exception e) {

                    }
                }
            }
        });
    }
    //마커를 만들기 위해 현재 사용자의 위치를 위도, 경도를 추적 및 파악함.
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
                latitude = currentLocation.getLongitude();
                longitude = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }
    //퀘스트 생성 시(메인 화면으로 돌아감), 카카오 지도로 부터 받은 도로명 주소를 메인 화면으로 전달
    public void onActivityResult(int request, int result, Intent intent){
        super.onActivityResult(request,result,intent);
        Log.i("test","onActivityResult");

        switch (request){
            case SEARCH_ADDRESS_ACTIVITY:
                if(result == RESULT_OK){
                    data = intent.getExtras().getString("data");
                    if(data != null){
                        Log.i("test", "data: "+data);
                        whereToGo.setText(data);
                        destination = data;
                    }
                }
                break;
        }
    }
    //팝업 밖 선택 시 닫힘 방지
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE ) {
            return false;
        }
        return true;
    }
}
