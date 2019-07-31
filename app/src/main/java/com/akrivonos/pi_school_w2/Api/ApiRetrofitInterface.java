package com.akrivonos.pi_school_w2.Api;

import com.akrivonos.pi_school_w2.Models.Rsp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiRetrofitInterface {
    @GET("?method=flickr.photos.search")
    Call<Rsp> searchPhotosByName(@Query("api_key") String api_key, @Query("text") String searchText);
}
