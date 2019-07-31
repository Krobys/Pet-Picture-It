package com.akrivonos.pi_school_w2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import static com.akrivonos.pi_school_w2.MainActivity.SPAN_URL;

public class LinkContentActivity extends AppCompatActivity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_content);

        webView = findViewById(R.id.web_view);
        final Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(SPAN_URL)) {
                webView.loadUrl(intent.getStringExtra(SPAN_URL));
            }
        }
    }
}
