package com.akrivonos.app_standart_java;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import static com.akrivonos.app_standart_java.AuthActivity.USER_NAME;
import static com.akrivonos.app_standart_java.MainActivity.SEARCH_TEXT;
import static com.akrivonos.app_standart_java.MainActivity.SPAN_URL;

public class LinkContentActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseControlListener databaseControlListener;
    private PhotoInfo photoInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_content);

        toolbar = findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        databaseControlListener = new DatabaseControl(getApplicationContext());
        setPhotoInfo();
        databaseControlListener.addToHistoryConvention(photoInfo);
        WebView webView = findViewById(R.id.web_view);

        webView.loadUrl(photoInfo.getUrlText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_info_menu, menu);
        if (databaseControlListener.checkIsFavorite(photoInfo.getUrlText())) {
            toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_favorite_black_active);
        } else {
            toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_favorite_border_black_unactive);
            }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.favorire_pick) {
            if (databaseControlListener.checkIsFavorite(photoInfo.getUrlText())) {
                databaseControlListener.setPhotoNotFavorite(photoInfo);
                toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_favorite_border_black_unactive);
            } else {
                databaseControlListener.setPhotoFavorite(photoInfo);
                toolbar.getMenu().getItem(1).setIcon(R.drawable.ic_favorite_black_active);
            }
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        toolbar.setTitle(photoInfo.getRequestText());
        toolbar.setSubtitle(photoInfo.getUserName());
        toolbar.getMenu().getItem(0).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setPhotoInfo() {
        Intent intent = getIntent();
        String userName = intent.getStringExtra(USER_NAME);
        String requestFieldText = intent.getStringExtra(SEARCH_TEXT);
        String urlText = intent.getStringExtra(SPAN_URL);

        photoInfo = new PhotoInfo(userName, requestFieldText, urlText);
    }
}
