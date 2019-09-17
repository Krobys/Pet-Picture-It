package com.akrivonos.app_standart_java.retrofit;

import com.akrivonos.app_standart_java.models.Rsp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRetrofitInterface {
    @GET("rest/")
    Call<Rsp> searchPhotosByName(@Query("method") String method, @Query("api_key") String api_key, @Query("text") String searchText, @Query("page") int page);
    @GET("rest/")
    Call<Rsp> searchPhotosByGeo(@Query("method") String method, @Query("api_key") String api_key, @Query("lat") Double lat, @Query("lng") Double lng, @Query("page") int page);
}
