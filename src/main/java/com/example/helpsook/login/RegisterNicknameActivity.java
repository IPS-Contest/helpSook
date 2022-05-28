package com.example.helpsook.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpsook.MainActivity;
import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.example.helpsook.UserVO;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// User 가 별명을 설정하지 않은 경우 넘어오는 페이지, 별명 설정 화면.
public class RegisterNicknameActivity extends AppCompatActivity {
    // Firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference("User");

    // Components
    private EditText nickname;
    private Button cancel_button, join_button, check_button;

    // Etc
    private AlertDialog dialog;
    private boolean validate = true;    // 유효한 별명을 입력했는지 여부
    private int ifCheck = 0;            // 중복 체크를 했는지 여부
    String userNickname = "";           // 유저의 별명을 담는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_register_nickname);

        // XML component 와 변수 연결
        nickname = findViewById(R.id.nickname);

        // 별명을 다른 것을 입력하는지 체크
        nickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {            }

            @Override
            public void afterTextChanged(Editable s) {
                ifCheck = 0;    // 다른 텍스트 입력시 중복체크 다시하도록 설정.
            }
        });

        // 별명 중복 체크 버튼 클릭시 수행
        check_button = findViewById(R.id.check_button);
        check_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userNickname = nickname.getText().toString();
                validate = true;

                if (userNickname.equals("")) {  // 별명을 입력하지 않은 경우
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterNicknameActivity.this);
                    dialog = builder.setMessage("별명을 입력하세요.").setPositiveButton("확인", null).create();
                    dialog.show();
                    return;
                } else {                        // 별명을 입력한 경우 중복 체크
                    ifCheck++;
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {  // 반복문으로 데이터 List를 추출해냄
                                UserVO tmp = snapshot.getValue(UserVO.class);

                                // 중복되는 별명이 존재하는 경우
                                if (tmp.getNickname().equals(userNickname)) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterNicknameActivity.this);
                                    dialog = builder.setMessage("이미 존재하는 별명입니다.").setNegativeButton("확인", null).create();
                                    dialog.show();
                                    validate = false;
                                    break;
                                }
                            }

                            if (validate) {  // 중복되는 별명이 없는 경우 => 사용 가능
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterNicknameActivity.this);
                                dialog = builder.setMessage("사용할 수 있는 별명입니다.").setPositiveButton("확인", null).create();
                                dialog.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // DB 접근 중 에러 발생 시
                            Log.e("Fraglike", String.valueOf(databaseError.toException())); // 에러문 출력
                        }
                    });
                }
            }
        });

        // 닉네임 생성 버튼 클릭 시 수행
        join_button = findViewById( R.id.join_button );
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userNickname = nickname.getText().toString();

                // 한 글자도 입력 안 한 경우
                if (userNickname.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterNicknameActivity.this);
                    dialog = builder.setMessage("별명을 입력해주세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;
                }

                // 별명 중복체크를 하지 않은 경우
                if (ifCheck == 0 || !validate) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterNicknameActivity.this);
                    dialog = builder.setMessage("중복된 별명이 있는지 확인하세요.").setNegativeButton("확인", null).create();
                    dialog.show();
                    return;

                } else {    // 중복되지 않은 별명을 입력했고, 중복 체크를 한 경우
                    User_Info.nickname = userNickname;  // 전역 변수에 값 설정.

                    UserVO userVO = new UserVO();
                    userVO.setNickname(User_Info.nickname);
                    userVO.setUid(User_Info.uid);

                    // 해당 DB 에 값 저장
                    databaseReference = database.getReference("User");
                    databaseReference.push().setValue(userVO);

                    // 입력 필드 초기화
                    nickname.setText("");

                    // Main 으로 이동
                    Toast.makeText(RegisterNicknameActivity.this, "별명을 정했습니다!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterNicknameActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        // 취소 버튼 클릭시 수행. LoginActivity 로 돌아감.
        cancel_button = findViewById( R.id.cancel_button );
        cancel_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                finish();
            }
        });
    }
}