package com.akrivonos.app_standart_java.retrofit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.akrivonos.app_standart_java.models.PhotoInfo;
import com.akrivonos.app_standart_java.models.PostDownloadPicturePack;
import com.akrivonos.app_standart_java.models.Rsp;
import com.akrivonos.app_standart_java.models.SettingsLoadPage;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

import static com.akrivonos.app_standart_java.constants.Values.API_KEY_FLICKR;
import static com.akrivonos.app_standart_java.constants.Values.METHOD_SEARCH_BY_GEO;
import static com.akrivonos.app_standart_java.constants.Values.METHOD_SEARCH_BY_TEXT;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_DEF_PIC;
import static com.akrivonos.app_standart_java.constants.Values.PAGE_MAP_PIC;
import static com.akrivonos.app_standart_java.models.PostDownloadPicturePack.TYPE_DOWNLOAD_CHEDULE;
import static com.akrivonos.app_standart_java.models.PostDownloadPicturePack.TYPE_DOWNLOAD_STANDART;

public class RetrofitSearchDownload {

    private String searchText;
    private int typeLoadPageTask;
    private String userName;

    private final static String BASE_URL = "https://www.flickr.com/services/";
    private static RetrofitSearchDownload retrofitSearchDownload;
    private MutableLiveData<PostDownloadPicturePack> transData;
    private final ApiRetrofitInterface apiService;
    private PublishSubject<ArrayList<PhotoInfo>> observableScheduledPhotos;
    private RetrofitSearchDownload() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiRetrofitInterface.class);
    }

    public static RetrofitSearchDownload getInstance() {
        if (retrofitSearchDownload == null) {
            retrofitSearchDownload = new RetrofitSearchDownload();
        }
        return retrofitSearchDownload;
    }

    public MutableLiveData<PostDownloadPicturePack> getDataBinder() {
        if (transData == null) {
            transData = new MutableLiveData<>();
        }
        return transData;
    }

    public void setObserverScheduled(io.reactivex.Observer<ArrayList<PhotoInfo>> observer) {
        observableScheduledPhotos = PublishSubject.create();
        observableScheduledPhotos
                .observeOn(Schedulers.io())
                .subscribe(observer);
    }

    public void startDownloadPictures(String searchText, String userName, int pageToLoad, int typeDownload) {
        typeLoadPageTask = PAGE_DEF_PIC;
        this.searchText = searchText;
        this.userName = userName;
        Call<Rsp> RspCall = apiService.searchPhotosByName(METHOD_SEARCH_BY_TEXT, API_KEY_FLICKR, searchText, pageToLoad);
        RspCall.enqueue(new Callback<Rsp>() {
            @Override
            public void onResponse(@NonNull Call<Rsp> call,@NonNull Response<Rsp> response) {
                PostDownloadPicturePack postDownloadPicturePack;
                Rsp rsp = response.body();
                if(rsp != null){
                    if (response.code() == 200){
                        switch (typeDownload) {
                            case TYPE_DOWNLOAD_STANDART:
                                postDownloadPicturePack = new PostDownloadPicturePack();
                                postDownloadPicturePack.setPhotos(convertPhotoToPhotoInfo(rsp.getPhotos().getPhoto()));
                                postDownloadPicturePack.setSettingsLoadPage(new SettingsLoadPage(rsp.getPhotos().getPage(), rsp.getPhotos().getPages(), typeLoadPageTask));
                                transData.setValue(postDownloadPicturePack);
                                break;
                            case TYPE_DOWNLOAD_CHEDULE:
                                observableScheduledPhotos.onNext(convertPhotoToPhotoInfo(rsp.getPhotos().getPhoto()));
                                break;
                        }
                    }
                    else{
                        transData.setValue(null);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Rsp> call,@NonNull Throwable t) {
                Log.d("test", "onFailure: "+t.getMessage());
                transData.setValue(null);
            }
        });
    }

    public void startDownloadPictures(LatLng latLng, String userName, int pageToLoad, int typeDownload) {
        typeLoadPageTask = PAGE_MAP_PIC;
        this.userName = userName;
        final Call<Rsp> RspCall = apiService.searchPhotosByGeo(METHOD_SEARCH_BY_GEO, API_KEY_FLICKR, latLng.latitude, latLng.longitude, pageToLoad);
        RspCall.enqueue(new Callback<Rsp>() {
            @Override
            public void onResponse(@NonNull Call<Rsp> call, @NonNull Response<Rsp> response) {
                PostDownloadPicturePack postDownloadPicturePack;
                Rsp rsp = response.body();

                if(rsp != null){
                if (response.code() == 200){
                    postDownloadPicturePack = new PostDownloadPicturePack();
                    postDownloadPicturePack.setPhotos(convertPhotoToPhotoInfo(rsp.getPhotos().getPhoto()));
                    postDownloadPicturePack.setSettingsLoadPage(new SettingsLoadPage(rsp.getPhotos().getPage(), rsp.getPhotos().getPages(), typeLoadPageTask));
                    transData.setValue(postDownloadPicturePack);
                }
                else{
                    transData.setValue(null);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Rsp> call, @NonNull Throwable t) {
                Log.d("test", "onFailure: "+t.getMessage());
                transData.setValue(null);
            }
        });
    }

    private ArrayList<PhotoInfo> convertPhotoToPhotoInfo(List<Rsp.Photo> photos) {
        ArrayList<PhotoInfo> photosFinal = new ArrayList<>();
        PhotoInfo photoInfo;
        for (Rsp.Photo photo : photos) {
            photoInfo = new PhotoInfo();
            photoInfo.setUrlText(getPhotoUrl(photo));
            photoInfo.setRequestText((typeLoadPageTask == PAGE_DEF_PIC) ? searchText : "geo");
            photoInfo.setUserName(userName);
            photosFinal.add(photoInfo);
        }
        return photosFinal;
    }

    private String getPhotoUrl(Rsp.Photo photo) { // генерация адреса для каждой фото
        String farm = photo.getFarm();
        String server = photo.getServer();
        String id = photo.getId();
        String secret = photo.getSecret();
        return "https://farm" + farm + ".staticflickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }
}
