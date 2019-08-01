package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import static com.akrivonos.app_standart_java.MainActivity.SPAN_URL;

public class LinkContentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_content);

        WebView webView = findViewById(R.id.web_view);
        final Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(SPAN_URL)) {
                webView.loadUrl(intent.getStringExtra(SPAN_URL));
            }
        }
    }
}
