package com.example.jhkim.data.remote

import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Vclip
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiManager {

    fun getData() {
        getImageData()
        getVclipData()
    }

    fun getImageData() {
//        apiRequest<List<Image>>(
//            getService().getImage("축구")
//        )
    }

    fun getVclipData() {
        apiRequest<List<Vclip>>(
            getService().getVclip("축구")
        )
    }

    fun getService(): ThumbnailService {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor { chain ->
            val request: Request = chain.request().newBuilder().addHeader("Authorization", "KakaoAK 40edd132c9b358ea0da3c55f6ff40ae4").build()
            chain.proceed(request)
        }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()

        return retrofit.create(ThumbnailService::class.java)
    }

    data class ResponseData<T>(@SerializedName("documents") val result: T)

    fun <T>apiRequest(
        serviceCall: Call<ResponseData<T>>,
    ) {
        serviceCall.enqueue(object : Callback<ResponseData<T>> {
            override fun onResponse(
                call: Call<ResponseData<T>>,
                response: Response<ResponseData<T>>
            ) {

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        val result = body.result
                        if (result != null) {

                        } else {
                            onFailure(call, Throwable())
                        }
                    } else {
                        onFailure(call, Throwable())
                    }
                } else {
                    onFailure(call, Throwable())
                }
             }

            override fun onFailure(call: Call<ResponseData<T>>, t: Throwable) {
                print("")
            }
        })

    }

}