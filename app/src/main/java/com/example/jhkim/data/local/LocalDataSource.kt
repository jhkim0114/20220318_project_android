package com.example.jhkim.data.local

import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val keywordDao: KeywordDao,
    private val thumbnailDao: ThumbnailDao
) {
    fun keywordDao() = keywordDao
    fun thumbnailDao() = thumbnailDao
}