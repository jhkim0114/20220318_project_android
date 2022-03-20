package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Vclip
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ThumbnailService {

//    @GET("/v2/search/image/{query}")
//    fun getImage(@Path("query") query: String): Response<ImageModel>
//
//    @GET("/v2/search/vclip/{query}")
//    fun getVclip(@Path("query") query: String): Response<VclipModel>

//    @Headers("Authorization: KakaoAK 40edd132c9b358ea0da3c55f6ff40ae4")

//    @GET("v2/search/image")
//    fun getImage(@Query("query") query: String): Call<ApiManager.ResponseData<Flow<List<Image>>>>

    @GET("v2/search/image")
    fun getImage(@Query("query") query: String, @Query("sort") sort: String): Call<ResponseData<List<Image>>>

    @GET("v2/search/vclip")
    fun getVclip(@Query("query") query: String): Call<ApiManager.ResponseData<List<Vclip>>>
}