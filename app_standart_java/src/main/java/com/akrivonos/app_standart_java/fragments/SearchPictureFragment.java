package com.akrivonos.app_standart_java.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akrivonos.app_standart_java.R;
import com.akrivonos.app_standart_java.adapters.PictureAdapter;
import com.akrivonos.app_standart_java.listeners.ControlBorderDownloaderListener;
import com.akrivonos.app_standart_java.listeners.OpenListItemLinkListener;
import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.PostDownloadPicturePack;
import com.akrivonos.app_standart_java.models.SettingsLoadPage;
import com.akrivonos.app_standart_java.retrofit.RetrofitSearchDownload;
import com.akrivonos.app_standart_java.utils.InternetUtils;
import com.akrivonos.app_standart_java.utils.PreferenceUtils;
import com.google.android.gms.maps.model.LatLng;
import com.jakewharton.rxbinding3.view.RxView;
import com.jakewharton.rxbinding3.widget.RxTextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.disposables.Disposable;

import static com.akrivonos.app_standart_java.constants.Values.BUNDLE_PHOTO_INFO;
import static com.akrivonos.app_standart_java.constants.Values.CURRENT_POSITION_LAYOUT;
import static com.akrivonos.app_standart_java.constants.Values.LATTITUDE_LONGITUDE;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.models.PostDownloadPicturePack.TYPE_DOWNLOAD_STANDART;

public class SearchPictureFragment extends Fragment implements ControlBorderDownloaderListener {
    public static final String SEARCH_PICTURE_FRAGMENT = "search_picture_fragment";
    private EditText searchRequestEditText;
    private Button searchButton;
    private String searchText;
    private ProgressBar progressBar;
    private String currentUser;
    private LinearLayoutManager linearLayoutManager;
    private PictureAdapter pictureAdapter;
    private final androidx.lifecycle.Observer<PostDownloadPicturePack> downloadPicturesObserverDuo = new androidx.lifecycle.Observer<PostDownloadPicturePack>() {
        @Override
        public void onChanged(@Nullable PostDownloadPicturePack postDownloadPicturePack) {
            if (postDownloadPicturePack != null) {
                SettingsLoadPage settingsLoadPage = postDownloadPicturePack.getSettingsLoadPage();
                if (settingsLoadPage.getCurrentPage() == 1) pictureAdapter.throwOffData();
                pictureAdapter.setTypeLoadingPage(settingsLoadPage.getTypeLoadPage());
                pictureAdapter.setData(postDownloadPicturePack.getPhotos());
                pictureAdapter.setPageSettings(settingsLoadPage.getCurrentPage(), settingsLoadPage.getPagesAmount());
            } else {
                Toast.makeText(getContext(), "Error while downloading", Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
            searchButton.setClickable(true);
        }
    };

    private Disposable buttonSearchDis;
    private Disposable editTextDis;

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
        LiveData<PostDownloadPicturePack> liveDataPhoto = RetrofitSearchDownload.getInstance().getDataBinder();
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

        editTextDis = RxTextView.textChanges(searchRequestEditText)
                .debounce(350, TimeUnit.MILLISECONDS)
                .filter(searchText -> searchText.length() >= 3 && searchText.length() < 10)
                .map(CharSequence::toString)
                .subscribe(searchText -> {
                    this.searchText = searchText;
                    RetrofitSearchDownload.getInstance().startDownloadPictures(searchText, currentUser, 1, TYPE_DOWNLOAD_STANDART);
                });

        buttonSearchDis = RxView.clicks(searchButton)
                .map(unit -> searchRequestEditText.getText().toString())
                .filter(searchText -> !TextUtils.isEmpty(searchText) && InternetUtils.isInternetConnectionEnable(getContext()))
                .subscribe(searchText -> {
                    this.searchText = searchText;
                    RetrofitSearchDownload.getInstance().startDownloadPictures(searchText, currentUser, 1, TYPE_DOWNLOAD_STANDART);
                            pictureAdapter.throwOffData();
                            progressBar.setVisibility(View.VISIBLE);
                            searchButton.setClickable(false);
                        }
                );

            currentUser = PreferenceUtils.getCurrentUserName(getContext());

        searchRequestEditText.setText(PreferenceUtils.restoreSearchField(getContext()));
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



    @Override
    public void loadNextPage(int pageToLoad, int typePage) {
        switch (typePage) {
            case PAGE_DEF_PIC:
                RetrofitSearchDownload.getInstance().startDownloadPictures(searchText, currentUser, pageToLoad, TYPE_DOWNLOAD_STANDART);
                break;
            case PAGE_MAP_PIC:
                RetrofitSearchDownload.getInstance().startDownloadPictures(coordinatesToFindPics, currentUser, pageToLoad, TYPE_DOWNLOAD_STANDART);
                break;
        }
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        PreferenceUtils.saveSearchField(getContext(), searchRequestEditText.getText().toString());
        editTextDis.dispose();
        buttonSearchDis.dispose();
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
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    private void startCoordinatesSearch(LatLng latLng) {
        coordinatesToFindPics = latLng;
        pictureAdapter.throwOffData();
        RetrofitSearchDownload.getInstance().startDownloadPictures(coordinatesToFindPics, currentUser, 1, TYPE_DOWNLOAD_STANDART);
        progressBar.setVisibility(View.VISIBLE);
        searchButton.setClickable(false);
    }
}
