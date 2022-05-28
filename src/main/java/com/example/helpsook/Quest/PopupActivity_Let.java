package com.example.helpsook.Quest;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// 같이하숙
public class PopupActivity_Let extends Activity {
    // Firebase
    FirebaseDatabase database;
    DatabaseReference databaseReference;

    // Etc
    String[] location;
    double longitude , latitude;
    private LocationManager locationManager;
    private static final int REQUEST_CODE_LOCATION = 2;

    public static final String ARRAYS_COUNT = "com.yourname.ARRAYS_COUNT";
    public static final String ARRAY_INDEX = "com.yourname.ARRAY_INDEX";

    private int type = 0;   // Type

    // max_expensive, mealtype
    int progress = 0;
    String mealtype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //actionbar 제거
        requestWindowFeature( Window.FEATURE_NO_TITLE );
        setContentView(R.layout.activity_popup_let);

        //변수 지정
        RadioButton letsGo = (RadioButton) findViewById(R.id.letsGo);
        RadioButton letsEat = (RadioButton) findViewById(R.id.letsEat);
        LinearLayout selectClass = (LinearLayout) findViewById(R.id.selectClass);
        LinearLayout selectMeal = (LinearLayout) findViewById(R.id.selectMeal);
        TextView meal_list = (TextView) findViewById(R.id.meal_list);
        Space blank = (Space) findViewById(R.id.blank);
        SeekBar priceRate = (SeekBar) findViewById(R.id.priceRate);
        TextView textViewPrice = (TextView)findViewById(R.id.textViewPrice);
        EditText lecture = findViewById(R.id.lecture);
        EditText whereAmI = findViewById(R.id.whereAmI);

        ImageButton closeButton3 = (ImageButton)findViewById(R.id.closeButton3);
        Button checkBtn3 = (Button)findViewById(R.id.letsDoFin);

        //x창 클릭시 창을 닫기
        closeButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //식비를 지정하기 위한 progress 바
        textViewPrice.setText(priceRate.getProgress()+"원 대");
        priceRate.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                        int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
                        textViewPrice.setText(progress+"000원 대");
                        //textViewScore.setY(280);
                        Point maxSizePoint = new Point();
                        getWindowManager().getDefaultDisplay().getSize(maxSizePoint);
                        int maxX = maxSizePoint.x;
                        textViewPrice.setX(textViewPrice.getMeasuredWidth() + (val + seekBar.getThumbOffset() / 10) >
                                maxX ? (maxX - textViewPrice.getMeasuredWidth() - 90) : val + seekBar.getThumbOffset() / 10);
                        progress = progressValue;
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        // Display the value in textview
                        textViewPrice.setText(progress+"000원 대");
                    }
                });
        //같이 수업듣기 버튼 클릭시 해당 내용만 보이게 하기
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blank.setVisibility(View.GONE);
                selectClass.setVisibility(View.VISIBLE);
                selectMeal.setVisibility(View.GONE);
                type = 3;
            }
        });
        //같이 식사하기 버튼 클릭시 해당 내용만 보이게 하기
        letsEat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                blank.setVisibility(View.GONE);
                selectMeal.setVisibility(View.VISIBLE);
                selectClass.setVisibility(View.GONE);
                type = 2;
            }
        });
        //같이 식사하기: 메뉴 종류 고르기
        meal_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(PopupActivity_Let.this,view);
                getMenuInflater().inflate(R.menu.meal_menu,popup.getMenu());

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.korean:
                                meal_list.setText("한식");
                                mealtype = "한식";
                                break;
                            case R.id.western:
                                meal_list.setText("양식");
                                mealtype = "양식";
                                break;
                            case R.id.china:
                                meal_list.setText("중식");
                                mealtype = "중식";
                                break;
                            case R.id.japan:
                                meal_list.setText("일식");
                                mealtype = "일식";
                                break;
                            case R.id.flour:
                                meal_list.setText("분식");
                                mealtype = "분식";
                                break;
                            case R.id.mexico:
                                meal_list.setText("멕시코식");
                                mealtype = "멕시코식";
                                break;
                            case R.id.thai:
                                meal_list.setText("태국식");
                                mealtype = "태국식";
                                break;
                            case R.id.vietnam:
                                meal_list.setText("베트남식");
                                mealtype = "베트남식";
                                break;
                        }
                        return false;
                    }
                });
                popup.show();
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
        checkBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 한 칸이라도 빈 칸이 있는 경우
                if (type == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity_Let.this);
                    AlertDialog dialog = builder.setMessage("모든 칸을 채워주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else if (type == 2 && (progress == 0 || mealtype.equals("") || whereAmI.getText().toString().equals(""))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity_Let.this);
                    AlertDialog dialog = builder.setMessage("모든 칸을 채워주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else if (type == 3 && (lecture.getText().toString().equals("") || whereAmI.getText().toString().equals(""))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PopupActivity_Let.this);
                    AlertDialog dialog = builder.setMessage("모든 칸을 채워주세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                } else {
                    // 퀘스트 생성 type = 2, 3
                    try {
                        // DB 설정
                        database = FirebaseDatabase.getInstance();
                        databaseReference = database.getReference("Quest");

                        // Database 에 저장할 Quest 객체를 종류에 따라 생성
                        QuestVO questVO = null;
                        if (type == 2)
                            questVO = new QuestVO(false, User_Info.uid, "#같이식사하숙", type, "", location[0], location[1]);
                        else if (type == 3)
                            questVO = new QuestVO(false, User_Info.uid, "#같이수업듣숙", type, "", location[0], location[1]);

                        // 해당 DB 에 값 저장
                        String qid = databaseReference.push().getKey();
                        databaseReference = databaseReference.child(qid);
                        databaseReference.setValue(questVO);

                        // 종류에 따른 퀘스트 내용 객체 생성, DB 설정 및 해당 DB 에 값 저장
                        if (type == 2) {
                            Content_2VO content_2VO = new Content_2VO(progress * 1000, mealtype, qid, whereAmI.getText().toString());
                            databaseReference = database.getReference("Content_2");
                            databaseReference.push().setValue(content_2VO);
                        } else if (type == 3) {
                            Content_3VO content_3VO = new Content_3VO(lecture.getText().toString(), qid, whereAmI.getText().toString());
                            databaseReference = database.getReference("Content_3");
                            databaseReference.push().setValue(content_3VO);
                        }

                        Toast.makeText(PopupActivity_Let.this, "도움 요청 퀘스트를 생성했습니다..", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("location_let", location);
                        setResult(RESULT_OK, intent);

                        //팝업창 닫기
                        finish();
                    }catch(Exception e){

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
