package com.example.jhkim.data.local

import android.util.Log
import com.example.jhkim.data.entities.Image
import com.example.jhkim.data.entities.Thumbnail
import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject


class LocalDataSource @Inject constructor(
    private val keywordDao: KeywordDao,
    private val thumbnailDao: ThumbnailDao
) {
    fun keywordDao() = keywordDao
    fun thumbnailDao() = thumbnailDao
}