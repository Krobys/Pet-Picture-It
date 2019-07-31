package com.akrivonos.app_kotlin.Api

import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.akrivonos.app_kotlin.Models.Rsp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

object RetrofitSearchDownload {
    private var transData: MutableLiveData<Rsp> = MutableLiveData()
    private val apiService: ApiRetrofitInterface
    private const val BASE_URL = "https://www.flickr.com/services/rest/"
    private const val API_KEY = "c67772a7cb8e4c8be058a309f88f62cf"

    val dataBinder: MutableLiveData<Rsp> = transData

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()

        apiService = retrofit.create(ApiRetrofitInterface::class.java)
    }

    fun startDownloadPictures(searchText: String) {

        val rspCall = apiService.searchPhotosByName(API_KEY, searchText)
        rspCall.enqueue(object : Callback<Rsp> {
            override fun onResponse(call: Call<Rsp>, response: Response<Rsp>) {
                if (response.code() == 200)
                    transData.value = response.body()
                else
                    transData.value = null
            }

            override fun onFailure(call: Call<Rsp>, t: Throwable) {
                Log.d("test", "onFail: ${t.message}")
                transData.value = null
            }
        })
    }
}
