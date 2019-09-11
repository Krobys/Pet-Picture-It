package com.akrivonos.app_standart_java.fragments;


import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.database.DatabaseControl;
import com.akrivonos.app_standart_java.database.DatabaseControlListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.MY_DOWNLOAD_PERMISSION_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class LinkContentFragment extends Fragment {

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    private PhotoInfo photoInfo;

    public LinkContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_link_content, container, false);

        DatabaseControlListener databaseControlListener = new DatabaseControl(getContext());
        setPhotoInfo();
        databaseControlListener.addToHistoryConvention(photoInfo);
        WebView webView = view.findViewById(R.id.web_view);

        webView.loadUrl(photoInfo.getUrlText());
        return view;
    }

    private void setPhotoInfo() {
        Bundle bundle = getArguments(); //TODO передача из активити
        photoInfo = bundle.getParcelable(BUNDLE_PHOTO_INFO);
    }

    private void downloadPhotoWithDownloadManager(String url) {
        String requestText = photoInfo.getRequestText();
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Downloading picture by request " + requestText);
        request.setTitle("Download with PI_School");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, requestText + ".png");

        DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
        Toast.makeText(getContext(), "Starting Download", Toast.LENGTH_SHORT).show();
    }

    private void checkPermissionsDownload() {//проверка разрешений
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, MY_DOWNLOAD_PERMISSION_CODE);
        } else {
            downloadPhotoWithDownloadManager(photoInfo.getUrlText());
        }
    }

}
