package com.akrivonos.app_kotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import com.akrivonos.app_kotlin.MainActivity.Companion.SPAN_URL

class LinkContentActivity : AppCompatActivity() {

    lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_link_content)

        webView = findViewById(R.id.web_view)
        val intent = intent
        if (intent != null) {
            if (intent.hasExtra(SPAN_URL)) {
                webView.loadUrl(intent.getStringExtra(SPAN_URL))
            }
        }
    }
}
