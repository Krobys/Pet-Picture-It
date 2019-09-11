package com.akrivonos.app_standart_java.fragments;


import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akrivonos.app_standart_java.MapPictureActivity;
import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.executors.PicturesDownloadTask;
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.SettingsLoadPage;
import com.akrivonos.app_standart_java.utils.InternetUtils;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.DEFAULT_MODE_NIGHT;
import static com.akrivonos.app_standart_java.constants.Values.MY_MAP_PERMISSION_CODE;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.constants.Values.RESULT_MAP_COORDINATES;
import static com.akrivonos.app_standart_java.constants.Values.SEARCH_FIELD_TEXT;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchPictureFragment extends Fragment implements LoaderListener,
        ControlBorderDownloaderListener {

    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private EditText searchRequestEditText;
    private Button searchButton;
    private String searchText;
    private ProgressBar progressBar;
    private String currentUser;
    private RecyclerView recyclerViewPictures;
    private LinearLayoutManager linearLayoutManager;
    private PictureAdapter pictureAdapter;
    private final View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) { // Кнопка начала скачивания
            searchText = searchRequestEditText.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(searchText)) {
                if (InternetUtils.isInternetConnectionEnable(getContext())) {
                    pictureAdapter.throwOffData();
                    new PicturesDownloadTask(SearchPictureFragment.this).startLoadPictures(searchText, currentUser, 1);
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };
    private final ItemTouchHelper.Callback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) { // Свайп для recycleView
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            pictureAdapter.deleteItem(viewHolder.getAdapterPosition());
        }
    };
    private LatLng coordinatesToFindPics;


    public SearchPictureFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_search_picture, container, false);
        ControlBorderDownloaderListener controlBorderDownloaderListener = this;
        OpenListItemLinkListener openListItemLinkListener = (OpenListItemLinkListener) getActivity();
        pictureAdapter = new PictureAdapter(openListItemLinkListener,
                controlBorderDownloaderListener,
                getContext()); //создаем адаптер

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewPictures = layoutView.findViewById(R.id.rec_view_picture);
        recyclerViewPictures.setLayoutManager(linearLayoutManager);
        recyclerViewPictures.setAdapter(pictureAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerViewPictures);

        progressBar = layoutView.findViewById(R.id.progressBar);
        searchRequestEditText = layoutView.findViewById(R.id.search_request);
        searchButton = layoutView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(startSearch);
        currentUser = PreferenceUtils.getCurrentUserName(getContext());

        restoreSearchField();
        return layoutView;
    }

    private void saveSearchField() { //сохранение состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String searchFieldText = searchRequestEditText.getText().toString();
        sharedPreferences.edit().putString(SEARCH_FIELD_TEXT, searchFieldText).apply();
    }

    private void restoreSearchField() { //востановление состояния поля для ввода
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (sharedPreferences.contains(SEARCH_FIELD_TEXT)) {
            String searchFieldText = sharedPreferences.getString(SEARCH_FIELD_TEXT, "");
            searchRequestEditText.setText(searchFieldText);
        }
    }

    private void saveDefaultNightMode(int defaultMode) { //сохранить тему приложения (восстанавливается в AuthActivity)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        sharedPreferences.edit().putInt(DEFAULT_MODE_NIGHT, defaultMode).apply();
    }

    private boolean checkPermissionsMap() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS_STORAGE, MY_MAP_PERMISSION_CODE);
        } else {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_MAP_PERMISSION_CODE) {
            for (int perm : grantResults) {
                if (perm != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            startActivityForResult(new Intent(getContext(), MapPictureActivity.class), RESULT_MAP_COORDINATES);
        }
    }

    @Override
    public void startLoading() {
        progressBar.setVisibility(View.VISIBLE);
        searchButton.setClickable(false);
    }

    @Override
    public void finishLoading(ArrayList<PhotoInfo> photos, SettingsLoadPage pageSettings) {
        progressBar.setVisibility(View.GONE);
        pictureAdapter.setTypeLoadingPage(pageSettings.getTypeLoadPage());
        pictureAdapter.setData(photos);
        searchButton.setClickable(true);
        pictureAdapter.setPageSettings(pageSettings.getCurrentPage(), pageSettings.getPagesAmount());
    }


    @Override
    public void loadNextPage(int pageToLoad, int typePage) {
        switch (typePage) {
            case PAGE_DEF_PIC:
                new PicturesDownloadTask(this).startLoadPictures(searchText, currentUser, pageToLoad);
                break;
            case PAGE_MAP_PIC:
                new PicturesDownloadTask(this).startLoadPictures(coordinatesToFindPics, currentUser, pageToLoad);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        saveSearchField();
        super.onDestroyView();
    }
}
