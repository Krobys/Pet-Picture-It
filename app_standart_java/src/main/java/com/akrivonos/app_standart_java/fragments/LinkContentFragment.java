package com.akrivonos.app_standart_java.fragments;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.akrivonos.app_standart_java.MainActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.room.FavoritePhoto;
import com.akrivonos.app_standart_java.room.HistoryPhoto;
import com.akrivonos.app_standart_java.room.RoomAppDatabase;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;

import java.util.List;

import static com.akrivonos.app_standart_java.constants.Values.ARGUMENT_EXPANABLE_FRAG;
import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.MY_DOWNLOAD_PERMISSION_CODE;
import static com.akrivonos.app_standart_java.constants.Values.TYPE_FRAG;

public class LinkContentFragment extends Fragment {

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private PhotoInfo photoInfo;
    private RoomAppDatabase appDatabase;
    private boolean isExpandable;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_link_content, container, false);

        MainActivity mainActivity = (MainActivity) getActivity();
        if(mainActivity != null) appDatabase = mainActivity.getDatabase();

        getArgumentsFragment();
        setUpFragmentButtonsNotExpandable(view);

        setHasOptionsMenu(true);
        AsyncTask.execute(() -> {
            HistoryPhoto historyPhoto = new HistoryPhoto(photoInfo);
            appDatabase.historyPhotoDao().addToHistoryConvention(historyPhoto);
            appDatabase.historyPhotoDao().deleteOverLimitHistory();
        });

        WebView webView = view.findViewById(R.id.web_view);

        webView.loadUrl(photoInfo.getUrlText());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if(!isExpandable){
            inflater.inflate(R.menu.link_content_menu, menu);
            int iconIsFavorite = (checkIsFavorite())
                    ? R.drawable.ic_favorite_black_active
                    : R.drawable.ic_favorite_border_black_unactive;
            menu.findItem(R.id.favorire_pick).setIcon(iconIsFavorite);
        }
        Activity activity = getActivity();
        if(activity == null) return;
        activity.setTitle(photoInfo.getRequestText());
        super.onCreateOptionsMenu(menu, inflater);
    }

    private final View.OnClickListener downloadButtonClick = v -> checkPermissionsDownload();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("test", "Photoinfo id: "+photoInfo.getId());
        switch (item.getItemId()) {
            case R.id.favorire_pick:
                if (checkIsFavorite()) {
                    appDatabase.favoritePhotoDao().setPhotoNotFavorite(photoInfo.getUrlText(), photoInfo.getUserName());
                    item.setIcon(R.drawable.ic_favorite_border_black_unactive);
                } else {
                    appDatabase.favoritePhotoDao().setPhotoFavorite(new FavoritePhoto(photoInfo));
                    item.setIcon(R.drawable.ic_favorite_black_active);
                }
                return true;
            case R.id.picture_download:
                checkPermissionsDownload();
                return true;

        }
        return false;
    }

    private final View.OnClickListener favoriteButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (checkIsFavorite()) {
                appDatabase.favoritePhotoDao().setPhotoNotFavorite(photoInfo.getUrlText(), photoInfo.getUserName());
                v.setBackgroundResource(R.drawable.ic_favorite_border_black_unactive);
            } else {
                appDatabase.favoritePhotoDao().setPhotoFavorite(new FavoritePhoto(photoInfo));
                v.setBackgroundResource(R.drawable.ic_favorite_black_active);
            }
        }
    };

    private boolean checkIsFavorite() {
        List<PhotoInfo> photoInfos = appDatabase.favoritePhotoDao()
                .checkIsFavorite(photoInfo.getUrlText(), PreferenceUtils.getCurrentUserName(getContext()));
        boolean isFavorite = photoInfos.size() != 0;
        Log.d("test", "checkIsFavorite: "+isFavorite);
        return isFavorite;
    }

    private void getArgumentsFragment(){
        Bundle bundle = getArguments();
        if(bundle != null) {
            photoInfo = bundle.getParcelable(BUNDLE_PHOTO_INFO);
            int typeFragment = bundle.getInt(TYPE_FRAG);
            isExpandable = (typeFragment == ARGUMENT_EXPANABLE_FRAG);
        }
    }

    private void downloadPhotoWithDownloadManager(String url) {
        String requestText = photoInfo.getRequestText();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading picture by request " + requestText);
        request.setTitle("Download with PI_School");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, requestText + ".png");
        Activity activity = getActivity();
        if(activity == null) return;
        DownloadManager manager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(activity, "Starting Download", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionsDownload() {//проверка разрешений
        Context context = getContext();
        if(context == null) return;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(PERMISSIONS_STORAGE, MY_DOWNLOAD_PERMISSION_CODE);
        } else {
            downloadPhotoWithDownloadManager(photoInfo.getUrlText());
        }
    }

    private void setUpFragmentButtonsNotExpandable(View view){
        if (isExpandable){
            ImageButton favoriteButton = view.findViewById(R.id.like_button);
            ImageButton downloadButton = view.findViewById(R.id.download_button);
            favoriteButton.setOnClickListener(favoriteButtonClick);
            downloadButton.setOnClickListener(downloadButtonClick);

            int iconIsFavorite = (checkIsFavorite())
                    ? R.drawable.ic_favorite_black_active
                    : R.drawable.ic_favorite_border_black_unactive;
            favoriteButton.setBackgroundResource(iconIsFavorite);
        }
    }

}
