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

import static com.akrivonos.app_standart_java.MainActivity.BUNDLE_PHOTO_INFO;

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
        int iconIsFavorite = (databaseControlListener.checkIsFavorite(photoInfo.getUrlText()))
                ? R.drawable.ic_favorite_black_active
                : R.drawable.ic_favorite_border_black_unactive;
        toolbar.getMenu().getItem(1).setIcon(iconIsFavorite);
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
        Bundle bundle = intent.getParcelableExtra(BUNDLE_PHOTO_INFO);
        photoInfo = bundle.getParcelable(BUNDLE_PHOTO_INFO);
    }
}
