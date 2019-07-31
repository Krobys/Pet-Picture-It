package com.akrivonos.pi_school_w2.Api;

import android.arch.lifecycle.MutableLiveData;

import com.akrivonos.pi_school_w2.Models.Rsp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

public class RetrofitSearchDownload {

    private final static String BASE_URL = "https://www.flickr.com/services/rest/";
    private final static String API_KEY = "c67772a7cb8e4c8be058a309f88f62cf";
    private static RetrofitSearchDownload retrofitSearchDownload;
    private MutableLiveData<Rsp> transData;
    private ApiRetrofitInterface apiService;

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

    public MutableLiveData<Rsp> getDataBinder() {
        if (transData == null) {
            transData = new MutableLiveData<>();
        }
        return transData;
    }

    public void startDownloadPictures(String searchText) {

        Call<Rsp> RspCall = apiService.searchPhotosByName(API_KEY, searchText);
        RspCall.enqueue(new Callback<Rsp>() {
            @Override
            public void onResponse(Call<Rsp> call, Response<Rsp> response) {
                if (response.code() == 200)
                    transData.setValue(response.body());
                else
                    transData.setValue(null);
            }

            @Override
            public void onFailure(Call<Rsp> call, Throwable t) {
                transData.setValue(null);
            }
        });
    }
}
