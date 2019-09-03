package com.akrivonos.app_standart_java;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.MY_DOWNLOAD_PERMISSION_CODE;

public class LinkContentActivity extends AppCompatActivity {

    private DatabaseControlListener databaseControlListener;
    private PhotoInfo photoInfo;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_content);

        databaseControlListener = new DatabaseControl(getApplicationContext());
        setPhotoInfo();
        databaseControlListener.addToHistoryConvention(photoInfo);
        WebView webView = findViewById(R.id.web_view);

        webView.loadUrl(photoInfo.getUrlText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.link_content_menu, menu);
        int iconIsFavorite = (databaseControlListener.checkIsFavorite(photoInfo.getUrlText()))
                ? R.drawable.ic_favorite_black_active
                : R.drawable.ic_favorite_border_black_unactive;
        menu.findItem(R.id.favorire_pick).setIcon(iconIsFavorite);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.favorire_pick:
                if (databaseControlListener.checkIsFavorite(photoInfo.getUrlText())) {
                    databaseControlListener.setPhotoNotFavorite(photoInfo);
                    item.setIcon(R.drawable.ic_favorite_border_black_unactive);
                } else {
                    databaseControlListener.setPhotoFavorite(photoInfo);
                   item.setIcon(R.drawable.ic_favorite_black_active);
                }
                return true;
            case R.id.picture_download:
                checkPermissionsDownload();
                return true;

        }
        return false;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setTitle(photoInfo.getRequestText() +"\n"+ photoInfo.getUserName());
        return super.onPrepareOptionsMenu(menu);
    }

    private void setPhotoInfo() {
        Intent intent = getIntent();
        Bundle bundle = intent.getParcelableExtra(BUNDLE_PHOTO_INFO);
        photoInfo = bundle.getParcelable(BUNDLE_PHOTO_INFO);
    }

    private void downloadPhotoWithDownloadManager(String url){
        String requestText = photoInfo.getRequestText();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading picture by request "+ requestText);
        request.setTitle("Download with PI_School");

            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, requestText+".png");

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(this, "Starting Download", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionsDownload() {//проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, MY_DOWNLOAD_PERMISSION_CODE);
        } else {
            downloadPhotoWithDownloadManager(photoInfo.getUrlText());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == MY_DOWNLOAD_PERMISSION_CODE){
            for (int perm : grantResults) {
                if (perm != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            downloadPhotoWithDownloadManager(photoInfo.getUrlText());
        }
    }
}
