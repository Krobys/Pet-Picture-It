package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.executors.PicturesDownloadTask;
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.LoaderListener;
import com.akrivonos.app_standart_java.listeners.OnResultCoordinatesPictureListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.SettingsLoadPage;
import com.akrivonos.app_standart_java.utils.InternetUtils;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_POSITION_LAYOUT;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.constants.Values.SEARCH_FIELD_TEXT;

public class SearchPictureFragment extends Fragment implements LoaderListener,
        ControlBorderDownloaderListener,
        OnResultCoordinatesPictureListener {

    private EditText searchRequestEditText;
    private Button searchButton;
    private String searchText;
    private ProgressBar progressBar;
    private String currentUser;
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
    public void onAttach(Context context) {

        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ControlBorderDownloaderListener controlBorderDownloaderListener = this;
        OpenListItemLinkListener openListItemLinkListener = (OpenListItemLinkListener) getActivity();
        pictureAdapter = new PictureAdapter(openListItemLinkListener,
                controlBorderDownloaderListener,
                getContext()); //создаем адаптер
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_search_picture, container, false);

            setRetainInstance(true);
            linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewPictures = layoutView.findViewById(R.id.rec_view_picture);
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {//сохранение списка загруженных картинок при пересоздании
            ArrayList<PhotoInfo> pictures = pictureAdapter.getData();
            int currentPosition = linearLayoutManager.findLastVisibleItemPosition();

            outState.putParcelableArrayList(BUNDLE_PHOTO_INFO, pictures);
            outState.putInt(CURRENT_POSITION_LAYOUT, currentPosition);
            super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Activity activity = getActivity();
        if(activity != null)
        activity.setTitle("PICTURE IT");
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void startCoordinatesSearch(LatLng latLng) {
        coordinatesToFindPics = latLng;
        pictureAdapter.throwOffData();
        new PicturesDownloadTask(this).startLoadPictures(coordinatesToFindPics, currentUser, 1);
    }
}
