package com.example.jhkim.data.repository

import com.example.jhkim.data.entities.*
import com.example.jhkim.data.local.LocalDataSource
import com.example.jhkim.data.remote.RemoteDataSource
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class ThumbnailRepository @Inject constructor(
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource,
) {
    // 로컬 키워드 5분 지나면 삭제
    // 로컬 키워드 정보 등록하기
    // 로컬 키워드 정보 가져오기
    // 로컬 keyword 썸네일 가져오기
    // 로컬 like 썸네일 가져오기
    // 로컬 like 업데이트
    // 리모트 image 데이터 가져오기
    // 리모트 vclip 데이터 가져오기

    // 로컬 시간 지난 키우더 리스트 가져오기
    suspend fun seleteKeywordTimeout(timeout: Long): List<Keyword> {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().seleteKeywordTimeout(now, timeout)
    }

    // 로컬 키워드 리스트 삭제
    suspend fun deleteKeywordList(idList: List<Long>) {
        localDataSource.keywordDao().deleteKeywordList(idList)
    }

    // 로컬 썸네일 리스트 삭제
    suspend fun deleteThumbnailList(textList: List<String>) {
        localDataSource.thumbnailDao().deleteThumbnailList(textList)
    }

    // 로컬 키워드 데이터 등록
    suspend fun insertKeyword(keyword: Keyword): Long {
        return localDataSource.keywordDao().insertKeyword(keyword)
    }

    // 로컬 키워드 데이터 업데이트
    suspend fun updateKeyword(keyword: Keyword) {
        return localDataSource.keywordDao().updateKeyword(keyword)
    }

    // 로컬 키워드 데이터 가져오기
    suspend fun seleteKeyword(text: String): Keyword? {
        return localDataSource.keywordDao().seleteKeyword(text)
    }

    // 로컬 flow 키워드 데이터 가져오기
    fun seleteFlowKeyword(): Flow<Keyword?> {
        return localDataSource.keywordDao().seleteFlowKeyword()
    }

    // 리모트 image 데이터 가져오기
    fun getImageData(text: String, page: Int, result: ((ResponseData<List<Image>>) -> Unit)) {
        remoteDataSource.getImageData(text, page, result)
    }

    // 리모트 vclip 데이터 가져오기
    fun getVclipData(text: String, page: Int, result: ((ResponseData<List<Vclip>>) -> Unit)) {
        remoteDataSource.getVclipData(text, page, result)
    }

    // 로컬 flow 썸네일 데이터 가져오기
    fun getLocalSearchData(): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().seleteThumbnailData()
    }

    // 로컬 썸네일 is_view true 업데이트
    suspend fun updateThumbnailIsViewTrue(text: String) {
        return localDataSource.thumbnailDao().updateThumbnailIsViewTrue(text)
    }

    // 로컬 썸네일 is_view false 업데이트
    suspend fun updateThumbnailIsViewFalse() {
        return localDataSource.thumbnailDao().updateThumbnailIsViewFalse()
    }

    // 로컬 키워드 마지막 검색시간 업데이트
    suspend fun updateKeywordUseDate(text: String) {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().updateKeywordSearchDate(now, text)
    }

    // 로컬 썸네일 데이터 저장
    suspend fun addLocalSearchData(data: List<Thumbnail>) {
        localDataSource.thumbnailDao().insert(data)
    }



    ////

    fun getLocalIsLikeData(): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().getLikeData()
    }



}