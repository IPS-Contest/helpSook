package com.example.helpsook.Quest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.app.AlertDialog;
import androidx.core.app.ActivityCompat;

// 좀도와주숙
public class PopupActivity_Get extends Activity {
    // Firebase
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    // Etc
    //현재 위치 좌표를 담을 double[] 선언
    String[] location;
    double longitude , latitude;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    public static final String ARRAYS_COUNT = "com.yourname.ARRAYS_COUNT";
    public static final String ARRAY_INDEX = "com.yourname.ARRAY_INDEX";

    // item
    String itemName = "";
    Boolean isEtc = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_get);

        TextView slist = findViewById(R.id.supplies_list);
        EditText editText = findViewById(R.id.editText);
        Button returnBtn = findViewById(R.id.returnBtn);

        ImageButton closeButton2 = (ImageButton)findViewById(R.id.closeButton2);
        Button checkBtn2 = (Button)findViewById(R.id.getHelpFin);
        EditText whereAmI = findViewById(R.id.whereAmI);

        //x창 클릭시 창을 닫기
        closeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        //menu list에서 아이템 클릭 시 textview에 선택된 값 전달 및 표시.
        slist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(PopupActivity_Get.this, view);
                getMenuInflater().inflate(R.menu.supplies_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.feminine:
                                slist.setText("여성용품");
                                itemName = "여성용품";
                                break;
                            case R.id.writing:
                                slist.setText("필기구");
                                itemName = "필기구";
                                break;
                            case R.id.paper:
                                slist.setText("이면지");
                                itemName = "이면지";
                                break;
                            case R.id.chargerC:
                                slist.setText("C타입 충전기");
                                itemName = "C타입 충전기";
                                break;
                            case R.id.charger8:
                                slist.setText("8핀 충전기");
                                itemName = "8핀 충전기";
                                break;
                            case R.id.chargerTabletS:
                                slist.setText("삼성 노트북 충전기");
                                itemName = "삼성 노트북 충전기";
                                break;
                            case R.id.chargerTabletA:
                                slist.setText("애플 노트북 충전기");
                                itemName = "애플 노트북 충전기";
                                break;
                            case R.id.etc:
                                isEtc = true;
                                editText.setVisibility(View.VISIBLE);
                                returnBtn.setVisibility(View.VISIBLE);
                                slist.setVisibility(View.GONE);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });

        //직접 입력한 item을 취소하기
        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.returnBtn:
                        isEtc = false;
                        editText.setVisibility(View.GONE);
                        returnBtn.setVisibility(View.GONE);
                        slist.setVisibility(View.VISIBLE);
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
        checkBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 한 칸이라도 빈 칸이 있는 경우
                if ((isEtc && editText.getText().toString().equals("")) || (!isEtc && itemName.equals("")) || whereAmI.getText().toString().equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity_Get.this);
                    AlertDialog dialog = builder.setMessage("모든 칸을 채워주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else {
                    // 퀘스트 생성 type = 4
                    try {
                        // DB 설정
                        database = FirebaseDatabase.getInstance();
                        databaseReference = database.getReference("Quest");

                        // Database 에 저장할 Quest 객체 생성
                        QuestVO questVO = new QuestVO(false, User_Info.uid, "#좀도와주숙", 4, "", location[0], location[1]);

                        // 해당 DB 에 값 저장
                        String qid = databaseReference.push().getKey();
                        databaseReference = databaseReference.child(qid);
                        databaseReference.setValue(questVO);

                        // 종류에 따른 퀘스트 내용 객체 생성
                        Content_4VO content_4VO = null;
                        if (isEtc)
                            content_4VO = new Content_4VO(editText.getText().toString(), qid, whereAmI.getText().toString());
                        else
                            content_4VO = new Content_4VO(itemName, qid, whereAmI.getText().toString());

                        // DB 설정 및 해당 DB 에 값 저장
                        databaseReference = database.getReference("Content_4");
                        databaseReference.push().setValue(content_4VO);

                        Toast.makeText(PopupActivity_Get.this, "도움 요청 퀘스트를 생성했습니다..", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("location_get", location);
                        setResult(RESULT_OK, intent);

                        //팝업창 닫기
                        finish();
                    } catch (Exception e) {

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

    //팝업 밖 선택 시 닫힘 방지
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if( event.getAction() == MotionEvent.ACTION_OUTSIDE ) {
            return false;
        }
        return true;
    }

}
