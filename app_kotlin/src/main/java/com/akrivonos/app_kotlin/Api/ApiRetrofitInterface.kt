package com.akrivonos.app_kotlin.Api

import com.akrivonos.app_kotlin.Models.Rsp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiRetrofitInterface {
    @GET("?method=flickr.photos.search")
    fun searchPhotosByName(@Query("api_key") api_key: String, @Query("text") searchText: String): Call<Rsp>
}
