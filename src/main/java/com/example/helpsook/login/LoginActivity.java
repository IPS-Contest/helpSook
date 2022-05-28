package com.example.helpsook.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.helpsook.MainActivity;
import com.example.helpsook.Public.User_Info;
import com.example.helpsook.R;
import com.example.helpsook.UserVO;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// 가장 처음에 띄워주는 Activity, 로그인 화면.
public class LoginActivity extends AppCompatActivity {
    // Firebase
    private FirebaseAuth mAuth = null;
    private FirebaseUser user;

    // Google
    private GoogleSignInClient mGoogleSignInClient;

    // Components
    private SignInButton signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // XML component 와 변수 연결
        signInButton = findViewById(R.id.signInButton);

        // 이미 로그인된 정보가 있는 경우
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            user = mAuth.getCurrentUser();
            updateUI(user);
        }

        // Google Sign In 환경 설정
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 구글 로그인 버튼 클릭시 작동하는 함수
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    // GoogleSignInApi.getSignInIntent(...) 에서 받아온 결과
                    if (result.getResultCode() == RESULT_OK) {  // 성공한 경우
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            // 구글 로그인을 성공적으로 마친 경우.
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            // 구글 로그인에 실패한 경우.
                            e.printStackTrace();
                        }
                    }
                }
            });

    // 로그인 함수. 구글에서 주관하는 로그인 페이지로 이동.
    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        launcher.launch(intent);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {  // 로그인 성공한 경우.
                        // 숙몀 구글 이메일인지 확인.
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        String emailForm = email.substring(email.indexOf("@") + 1);
                        if (emailForm.equals("sookmyung.ac.kr")) {  // 숙명 이메일인 경우
                            updateUI(user);
                        } else {                                    // 숙명 이메일이 아닌 다른 구글 계정인 경우
                            mAuth.getCurrentUser().delete();        // 게정 삭제
                            mAuth.signOut();                        // 로그아웃
                            revokeAccess();                         // 앱 & 구글 연결 끊기

                            // 숙명 이메일로만 로그인이 가능하다는 것을 Dialog 로 알림.
                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                            AlertDialog dialog = builder.setMessage("숙명 이메일로만 로그인이 가능합니다.").setPositiveButton("확인", null).create();
                            dialog.show();
                        }
                    } else {
                        // 로그인 실패한 경우.
                        updateUI(null);
                    }
                }
            });
    }

    // 로그인 여부에 따라 화면 전환하는 함수.
    private void updateUI(FirebaseUser user) {
        User_Info.uid = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("User");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    UserVO userVO = snapshot.getValue(UserVO.class);
                    if (User_Info.uid.equals(userVO.getUid())) {
                        User_Info.nickname = userVO.getNickname();
                    }
                }

                if (!User_Info.nickname.equals("")) { // 별명을 설정한 경우.
                    Toast.makeText(LoginActivity.this, "로그인되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {                             // 별명을 설정하지 않아서 볆명을 설정해야 하는 경우.
                    Toast.makeText(LoginActivity.this, "가입에 성공하셨습니다! 앱에서 사용할 별명을 정해주세요 :)", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, RegisterNicknameActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // DB 를 가져오던중 에러 발생.
                Log.e("Fraglike", String.valueOf(databaseError.toException()));
            }
        });
    }

    // 구글 & 앱 연결 끊는 함수.
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
                    }
                });

    }
}