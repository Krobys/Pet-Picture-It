package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
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
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.OnResultCoordinatesPictureListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.PostDownloadPicturePack;
import com.akrivonos.app_standart_java.models.SettingsLoadPage;
import com.akrivonos.app_standart_java.retrofit.RetrofitSearchDownload;
import com.akrivonos.app_standart_java.utils.InternetUtils;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_POSITION_LAYOUT;
import static com.akrivonos.app_standart_java.constants.Values.LATTITUDE_LONGITUDE;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.constants.Values.SEARCH_FIELD_TEXT;

public class SearchPictureFragment extends Fragment implements ControlBorderDownloaderListener,
        OnResultCoordinatesPictureListener {

    private EditText searchRequestEditText;
    private Button searchButton;
    private String searchText;
    private ProgressBar progressBar;
    private String currentUser;
    private LinearLayoutManager linearLayoutManager;
    private PictureAdapter pictureAdapter;
    private LiveData<PostDownloadPicturePack> liveDataPhoto;
    private final View.OnClickListener startSearch = new View.OnClickListener() {
        @Override
        public void onClick(View v) { // Кнопка начала скачивания
            searchText = searchRequestEditText.getText().toString().toLowerCase();
            if (!TextUtils.isEmpty(searchText)) {
                if (InternetUtils.isInternetConnectionEnable(getContext())) {
                    pictureAdapter.throwOffData();
                    RetrofitSearchDownload.getInstance().startDownloadPictures(searchText, currentUser, 1);
                    progressBar.setVisibility(View.VISIBLE);
                    searchButton.setClickable(false);
                    //new PicturesDownloadTask(SearchPictureFragment.this).startLoadPictures(searchText, currentUser, 1);
                } else {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), getString(R.string.empty_field), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final Observer<PostDownloadPicturePack> downloadPicturesObserverDuo = new Observer<PostDownloadPicturePack>() {
        @Override
        public void onChanged(@Nullable PostDownloadPicturePack postDownloadPicturePack) {
            if (postDownloadPicturePack != null) {
                SettingsLoadPage settingsLoadPage = postDownloadPicturePack.getSettingsLoadPage();
                pictureAdapter.setTypeLoadingPage(settingsLoadPage.getTypeLoadPage());
                pictureAdapter.setData(postDownloadPicturePack.getPhotos());
                pictureAdapter.setPageSettings(settingsLoadPage.getCurrentPage(), settingsLoadPage.getPagesAmount());
            }else{
                Toast.makeText(getContext(), "Error while downloading", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
            searchButton.setClickable(true);
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
        ControlBorderDownloaderListener controlBorderDownloaderListener = this;
        OpenListItemLinkListener openListItemLinkListener = (OpenListItemLinkListener) getActivity();
        pictureAdapter = new PictureAdapter(openListItemLinkListener,
                controlBorderDownloaderListener,
                getContext()); //создаем адаптер
        liveDataPhoto = RetrofitSearchDownload.getInstance().getDataBinder();
        liveDataPhoto.observe(this, downloadPicturesObserverDuo);
        super.onAttach(context);
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

    @Override
    public void onResume() {
        checkGeoSearch();
        super.onResume();
    }

    private void checkGeoSearch(){
        Bundle bundle = getArguments();
        if(bundle!=null && bundle.containsKey(LATTITUDE_LONGITUDE)){
            LatLng latLng = bundle.getParcelable(LATTITUDE_LONGITUDE);
            startCoordinatesSearch(latLng);
        }
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
    public void loadNextPage(int pageToLoad, int typePage) {
        switch (typePage) {
            case PAGE_DEF_PIC:
                RetrofitSearchDownload.getInstance().startDownloadPictures(searchText, currentUser, pageToLoad);
                break;
            case PAGE_MAP_PIC:
                RetrofitSearchDownload.getInstance().startDownloadPictures(coordinatesToFindPics, currentUser, pageToLoad);
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

    public void startCoordinatesSearch(LatLng latLng) {
        coordinatesToFindPics = latLng;
        pictureAdapter.throwOffData();
        RetrofitSearchDownload.getInstance().startDownloadPictures(coordinatesToFindPics, currentUser, 1);
        progressBar.setVisibility(View.VISIBLE);
        searchButton.setClickable(false);
    }
}
