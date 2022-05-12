package com.example.helpsook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session.*;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.helpsook.chatting.ChatListActivity;
import com.example.helpsook.login.LoginActivity;

import java.util.logging.Handler;

public class MainActivity extends AppCompatActivity {
    private ImageButton chat_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chat_button = findViewById(R.id.chat_button);
        chat_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // 임시. 화면 이동만 구현
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            }
        });
    }
}