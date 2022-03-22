package com.example.jhkim.di

import com.example.jhkim.data.remote.RemoteDataSource
import com.example.jhkim.data.remote.ThumbnailService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    private const val BASE_URL = "https://dapi.kakao.com/"
    private const val AUTH_KEY = "KakaoAK 40edd132c9b358ea0da3c55f6ff40ae4"

    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request: Request = chain.request().newBuilder().addHeader("Authorization", AUTH_KEY).build()
                chain.proceed(request)
            }
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
    }

    @Provides
    fun provideConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    fun provideThumbnailService(retrofit: Retrofit): ThumbnailService {
        return retrofit.create(ThumbnailService::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(thumbnailService: ThumbnailService): RemoteDataSource {
        return RemoteDataSource(thumbnailService)
    }

}