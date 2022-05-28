package com.example.helpsook.Map;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.helpsook.R;

public class AddressApiActivity extends AppCompatActivity {

    //카카오 도로명 주소 검색을 위해 웹 뷰 이용
    private WebView webView;

    //웹 뷰에서 검색 결과로 나온 주소를 안드로이드로 가져오기
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        @SuppressWarnings("unused")
        public void processDATA(String data) {
            Bundle extra = new Bundle();
            Intent intent = new Intent();
            extra.putString("data", data);
            intent.putExtras(extra);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //자바 스크립트를 이용하여 서버에 올려둔 지도 html을 불러오기
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_api);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new MyJavaScriptInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                //android -> Javascript 함수 호출!
                webView.loadUrl("javascript:sample2_execDaumPostcode();");
            }
        });

        //최초의
        webView.loadUrl("http://helpsook.web.app");


    }
}
