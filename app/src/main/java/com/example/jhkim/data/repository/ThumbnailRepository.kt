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

    // 시간 지난 키우더 리스트 가져오기
    suspend fun seleteKeywordTimeout(timeout: Long): List<Keyword> {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().seleteKeywordTimeout(now, timeout)
    }

    // 키워드 리스트 삭제
    suspend fun deleteKeywordList(idList: List<Long>) {
        localDataSource.keywordDao().deleteKeywordList(idList)
    }

    // 썸네일 리스트 삭제
    suspend fun deleteThumbnailList(textList: List<String>) {
        localDataSource.thumbnailDao().deleteThumbnailList(textList)
    }

    // 키워드 데이터 등록
    suspend fun insertKeyword(keyword: Keyword): Long {
        return localDataSource.keywordDao().insertKeyword(keyword)
    }

    // 키워드 데이터 업데이트
    suspend fun updateKeyword(keyword: Keyword) {
        return localDataSource.keywordDao().updateKeyword(keyword)
    }

    // 키워드 데이터 image 업데이트
    suspend fun updateKeywordImage(keyword: Keyword) {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().updateKeywordImage(keyword.text, keyword.image_is_end, keyword.image_page, now)
    }

    // 키워드 데이터 vclip 업데이트
    suspend fun updateKeywordVclip(keyword: Keyword) {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().updateKeywordVclip(keyword.text, keyword.vclip_is_end, keyword.vclip_page, now)
    }

    // 로컬 키워드 데이터 가져오기
    suspend fun seleteKeyword(text: String): Keyword? {
        return localDataSource.keywordDao().seleteKeyword(text)
    }

    // flow 키워드 데이터 가져오기
    fun seleteFlowKeyword(): Flow<Keyword?> {
        return localDataSource.keywordDao().seleteFlowKeyword()
    }

    // api image 데이터 가져오기
    fun getImageData(text: String, page: Int, result: ((ResponseData<List<Image>>) -> Unit)) {
        remoteDataSource.getImageData(text, page, result)
    }

    // api vclip 데이터 가져오기
    fun getVclipData(text: String, page: Int, result: ((ResponseData<List<Vclip>>) -> Unit)) {
        remoteDataSource.getVclipData(text, page, result)
    }

    // flow 썸네일 데이터 가져오기
    fun getLocalSearchData(text: String): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().seleteThumbnailData(text)
    }

    // 키워드 마지막 검색시간 업데이트
    suspend fun updateKeywordUseDate(text: String) {
        val now = Calendar.getInstance().timeInMillis
        return localDataSource.keywordDao().updateKeywordSearchDate(now, text)
    }

    // 썸네일 데이터 저장
    suspend fun addLocalSearchData(data: List<Thumbnail>) {
        localDataSource.thumbnailDao().insert(data)
    }

    // 썸네일 is_like 업데이트
    suspend fun updateThumbnailIsLike(thumbnail: Thumbnail) {
        val isLike = !thumbnail.is_like
        localDataSource.thumbnailDao().updateThumbnailIsLike(thumbnail.id, isLike)
    }

    // flow 썸네일 is_like 데이터 가져오기
    fun seleteThumbnailIsLikeData(): Flow<List<Thumbnail>> {
        return localDataSource.thumbnailDao().seleteThumbnailIsLikeData()
    }



}