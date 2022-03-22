package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.ResponseData
import com.example.jhkim.data.entities.Vclip
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ThumbnailService {

    @GET("v2/search/image")
    fun getImage(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Call<ResponseData<Image>>

    @GET("v2/search/vclip")
    fun getVclip(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int
    ): Call<ResponseData<Vclip>>

}